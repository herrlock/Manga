@echo off
echo. > log/err.log
echo ------------------------ >> log/err.log
echo %date%%time% >> log/err.log
bin/Manga.bat 2>>log/err.log