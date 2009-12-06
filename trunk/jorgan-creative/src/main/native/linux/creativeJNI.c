#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <alsa/asoundlib.h>
#include <awebank.h>
#include <sfopts.h>
#include <seq.h>
#include "exception.h"
#include "logging.h"
#include "jorgan.creative.SoundFontManager.h"

static AWEOps load_ops = {
	seq_load_patch,
	seq_mem_avail,
	seq_reset_samples,
	seq_remove_samples,
	seq_zero_atten
};

extern sf_options awe_option;

typedef struct _Context {
	char* deviceName;
} Context;

static Context* createContext() {
	return (Context*) malloc(sizeof(Context));
}

static void destroyContext(Context* context) {
	free(context->deviceName);

	free(context);
}

JNIEXPORT
jobject JNICALL Java_jorgan_creative_SoundFontManager_init(JNIEnv* env, jclass jclass, jstring jdeviceName) {
	Context* context = createContext();

	const char* deviceName = (char*) (*env)->GetStringUTFChars(env, jdeviceName, NULL);

	char* leftBrace = strchr(deviceName, '[');
	if (leftBrace == NULL) {
		leftBrace = (char*)deviceName;
	} else {
		leftBrace++;
	}
	char* rightBrace = strchr(deviceName, ']');
	if (rightBrace == NULL) {
		rightBrace = (char*)deviceName;
	}
	context->deviceName = (char*)calloc(strlen(deviceName) + 1, sizeof(char));
	strncpy(context->deviceName, leftBrace, rightBrace - leftBrace);

	jorgan_info(env, context->deviceName);

	(*env)->ReleaseStringUTFChars(env, jdeviceName, deviceName);

	return (*env)->NewDirectByteBuffer(env, (void*) context, sizeof(Context));
}

JNIEXPORT
void JNICALL Java_jorgan_creative_SoundFontManager_destroy(JNIEnv* env, jclass jclass, jobject jcontext) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	destroyContext(context);
}

JNIEXPORT void JNICALL Java_jorgan_creative_SoundFontManager_clear(JNIEnv* env, jclass jclass, jobject jcontext, jint jbank) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	seq_alsa_init(context->deviceName);

	//awe_option.default_bank = bank;

	seq_remove_samples();

	seq_alsa_end();
}

JNIEXPORT jboolean JNICALL Java_jorgan_creative_SoundFontManager_isLoaded(JNIEnv *env, jclass jclass, jobject jcontext, jint jbank) {
	// not implemented
	return JNI_FALSE;
}

JNIEXPORT void JNICALL Java_jorgan_creative_SoundFontManager_load(JNIEnv *env, jclass jclass, jobject jcontext, jint jbank, jstring jfileName) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	seq_alsa_init(context->deviceName);

	//awe_option.default_bank = bank;

	char* fileName = (char*) (*env)->GetStringUTFChars(env, jfileName, NULL);
	awe_load_bank(&load_ops, fileName, NULL, 0);
	(*env)->ReleaseStringUTFChars(env, jfileName, fileName);

	seq_alsa_end();
}

JNIEXPORT jstring JNICALL Java_jorgan_creative_SoundFontManager_getDescriptor(JNIEnv *env, jclass jclass, jobject jcontext, jint jbank) {
	jorgan_throwException(env, "java/lang/UnsupportedOperationException", "not implemented");
	return NULL;
}
	
JNIEXPORT jstring JNICALL Java_jorgan_creative_SoundFontManager_getPresetDescriptor(JNIEnv *env, jclass jclass, jobject jcontext, jint jbank, jint jpreset) {
	jorgan_throwException(env, "java/lang/UnsupportedOperationException", "not implemented");
	return NULL;
}
