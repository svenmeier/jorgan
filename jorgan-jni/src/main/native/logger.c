#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

void jorgan_log(JNIEnv* env, char* pattern, ...) {

	char message[256];

	va_list args;
	va_start(args, pattern);
	vsnprintf(message, 256, pattern, args);
	va_end(args);

	puts((const char*)&message);
	puts("\n");
	fflush(stdout);
}
