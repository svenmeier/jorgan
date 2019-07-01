#!/bin/bash

set -e

java \
-Djorgan.skins.path=jorgan-skins \
-Djorgan.creative.library.path=jorgan-creative/target/native \
-Djorgan.fluidsynth.library.path=jorgan-fluidsynth/target/native \
-Djorgan.fluidsynth.backend.path=fluidsynth \
-Duser.language=en \
-Xms256m \
-Xmx256m \
-classpath \
jorgan-core/lib/*:jorgan-core/target/classes:\
jorgan-creative/lib/*:jorgan-creative/target/classes:\
jorgan-importer/lib/*:jorgan-importer/target/classes:\
jorgan-gui/lib/*:jorgan-gui/target/classes:\
jorgan-customizer/lib/*:jorgan-customizer/target/classes:\
jorgan-executor/lib/*:jorgan-executor/target/classes:\
jorgan-exporter/lib/*:jorgan-exporter/target/classes:\
jorgan-fluidsynth/lib/*:jorgan-fluidsynth/target/classes:\
jorgan-keyboard/lib/*:jorgan-keyboard/target/classes:\
jorgan-lan/lib/*:jorgan-lan/target/classes:\
jorgan-lcd/lib/*:jorgan-lcd/target/classes:\
jorgan-linuxsampler/lib/*:jorgan-linuxsampler/target/classes:\
jorgan-memory/lib/*:jorgan-memory/target/classes:\
jorgan-midimerger/lib/*:jorgan-midimerger/target/classes:\
jorgan-recorder/lib/*:jorgan-recorder/target/classes:\
jorgan-sams/lib/*:jorgan-sams/target/classes:\
jorgan-soundfont/lib/*:jorgan-soundfont/target/classes:\
jorgan-tools/lib/*:jorgan-tools/target/classes \
jorgan.App

