#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <alsa/asoundlib.h>
#include <awebank.h>
#include <sfopts.h>
#include "jorgan.creative.SoundFontManager.h"

  /**
   * Class:     jorgan_creative_SoundFontManager
   * Method:    getNumDevices
   * Signature: ()I
   */
  JNIEXPORT jint JNICALL Java_jorgan_creative_SoundFontManager_getNumDevices (JNIEnv *env, jobject obj) {
	
    return 0;
  }

  /**
   * Class:     jorgan_creative_SoundFontManager
   * Method:    getDeviceName
   * Signature: (I)Ljava/lang/String;
   */
  JNIEXPORT jstring JNICALL Java_jorgan_creative_SoundFontManager_getDeviceName(JNIEnv *env, jobject obj, jint device) {

    return 0;
  }
	
  /**
   * Class:     jorgan_creative_SoundFontManager
   * Method:    isBankUsed
   * Signature: (II)Z
   */
  JNIEXPORT jboolean JNICALL Java_jorgan_creative_SoundFontManager_isBankUsed(JNIEnv *env, jobject obj, jint device, jint bank) {

    return JNI_FALSE;
  }

  /**
   * Class:     jorgan_creative_SoundFontManager
   * Method:    getBankDescriptor
   * Signature: (II)Ljava/lang/String;
   */
  JNIEXPORT jstring JNICALL Java_jorgan_creative_SoundFontManager_getBankDescriptor(JNIEnv *env, jobject obj, jint device, jint bank) {
	
    return 0;
  }
	
  /**
   * Class:     jorgan_creative_SoundFontManager
   * Method:    loadBank
   * Signature: (IILjava/lang/String;)V
   */
  JNIEXPORT void JNICALL Java_jorgan_creative_SoundFontManager_loadBank(JNIEnv *env, jobject obj, jint device, jint bank, jstring jfile) {

  }

  /**
   * Class:     jorgan_creative_SoundFontManager
   * Method:    clearBank
   * Signature: (II)V
   */
  JNIEXPORT void JNICALL Java_jorgan_creative_SoundFontManager_clearBank(JNIEnv *env, jobject obj, jint device, jint bank) {
	
  }

  /**
   * Class:     jorgan_creative_SoundFontManager
   * Method:    getPresetDescriptor
   * Signature: (III)Ljava/lang/String;
   */
  JNIEXPORT jstring JNICALL Java_jorgan_creative_SoundFontManager_getPresetDescriptor(JNIEnv *env, jobject obj, jint device, jint bank, jint program) {

    return 0;
  }
