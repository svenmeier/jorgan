#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

void jorgan_warn(JNIEnv* env, char* pattern, ...) {

	static jclass jclass;
	if (jclass == NULL) {
		jclass = (*env)->FindClass(env, "jorgan/jni/Logging");
		if (jclass == NULL) {
			return;
		}
	}
	jmethodID mid = (*env)->GetStaticMethodID(env, jclass, "warn", "(Ljava/lang/String;)V");

	char message[256];

	va_list args;
	va_start(args, pattern);
	vsnprintf(message, 256, pattern, args);
	va_end(args);

	jstring jmessage = (*env)->NewStringUTF(env, message);

	(*env)->CallStaticVoidMethod(env, jclass, mid, jmessage);
}

void jorgan_info(JNIEnv* env, char* pattern, ...) {

	static jclass jclass;
	if (jclass == NULL) {
		jclass = (*env)->FindClass(env, "jorgan/jni/Logging");
		if (jclass == NULL) {
			return;
		}
	}
	jmethodID mid = (*env)->GetStaticMethodID(env, jclass, "info", "(Ljava/lang/String;)V");

	char message[256];

	va_list args;
	va_start(args, pattern);
	vsnprintf(message, 256, pattern, args);
	va_end(args);

	jstring jmessage = (*env)->NewStringUTF(env, message);

	(*env)->CallStaticVoidMethod(env, jclass, mid, jmessage);
}
