/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.appcloud.core.docker;

import com.google.common.base.Strings;
import io.fabric8.docker.client.Config;
import io.fabric8.docker.client.ConfigBuilder;
import io.fabric8.docker.client.DefaultDockerClient;
import io.fabric8.docker.client.DockerClient;
import io.fabric8.docker.dsl.EventListener;
import io.fabric8.docker.dsl.OutputHandle;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.appcloud.common.AppCloudException;
import org.wso2.appcloud.common.util.AppCloudUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;

public class DockerOpClient {

    private static Log log = LogFactory.getLog(DockerOpClient.class);
    private final  static  String tokenizeStartLine = "FROM";

    DockerClient dockerClient;
    OutputHandle handle;

    final CountDownLatch buildDone = new CountDownLatch(1);
    final CountDownLatch pushDone = new CountDownLatch(1);

    public DockerOpClient() {
        Config config = new ConfigBuilder()
                .withDockerUrl(DockerOpClientConstants.DEFAULT_DOCKER_URL)
                .build();
        dockerClient = new DefaultDockerClient(config);
    }

    public DockerOpClient(String uri) {
        Config config = new ConfigBuilder()
                .withDockerUrl(uri)
                .withConnectionTimeout(Integer.parseInt(AppCloudUtil.getPropertyValue(DockerOpClientConstants
                        .DOCKER_CONNECTION_TIMEOUT)))
                .withRequestTimeout(Integer.parseInt(AppCloudUtil.getPropertyValue(DockerOpClientConstants
                        .DOCKER_REQUEST_TIMEOUT)))
                .withImagePushTimeout(Integer.parseInt(AppCloudUtil.getPropertyValue(DockerOpClientConstants
                        .DOCKER_PUSH_TIMEOUT)))
                .withImageBuildTimeout(Integer.parseInt(AppCloudUtil.getPropertyValue(DockerOpClientConstants
                        .DOCKER_BUILLD_TIMEOUT)))
                .withImageSearchTimeout(Integer.parseInt(AppCloudUtil.getPropertyValue(DockerOpClientConstants
                        .DOCKER_SEARCH_TIMEOUT)))
                .build();
        dockerClient = new DefaultDockerClient(config);
    }

    public void createDockerFile(String dockerFilePath, String runtimeId, String dockerTemplateFilePath,
            String dockerFileCategory, Map<String, String> dockerFilePropertyMap,
            Map<String, String> customDockerFileProperties) throws IOException, AppCloudException {

        customDockerFileProperties.keySet().removeAll(dockerFilePropertyMap.keySet());
        dockerFilePropertyMap.putAll(customDockerFileProperties);

        String dockerFileTemplatePath = DockerUtil
                .getDockerFileTemplatePath(runtimeId, dockerTemplateFilePath, dockerFileCategory);
        List<String> dockerFileConfigs = new ArrayList<>();

        for (String line : FileUtils.readLines(new File(dockerFileTemplatePath))) {
            StringTokenizer stringTokenizer = new StringTokenizer(line);
            //Search if line contains keyword to replace with the value
            while (stringTokenizer.hasMoreElements()) {
                String element = stringTokenizer.nextElement().toString().trim();
                String value;
                if (dockerFilePropertyMap.containsKey(element)) {
                    value = dockerFilePropertyMap.get(element);
                    line = line.replace(element, value);
                }
            }
            dockerFileConfigs.add(line);
        }
        FileUtils.writeLines(new File(dockerFilePath), dockerFileConfigs);
    }

    public void buildDockerImage(String repoUrl, String imageName, String tag, String dockerFileUrl)
            throws InterruptedException, IOException, AppCloudException {

        String dockerImage = repoUrl + "/" + imageName + ":" + tag;
        final boolean[] dockerStatusCheck = new boolean[1];
	    dockerStatusCheck[0] = true;
        handle = dockerClient.image().build()
                .withRepositoryName(dockerImage)
                .withNoCache()
                .usingListener(new EventListener() {
                    @Override
                    public void onSuccess(String message) {
                        log.info("Build Success:" + message);
                        buildDone.countDown();
                    }

                    @Override
                    public void onError(String message) {
                        log.error("Build Failure:" + message);
                        buildDone.countDown();
                        dockerStatusCheck[0] = false;
                    }

                    @Override
                    public void onEvent(String event) {
                        log.info(event);
                    }
                })
                .fromFolder(dockerFileUrl);
        buildDone.await();
        handle.close();

        if (!dockerStatusCheck[0]) {
            log.error("Docker image building failed: " + imageName + " repo: " + repoUrl + " docker file: "
                    + dockerFileUrl + " tag: " + tag);
            throw new AppCloudException(
                    "Docker image building failed: " + imageName + " repo: " + repoUrl + " docker file: "
                            + dockerFileUrl + " tag: " + tag);
        }
    }

    public void pushDockerImage(String repoUrl, String imageName, String tag)
            throws InterruptedException, IOException, AppCloudException {

        final boolean[] dockerStatusCheck = new boolean[1];
	    dockerStatusCheck[0] = true;
        String dockerImageName = repoUrl + "/" + imageName;
        handle = dockerClient.image().withName(dockerImageName).push().usingListener(new EventListener() {
            @Override
            public void onSuccess(String message) {
                log.info("Push Success:" + message);
                pushDone.countDown();
            }

            @Override
            public void onError(String message) {
                log.error("Push Failure:" + message);
                pushDone.countDown();
                dockerStatusCheck[0] = false;
            }

            @Override
            public void onEvent(String event) {
                log.info(event);
            }
        }).withTag(tag).toRegistry();
        pushDone.await();
        handle.close();

        if (!dockerStatusCheck[0]) {
            log.error("Docker image push failed: " + imageName + " repo: " + repoUrl + " tag: " + tag);
            throw new AppCloudException(
                    "Docker image push failed: " + imageName + " repo: " + repoUrl + " tag: " + tag);
        }
    }

    public void clientClose() throws IOException {
        dockerClient.close();
    }
}
