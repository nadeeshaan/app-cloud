package org.wso2.appcloud.integration.test.scenarios;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.junit.Assert;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestConstants;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestUtils;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;

import java.io.File;

public class JaggeryApplicationTestCase extends AppCloudIntegrationBaseTestCase {
    private static final Log log = LogFactory.getLog(JaggeryApplicationTestCase.class);

    public static final String APPLICATION_SERVER_STARTED_MESSAGE = "WSO2 Carbon started";
    public static final String JAGGERY_APPLICATION_TYPE = "jaggery";


    public JaggeryApplicationTestCase() {
        super(AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.JAGGERY_APP_RUNTIME_ID_KEY),
              AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.JAGGERY_APP_FILE_NAME_KEY),
              JAGGERY_APPLICATION_TYPE,
              AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.JAGGERY_APP_CONTENT),
              Long.parseLong(AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.
                                                                                   JAGGERY_RUNTIME_START_TIMEOUT)),
              AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.EXTRA_LARGE_CPU),
              AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.EXTRA_LARGE_MEMORY));
    }

    @Override
    protected void assertLogContent(String logContent) {
        Assert.assertTrue("Received log:" + logContent + " but expected line: " + APPLICATION_SERVER_STARTED_MESSAGE,
                          logContent.contains(APPLICATION_SERVER_STARTED_MESSAGE));

    }

    @Override
    public void testLaunchApplication() throws Exception {

        log.info("Waiting " + runtimeStartTimeout + "milliseconds before trying application launch...");
        Thread.sleep(runtimeStartTimeout);
        JSONObject applicationBean = applicationClient.getApplicationBean(applicationName);
        String launchURL = ((JSONObject) ((JSONObject) applicationBean
                .get(AppCloudIntegrationTestConstants.PROPERTY_VERSIONS_NAME))
                .get(applicationRevision)).getString(AppCloudIntegrationTestConstants.PROPERTY_DEPLOYMENT_URL);
        launchURL = launchURL + "/" + fileName.replace(".zip","") ;
        //make the launch url http
        launchURL = launchURL.replace("https", "http");
        Boolean isLaunchSuccessfull = applicationClient.launchApplication(launchURL, sampleAppContent);
        Assert.assertTrue("Application launch failed!", isLaunchSuccessfull);

    }

}
