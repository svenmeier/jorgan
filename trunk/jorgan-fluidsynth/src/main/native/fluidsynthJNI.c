#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include "fluidsynth.h"
#include "jorgan.fluidsynth.Fluidsynth.h"

#define MAX_CONTEXTS 16

/**
 * Helper for throwing an Exception
 * @return FALSE
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

struct Context {
  jobject object;
  fluid_settings_t* settings;
  fluid_synth_t* synth;
  fluid_audio_driver_t* adriver;
};

struct Context contexts[MAX_CONTEXTS];

struct Context *createContext(JNIEnv *env) {
  int index;
  for (index = 0; index < MAX_CONTEXTS; index++) {
    if (contexts[index].object == NULL) {
      return &contexts[index];
    }
  }
  throwException(env, "java/lang/Error", "Contexts exceeded");
  return NULL;
}

struct Context *getContext(JNIEnv *env, jobject object) {
  int index;
  for (index = 0; index < MAX_CONTEXTS; index++) {
    if ((*env)->IsSameObject(env, object, contexts[index].object)) {
      return &contexts[index];
    }
  }
  throwException(env, "java/lang/Error", "No Context found");
  return NULL;
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_create(JNIEnv *env, jobject object, jstring name, jint channels, jfloat sampleRate, jstring audioDriver, jstring audioDevice, jint buffers, jint bufferSize) {
  struct Context *context = createContext(env);
  if (context == NULL) {
    return;
  }

  (*context).object = (*env)->NewGlobalRef(env, object);

  (*context).settings = new_fluid_settings();

  fluid_settings_setint((*context).settings, "synth.midi-channels", channels);
  int check = 0;
  check = fluid_settings_setnum((*context).settings, "synth.sample-rate", sampleRate);
  if (check == 0) {
    throwException(env, "java/lang/Error", "unable to set");
    return;
  };


  if (audioDriver != NULL) {
    const char* cAudioDriver = (*env)->GetStringUTFChars(env, audioDriver, NULL);
    fluid_settings_setstr((*context).settings, "audio.driver", (char*)cAudioDriver);
    (*env)->ReleaseStringUTFChars(env, audioDriver, cAudioDriver);
  }

  if (audioDevice != NULL) {
    const char* cAudioDevice = (*env)->GetStringUTFChars(env, audioDevice, NULL);
    fluid_settings_setstr((*context).settings, "audio.alsa.device", (char*)cAudioDevice);
    fluid_settings_setstr((*context).settings, "audio.oss.device", (char*)cAudioDevice);
    fluid_settings_setstr((*context).settings, "audio.jack.device", (char*)cAudioDevice);
    fluid_settings_setstr((*context).settings, "audio.dsound.device", (char*)cAudioDevice);
    fluid_settings_setstr((*context).settings, "audio.sndman.device", (char*)cAudioDevice);
    fluid_settings_setstr((*context).settings, "audio.coreaudio.device", (char*)cAudioDevice);
    fluid_settings_setstr((*context).settings, "audio.portaudio.device", (char*)cAudioDevice);
    (*env)->ReleaseStringUTFChars(env, audioDevice, cAudioDevice);
  }

  fluid_settings_setint((*context).settings, "audio.periods", buffers);
  fluid_settings_setint((*context).settings, "audio.period-size", bufferSize);

  // JACK specialities
  fluid_settings_setint((*context).settings, "audio.jack.autoconnect", 1);
  const char* cName = (*env)->GetStringUTFChars(env, name, NULL);
  fluid_settings_setstr((*context).settings, "audio.jack.id", (char*)cName);
  (*env)->ReleaseStringUTFChars(env, name, cName);

  (*context).synth = new_fluid_synth((*context).settings);
  if ((*context).synth == NULL) {
    throwException(env, "java/lang/IllegalStateException", "Couldn't create synth");
    Java_jorgan_fluidsynth_Fluidsynth_destroy(env, object);
    return;
  }

  (*context).adriver = new_fluid_audio_driver((*context).settings, (*context).synth);
  if ((*context).adriver == NULL) {
    throwException(env, "java/io/IOException", "Couldn't create audio driver");
    Java_jorgan_fluidsynth_Fluidsynth_destroy(env, object);
    return;
  }
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_destroy(JNIEnv *env, jobject object) {
  struct Context *context = getContext(env, object);
  if (context == NULL) {
    return;
  }

  if ((*context).adriver != NULL) {
    delete_fluid_audio_driver((*context).adriver);
  }
  if ((*context).synth != NULL) {
    delete_fluid_synth((*context).synth);
  }
  if ((*context).settings != NULL) {
    delete_fluid_settings((*context).settings);
  }

  (*env)->DeleteGlobalRef(env, (*context).object);
  (*context).object = NULL;
}

fluid_synth_t *getSynth(JNIEnv *env, jobject object) {
  struct Context *context = getContext(env, object);
  if (context == NULL) {
    return NULL;
  }

  return (*context).synth;
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_soundFontLoad(JNIEnv *env, jobject object, jstring filename) {
  fluid_synth_t* synth = getSynth(env, object);
  if (synth == NULL) {
    return;
  }

  const char* cfilename = (*env)->GetStringUTFChars(env, filename, NULL);
  int soundfont = fluid_synth_sfload(synth , cfilename, 0);
  if (soundfont == -1) {
    throwException(env, "java/io/IOException", "Couldn't load file %s, error %d", cfilename, soundfont);
  }
  (*env)->ReleaseStringUTFChars(env, filename, cfilename);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_setGain(JNIEnv *env, jobject object, jfloat gain) {
  fluid_synth_t* synth = getSynth(env, object);
  if (synth == NULL) {
    return;
  }

  fluid_synth_set_gain(synth, gain);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_noteOn(JNIEnv *env, jobject object, jint channel, jint pitch, jint velocity) {
  fluid_synth_t* synth = getSynth(env, object);
  if (synth == NULL) {
    return;
  }

  fluid_synth_noteon(synth, channel, pitch, velocity);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_noteOff(JNIEnv *env, jobject object, jint channel, jint pitch) {
  fluid_synth_t* synth = getSynth(env, object);
  if (synth == NULL) {
    return;
  }

  fluid_synth_noteoff(synth, channel, pitch);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_controlChange(JNIEnv *env, jobject object, jint channel, jint controller, jint value) {
  fluid_synth_t* synth = getSynth(env, object);
  if (synth == NULL) {
    return;
  }

  fluid_synth_cc(synth, channel, controller, value);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_pitchBend(JNIEnv *env, jobject object, jint channel, jint value) {
  fluid_synth_t* synth = getSynth(env, object);
  if (synth == NULL) {
    return;
  }

  fluid_synth_pitch_bend(synth, channel, value); 
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_programChange(JNIEnv *env, jobject object, jint channel, jint program) {
  fluid_synth_t* synth = getSynth(env, object);
  if (synth == NULL) {
    return;
  }

  fluid_synth_program_change(synth, channel, program); 
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_setReverbOn(JNIEnv *env, jobject object, jboolean on) {
  fluid_synth_t* synth = getSynth(env, object);
  if (synth == NULL) {
    return;
  }

  fluid_synth_set_reverb_on(synth, on);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_setReverb(JNIEnv *env, jobject object, jdouble roomsize, jdouble damping, jdouble width, jdouble level) {
  fluid_synth_t* synth = getSynth(env, object);
  if (synth == NULL) {
    return;
  }

  fluid_synth_set_reverb(synth, roomsize, damping, width, level);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_setChorusOn(JNIEnv *env, jobject object, jboolean on) {
  fluid_synth_t* synth = getSynth(env, object);
  if (synth == NULL) {
    return;
  }

  fluid_synth_set_chorus_on(synth, on);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_setChorus(JNIEnv *env, jobject object, jint nr, jdouble level, jdouble speed, jdouble depth_ms, jint type) {
  fluid_synth_t* synth = getSynth(env, object);
  if (synth == NULL) {
    return;
  }

  fluid_synth_set_chorus(synth, nr, level, speed, depth_ms, type);
}

struct forEachData {
  JNIEnv *env;
  jobject list;
  jmethodID add;
};

void initData(JNIEnv *env, struct forEachData *data) {
  data->env = env;

  jclass class = (*env)->FindClass(env, "java/util/ArrayList");

  jmethodID constructor = (*env)->GetMethodID(env, class, "<init>", "()V");
  data->list = (*env)->NewObject(env, class, constructor);

  data->add = (*env)->GetMethodID(env, class, "add", "(Ljava/lang/Object;)Z");
}

void onOption(void *vdata, char *name, char *value) {
  struct forEachData *data = (struct forEachData *)vdata;

  JNIEnv *env = data->env;

  jstring string = (*env)->NewStringUTF(env, value);

  (*env)->CallVoidMethod(env, data->list, data->add, string);
}

JNIEXPORT
jobject JNICALL Java_jorgan_fluidsynth_Fluidsynth_getAudioDrivers(JNIEnv *env, jclass class) {

  struct forEachData data;
  initData(env, &data);

  fluid_settings_t* settings = new_fluid_settings();
  fluid_settings_foreach_option(settings, "audio.driver", &data, onOption);
  delete_fluid_settings(settings);

  return data.list;
}

JNIEXPORT
jobject JNICALL Java_jorgan_fluidsynth_Fluidsynth_getAudioDevices(JNIEnv *env, jclass class, jstring audioDriver) {

  struct forEachData data;
  initData(env, &data);

  const char* prefix = "audio.";
  const char* cAudioDriver = (*env)->GetStringUTFChars(env, audioDriver, NULL);
  const char* suffix = ".device";

  char *key = (char *)calloc(strlen(prefix) + strlen(cAudioDriver) + strlen(suffix) + 1, 
                        sizeof(char));
  strcat(key, prefix);
  strcat(key, cAudioDriver);
  strcat(key, suffix);
 
  fluid_settings_t* settings = new_fluid_settings();
  fluid_settings_foreach_option(settings, key, &data, onOption);
  delete_fluid_settings(settings);

  free(key);

  (*env)->ReleaseStringUTFChars(env, audioDriver, cAudioDriver);

  return data.list;
}

