@echo off
echo. > log/err.log
echo ------------------------ >> log/err.log
echo %date%, %time% >> log/err.log
start javaw -cp "lib/*" de.herrlock.manga.Ctrl 2>>log/err.log
