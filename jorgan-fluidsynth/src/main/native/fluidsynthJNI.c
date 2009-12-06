#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include "exception.h"
#include "logging.h"
#include "fluidsynth.h"
#include "jorgan.fluidsynth.Fluidsynth.h"

typedef struct _Context {
	fluid_settings_t* settings;
	fluid_synth_t* synth;
	fluid_audio_driver_t* adriver;
} Context;

static Context* createContext() {
	return (Context*) malloc(sizeof(Context));
}

static void destroyContext(JNIEnv* env, Context* context) {
	if (context->adriver != NULL) {
		jorgan_info(env, "deleting audio driver");
		delete_fluid_audio_driver(context->adriver);
	}
	if (context->synth != NULL) {
		jorgan_info(env, "deleting synth");
		delete_fluid_synth(context->synth);
	}
	if (context->settings != NULL) {
		jorgan_info(env, "deleting settings");
		delete_fluid_settings(context->settings);
	}
	free(context);
}

JNIEXPORT
jobject JNICALL Java_jorgan_fluidsynth_Fluidsynth_init(JNIEnv* env, jclass jclass, jstring jname, jint jchannels, jint jpolyphony, jfloat jsampleRate, jstring jaudioDriver, jstring jaudioDevice, jint jbuffers, jint jbufferSize) {
	Context* context = createContext();

	context->settings = new_fluid_settings();

	fluid_settings_setint(context->settings, "synth.midi-channels", jchannels);
	fluid_settings_setint(context->settings, "synth.polyphony", jpolyphony);
	fluid_settings_setnum(context->settings, "synth.sample-rate", jsampleRate);
	
	if (jaudioDriver != NULL) {
		const char* audioDriver = (*env)->GetStringUTFChars(env, jaudioDriver, NULL);
		fluid_settings_setstr(context->settings, "audio.driver", (char*)audioDriver);
		(*env)->ReleaseStringUTFChars(env, jaudioDriver, audioDriver);
	}

	if (jaudioDevice != NULL) {
		const char* audioDevice = (*env)->GetStringUTFChars(env, jaudioDevice, NULL);
		fluid_settings_setstr(context->settings, "audio.alsa.device", (char*)audioDevice);
		fluid_settings_setstr(context->settings, "audio.oss.device", (char*)audioDevice);
		fluid_settings_setstr(context->settings, "audio.jack.device", (char*)audioDevice);
		fluid_settings_setstr(context->settings, "audio.dsound.device", (char*)audioDevice);
		fluid_settings_setstr(context->settings, "audio.sndman.device", (char*)audioDevice);
		fluid_settings_setstr(context->settings, "audio.coreaudio.device", (char*)audioDevice);
		fluid_settings_setstr(context->settings, "audio.portaudio.device", (char*)audioDevice);
		(*env)->ReleaseStringUTFChars(env, jaudioDevice, audioDevice);
	}

	fluid_settings_setint(context->settings, "audio.periods", jbuffers);
	fluid_settings_setint(context->settings, "audio.period-size", jbufferSize);

	// JACK specialities
	fluid_settings_setint(context->settings, "audio.jack.autoconnect", 1);
	const char* name = (*env)->GetStringUTFChars(env, jname, NULL);
	fluid_settings_setstr(context->settings, "audio.jack.id", (char*)name);
	(*env)->ReleaseStringUTFChars(env, jname, name);

	context->synth = new_fluid_synth(context->settings);
	if (context->synth == NULL) {
		jorgan_throwException(env, "java/lang/IllegalStateException", "Couldn't create synth");
		destroyContext(env, context);
		return NULL;
	}

	context->adriver = new_fluid_audio_driver(context->settings, context->synth);
	if (context->adriver == NULL) {
		jorgan_throwException(env, "java/io/IOException", "Couldn't create audio driver");
		destroyContext(env, context);
		return NULL;
	}

	return (*env)->NewDirectByteBuffer(env, (void*) context, sizeof(Context));
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_destroy(JNIEnv* env, jclass jclass, jobject jcontext) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	destroyContext(env, context);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_soundFontLoad(JNIEnv* env, jclass jclass, jobject jcontext, jstring jfilename) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	const char* filename = (*env)->GetStringUTFChars(env, jfilename, NULL);
	int soundfont = fluid_synth_sfload(context->synth , filename, 0);
	if (soundfont == -1) {
		jorgan_throwException(env, "java/io/IOException", "Couldn't load file %s, error %d", filename, soundfont);
	}
	(*env)->ReleaseStringUTFChars(env, jfilename, filename);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_setGain(JNIEnv* env, jclass jclass, jobject jcontext, jfloat jgain) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	fluid_synth_set_gain(context->synth, jgain);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_noteOn(JNIEnv* env, jclass jclass, jobject jcontext, jint jchannel, jint jpitch, jint jvelocity) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	fluid_synth_noteon(context->synth, jchannel, jpitch, jvelocity);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_noteOff(JNIEnv* env, jclass jclass, jobject jcontext, jint jchannel, jint jpitch) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	fluid_synth_noteoff(context->synth, jchannel, jpitch);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_controlChange(JNIEnv* env, jclass jclass, jobject jcontext, jint jchannel, jint jcontroller, jint jvalue) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	fluid_synth_cc(context->synth, jchannel, jcontroller, jvalue);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_pitchBend(JNIEnv* env, jclass jclass, jobject jcontext, jint jchannel, jint jvalue) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	fluid_synth_pitch_bend(context->synth, jchannel, jvalue); 
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_programChange(JNIEnv* env, jclass jclass, jobject jcontext, jint jchannel, jint jprogram) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	fluid_synth_program_change(context->synth, jchannel, jprogram); 
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_setReverbOn(JNIEnv* env, jclass jclass, jobject jcontext, jboolean jon) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	fluid_synth_set_reverb_on(context->synth, jon);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_setReverb(JNIEnv* env, jclass jclass, jobject jcontext, jdouble jroomsize, jdouble jdamping, jdouble jwidth, jdouble jlevel) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	fluid_synth_set_reverb(context->synth, jroomsize, jdamping, jwidth, jlevel);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_setChorusOn(JNIEnv* env, jclass jclass, jobject jcontext, jboolean jon) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	fluid_synth_set_chorus_on(context->synth, jon);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_setChorus(JNIEnv* env, jclass jclass, jobject jcontext, jint jnr, jdouble jlevel, jdouble jspeed, jdouble jdepth_ms, jint jtype) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	fluid_synth_set_chorus(context->synth, jnr, jlevel, jspeed, jdepth_ms, jtype);
}

