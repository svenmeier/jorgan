#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <alsa/asoundlib.h>
#include <awebank.h>
#include <sfopts.h>
#include <awe_voice.h>
#include <sys/ioctl.h>
#include "exception.h"
#include "logging.h"
#include "jorgan.creative.SoundFontManager.h"

#define SNDRV_EMUX_HWDEP_NAME "Emux WaveTable"

struct sndrv_emux_misc_mode {
	int port;	/* -1 = all */
	int mode;
	int value;
	int value2;	/* reserved */
};

enum {
	SNDRV_EMUX_IOCTL_VERSION = _IOR('H', 0x80, unsigned int),
	SNDRV_EMUX_IOCTL_LOAD_PATCH = _IOWR('H', 0x81, awe_patch_info),
	SNDRV_EMUX_IOCTL_RESET_SAMPLES = _IO('H', 0x82),
	SNDRV_EMUX_IOCTL_REMOVE_LAST_SAMPLES = _IO('H', 0x83),
	SNDRV_EMUX_IOCTL_MEM_AVAIL = _IOW('H', 0x84, int),
	SNDRV_EMUX_IOCTL_MISC_MODE = _IOWR('H', 0x84, struct sndrv_emux_misc_mode),
};

static snd_hwdep_t* hwdep;

static void close_emux()
{
	if (hwdep) {
		snd_hwdep_close(hwdep);
		hwdep = NULL;
	}
}

static int open_emux(char *name)
{
	snd_hwdep_info_t* info;
	unsigned int version;

	if (snd_hwdep_open(&hwdep, name, 0) < 0) {
		return -1;
	}

	snd_hwdep_info_alloca(&info);
	if (snd_hwdep_info(hwdep, info) < 0) {
		close_emux();
		return -1;
	}
	if (strcmp(snd_hwdep_info_get_name(info), SNDRV_EMUX_HWDEP_NAME)) {
		close_emux();
		return -1;
	}
	if (snd_hwdep_ioctl(hwdep, SNDRV_EMUX_IOCTL_VERSION, &version) < 0) {
		close_emux();
		return -1;
	}
	if ((version >> 16) != 0x01) {
		/* version 1 compatible */
		close_emux();
		return -1;
	}

	return 0;
}

int seq_load_patch(void *patch, int len) {
 	awe_patch_info *p;
 	p = (awe_patch_info*)patch;
 	p->key = AWE_PATCH;
	p->device_no = 0;
 	p->sf_id = 0;
	return snd_hwdep_ioctl(hwdep, SNDRV_EMUX_IOCTL_LOAD_PATCH, p);
}

int seq_mem_avail(void)
{
	int mem_avail = 0;
	snd_hwdep_ioctl(hwdep, SNDRV_EMUX_IOCTL_MEM_AVAIL, &mem_avail);
	return mem_avail;
}

int seq_reset_samples(void)
{
	return snd_hwdep_ioctl(hwdep, SNDRV_EMUX_IOCTL_RESET_SAMPLES, NULL);
}

int seq_remove_samples(void)
{
	return snd_hwdep_ioctl(hwdep, SNDRV_EMUX_IOCTL_REMOVE_LAST_SAMPLES, NULL);
}

int seq_zero_atten(int atten)
{
	struct sndrv_emux_misc_mode mode;
	mode.port = -1;
	mode.mode = AWE_MD_ZERO_ATTEN;
	mode.value = atten;
	mode.value2 = 0;
	return snd_hwdep_ioctl(hwdep, SNDRV_EMUX_IOCTL_MISC_MODE, &mode);
}

static AWEOps load_ops = {
	seq_load_patch,
	seq_mem_avail,
	seq_reset_samples,
	seq_remove_samples,
	seq_zero_atten
};

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
		jorgan_throwException(env, "java/io/IOException", "no Creative device '%s'", context->deviceName);
		destroyContext(context);
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
		jorgan_throwException(env, "java/lang/IllegalStateException", "no Creative device");
		return;
	}

	awe_option.default_bank = jbank;

	seq_remove_samples();

	close_emux();
}

JNIEXPORT jboolean JNICALL Java_jorgan_creative_SoundFontManager_isLoaded(JNIEnv *env, jclass jclass, jobject jcontext, jint jbank) {
	// not implemented
	return JNI_FALSE;
}

JNIEXPORT void JNICALL Java_jorgan_creative_SoundFontManager_load(JNIEnv *env, jclass jclass, jobject jcontext, jint jbank, jstring jfileName) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	if (open_emux(context->deviceName) != 0) {
		jorgan_throwException(env, "java/lang/IllegalStateException", "no Creative device");
		return;
	}

	awe_option.default_bank = jbank;

	char* fileName = (char*) (*env)->GetStringUTFChars(env, jfileName, NULL);
	awe_load_bank(&load_ops, fileName, NULL, 0);
	(*env)->ReleaseStringUTFChars(env, jfileName, fileName);

	close_emux();
}

JNIEXPORT jstring JNICALL Java_jorgan_creative_SoundFontManager_getDescriptor(JNIEnv *env, jclass jclass, jobject jcontext, jint jbank) {
	jorgan_throwException(env, "java/lang/IllegalArgumentException", "not implemented");
	return NULL;
}
	
JNIEXPORT jstring JNICALL Java_jorgan_creative_SoundFontManager_getPresetDescriptor(JNIEnv *env, jclass jclass, jobject jcontext, jint jbank, jint jpreset) {
	jorgan_throwException(env, "java/lang/IllegalArgumentException", "not implemented");
	return NULL;
}
