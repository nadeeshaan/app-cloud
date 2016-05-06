#!/usr/bin/env bash

#remove default java opts
sed -i '/-Xms256m/d' /opt/wso2as-5.3.0/bin/wso2server.sh 

/opt/wso2as-5.3.0/bin/wso2server.sh -Dprofile=jaggery
