#include <jni.h>

#define ILLEGAL_ARGUMENT_EXCEPTION "java/lang/IllegalArgumentException"
#define IO_EXCEPTION               "java/io/IOException"
#define FILE_NOT_FOUND_EXCEPTION   "java/io/FileNotFoundException"
#define ERROR                      "java/lang/ERROR"

void jorgan_throw(JNIEnv* env, char* exception, char* pattern, ...);
