# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.18.1)

# Declares and names the project.

project("acodecmobile")

include_directories(include)

# 设置Debug编译模式，这样才能生成符号表，方便使用addr2line来定位内存地址对应代码行
set(CMAKE_BUILD_TYPE DEBUG)

# 设置变量
# CMAKE_CXX_FLAGS 表示会将 C++ 的参数传给编译器
# CMAKE_C_FLAGS 表示会将 C 参数传给编译器
# 参数设置 : 传递 CMAKE_CXX_FLAGS C+= 参数给编译器时 , 在该参数后面指定库的路径
#   CMAKE_SOURCE_DIR 指的是当前的文件地址
#   -L 参数指定动态库的查找路径find_package
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -frtti -fexceptions -fopenmp -static-openmp")
# set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -frtti -fexceptions -L${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}")

set(OpenCV_DIR ${CMAKE_SOURCE_DIR}/opencv/sdk/native/jni)
find_package(OpenCV 4.5.4 REQUIRED core features2d highgui imgproc photo video)
message("OpenCV_FOUND is ${OpenCV_FOUND}")
message("OpenCV_LIBS is ${OpenCV_LIBS}")
# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

# 引入静态库
#       ① 参数 1 ( add ) : 设置引入的静态库名称
#       ② 参数 2 ( SHARED ) : 设置引入的函数库类型 : ① 静态库 STATIC ② 动态库 SHARED
#       ③ 参数 3 ( IMPORTED ) : 表示引入第三方静态库 , 导入静态库 , 相当于预编译静态库
add_library( # Sets the name of the library.
        acodecmobile

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        acodecmobile.cpp)

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

# 链接函数库
#       参数 1 : 本构建脚本要生成的动态库目 标
#       参数 2 ~ ... : 后面是之前预编译的动态库或静态库 , 或引入的动态库
target_link_libraries( # Specifies the target library.
        acodecmobile
        ${OpenCV_LIBS}
        # Links the target library to the log library
        # included in the NDK.
        android
        ${log-lib})