typedef struct _ForEachData {
	JNIEnv *env;
	jobject jlist;
	jmethodID jadd;
} ForEachData;

static void initData(JNIEnv* env, ForEachData* data) {
	data->env = env;

	jclass class = (*env)->FindClass(env, "java/util/ArrayList");

	jmethodID constructor = (*env)->GetMethodID(env, class, "<init>", "()V");
	data->jlist = (*env)->NewObject(env, class, constructor);

	data->jadd = (*env)->GetMethodID(env, class, "add", "(Ljava/lang/Object;)Z");
}

static void onOption(void* vdata, char* name, char* value) {
	ForEachData* data = (ForEachData*)vdata;

	JNIEnv* env = data->env;

	jstring string = (*env)->NewStringUTF(env, value);

	(*env)->CallVoidMethod(env, data->jlist, data->jadd, string);
}

JNIEXPORT
jobject JNICALL Java_jorgan_fluidsynth_Fluidsynth_getAudioDrivers(JNIEnv* env, jclass jclass) {

	ForEachData data;
	initData(env, &data);

	fluid_settings_t* settings = new_fluid_settings();
	fluid_settings_foreach_option(settings, "audio.driver", &data, onOption);
	delete_fluid_settings(settings);

	return data.jlist;
}

JNIEXPORT
jobject JNICALL Java_jorgan_fluidsynth_Fluidsynth_getAudioDevices(JNIEnv* env, jclass jclass, jstring jaudioDriver) {

	ForEachData data;
	initData(env, &data);

	const char* prefix = "audio.";
	const char* audioDriver = (*env)->GetStringUTFChars(env, jaudioDriver, NULL);
	const char* suffix = ".device";

	char *key = (char*)calloc(strlen(prefix) + strlen(audioDriver) + strlen(suffix) + 1, sizeof(char));
	strcat(key, prefix);
	strcat(key, audioDriver);
	strcat(key, suffix);
 
	fluid_settings_t* settings = new_fluid_settings();
	fluid_settings_foreach_option(settings, key, &data, onOption);
	delete_fluid_settings(settings);

	free(key);

	(*env)->ReleaseStringUTFChars(env, jaudioDriver, audioDriver);

	return data.jlist;
}
