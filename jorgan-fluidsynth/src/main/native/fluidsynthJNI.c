#include <jni.h>
#include <stdio.h>
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
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_create(JNIEnv *env, jobject object, jint channels) {
  struct Context *context = createContext(env);
  if (context == NULL) {
    return;
  }

  (*context).object = (*env)->NewGlobalRef(env, object);

  (*context).settings = new_fluid_settings();
  fluid_settings_setint((*context).settings, "synth.midi-channels", channels);

  (*context).synth = new_fluid_synth((*context).settings);

  (*context).adriver = new_fluid_audio_driver((*context).settings, (*context).synth);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_destroy(JNIEnv *env, jobject object) {
  struct Context *context = getContext(env, object);
  if (context == NULL) {
    return;
  }

  delete_fluid_audio_driver((*context).adriver);
  delete_fluid_synth((*context).synth);
  delete_fluid_settings((*context).settings);

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
