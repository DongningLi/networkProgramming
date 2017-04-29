#!/bin/bash

str=$1
echo -n $str | shasum -a 256 -b | cut -f 1 -d ' '| xxd -r -p | uuencode -m -
