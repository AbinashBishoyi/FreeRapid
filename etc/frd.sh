#!/bin/sh
# How to configure FreeRapid application installed locally in your computer.

# The script bellow shows how I am running FreeRapid on my Linux (Kubuntu)

#How do I register Java binaries globally on Linux?
#You must place the correct path to the installed binaries in your /etc/bashrc configuration file.
#for example:
#PATH=/jdk1.6.0/bin:/jdk1.6.0/jre/bin:$PATH 

#If you need to set a path to JRE uncomment and update these lines:
# export JRE_PATH=/opt/jre1.6.0_08
# $JRE_PATH/bin/java -jar fotogrametrie.jar "$@"

#otherwise...
java -jar frd.jar "$@"