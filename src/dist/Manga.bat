@echo off
echo. > log/err.log
echo ------------------------ >> log/err.log
echo %date%%time% >> log/err.log
java -cp "lib/*" de.herrlock.manga.Ctrl 2>>log/err.log
if (%ERRORCODE% neq "0") notepad log/err.log