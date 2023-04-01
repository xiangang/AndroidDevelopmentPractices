//
// Created by xiangang on 2022/3/27.
//

#ifndef RTMPDUMPJNI_IO_H
#define RTMPDUMPJNI_IO_H
#include <stdio.h>
#include <unistd.h>
#endif //RTMPDUMPJNI_IO_H

//读文件的回调函数
//we use this callback function to read data from buffer
int read_buffer(unsigned char *buf, int buf_size);