set JAVA_HOME="C:\Programme\Java\jdk1.6.0_17"
set ANT="C:\Programme\apache-ant-1.8.0RC1\bin\ant.bat"
set LAUNCH4J="C:\Programme\Launch4j\launch4jc.exe"
set INNO_SETUP="C:\Programme\Inno Setup 5\ISCC.exe"

rmdir /S /Q target

ant -buildfile ..\build.xml
if errorlevel 1 exit /B 1

mkdir target

%LAUNCH4J% launch4j.cfg.xml
if errorlevel 1 exit /B 1

%INNO_SETUP% jorgan.iss
