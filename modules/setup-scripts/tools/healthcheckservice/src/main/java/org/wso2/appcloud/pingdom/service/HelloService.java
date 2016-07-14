/*
 * Copyright (c) 2016, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.appcloud.pingdom.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.appcloud.pingdom.service.util.Utils;

import java.util.Set;

/**
 * Hello service resource class.
 */
@Path("/hello")
public class HelloService {
    private static final Logger log = LoggerFactory.getLogger(HelloService.class);

    @GET
    @Path("/")
    public Response hello() {
        String dbURL = System.getenv("PINGDOM_SERVICE_DB_URL");
        String dbUser = System.getenv("PINGDOM_SERVICE_DB_USER");
        String dbUserPassword = System.getenv("PINGDOM_SERVICE_DB_USER_PASSWORD");
        boolean areAppsHealthy = true;
        String msg = null;

        try {
            Set<String> launchURLs = Utils.getApplicationLaunchURLs(dbURL, dbUser, dbUserPassword);
            for (String url : launchURLs) {
                if (!Utils.isApplicationHealthy(url)) {
                    areAppsHealthy = false;
                    break;
                } else {
                    log.info("Launch URL:" + url + " completed successfully.");
                }
            }
        } catch (HeartbeatServiceException e) {
            areAppsHealthy = false;
            msg = "Failed to connect to database and get launch URLs of health check applications.";
        }

        if (areAppsHealthy) {
            return Response.ok("Health check applications responded properly.").build();
        } else {
            log.error(msg);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }

}
