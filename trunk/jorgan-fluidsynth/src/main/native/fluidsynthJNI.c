#include <jni.h>
#include <stdio.h>
#include "fluidsynthJNI.h"
#include "fluidsynth.h"

#define IOEX "java/io/IOException"

fluid_settings_t* settings;
fluid_synth_t* synth;
fluid_audio_driver_t* adriver;
int sfont_id;

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

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_create(JNIEnv *env, jobject object) {
  settings = new_fluid_settings();

  synth = new_fluid_synth(settings);

  adriver = new_fluid_audio_driver(settings, synth);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_destroy(JNIEnv *env, jobject object) {
  delete_fluid_audio_driver(adriver);
  delete_fluid_synth(synth);
  delete_fluid_settings(settings);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_soundFontLoad(JNIEnv *env, jobject object, jstring filename) {
  char* cfilename = (*env)->GetStringUTFChars(env, filename, 0);		
  int soundfont = fluid_synth_sfload(synth, cfilename, 0);
  if (soundfont == -1) {
    throwException(env, IOEX, "Couldn't load file %s, error %d", cfilename, soundfont);
  }
  (*env)->ReleaseStringUTFChars(env, filename, cfilename);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_noteOn(JNIEnv *env, jobject object, jint channel, jint pitch, jint velocity) {
  fluid_synth_noteon(synth, channel, pitch, velocity);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_noteOff(JNIEnv *env, jobject object, jint channel, jint pitch) {
  fluid_synth_noteoff(synth, channel, pitch);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_controlChange(JNIEnv *env, jobject object, jint channel, jint controller, jint value) {
  fluid_synth_cc(synth, channel, controller, value);
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_pitchBend(JNIEnv *env, jobject object, jint channel, jint value) {
  fluid_synth_pitch_bend(synth, channel, value); 
}

JNIEXPORT
void JNICALL Java_jorgan_fluidsynth_Fluidsynth_programChange(JNIEnv *env, jobject object, jint channel, jint program) {
  fluid_synth_program_change(synth, channel, program); 
}
