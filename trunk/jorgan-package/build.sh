#!/bin/bash
sudo rm *.deb

ant -buildfile ../jorgan/build.xml
ant -buildfile ../jorgan-importer/build.xml
ant -buildfile ../jorgan-customizer/build.xml
ant -buildfile ../jorgan-creative/build.xml
ant -buildfile ../jorgan-executor/build.xml
ant -buildfile ../jorgan-fluidsynth/build.xml
ant -buildfile ../jorgan-keyboard/build.xml
ant -buildfile ../jorgan-lan/build.xml
ant -buildfile ../jorgan-linuxsampler/build.xml
ant -buildfile ../jorgan-memory/build.xml
ant -buildfile ../jorgan-midimerger/build.xml
ant -buildfile ../jorgan-recorder/build.xml
ant -buildfile ../jorgan-sams/build.xml
ant -buildfile ../jorgan-skins/build.xml
ant -buildfile ../jorgan-soundfont/build.xml
ant -buildfile ../jorgan-sysex/build.xml

cd src
sudo ./rules
cd ..
