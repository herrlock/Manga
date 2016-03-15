@echo off

@REM check if arguments are given
if "%1"=="" goto HAS_0

:HAS_ARGS
@REM start MangaLauncher with the given arguments
java -jar MangaLauncher.jar %*
goto END

:HAS_0
@REM show help since no arguments are given
java -jar MangaLauncher.jar --help
goto END

:END
@REM end of the script