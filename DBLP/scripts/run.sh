#!/bin/bash

(
  cd `dirname $0` &&
  cd .. &&
  java -Djdk.xml.entityExpansionLimit=0 -Xmx3000m -jar ./target/dblpeksploracjadanych-1.0-jar-with-dependencies.jar $@;
)
