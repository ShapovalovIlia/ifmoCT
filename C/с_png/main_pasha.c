#include "return_codes.h"
#define ZLIB
#include "zlib.h"

#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
typedef unsigned char byte;
typedef uint32_t uint;

typedef struct
{
	uint length;
	byte name[4];
	byte *data;
	uint crc;
} Chunk;

typedef struct
{
	uint length;
	byte name[4];
	uint width;
	uint height;
	byte data[5];
	uint crc;
} Chunk_IHDR;

typedef struct
{
	byte signature[8];
	Chunk_IHDR ihdr;
	Chunk plte;
	Chunk *chunks;
	uint num_chunks;
	byte *data_buffer;
	uint size;

} PNG;

typedef struct
{
	FILE *input_file;
	PNG png;
	FILE *output_file;
	byte *buffer;

} Context;

char *error_message_table[] = {
	[SUCCESS] = "",
	[ERROR_CANNOT_OPEN_FILE] = "File can't be opened",
	[ERROR_OUT_OF_MEMORY] = "Not enough memory, memory allocation failed",
	[ERROR_DATA_INVALID] = "The data is invalid",
	[ERROR_PARAMETER_INVALID] = "The parameter or number of parameters (argv) is incorrect",
	[ERROR_UNSUPPORTED] = "Unsupported functionality",
	[ERROR_UNKNOWN] = "Some unknown error",
};

int release_context(Context *context, int error_code)
{
	if (context->input_file)
		fclose(context->input_file);

	if (context->png.plte.data)
	{
		free(context->png.plte.data);
	}
	if (context->png.chunks)
	{
		for (size_t i = 0; i < context->png.num_chunks; ++i)
		{
			free(context->png.chunks[i].data);
		}
		free(context->png.chunks);
	}
	if (context->output_file)
		fclose(context->output_file);
	if (context->png.data_buffer)
		free(context->png.data_buffer);
	if (context->buffer)
		free(context->buffer);
	if (error_code)
		fprintf(stderr, "%s\n", error_message_table[error_code]);
	return error_code;
}

int read_data(Context *context)
{
	PNG *png = &context->png;
	png->data_buffer = malloc(sizeof(byte) * png->size);
	if (!png->data_buffer)
		return release_context(context, ERROR_OUT_OF_MEMORY);
	uint offset = 0;
	for (size_t i = 0; i < png->num_chunks; ++i)
	{
		if (!memcpy(png->data_buffer + offset, png->chunks[i].data, png->chunks[i].length))
			return release_context(context, ERROR_OUT_OF_MEMORY);
		offset += png->chunks[i].length;
	}
	return SUCCESS;
}

uint reverser(uint x)
{
	return ((x >> 24) & 0x000000FFul) | ((x >> 8) & 0x0000FF00ul) | ((x << 8) & 0x00FF0000ul) | ((x << 24) & 0xFF000000ul);
}

int read_chunk(Chunk *chunk, Context *context)
{
	FILE *f = context->input_file;
	fread(&chunk->length, sizeof(uint), 1, f);
	chunk->length = reverser(chunk->length);
	fread(chunk->name, sizeof(byte), 4, f);
	chunk->data = malloc(sizeof(byte) * chunk->length);
	if (!chunk->data)
	{
		return release_context(context, ERROR_OUT_OF_MEMORY);
	}
	fread(chunk->data, sizeof(byte), chunk->length, f);
	fread(&chunk->crc, sizeof(uint), 1, f);
	chunk->crc = reverser(chunk->crc);
	return SUCCESS;
}

