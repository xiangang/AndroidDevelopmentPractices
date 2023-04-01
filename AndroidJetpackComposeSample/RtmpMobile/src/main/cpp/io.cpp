//
// Created by xiangang on 2022/3/27.
//
#include <stdio.h>
#include <string.h>
#include "io.h"

FILE *fp_send;
int read_buffer(unsigned char *buf, int buf_size){

    if (!feof(fp_send))
    {
        int true_size = fread(buf, 1, buf_size, fp_send);
        printf("!feof read_buffer %d\n", true_size);
        return true_size;
    }
    else
    {
        return -1;
    }

}