@echo off
echo. > log/err.log
echo ------------------------ >> log/err.log
echo %date%%time% >> log/err.log
java -cp "lib/*" de.herrlock.manga.Ctrl 2>>log/err.log
if errorlevel 1 notepad log/err.log