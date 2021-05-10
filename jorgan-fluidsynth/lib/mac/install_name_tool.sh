#! /bin/sh

# Tweak bundled libraries to be resolved relatively to their loader's location

set -e

install_name_tool -id @loader_path/libfluidsynth.dylib libfluidsynth.dylib
install_name_tool -change $HOME/workspace/fluidsynth/build/src/libfluidsynth.3.0.1.dylib @loader_path/libfluidsynth.dylib libfluidsynth.dylib
install_name_tool -change /usr/local/opt/glib/lib/libgthread-2.0.0.dylib @loader_path/libgthread-2.0.0.dylib libfluidsynth.dylib
install_name_tool -change /usr/local/opt/glib/lib/libglib-2.0.0.dylib @loader_path/libglib-2.0.0.dylib libfluidsynth.dylib
install_name_tool -change /usr/local/opt/gettext/lib/libintl.8.dylib @loader_path/libintl.8.dylib libfluidsynth.dylib
install_name_tool -change /usr/local/opt/libsndfile/lib/libsndfile.1.dylib @loader_path/libsndfile.1.dylib libfluidsynth.dylib
install_name_tool -change /usr/local/opt/portaudio/lib/libportaudio.2.dylib @loader_path/libportaudio.2.dylib libfluidsynth.dylib

otool -L libfluidsynth.dylib | grep "Cellar"
if test $? -eq 0
then
  echo "Error: still linking to Cellar"
  exit 1
fi

install_name_tool -id @loader_path/libglib-2.0.0.dylib libglib-2.0.0.dylib
install_name_tool -change /usr/local/opt/gettext/lib/libintl.8.dylib @loader_path/libintl.8.dylib libglib-2.0.0.dylib
otool -L libglib-2.0.0.dylib | grep "Cellar"
if test $? -eq 0
then
  echo "Error: still linking to Cellar"
  exit 1
fi

install_name_tool -id @loader_path/libgthread-2.0.0.dylib libgthread-2.0.0.dylib
install_name_tool -change /usr/local/Cellar/glib/2.28.5/lib/libglib-2.0.0.dylib @loader_path/libglib-2.0.0.dylib libgthread-2.0.0.dylib
install_name_tool -change /usr/local/Cellar/gettext/0.18.1.1/lib/libintl.8.dylib @loader_path/libintl.8.dylib libgthread-2.0.0.dylib
otool -L libgthread-2.0.0.dylib | grep "Cellar"
if test $? -eq 0
then
  echo "Error: still linking to Cellar"
  exit 1
fi

install_name_tool -id @loader_path/libintl.8.dylib libintl.8.dylib
otool -L libintl.8.dylib| grep "Cellar"
if test $? -eq 0
then
  echo "Error: still linking to Cellar"
  exit 1
fi

install_name_tool -id @loader_path/libsndfile.1.dylib libsndfile.1.dylib
otool -L libsndfile.1.dylib | grep "Cellar"
if test $? -eq 0
then
  echo "Error: still linking to Cellar"
  exit 1
fi