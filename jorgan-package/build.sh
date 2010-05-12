#!/bin/bash

# adjust:
# - version in src/debian/changelog
# - architecture in src/debian/control
sudo rm *.deb

ant -buildfile ../build.xml

cd src
sudo ./rules
cd ..
