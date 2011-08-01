#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

void jorgan_throw(JNIEnv* env, char* exception, char* pattern, ...) {

	va_list args;
	va_start(args, pattern);
	char msg[256];
	vsnprintf(msg, 256, pattern, args);
	va_end(args);

	jclass jclass = (*env)->FindClass(env, exception);
	(*env)->ThrowNew(env, jclass, msg);
}
