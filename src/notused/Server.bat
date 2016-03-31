@echo off
echo. > log/err.log
echo ------------------------ >> log/err.log
echo %date%, %time% >> log/err.log
start javaw -cp "lib/*" de.herrlock.manga.http.StartWithDesktop 2>>log/err.log
