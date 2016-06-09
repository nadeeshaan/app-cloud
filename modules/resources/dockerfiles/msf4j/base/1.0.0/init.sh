#!/usr/bin/env bash

#Calculate max heap size and the perm size for Java Opts
#Check whether TOTAL_MEMORY env variable defined or and not empty
if [[ $TOTAL_MEMORY && ${TOTAL_MEMORY-_} ]]; then
    let MAX_HEAP_SIZE=$TOTAL_MEMORY/256*128
    JAVA_OPTS="-Xms128m -Xmx"$MAX_HEAP_SIZE"m"
    export JAVA_OPTS=$JAVA_OPTS
fi

java $JAVA_OPTS -jar -Dtransports.netty.conf=/opt/conf/https/netty-transports.yaml $MSF4J_JAR