int read_IHDR(Context *context)
{
	Chunk_IHDR *chunk = &context->png.ihdr;
	FILE *f = context->input_file;
	fread(&chunk->length, sizeof(uint), 1, f);
	chunk->length = reverser(chunk->length);
	if (chunk->length != 13)
	{
		fprintf(stderr, "Wrong length of IHDR chunk\n");
		return release_context(context, ERROR_DATA_INVALID);
	}
	fread(chunk->name, sizeof(byte), 4, f);
	if (memcmp(chunk->name, "IHDR", 4) != 0)
	{
		fprintf(stderr, "Not IHDR chunk in the beginning of file\n");
		return release_context(context, ERROR_DATA_INVALID);
	}
	fread(&chunk->width, sizeof(uint), 1, f);
	chunk->width = reverser(chunk->width);

	fread(&chunk->height, sizeof(uint), 1, f);
	chunk->height = reverser(chunk->height);

	fread(chunk->data, sizeof(byte), 5, f);

	if (chunk->data[0] != 8)
	{
		fprintf(stderr, "Sorry, we don't support not 8 bits per channel\n");
		return release_context(context, ERROR_UNSUPPORTED);
	}

	if (chunk->data[1] != 0 && chunk->data[1] != 2 && chunk->data[1] != 3)
	{
		fprintf(stderr, "Invalid color type\n");
		return release_context(context, ERROR_DATA_INVALID);
	}

	fread(&chunk->crc, sizeof(uint), 1, f);
	chunk->crc = reverser(chunk->crc);
	return SUCCESS;
}

int read_PLTE_chunk(Chunk chunk, Context *context)
{
	if (chunk.length % 3 != 0)
	{
		fprintf(stderr, "RGB but PLTE chunk length not divisible by 3\n");
		return release_context(context, ERROR_DATA_INVALID);
	}

	context->png.plte = chunk;
	return SUCCESS;
}

int read_chunks(Context *context)
{
	PNG *png = &context->png;

	png->num_chunks = 0;
	png->size = 0;
	Chunk *chunks = NULL;
	while (true)
	{
		Chunk chunk = { 0 };
		read_chunk(&chunk, context);

		if (memcmp(chunk.name, "PLTE", 4) == 0)
		{
			if (png->ihdr.data[1] == 0)
			{
				fprintf(stderr, "palette in grayscale picture\n");
				return release_context(context, ERROR_DATA_INVALID);
			}
			if (png->ihdr.data[1] == 2)
			{
				fprintf(stderr, "palette in true color picture\n");
				return release_context(context, ERROR_DATA_INVALID);
			}
			read_PLTE_chunk(chunk, context);
			continue;
		}
		if (memcmp(chunk.name, "IEND", 4) != 0 && memcmp(chunk.name, "IDAT", 4) != 0)
		{
			continue;
		}
		png->num_chunks++;
		png->size += chunk.length;
		chunks = realloc(chunks, png->num_chunks * sizeof(Chunk));
		if (!chunks)
		{
			return release_context(context, ERROR_OUT_OF_MEMORY);
		}
		chunks[png->num_chunks - 1] = chunk;
		if (memcmp(chunk.name, "IEND", 4) == 0)
		{
			if (chunk.length != 0)
			{
				fprintf(stderr, "IEND chunk length is not zero!\n");
				return release_context(context, ERROR_DATA_INVALID);
			}
			break;
		}
	}
	png->chunks = chunks;
	return SUCCESS;
}

int paeth_filter(int a, int b, int c)
{
	int p = a + b - c;
	int pa = abs(p - a);
	int pb = abs(p - b);
	int pc = abs(p - c);

	if (pa <= pb && pa <= pc)
	{
		return a;
	}
	else if (pb <= pc)
	{
		return b;
	}
	else
	{
		return c;
	}
}

int apply_filter(int bytes_per_pixel, Context *context)
{
	uint width = context->png.ihdr.width;
	uint height = context->png.ihdr.height;
	byte *res = context->buffer;
	uint row_length = bytes_per_pixel * width + 1;

	for (size_t row = 0; row < height; ++row)
	{
		byte filter_type = res[row * row_length];

		for (size_t col = 1; col < row_length; ++col)
		{
			byte pixel_value = res[row * row_length + col];

			switch (filter_type)
			{
			case 0:
				break;
			case 1:
				if (col > bytes_per_pixel)
				{
					pixel_value += res[row * row_length + col - bytes_per_pixel];
				}
				break;
			case 2:
				if (row > 0)
				{
					pixel_value += res[(row - 1) * row_length + col];
				}
				break;
			case 3:
				if (row > 0 && col > bytes_per_pixel)
				{
					pixel_value += (res[(row - 1) * row_length + col] + res[row * row_length + col - bytes_per_pixel]) / 2;
				}
				else if (row > 0 && col <= bytes_per_pixel)
				{
					pixel_value += res[(row - 1) * row_length + col] / 2;
				}
				break;
			case 4:
				if (row > 0 && col > bytes_per_pixel)
				{
					pixel_value += paeth_filter(
						res[row * row_length + col - bytes_per_pixel],
						res[(row - 1) * row_length + col],
						res[(row - 1) * row_length + col - bytes_per_pixel]);
				}
				else if (row > 0 && col <= bytes_per_pixel)
				{
					pixel_value += res[(row - 1) * row_length + col];
				}
				else if (col > bytes_per_pixel)
				{
					pixel_value += res[row * row_length + col - bytes_per_pixel];
				}
				break;
			default:
				fprintf(stderr, "Invalid filter type %d\n", filter_type);
				return release_context(context, ERROR_DATA_INVALID);
			}

			res[row * row_length + col] = pixel_value;
		}
	}

	if (context->png.ihdr.data[1] == 3)
	{
		for (size_t row = 1; row < height * row_length; row += row_length)
		{
			for (size_t col = 1; col < row_length; ++col)
			{
				fwrite(&context->png.plte.data[res[row + col] * 3], 1, 3, context->output_file);
			}
		}
	}
	else
	{
		for (size_t row = 0; row < height; ++row)
		{
			fwrite(res + (row * row_length) + 1, sizeof(byte), row_length - 1, context->output_file);
		}
	}
	return SUCCESS;
}

