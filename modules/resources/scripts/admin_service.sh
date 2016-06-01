#!/bin/bash

ACTION=$1
LOGFILE=admin_service.log
source admin_service.cfg

echo "----------Login to admin service----------" >> $LOGFILE
curl -c cookies -v -X POST -k $SERVICE_URL/appmgt/site/blocks/user/login/ajax/login.jag -d "action=login&userName=$ADMIN_USERNAME&password=$ADMIN_PASSWORD" >> $LOGFILE 2>&1
echo -e "\n" >> $LOGFILE

if [ $ACTION = "help" ]; then
    echo "----------Sample admin service calls----------"
    echo -e "\n"
    echo "***For white list application version***"
    echo "./admin_service.sh whiteListAppVersion <tenant> <app> <version>"
    echo -e "\n"
    echo "***For list all version of the application***"
    echo "./admin_service.sh getApplicationVersions <tenant> <app>"
    echo -e "\n"
    echo "***For white list tenant and set max app count***"
    echo "./admin_service.sh whiteListTenant <tenant> <max-app-count>"
    echo -e "\n"
    echo "***For view tenant max application count***"
    echo "./admin_service.sh getTenantMaxAppCount <tenant>"
    echo -e "\n"
    echo "***For update application version container specification***"
    echo "./admin_service.sh updateConSpec <tenant> <app> <version> <memory> <cpu>"
    echo -e "\n"
elif [ $ACTION = "whiteListAppVersion" ]; then
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
