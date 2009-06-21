set JAVA_HOME="C:\Programme\Java\jdk1.6.0_13"
set ANT="C:\Programme\apache-ant-1.7.1\bin\ant.bat"
set LAUNCH4J="C:\Programme\Launch4j\launch4jc.exe"
set INNO_SETUP="C:\Programme\Inno Setup 5\ISCC.exe"

call %ANT% -buildfile ../core/build.xml
call %ANT% -buildfile ../creative/build.xml
call %ANT% -buildfile ../customizer/build.xml
call %ANT% -buildfile ../fluidsynth/build.xml
call %ANT% -buildfile ../importer/build.xml
call %ANT% -buildfile ../installer/build.xml
call %ANT% -buildfile ../keyboard/build.xml
call %ANT% -buildfile ../linuxsampler/build.xml
call %ANT% -buildfile ../midimerger/build.xml
call %ANT% -buildfile ../recorder/build.xml
call %ANT% -buildfile ../skins/build.xml
call %ANT% -buildfile ../soundfont/build.xml

:Clean
rmdir /S /Q target

:launcher
mkdir target
%LAUNCH4J% launch4j.cfg.xml

:installer
%INNO_SETUP% jorgan.iss