#!/bin/sh

# check for existence of at least one argument
if [ "$1" ]
then
    # start MangaLauncher with the given arguments
    java -jar MangaLauncher.jar $*
else
    # show help since no arguments are given
    java -jar MangaLauncher.jar --help
fi
