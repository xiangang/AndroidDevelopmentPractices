# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.18.1)

# Declares and names the project.

project("ffmpeg_mobile")

include_directories(include)

# 设置Debug编译模式，这样才能生成符号表，方便使用addr2line来定位内存地址对应代码行
set(CMAKE_BUILD_TYPE DEBUG)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI} ")

set(JNI_LIBS_DIR ${CMAKE_SOURCE_DIR}/libs)

# 查找当前目录下的所有源文件，并保存到 DIR_LIB_SRCS 变量
aux_source_directory(. DIR_LIB_SRCS)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        ffmpeg_mobile

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ffmpeg_mobile.cpp
        decode_av.cpp)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

# 引入已编译的库
add_library(avutil
        SHARED
        IMPORTED)
set_target_properties(avutil
        PROPERTIES IMPORTED_LOCATION
        ${JNI_LIBS_DIR}/${ANDROID_ABI}/libavutil.so)

add_library(swresample
        SHARED
        IMPORTED)
set_target_properties(swresample
        PROPERTIES IMPORTED_LOCATION
        ${JNI_LIBS_DIR}/${ANDROID_ABI}/libswresample.so)

add_library(swscale
        SHARED
        IMPORTED)
set_target_properties(swscale
        PROPERTIES IMPORTED_LOCATION
        ${JNI_LIBS_DIR}/${ANDROID_ABI}/libswscale.so)

add_library(avcodec
        SHARED
        IMPORTED)
set_target_properties(avcodec
        PROPERTIES IMPORTED_LOCATION
        ${JNI_LIBS_DIR}/${ANDROID_ABI}/libavcodec.so)

add_library(avformat
        SHARED
        IMPORTED)
set_target_properties(avformat
        PROPERTIES IMPORTED_LOCATION
        ${JNI_LIBS_DIR}/${ANDROID_ABI}/libavformat.so)

add_library(avfilter
        SHARED
        IMPORTED)
set_target_properties(avfilter
        PROPERTIES IMPORTED_LOCATION
        ${JNI_LIBS_DIR}/${ANDROID_ABI}/libavfilter.so)

add_library(avresample
        SHARED
        IMPORTED)
set_target_properties(avresample
        PROPERTIES IMPORTED_LOCATION
        ${JNI_LIBS_DIR}/${ANDROID_ABI}/libavresample.so)

target_link_libraries( # Specifies the target library.
        ffmpeg_mobile
        # 这里需要注意下,下面这些ffmpeg的so库编译是有先后顺序的
        # 下面的顺序是没问题的,如果想验证编译顺序,可以自己变换顺序试试.
        avcodec
        avfilter
        avformat
        avutil
        avresample
        swresample
        swscale
        # Links the target library to the log library
        # included in the NDK.
        android
        ${log-lib})