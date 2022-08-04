#include <jni.h>
#include <string>
#include <android/log.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_nxg_asantest_ASanTestUtil_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT void Java_com_nxg_asantest_ASanTestUtil_testUseAfterFree(JNIEnv *env, jclass cls) {
    __android_log_print(ANDROID_LOG_DEBUG, "ASan_Crash", "enter fun %s ...", __FUNCTION__ );
    int *array = new int[100];
    delete [] array;
    int i = array[0];  // BOOM
    __android_log_print(ANDROID_LOG_DEBUG, "ASan_Crash", "level fun %s ...", __FUNCTION__ );
}

extern "C" JNIEXPORT void Java_com_nxg_asantest_ASanTestUtil_testHeapBufferOverflow(JNIEnv *env, jclass cls) {
    __android_log_print(ANDROID_LOG_DEBUG, "ASan_Crash", "enter fun %s ...", __FUNCTION__ );
    int *array = new int[100];
    array[101] = 0;  // BOOM
    delete [] array;
    __android_log_print(ANDROID_LOG_DEBUG, "ASan_Crash", "level fun %s ...", __FUNCTION__ );
}

extern "C" JNIEXPORT void Java_com_nxg_asantest_ASanTestUtil_testStackBufferOverflow(JNIEnv *env, jclass cls) {
    __android_log_print(ANDROID_LOG_DEBUG, "ASan_Crash", "enter fun %s ...", __FUNCTION__ );
    int stack_array[100];
    stack_array[100] = 0;
//    return stack_array[argc + 100];  // BOOM
    __android_log_print(ANDROID_LOG_DEBUG, "ASan_Crash", "level fun %s ...", __FUNCTION__ );
}

int global_array[100] = {-1};
extern "C" JNIEXPORT void Java_com_nxg_asantest_ASanTestUtil_testGlobalBufferOverflow(JNIEnv *env, jclass cls) {
    __android_log_print(ANDROID_LOG_DEBUG, "ASan_Crash", "enter fun %s ...", __FUNCTION__ );
    global_array[101] = 0;
    __android_log_print(ANDROID_LOG_DEBUG, "ASan_Crash", "level fun %s ...", __FUNCTION__ );
}

int *ptr;
__attribute__((noinline))
void FunctionThatEscapesLocalObject() {
    int local[100];
    ptr = &local[0];
}
extern "C" JNIEXPORT void Java_com_nxg_asantest_ASanTestUtil_testUseAfterReturn(JNIEnv *env, jclass cls) {
    __android_log_print(ANDROID_LOG_DEBUG, "ASan_Crash", "enter fun %s ...", __FUNCTION__ );
    FunctionThatEscapesLocalObject();
    ptr[0] = 1;
    __android_log_print(ANDROID_LOG_DEBUG, "ASan_Crash", "level fun %s ...", __FUNCTION__ );
}


extern "C" JNIEXPORT void Java_com_nxg_asantest_ASanTestUtil_testUseAfterScope(JNIEnv *env, jclass cls) {
    __android_log_print(ANDROID_LOG_DEBUG, "ASan_Crash", "enter fun %s ...", __FUNCTION__ );
    int *p;
    {
        int x = 0;
        p = &x;
    }
    *p = 5;
    __android_log_print(ANDROID_LOG_DEBUG, "ASan_Crash", "level fun %s ...", __FUNCTION__ );
}

extern "C" JNIEXPORT void Java_com_nxg_asantest_ASanTestUtil_testRepeatFree(JNIEnv *env, jclass cls) {
    __android_log_print(ANDROID_LOG_DEBUG, "ASan_Crash", "enter fun %s ...", __FUNCTION__ );
    int *p = new int[3];
    delete []p;
    delete []p;
    delete []p;
    delete []p;
    __android_log_print(ANDROID_LOG_DEBUG, "ASan_Crash", "level fun %s ...", __FUNCTION__ );
}

extern "C" JNIEXPORT void Java_com_nxg_asantest_ASanTestUtil_testMemoryLeak(JNIEnv *env, jclass cls) {
    __android_log_print(ANDROID_LOG_DEBUG, "ASan_Crash", "enter fun %s ...", __FUNCTION__ );
    void *p;
    p = new int[7];
    p = 0; // The memory is leaked here
    __android_log_print(ANDROID_LOG_DEBUG, "ASan_Crash", "level fun %s ...", __FUNCTION__ );
}
