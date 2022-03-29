#!/bin/bash

set -e
echo 'Note: requires rpmdevtools ant java-11-openjdk alsa-lib-devel fluidsynth fluidsynth-devel'
fedpkg --release f35 local 
