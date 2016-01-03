#!/bin/sh

if [[ $* -eq 0 ]] 
then
    # show help since no arguments are given
    java -jar MangaLauncher.jar --help
else
    # start MangaLauncher with the given arguments
    java -jar MangaLauncher.jar $*
fi
