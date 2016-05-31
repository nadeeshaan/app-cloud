#!/bin/bash

ACTION=$1
LOGFILE=admin_service.log
source admin_service.cfg

echo "----------Login to admin service----------" >> $LOGFILE
curl -c cookies -v -X POST -k $SERVICE_URL/appmgt/site/blocks/user/login/ajax/login.jag -d "action=login&userName=$ADMIN_USERNAME&password=$ADMIN_PASSWORD" >> $LOGFILE 2>&1
echo -e "\n" >> $LOGFILE

if [ $ACTION = "whiteListAppVersion" ]; then
    echo "----------White list application version----------" >> $LOGFILE
    curl -b cookies  -v -X POST -k $SERVICE_URL/appmgt/site/blocks/admin/admin.jag -d "action=whiteListApplicationVersion&tenantDomain=$2&applicationName=$3&applicationRevision=$4" >> $LOGFILE 2>&1
    echo -e "\n" >> $LOGFILE
elif [ $ACTION = "whiteListTenant" ]; then
    echo "----------White list tenant----------" >> $LOGFILE
    curl -b cookies  -v -X POST -k $SERVICE_URL/appmgt/site/blocks/admin/admin.jag -d "action=whiteListTenant&tenantDomain=$2&maxAppCount=$3" >> $LOGFILE 2>&1
    echo -e "\n" >> $LOGFILE
elif [ $ACTION = "getApplicationVersions" ]; then
    echo "----------List application versions----------" >> $LOGFILE
    curl -b cookies  -v -X GET -k "$SERVICE_URL/appmgt/site/blocks/admin/admin.jag?action=getApplicationVersions&tenantDomain=$2&applicationName=$3" | tee -a $LOGFILE 2>&1 | less
    echo -e "\n" >> $LOGFILE
elif [ $ACTION = "getTenantMaxAppCount" ]; then
    echo "----------Tenant max application count----------" >> $LOGFILE
    curl -b cookies  -v -X GET -k "$SERVICE_URL/appmgt/site/blocks/admin/admin.jag?action=getTenantMaxAppCount&tenantDomain=$2" | tee -a $LOGFILE 2>&1 | less
    echo -e "\n" >> $LOGFILE
elif [ $ACTION = "updateConSpec" ]; then
    echo "----------Update container specification----------" >> $LOGFILE
    curl -b cookies -v -X POST -k $SERVICE_URL/appmgt/site/blocks/admin/admin.jag -d "action=updateConSpec&tenantDomain=$2&applicationName=$3&applicationRevision=$4&memory=$5&cpu=$6" >> $LOGFILE 2>&1
    echo -e "\n" >> $LOGFILE
fi

