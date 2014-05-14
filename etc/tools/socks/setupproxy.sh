#!/bin/bash
# Simple script for starting SOCKS proxy on Linux, created by Benjamin 2009
pfile=proxy-list.txt 	# File for proxy list (for FreeRapid)
port=8000 		        # Port to begin with, counts up

# This example will create SOCKS proxy as 
# username@foo.example.com at 127.0.0.1:8000,
# username@bar.example.com at 127.0.0.1:8001 and
# username@baz.example.com at 127.0.0.1:8002
# assuming you have Public/Private key authentification

# you may call createProxies multiple times with different variables set in advance

USR=username
plist=(foo bar baz)
domain=example.com

function createProxies {
	for proxy in ${plist[@]};
	do
  		if ping $proxy.$domain -c 1 -w 1 > /dev/null && ssh $USR@$proxy.$domain uname > /dev/null; then
                	echo Creating SOCKS proxy using $proxy on port $port
	                ssh -D $port -N $USR@$proxy.$domain &
			echo "\$SOCKS\$127.0.0.1:$port" >> $pfile
        	        let port=$port+1
	        else
        	    	echo "Can't connect to $proxy"
	        fi
	done
}

rm proxy-list.txt

createProxies

