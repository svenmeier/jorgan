#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include "exception.h"
#include "logging.h"
#include "emux.h"
#include "jorgan_creative_SoundFontManager.h"

typedef struct _Context {
	char* deviceName;
} Context;

static Context* createContext() {
	Context* context = (Context*) malloc(sizeof(Context));
	context->deviceName = NULL;
	return context;
}

static void destroyContext(Context* context) {
	free(context->deviceName);

	free(context);
}

static void initDeviceName(Context* context, JNIEnv* env, jstring jdeviceName) {
	const char* deviceName = (char*) (*env)->GetStringUTFChars(env, jdeviceName, NULL);

	char* leftBrace = strchr(deviceName, '[');
	if (leftBrace == NULL) {
		leftBrace = (char*)deviceName;
	} else {
		leftBrace++;
	}
	char* rightBrace = strchr(deviceName, ']');
	if (rightBrace == NULL) {
		rightBrace = (char*)deviceName + strlen(deviceName);
	}
	context->deviceName = (char*)calloc(strlen(deviceName) + 1, sizeof(char));
	strncpy(context->deviceName, leftBrace, rightBrace - leftBrace);

	(*env)->ReleaseStringUTFChars(env, jdeviceName, deviceName);
}

JNIEXPORT
jobject JNICALL Java_jorgan_creative_SoundFontManager_init(JNIEnv* env, jclass jclass, jstring jdeviceName) {
	Context* context = createContext();

	initDeviceName(context, env, jdeviceName);

	if (open_emux(context->deviceName) != 0) {
		destroyContext(context);
		jorgan_throw(env, IO_EXCEPTION, "no Creative device '%s'", context->deviceName);
		return NULL;
	}
	close_emux();

	return (*env)->NewDirectByteBuffer(env, (void*) context, sizeof(Context));
}

JNIEXPORT
void JNICALL Java_jorgan_creative_SoundFontManager_destroy(JNIEnv* env, jclass jclass, jobject jcontext) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	destroyContext(context);
}

JNIEXPORT void JNICALL Java_jorgan_creative_SoundFontManager_clear(JNIEnv* env, jclass jclass, jobject jcontext, jint jbank) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	if (open_emux(context->deviceName) != 0) {
		jorgan_throw(env, ERROR, "no Creative device");
		return;
	}

	emux_remove_samples(jbank);

	close_emux();
}

JNIEXPORT jboolean JNICALL Java_jorgan_creative_SoundFontManager_isLoaded(JNIEnv *env, jclass jclass, jobject jcontext, jint jbank) {
	// not implemented
	return JNI_FALSE;
}

JNIEXPORT void JNICALL Java_jorgan_creative_SoundFontManager_load(JNIEnv *env, jclass jclass, jobject jcontext, jint jbank, jstring jfileName) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	if (open_emux(context->deviceName) != 0) {
		jorgan_throw(env, ERROR, "no Creative device");
		return;
	}

	char* fileName = (char*) (*env)->GetStringUTFChars(env, jfileName, NULL);
	emux_load_samples(jbank, fileName);
	(*env)->ReleaseStringUTFChars(env, jfileName, fileName);

	close_emux();
}

JNIEXPORT jstring JNICALL Java_jorgan_creative_SoundFontManager_getDescriptor(JNIEnv *env, jclass jclass, jobject jcontext, jint jbank) {
	jorgan_throw(env, ILLEGAL_ARGUMENT_EXCEPTION, "not implemented");
	return NULL;
}
	
JNIEXPORT jstring JNICALL Java_jorgan_creative_SoundFontManager_getPresetDescriptor(JNIEnv *env, jclass jclass, jobject jcontext, jint jbank, jint jpreset) {
	jorgan_throw(env, ILLEGAL_ARGUMENT_EXCEPTION, "not implemented");
	return NULL;
}
