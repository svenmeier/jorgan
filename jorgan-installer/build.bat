set JAVA_HOME="C:\Programme\Java\jdk1.6.0_17"
set ANT="C:\Programme\apache-ant-1.8.0RC1\bin\ant.bat"
set LAUNCH4J="C:\Programme\Launch4j\launch4jc.exe"
set INNO_SETUP="C:\Programme\Inno Setup 5\ISCC.exe"

call %ANT% -buildfile ../jorgan/build.xml
call %ANT% -buildfile ../jorgan-creative/build.xml
call %ANT% -buildfile ../jorgan-customizer/build.xml
call %ANT% -buildfile ../jorgan-executor/build.xml
call %ANT% -buildfile ../jorgan-fluidsynth/build.xml
call %ANT% -buildfile ../jorgan-importer/build.xml
call %ANT% -buildfile ../jorgan-keyboard/build.xml
call %ANT% -buildfile ../jorgan-lan/build.xml
call %ANT% -buildfile ../jorgan-linuxsampler/build.xml
call %ANT% -buildfile ../jorgan-memory/build.xml
call %ANT% -buildfile ../jorgan-midimerger/build.xml
call %ANT% -buildfile ../jorgan-recorder/build.xml
call %ANT% -buildfile ../jorgan-sams/build.xml
call %ANT% -buildfile ../jorgan-skins/build.xml
call %ANT% -buildfile ../jorgan-soundfont/build.xml
call %ANT% -buildfile ../jorgan-sysex/build.xml

:Clean
rmdir /S /Q target

:launcher
mkdir target
%LAUNCH4J% launch4j.cfg.xml

:installer
%INNO_SETUP% jorgan.iss