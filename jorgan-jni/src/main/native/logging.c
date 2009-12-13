#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

static void jorgan_log(JNIEnv* env, char* pattern, char* level, va_list args) {

	jclass jclass = (*env)->FindClass(env, "jorgan/jni/Logging");
	if (jclass == NULL) {
		return;
	}
	jmethodID mid = (*env)->GetStaticMethodID(env, jclass, level, "(Ljava/lang/String;)V");

	char message[256];
	vsnprintf(message, 256, pattern, args);

	jstring jmessage = (*env)->NewStringUTF(env, message);

	(*env)->CallStaticVoidMethod(env, jclass, mid, jmessage);
}

void jorgan_warn(JNIEnv* env, char* pattern, ...) {

	va_list args;
	va_start(args, pattern);
	jorgan_log(env, pattern, "warn", args);
	va_end(args);
}

void jorgan_info(JNIEnv* env, char* pattern, ...) {

	va_list args;
	va_start(args, pattern);
	jorgan_log(env, pattern, "info", args);
	va_end(args);
}


