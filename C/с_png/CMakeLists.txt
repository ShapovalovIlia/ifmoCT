cmake_minimum_required(VERSION 3.24)
project(itmo_c_png_ShapovalovIlia_main C)

set(CMAKE_C_STANDARD 11)

include_directories(.)

add_executable(itmo_c_png_ShapovalovIlia_main
        main.c
        return_codes.h main_ref.c test.c main_pasha.c)
