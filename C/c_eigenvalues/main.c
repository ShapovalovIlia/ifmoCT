#include "return_codes.h"

#include <math.h>
#include <stdio.h>
#include <stdlib.h>

void arr2d_free(float **arr, size_t size)
{
	for (size_t k = 0; k < size; ++k)
	{
		free(arr[k]);
	}
	free(arr);
}

float vector_scalar_mul(const float *vector_a, const float *vector_b, const size_t size)
{
	float ans = 0;
	for (size_t i = 0; i < size; ++i)
	{
		ans += vector_a[i] * vector_b[i];
	}
	return ans;
}

float *vector_subtract(const float *vector_a, const float *vector_b, const size_t size)
{
	float *vector_ans = malloc(sizeof(float) * size);
	for (size_t i = 0; i < size; ++i)
	{
		vector_ans[i] = vector_a[i] - vector_b[i];
	}
	return vector_ans;
}

float *vector_num_mul(const float num, const float *vector_orig, const size_t size)
{
	float *vector_ans = malloc(sizeof(float) * size);
	for (size_t i = 0; i < size; ++i)
	{
		vector_ans[i] = num * vector_orig[i];
	}
	return vector_ans;
}

float *vector_divide(const float num, const float *vector_orig, const size_t size)
{
	return vector_num_mul(1 / num, vector_orig, size);
}

float *find_projection(const float *vector_a, const float *vector_b, const size_t size)
{
	return vector_num_mul(vector_scalar_mul(vector_a, vector_b, size) / vector_scalar_mul(vector_b, vector_b, size), vector_b, size);
}

float vector_length(const float *vector, const size_t size)
{
	float ans = 0;
	for (size_t i = 0; i < size; ++i)
	{
		ans += powf(vector[i], 2);
	}
	return sqrtf(ans);
}

float **gram_schmidt_process(float **vectors_a, const size_t size)
{
	float **vectors_b = malloc(sizeof(float *) * size);
	float **vectors_projection = malloc(sizeof(float *) * size);
	for (size_t i = 0; i < size; ++i)
	{
		vectors_b[i] = malloc(sizeof(float) * size);
		vectors_projection[i] = malloc(sizeof(float) * size);
		vectors_b[i] = vectors_a[i];
		for (size_t j = 0; j < i; ++j)
		{
			vectors_b[i] = vector_subtract(vectors_b[i], find_projection(vectors_a[i], vectors_b[j], size), size);
		}
		vectors_projection[i] = vector_divide(vector_length(vectors_b[i], size), vectors_b[i], size);
	}
	return vectors_projection;
}

float **transpose(float **matrix, size_t size)
{
	float **matrix_transpose = malloc(sizeof(float *) * size);
	for (size_t i = 0; i < size; ++i)
	{
		matrix_transpose[i] = malloc(sizeof(float) * size);
	}
	for (size_t i = 0; i < size; ++i)
	{
		for (size_t j = 0; j < size; ++j)
		{
			matrix_transpose[j][i] = matrix[i][j];
		}
	}

	return matrix_transpose;
}

float **matrix_mul(float **matrix_a, float **matrix_b, size_t size)
{
	float **matrix_ans = malloc(sizeof(float *) * size);
	for (size_t i = 0; i < size; ++i)
	{
		matrix_ans[i] = malloc(sizeof(float) * size);
		for (size_t j = 0; j < size; ++j)
		{
			for (size_t k = 0; k < size; ++k)
			{
				matrix_ans[i][j] += matrix_a[i][k] * matrix_b[k][j];
			}
		}
	}
	return matrix_ans;
}

float **QR_decomposition(float **matrix, size_t size)
{
	float **Q_t = gram_schmidt_process(matrix, size);

	float **Q = transpose(Q_t, size);

	float **R = matrix_mul(Q_t, matrix, size);

	float **QR = matrix_mul(R, Q, size);

	arr2d_free(Q, size);
	arr2d_free(R, size);

	return QR;
}

int main(int argc, char **argv)
{
	FILE *f_read;
	size_t size;

	if (argc != 3)
	{	 // check the number of arguments
		return ERROR_PARAMETER_INVALID;
	}

	f_read = fopen(argv[1], "r");	 // open the file for reading
	if (f_read == NULL)
	{	 // check that the file was opened successfully
		return ERROR_CANNOT_OPEN_FILE;
	}

	if (fscanf(f_read, "%zd", &size) != 1)
	{
		return ERROR_DATA_INVALID;
	}

	float **arr = malloc(sizeof(float *) * size);	 // allocate memory for the pointer array and make a pointer to it

	if (arr == NULL)
	{
		return ERROR_OUT_OF_MEMORY;
	}

	for (size_t i = 0; i < size; ++i)
	{	 // read the file and fill the array
		arr[i] = malloc(sizeof(float) * size);
		if (arr[i] == NULL)
		{
			return ERROR_OUT_OF_MEMORY;
		}
		for (size_t j = 0; j < size; ++j)
		{
			if (fscanf(f_read, "%f", &arr[i][j]) != 1)
			{
				perror("Invalid number input");
				return ERROR_DATA_INVALID;
			}
		}
	}
	fclose(f_read);	   // закрываем файл

	for (size_t i = 0; i < 1500; ++i)
	{
		arr = QR_decomposition(arr, size);
	}

	FILE *f_write = fopen(argv[2], "wt");
	if (f_write == NULL)
	{
		return ERROR_CANNOT_OPEN_FILE;
	}

	for (size_t i = 0; i < size; ++i)
	{
		fprintf(f_write, "%g\n", arr[i][i]);
	}
	free(arr);
	fclose(f_write);
	return SUCCESS;
}
