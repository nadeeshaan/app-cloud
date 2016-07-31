#!/usr/bin/env bash

CARBON_HOME_PATH=/opt/wso2esb-5.0.0
#remove default java opts
sed -i '/-Xms256m/d' $CARBON_HOME_PATH/bin/wso2server.sh

sed -i "/port=\"9763\"/a  \\\t\t   proxyPort=\"80\"" $CARBON_HOME_PATH/repository/conf/tomcat/catalina-server.xml
sed -i "/port=\"9443\"/a  \\\t\t   proxyPort=\"443\"" $CARBON_HOME_PATH/repository/conf/tomcat/catalina-server.xml
#sed -i '/<WebContextRoot>/c\\t<WebContextRoot>/dss</WebContextRoot>' $CARBON_HOME_PATH/repository/conf/carbon.xml

#Remove bundles from plugins dir and the bundles.info to minimize jaggery runtime
PLUGINS_DIR_PATH="$CARBON_HOME_PATH/repository/components/plugins/"
DEFAULT_PROFILE_BUNDLES_INFO_FILE="$CARBON_HOME_PATH/repository/components/default/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info"
#LIST_OF_BUNDLES_FILE="removed-bundles.txt"

# while read in; do rm -rf "$PLUGINS_DIR_PATH""$in" && sed -i "/$in/d" "$DEFAULT_PROFILE_BUNDLES_INFO_FILE"; done < $LIST_OF_BUNDLES_FILE

#Calculate max heap size and the perm size for Java Opts
#Check whether TOTAL_MEMORY env variable defined or and not empty
if [[ $TOTAL_MEMORY && ${TOTAL_MEMORY-_} ]]; then
    let MAX_HEAP_SIZE=$TOTAL_MEMORY/512*256
    let PERM_SIZE=$TOTAL_MEMORY/512*64
    JAVA_OPTS="-Xms128m -Xmx"$MAX_HEAP_SIZE"m -XX:PermSize="$PERM_SIZE"m"
    export JAVA_OPTS=$JAVA_OPTS
fi

$CARBON_HOME_PATH/bin/wso2server.sh
