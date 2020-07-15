#!/bin/bash

set -e

sudo rm *.deb || true

ant -buildfile ../build.xml

echo 'Note: requires dh-make'
echo '      sudo apt-get install dh-make'

cd src
sudo ./rules
cd ..
