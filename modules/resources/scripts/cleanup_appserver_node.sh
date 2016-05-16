#!/bin/bash
#This script is run as a cronjob and it will clean up application server node where applications are created in app cloud.

#delete older docker images.
docker rmi -f $(docker images | grep -v "base" | grep -E 'hours ago | days ago | weeks ago' | grep -E 'registry.cloudstaging.wso2.com | <none>' | awk '{print $3}')

#delete files older than 5 days on app creation temp directory. Make sure to update direcotry path of tmpUploadedApps directory
find /mnt/xxx.xxx.xxx.xxx/wso2as-5.2.1/repository/deployment/server/jaggeryapps/appmgt/tmpUploadedApps/ -mtime +5 -type f -exec rm {} \;

