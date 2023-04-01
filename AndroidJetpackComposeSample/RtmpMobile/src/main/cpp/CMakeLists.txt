# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.18.1)

# Declares and names the project.

project("rtmp_mobile")

# 添加位于librtmp目录下的 CMakeLists.txt 文件
add_subdirectory(librtmp)

# Specifies a path to native header files.
#指定librtmp头文件查找路径
include_directories(librtmp)
include_directories(include)

# 查找当前目录下的所有源文件，并保存到 DIR_LIB_SRCS 变量
aux_source_directory(. DIR_LIB_SRCS)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        rtmp_mobile

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${DIR_LIB_SRCS})

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

target_link_libraries( # Specifies the target library.
        rtmp_mobile

        #编译并关联rtmp库到rtmp_mobile库
        rtmp

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})