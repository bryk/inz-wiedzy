#!/bin/bash

(
  cd `dirname $0` &&
  cd ../inputs &&
  wget http://dblp.uni-trier.de/xml/dblp.xml.gz &&
  echo "unpacking..." &&
  gunzip dblp.xml.gz;
)
