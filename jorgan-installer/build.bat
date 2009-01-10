set LAUNCH4J="C:\Programme\Launch4j\launch4jc.exe"
set INNO_SETUP="C:\Programme\Inno Setup 5\ISCC.exe"

:Clean
rmdir /S /Q target

:launcher
mkdir target
%LAUNCH4J% launch4j.cfg.xml

:installer
%INNO_SETUP% jorgan.iss