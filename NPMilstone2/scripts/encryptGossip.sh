#!/bin/sh

#  Script.sh
#  
#
#  Created by Dongning Li on 2/28/17.
#

str = $1
echo -n $str | sha256sum -b | cut -f 1 -d ' '| xxd -r -p | uuencode -m -
