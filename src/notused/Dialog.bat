@echo off
echo. > log/err.log
echo ------------------------ >> log/err.log
echo %date%, %time% >> log/err.log
java -cp "lib/*" de.herrlock.manga.Ctrl 2>>log/err.log
if errorlevel 1 goto showerrlog

for /f %%C in ('Find /V /C "" ^< log/err.log') do set Count=%%C
if %Count% GTR 3 goto showerrlog

goto end

:showerrlog
notepad log/err.log

:end