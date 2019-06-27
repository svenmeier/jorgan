#!/bin/bash

set -e

ant -buildfile ../build.xml

ant -buildfile build.xml

IMAGE=`ls target`

chmod a+x ./target/${IMAGE}/jOrgan.app/Contents/MacOS/JavaApplicationStub

hdiutil create -srcfolder ./target/${IMAGE} ./target/${IMAGE}.dmg

hdiutil internet-enable -yes ./target/${IMAGE}.dmg