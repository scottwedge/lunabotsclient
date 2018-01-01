#!/bin/bash          
echo Making JAR of all server software
rm LunaPi.jar
jar cvfm LunaPi.jar Lunabot.mf RXTXcomm.jar jinput-dx8.dll jinput-dx8_64.dll jinput-raw.dll	jinput-raw_64.dll jinput-wintab.dll libjinput-linux.so libjinput-linux64.so libjinput-osx.jnilib jinput.jar -C build/classes/ .
echo Done making JAR NOTE: MIGHT HAVE FAILED