byte *uncompress_data(byte *out_data, size_t out_data_size, byte *in_data, size_t in_data_size)
{
	if (out_data == NULL || in_data == NULL)
	{
		return NULL;
	}

	int error = -1;
	size_t out_size = 0;

#if defined(ZLIB)

	error = uncompress(out_data, &out_data_size, in_data, in_data_size);
	if (error != Z_OK)
	{
		if (error == Z_MEM_ERROR)
		{
			return NULL;
		}
		return out_data;
	}
#elif defined(LIBDEFLATE)

	struct libdeflate_decompressor *decompressor = libdeflate_alloc_decompressor();
	if (decompressor == NULL)
	{
		return NULL;
	}
	error = libdeflate_zlib_decompress(decompressor, in_data, in_data_size, out_data, out_data_size, &out_size);
	libdeflate_free_decompressor(decompressor);

#elif defined(ISAL)

	struct inflate_state state;
	isal_inflate_init(&state);
	state.next_in = in_data;
	state.avail_in = in_data_size;
	state.next_out = out_data;
	state.avail_out = out_data_size;
	state.crc_flag = ISAL_ZLIB;
	error = isal_inflate(&state);
	if (error != ISAL_DECOMP_OK)
	{
		return NULL;
	}
#endif

	return out_data;
}



int main(int argc, char **argv)
{
	Context context[1] = { NULL };
	if (argc != 3)
	{
		return release_context(context, ERROR_PARAMETER_INVALID);
	}
	context->input_file = fopen(argv[1], "rb");

	if (!context->input_file)
		return release_context(context, ERROR_CANNOT_OPEN_FILE);

	byte correct_png_signature[8] = { 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };
	fread(context->png.signature, sizeof(byte), 8, context->input_file);

	if (memcmp(context->png.signature, correct_png_signature, 8) != 0)
	{
		fprintf(stderr, "Incorrect png signature\n");
		return release_context(context, ERROR_DATA_INVALID);
	}
	read_IHDR(context);
	read_chunks(context);
	read_data(context);

	int pixel_size = context->png.ihdr.data[1] == 2 ? 3 : 1;
	int c = context->png.ihdr.data[1] == 0 ? 5 : 6;
	size_t sz = context->png.ihdr.width * context->png.ihdr.height * pixel_size;
	context->buffer = malloc(sz);

	if (!context->buffer)
	{
		return release_context(context, ERROR_OUT_OF_MEMORY);
	}

	if (!uncompress_data(context->buffer, sz, context->png.data_buffer, context->png.size))
	{
		fprintf(stderr, "Uncompress error\n");
		return release_context(context, ERROR_UNKNOWN);
	}

	context->output_file = fopen(argv[2], "wb");

	if (!context->output_file)
	{
		fprintf(stderr, "File opening fail\n");
		return release_context(context, ERROR_CANNOT_OPEN_FILE);
	}

	fprintf(context->output_file, "P%d\n%u %u\n255\n", c, context->png.ihdr.width, context->png.ihdr.height);

	apply_filter(pixel_size, context);

	return release_context(context, SUCCESS);
}