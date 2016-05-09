#!/usr/bin/env bash

#remove default java opts
sed -i '/-Xms256m/d' /opt/wso2as-5.3.0/bin/wso2server.sh

sed -i "/port=\"9763\"/a  \\\t\t   proxyPort=\"80\"" /opt/wso2as-5.3.0/repository/conf/tomcat/catalina-server.xml
sed -i "/port=\"9443\"/a  \\\t\t   proxyPort=\"443\"" /opt/wso2as-5.3.0/repository/conf/tomcat/catalina-server.xml

/opt/wso2as-5.3.0/bin/wso2server.sh -Dprofile=jaggery
