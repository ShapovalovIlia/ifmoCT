#include "return_codes.h"

#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <zlib.h>

typedef struct
{
	uint32_t len;
	unsigned char name[4];
	uint32_t width;
	uint32_t height;
	unsigned char data[5];
	uint32_t crc;
} chunk_IHDR;

typedef struct
{
	uint32_t len;
	unsigned char name[4];
	uint32_t crc;
} chunk_IEND;

typedef struct
{
	uint32_t len;
	unsigned char name[4];
	uint32_t crc;
} chunk_PLTE;

typedef struct
{
	unsigned char signature[8];
	chunk_IHDR IHDR;
	uint32_t curr_elem_data;
	uint8_t PLTE[255][3];
	unsigned char *data;
	chunk_IEND IEND;
} png_file;

uint32_t reverse_u32(uint32_t x)
{
	return ((x >> 24) & 0x000000FFul) | ((x >> 8) & 0x0000FF00ul) | ((x << 8) & 0x00FF0000ul) | ((x << 24) & 0xFF000000ul);
}

int check_signature(FILE *fp, png_file *png)
{
	unsigned char correct_png_sign[8] = { '\211', 'P', 'N', 'G', '\r', '\n', '\032', '\n' };
	fread(png->signature, sizeof(unsigned char), 8, fp);
	for (size_t i = 0; i < 8; ++i) // подумать почему size_t
	{
		if (correct_png_sign[i] != png->signature[i])
		{
			fprintf(stderr, "Incorrect png signature"); // checked
			return ERROR_DATA_INVALID;
		}
	}
	return SUCCESS;
}

int check_IHDR(FILE *fp, png_file *png)
{
	fread(&png->IHDR.len, sizeof(uint32_t), 1, fp);
	png->IHDR.len = reverse_u32(png->IHDR.len);
	if (png->IHDR.len != 13)
	{
		fprintf(stderr, "Incorrect IHDR chunk"); // checked
		return ERROR_DATA_INVALID;
	}
	fread(&png->IHDR.name, sizeof(unsigned char), 4, fp);
	fread(&png->IHDR.width, sizeof(uint32_t), 1, fp);
	png->IHDR.width = reverse_u32(png->IHDR.width);
	fread(&png->IHDR.height, sizeof(uint32_t), 1, fp);
	png->IHDR.height = reverse_u32(png->IHDR.height);
	fread(png->IHDR.data, sizeof(unsigned char), 5, fp);
	if (png->IHDR.data[0] != 8)
	{
		fprintf(stderr, "Unsupported bit depth"); // checked
		return ERROR_UNSUPPORTED;
	}
	else if (png->IHDR.data[1] != 0 && png->IHDR.data[1] != 2 && png->IHDR.data[1] != 3)
	{
		fprintf(stderr, "Unsupported color type"); // checked
		return ERROR_UNSUPPORTED;
	}
	else if (png->IHDR.data[2] != 0)
	{	 // сжатие
		fprintf(stderr, "Unsupported uncompress type"); // checked
		return ERROR_UNSUPPORTED;
	}
	else if (png->IHDR.data[3] != 0)
	{	 // фильтрация
		fprintf(stderr, "Unsupported filtration type");
		return ERROR_UNSUPPORTED;
	}
	else if (png->IHDR.data[4] != 0)
	{	 // interlace
		fprintf(stderr, "Unsupported interlace type");
		return ERROR_UNSUPPORTED;
	}
	fread(&png->IHDR.crc, sizeof(uint32_t), 1, fp);
	png->IHDR.crc = reverse_u32(png->IHDR.crc);
	return SUCCESS;
}

int dump_IDAT(FILE *fp, png_file *png)
{
	png->data = NULL;	 // без этой строчки падает в дебаге на realloc( png->data, png->curr_elem_data + len)
	do
	{
		uint32_t len;
		fread(&len, sizeof(uint32_t), 1, fp);
		len = reverse_u32(len);
		unsigned char name[4];
		fread(name, sizeof(unsigned char), 4, fp);
		if (memcmp(name, "IEND", 4) == 0)
		{
			png->IEND.len = len;
			fread(&png->IEND.crc, sizeof(uint32_t), 1, fp);
			png->IEND.crc = reverse_u32(png->IEND.crc);
			break;
		}
		else if (memcmp(name, "IDAT", 4) == 0)
		{
			png->data = realloc(png->data, png->curr_elem_data + len);
			png->curr_elem_data += fread(&png->data[png->curr_elem_data], sizeof(unsigned char), len, fp);
			if (fseek(fp, 4, SEEK_CUR) != 0)
			{
				fprintf(stderr, "Error while reading crc in IDAT chunk");
				return ERROR_UNKNOWN;
			}
		}
		else if (memcmp(name, "PLTE", 4) == 0)
		{
			for (int i = 0; i < len / 3; ++i)
			{
				fread(&png->PLTE[i], sizeof(uint8_t), 3, fp);
			}
			if (fseek(fp, 4, SEEK_CUR) != 0)
			{
				fprintf(stderr, "Error while reading crc in PLTE chunk");
				return ERROR_UNKNOWN;
			}
		}
		else
		{
			if (fseek(fp, len + 4, SEEK_CUR) != 0)
			{
				fprintf(stderr, "Error while reading crc");
				return ERROR_UNKNOWN;
			}
		}
	} while (true);
	return SUCCESS;
}

int read_png(FILE *fp, png_file *png)
{
	int res = check_signature(fp, png);
	if (res != 0)
	{
		return res;
	}
	res = check_IHDR(fp, png);
	if (res != 0)
	{
		return res;
	}
	res = dump_IDAT(fp, png);
	return res;
}

int write_pnm(FILE *w, png_file *png)
{
	short pix_size = png->IHDR.data[1] == 2 ? 3 : 1;
	unsigned long size = png->IHDR.height * png->IHDR.width * pix_size;
	unsigned char *buff = malloc(sizeof(unsigned char) * size);
	if (!buff)
	{
		fprintf(stderr, "Malloc failed");
		return ERROR_OUT_OF_MEMORY;
	}
	fprintf(w, "P%u\n%u %u\n255\n", pix_size == 1 ? 5 : 6, png->IHDR.width, png->IHDR.height);
	uncompress(buff, &size, png->data, png->curr_elem_data);
	//	if (png->IHDR.data[1] == 3)
	//	{
	//		for (int i = 0; i < png->IHDR.height * pix_size; ++i)
	//		{
	//		}
	//	}
	//	else
	{
		for (int i = 0; i < png->IHDR.height * pix_size; ++i)
		{
			fwrite(buff + i * (png->IHDR.width * pix_size + 1) + (pix_size == 1 ? 0 : 1), 1, png->IHDR.width * pix_size, w);
		}
	}
	free(buff);
	return SUCCESS;
}

int main(int argc, char **argv)
{
	if (argc != 3)
	{
		fprintf(stderr, "Incorrect amount of args");
		return ERROR_PARAMETER_INVALID;
	}
	FILE *fp = fopen(argv[1], "rb");
	if (!fp)
	{
		fprintf(stderr, "Can't open the file");
		return ERROR_CANNOT_OPEN_FILE;
	}
	png_file *png = malloc(sizeof(png_file));
	if (!png)
	{
		fprintf(stderr, "Malloc failed");
		return ERROR_OUT_OF_MEMORY;
	}
	int res = read_png(fp, png);
	if (res != 0)
	{
		return res;
	}
	FILE *w = fopen(argv[2], "wb");
	res = write_pnm(w, png);
	free(png);
	return res;
}
