#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <alsa/asoundlib.h>
#include "awebank.h"
#include "sfopts.h"
#include "seq.h"
#include "jorgan.creative.SoundFontManager.h"

static jfieldID fieldID;

static AWEOps load_ops = {
	seq_load_patch,
	seq_mem_avail,
	seq_reset_samples,
	seq_remove_samples,
	seq_zero_atten
};

extern sf_options awe_option;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
  
  JNIEnv *env;
  
  if ((*vm)->GetEnv(vm, (void **)&env, JNI_VERSION_1_2)) {
    return JNI_ERR;
  }

  jclass clazz = (*env)->FindClass(env, "jorgan/creative/SoundFontManager");

  fieldID = (*env)->GetFieldID(env, clazz, "deviceName", "Ljava/lang/String;");
  if (fieldID == NULL) {
    return JNI_ERR;
  }

  return JNI_VERSION_1_2;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
}

/**
 * Helper for throwing an Exception
 */
void throwException(JNIEnv* env, char* exception, char* pattern, ...) {

  // Assemble message from pattern and variable number of parameters
  va_list args;
  va_start(args, pattern);
  char msg[256];
  vsnprintf(msg, 256, pattern, args);
  va_end(args);

  // create the IOException  
  jclass ioex = (*env)->FindClass(env, exception);
  (*env)->ThrowNew(env, ioex, msg);
}

jstring getDeviceName(JNIEnv *env, jobject obj) {
  // TODO strip anything outside []
  return (*env)->GetObjectField(env, obj, fieldID);
}

JNIEXPORT void JNICALL Java_jorgan_creative_SoundFontManager_init(JNIEnv *env, jobject obj) {
  // TODO
}

JNIEXPORT void JNICALL Java_jorgan_creative_SoundFontManager_clear(JNIEnv *env, jobject obj, jint bank) {
  jstring deviceName = getDeviceName(env, obj);
  char* cDeviceName = (*env)->GetStringUTFChars(env, deviceName, NULL);
  seq_alsa_init(cDeviceName);

  //awe_option.default_bank = bank;
  seq_remove_samples();

  seq_alsa_end();
  (*env)->ReleaseStringUTFChars(env, deviceName, cDeviceName);
}

JNIEXPORT jboolean JNICALL Java_jorgan_creative_SoundFontManager_isLoaded(JNIEnv *env, jobject obj, jint bank) {
  // TODO
  return JNI_FALSE;
}

JNIEXPORT void JNICALL Java_jorgan_creative_SoundFontManager_load(JNIEnv *env, jobject obj, jint bank, jstring fileName) {
  jstring deviceName = getDeviceName(env, obj);
  //char* cDeviceName = (*env)->GetStringUTFChars(env, deviceName, NULL);
  char* cDeviceName = "hw:0,2";
  seq_alsa_init(cDeviceName);

  char* cfileName = (*env)->GetStringUTFChars(env, fileName, NULL);
  //awe_option.default_bank = bank;
  awe_load_bank(&load_ops, cfileName, NULL, 0);
  (*env)->ReleaseStringUTFChars(env, fileName, cfileName);

  seq_alsa_end();
  //(*env)->ReleaseStringUTFChars(env, deviceName, cDeviceName);
}

JNIEXPORT jstring JNICALL Java_jorgan_creative_SoundFontManager_getDescriptor(JNIEnv *env, jobject obj, jint bank) {
  // TODO
  return NULL;
}
	
JNIEXPORT jstring JNICALL Java_jorgan_creative_SoundFontManager_getPresetDescriptor(JNIEnv *env, jobject obj, jint bank, jint preset) {
  // TODO
  return NULL;
}
