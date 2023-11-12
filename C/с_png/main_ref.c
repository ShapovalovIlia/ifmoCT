#include "return_codes.h"

#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <zlib.h>

typedef unsigned char byte;

uint32_t reverse_u32(uint32_t x)
{
	return ((x >> 24) & 0x000000FFul) | ((x >> 8) & 0x0000FF00ul) | ((x << 8) & 0x00FF0000ul) | ((x << 24) & 0xFF000000ul);
}

typedef struct
{
	uint32_t len;
	byte name[4];
	uint32_t width;
	uint32_t height;
	byte data[5];
	uint32_t crc;
} chunk_IHDR;

typedef struct
{
	uint32_t len;
	uint32_t crc;
} chunk_IEND;

typedef struct
{
	uint32_t len;
	uint8_t PLTE[255][3];
	uint32_t crc;
} chunk_PLTE;

typedef struct
{
	byte signature[8];
	chunk_IHDR IHDR;
	chunk_PLTE PLTE;
	byte *data;
	chunk_IEND IEND;
	uint32_t curr_elem_data;
} png_file;

typedef struct
{
	byte *buff;
	png_file *png;
	FILE *fp;
	FILE *w;
} Context;

char *error_message_table[] = {
	[SUCCESS] = "",
	[ERROR_CANNOT_OPEN_FILE] = "can't open file",
	[ERROR_OUT_OF_MEMORY] = "out of memory",
	[ERROR_DATA_INVALID] = "invalid data",
	[ERROR_PARAMETER_INVALID] = "incorrect amount of args",
	[ERROR_UNSUPPORTED] = "unsupported functionality",
	[ERROR_UNKNOWN] = "unknown error"
};

int release_context(Context *context, int error_code)
{
	if (context->fp)
		fclose(context->fp);
	if (context->png->data)
		free(context->png->data);
	if (context->buff)
		free(context->buff);
	if (context->w)
		fclose(context->w);
	if (error_code != SUCCESS)
		fprintf(stderr, "%s\n", error_message_table[error_code]);
	return error_code;
}

int check_file_signature(Context *context)
{
	byte correct_png_sign[8] = { '\211', 'P', 'N', 'G', '\r', '\n', '\032', '\n' };
	fread(context->png->signature, sizeof(byte), 8, context->fp);
	if (memcmp(correct_png_sign, context->png->signature, 8) != 0)
	{
		return release_context(context, ERROR_DATA_INVALID);
	}
	return SUCCESS;
}	 // checked

int check_IHDR(Context *context)
{
	if (memcmp(context->png->IHDR.name, "IHDR", 4) != 0 || context->png->IHDR.data[0] != 8 ||
		context->png->IHDR.data[1] != 0 && context->png->IHDR.data[1] != 2 && context->png->IHDR.data[1] != 3 ||
		context->png->IHDR.data[2] != 0 || context->png->IHDR.data[3] != 0 || context->png->IHDR.data[4] != 0)
	{
		return release_context(context, ERROR_DATA_INVALID);
	}
	return SUCCESS;
}

int dump_IHDR(Context *context)
{
	fread(&context->png->IHDR.len, sizeof(uint32_t), 1, context->fp);
	context->png->IHDR.len = reverse_u32(context->png->IHDR.len);
	if (context->png->IHDR.len != 13)
	{
		return release_context(context, ERROR_DATA_INVALID);
	}
	fread(&context->png->IHDR.name, sizeof(byte), 4, context->fp);
	fread(&context->png->IHDR.width, sizeof(uint32_t), 1, context->fp);
	context->png->IHDR.width = reverse_u32(context->png->IHDR.width);
	fread(&context->png->IHDR.height, sizeof(uint32_t), 1, context->fp);
	context->png->IHDR.height = reverse_u32(context->png->IHDR.height);
	fread(context->png->IHDR.data, sizeof(byte), 5, context->fp);
	fread(&context->png->IHDR.crc, sizeof(uint32_t), 1, context->fp);
	context->png->IHDR.crc = reverse_u32(context->png->IHDR.crc);
	return check_IHDR(context);
}	 // checked

