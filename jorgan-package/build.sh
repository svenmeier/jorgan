#!/bin/bash

set -e

sudo rm *.deb || true

ant -buildfile ../build.xml

cd src
sudo ./rules
cd ..
