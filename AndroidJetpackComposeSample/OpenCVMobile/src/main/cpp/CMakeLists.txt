# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.18.1)

# Declares and names the project.

project("opencv_mobile")

include_directories(include)

# 设置Debug编译模式，这样才能生成符号表，方便使用addr2line来定位内存地址对应代码行
set(CMAKE_BUILD_TYPE DEBUG)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -frtti -fexceptions -fopenmp -static-openmp")

set(OpenCV_DIR ${CMAKE_SOURCE_DIR}/opencv/sdk/native/jni)
find_package(OpenCV 4.5.4 REQUIRED core features2d highgui imgproc photo video)
message("OpenCV_FOUND is ${OpenCV_FOUND}")
message("OpenCV_LIBS is ${OpenCV_LIBS}")
# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
add_library( # Sets the name of the library.
        opencv_mobile

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        opencv_mobile.cpp)

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
        opencv_mobile
        ${OpenCV_LIBS}
        # Links the target library to the log library
        # included in the NDK.
        android
        ${log-lib})