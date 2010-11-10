#!/bin/bash

set -e

ant -buildfile ../build.xml

ant -buildfile build.xml

chmod a+x ./target/jOrgan/jOrgan.app/Contents/MacOS/JavaApplicationStub

hdiutil create -srcfolder ./target/jOrgan ./target/jOrgan.dmg

hdiutil internet-enable -yes ./target/jOrgan.dmg