int parse_chunk(Context *context, byte *name, uint32_t len)
{
	if (memcmp(name, "IDAT", 4) == 0)
	{
		context->png->data = realloc(context->png->data, context->png->curr_elem_data + len);
		context->png->curr_elem_data +=
			fread(&context->png->data[context->png->curr_elem_data], sizeof(byte), len, context->fp);
		if (fseek(context->fp, 4, SEEK_CUR) != 0)
		{
			return release_context(context, ERROR_UNKNOWN);
		}
	}
	else if (memcmp(name, "PLTE", 4) == 0)	  // переделать
	{
		for (int i = 0; i < len / 3; ++i)
		{
			fseek(context->fp, sizeof(uint8_t) * 3, SEEK_CUR);
			//			fread(, sizeof(uint8_t), 3, fp);
		}
		if (fseek(context->fp, 4, SEEK_CUR) != 0)
		{
			return release_context(context, ERROR_UNKNOWN);
		}
	}
	else
	{
		if (fseek(context->fp, len + 4, SEEK_CUR) != 0)
		{
			return release_context(context, ERROR_UNKNOWN);
		}
	}
	return SUCCESS;
}

int dump_other_chunks(Context *context)
{
	context->png->data = NULL;
	do
	{
		uint32_t len = 0;
		fread(&len, sizeof(uint32_t), 1, context->fp);
		len = reverse_u32(len);

		byte name[4];
		fread(name, sizeof(byte), 4, context->fp);

		if (memcmp(name, "IEND", 4) == 0)
		{
			context->png->IEND.len = len;
			fread(&context->png->IEND.crc, sizeof(uint32_t), 1, context->fp);
			context->png->IEND.crc = reverse_u32(context->png->IEND.crc);
			return SUCCESS;
		}

		int res = parse_chunk(context, name, len);
		if (res != SUCCESS)
		{
			release_context(context, res);
		}
	} while (true);
}

int read_png(Context *context)
{
	int res = check_file_signature(context);
	if (res != SUCCESS)
	{
		return res;
	}
	res = dump_IHDR(context);
	if (res != SUCCESS)
	{
		return res;
	}
	res = dump_other_chunks(context);
	return res;
}

int write_pnm(Context *context)
{
	short pix_size = context->png->IHDR.data[1] == 2 ? 3 : 1;
	unsigned long size = context->png->IHDR.height * context->png->IHDR.width * pix_size;
	context->buff = malloc(sizeof(byte) * size);
	if (!context->buff)
	{
		fprintf(stderr, "Malloc failed");
		return ERROR_OUT_OF_MEMORY;
	}
	fprintf(context->w, "P%u\n%u %u\n255\n", pix_size == 1 ? 5 : 6, context->png->IHDR.width, context->png->IHDR.height);
	uncompress(context->buff, &size, context->png->data, context->png->curr_elem_data);
	//	if (png->IHDR.data[1] == 3)
	//	{
	//		for (int i = 0; i < png->IHDR.height * pix_size; ++i)
	//		{
	//		}
	//	}
	//	else
	{
		for (int i = 0; i < context->png->IHDR.height * pix_size; ++i)
		{
			fwrite(context->buff + i * (context->png->IHDR.width * pix_size + 1) + (pix_size == 1 ? 0 : 1),
				   1,
				   context->png->IHDR.width * pix_size,
				   context->w);
		}
	}
	return SUCCESS;
}

int main(int argc, char **argv)
{
	if (argc != 3)
	{
		fprintf(stderr, "Incorrect amount of args");
		return ERROR_PARAMETER_INVALID;
	}

	Context context[1] = { NULL };

	context->fp = fopen(argv[1], "rb");
	if (!context->fp)
	{
		return release_context(context, ERROR_CANNOT_OPEN_FILE);
	}

	context->png = malloc(sizeof(png_file));
	if (!context->png)
	{
		return release_context(context, ERROR_OUT_OF_MEMORY);
	}

	int result = read_png(context);
	if (result != SUCCESS)
	{
		return result;
	}
	fclose(context->fp);

	context->w = fopen(argv[2], "wb");
	if (!context->w)
	{
		return release_context(context, ERROR_CANNOT_OPEN_FILE);
	}

	result = write_pnm(context);
	return release_context(context, result);
}
