#!/usr/bin/env bash

#remove default java opts
sed -i '/-Xms256m/d' /opt/wso2as-5.3.0/bin/wso2server.sh

sed -i "/port=\"9763\"/a  \\\t\t   proxyPort=\"80\"" /opt/wso2as-5.3.0/repository/conf/tomcat/catalina-server.xml
sed -i "/port=\"9443\"/a  \\\t\t   proxyPort=\"443\"" /opt/wso2as-5.3.0/repository/conf/tomcat/catalina-server.xml

#Changing admin password
if [ -z ${ADMIN_PASSWORD+x} ]; then
    echo "ADMIN_PASSWORD is not set.";
    echo "Genarating admin password.";
    ADMIN_PASSWORD=${ADMIN_PASS:-$(pwgen -s 12 1)}
    echo "========================================================================="
    echo "Credentials for the instance:"
    echo
    echo "    user name: admin"
    echo "    password : $ADMIN_PASSWORD"
    echo "========================================================================="
    sed -i "s/.*<Password>admin<\/Password>.*/<Password>$ADMIN_PASSWORD<\/Password>/" /opt/wso2as-5.3.0/repository/conf/user-mgt.xml
else
    echo "ADMIN_PASSWORD set by user.";
fi


/opt/wso2as-5.3.0/bin/wso2server.sh -Dprofile=jaggery
