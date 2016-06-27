/*
* Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.appcloud.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.appcloud.common.AppCloudException;
import org.wso2.appcloud.core.dao.ApplicationDAO;
import org.wso2.appcloud.core.dto.*;
import org.wso2.carbon.context.CarbonContext;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * This class provide the interface for accessing the dao layer.
 */
public class ApplicationManager {

    private static Log log = LogFactory.getLog(ApplicationManager.class);
    private static ApplicationDAO applicationDAO;

    /**
     * Method for adding application.
     *
     * @param application application object
     * @return
     * @throws AppCloudException
     */
    public static void addApplication(Application application) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();

        try {

            getApplicationDAO().addApplication(dbConnection, application, tenantId);
            dbConnection.commit();

        } catch (AppCloudException e) {
            String msg = "Error while adding application with name : " + application.getApplicationName() +
                    " in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } catch (SQLException e) {
            String msg = "Error while committing the application adding transaction for application : " +
                         application.getApplicationName() + " in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }

    }

    /**
     * Method for adding application version.
     *
     * @param version version object
     * @throws AppCloudException
     */
    public static void addApplicationVersion(Version version, String applicationHashId) throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();

        try {
            int applicationId = getApplicationDAO().getApplicationId(dbConnection, applicationHashId, tenantId);
            getApplicationDAO().addVersion(dbConnection, version, "", applicationId, tenantId);
            dbConnection.commit();
        } catch (AppCloudException e) {
            String msg = "Error while adding the application version for application id : " + applicationHashId +
                    ", version:"+ version.getVersionName()+" in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } catch (SQLException e) {
            String msg = "Error while committing the application version adding transaction for application id : " +
                    applicationHashId + ", version:"+ version.getVersionName()+" in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }

    }

    /**
     * Method for adding runtime properties for a specific version.
     *
     * @param runtimeProperties list of runtime properties
     * @param versionHashId version hash id
     * @throws AppCloudException
     */
    public static void addRuntimeProperties(List<RuntimeProperty> runtimeProperties, String versionHashId) throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();

        try {
            int versionId = getApplicationDAO().getVersionId(dbConnection, versionHashId, tenantId);

            if (runtimeProperties != null) {
                getApplicationDAO().addRunTimeProperties(dbConnection, runtimeProperties, versionHashId, tenantId);
                dbConnection.commit();
            }
        } catch (AppCloudException e) {
            String msg = "Error while adding runtime properties for version with version id : " + versionHashId;;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } catch (SQLException e) {
            String msg = "Error while committing the transaction when adding runtime properties for version with version" +
                         " id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }

    /**
     * Method for adding tags for a specific version.
     *
     * @param tags list of tags
     * @param versionHashId version hash id
     * @throws AppCloudException
     */
    public static void addTags(List<Tag> tags, String versionHashId)
            throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();

        try {
            if (tags != null) {
                getApplicationDAO().addTags(dbConnection, tags, versionHashId, tenantId);
                dbConnection.commit();
            }
        } catch (AppCloudException e) {
            String msg = "Error while adding tags for version with hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } catch (SQLException e) {
            String msg = "Error while committing the transaction when adding tags for version with hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }


    /**
     * Method for getting the list of application of a tenant.
     *
     * @return
     * @throws AppCloudException
     */
    public static Application[] getApplicationList() throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();

        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        List<Application> applications;

        try {
            applications = getApplicationDAO().getAllApplicationsList(dbConnection, tenantId);
        } catch (AppCloudException e) {
            String msg = "Error while getting application list for tenant id: " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }

        return applications.toArray(new Application[applications.size()]);
    }


    public static List<String> getVersionListOfApplication(String applicationHashId) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            return getApplicationDAO().getAllVersionListOfApplication(dbConnection, applicationHashId, tenantId);
        } catch (AppCloudException e) {
            String msg = "Error while getting version list for application with hash id: " + applicationHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }

    public static List<String> getVersionHashIdsOfApplication(String applicationHashId) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            return getApplicationDAO().getAllVersionHashIdsOfApplication(dbConnection, applicationHashId, tenantId);
        } catch (AppCloudException e) {
            String msg = "Error while getting version hash ids of application with hash id: " + applicationHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }

    public static boolean isSingleVersion(String versionHashId) throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            return getApplicationDAO().isSingleVersion(dbConnection, versionHashId, tenantId);
        } catch (AppCloudException e) {
            String msg = "Error while getting if version is a single version for version with hash id: " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }

    public static String getApplicationHashIdByVersionHashId(String versionHashId) throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            return getApplicationDAO().getApplicationHashIdByVersionHashId(dbConnection, versionHashId, tenantId);
        } catch (AppCloudException e) {
            String msg = "Error while getting application hash id by version hash id for version with hash id: " +
                    versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }

    public static String getApplicationNameByHashId(String applicationHashId) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {

            return getApplicationDAO().getApplicationNameByHashId(dbConnection, applicationHashId, tenantId);
        } catch (AppCloudException e) {
            String msg = "Error while getting application name by hash id for application with hash id: " +
                    applicationHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }

    public static String getApplicationHashIdByName(String applicationName) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();

        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();

        try {
            return getApplicationDAO().getApplicationHashIdByName(dbConnection, applicationName, tenantId);
        } catch (AppCloudException e) {
            String msg = "Error while getting application hash id by name for application with name: " + applicationName;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }

    /**
     * Method for getting application by hash id.
     *
     * @param applicationHashId application hash id
     * @return
     * @throws AppCloudException
     */
    public static Application getApplicationByHashId(String applicationHashId) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            return getApplicationDAO().getApplicationByHashId(dbConnection, applicationHashId, tenantId);
        } catch (AppCloudException e) {
            String msg = "Error while getting application by hash id for hash id: " + applicationHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }

    public static List<RuntimeProperty> getAllRuntimePropertiesOfVersion (String versionHashId) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            return getApplicationDAO().getAllRuntimePropertiesOfVersion(dbConnection, versionHashId, tenantId);
        } catch (AppCloudException e) {
            String msg = "Error while getting all runtime properties of version for version hash id: " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }


    public static List<Tag> getAllTagsOfVersion(String versionHashId) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            return getApplicationDAO().getAllTagsOfVersion(dbConnection, versionHashId, tenantId);
        } catch (AppCloudException e) {
            String msg = "Error while getting all tags of version for version with hash id: " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }


    public static void updateRuntimeProperty(String versionHashId, String oldKey, String newKey,
                                                        String newValue) throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            getApplicationDAO().updateRuntimeProperty(dbConnection, versionHashId, oldKey, newKey, newValue, tenantId);
            dbConnection.commit();
        } catch (AppCloudException e) {
            String msg = "Error while adding runtime property with key : " + oldKey + " for version with hash id : " +
                    versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } catch (SQLException e) {
            String msg = "Error while committing transaction when adding runtime property with key : " + oldKey +
                         " for version with hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }


    public static void updateTag(String versionHashId, String oldKey, String newKey, String newValue)
            throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            getApplicationDAO().updateTag(dbConnection, versionHashId, oldKey, newKey, newValue, tenantId);
            dbConnection.commit();
        } catch (AppCloudException e) {
            String msg = "Error while updating tag with the key : " + oldKey + " for version with hash id : " +
                    versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } catch (SQLException e) {
            String msg = "Error while committing the transaction when updating tag with the key : " + oldKey +
                         " for version with hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }

    public static void deleteRuntimeProperty(String versionHashId, String key)
            throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            getApplicationDAO().deleteRuntimeProperty(dbConnection, versionHashId, key, tenantId);
            dbConnection.commit();
        } catch (AppCloudException e) {
            String msg = "Error while deleting runtime property with key : " + key + " for version with hash id : " +
                    versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } catch (SQLException e) {
            String msg = "Error while committing transaction when deleting runtime property with key : " + key +
                    " for version with hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }


    public static void deleteTag(String versionHashId, String key)
            throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            getApplicationDAO().deleteTag(dbConnection, versionHashId, key, tenantId);
            dbConnection.commit();
        } catch (AppCloudException e) {
            String msg = "Error while deleting tag with key : " + key + " for version with hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } catch (SQLException e) {
            String msg = "Error while committing transaction when deleting tag with key : " + key +
                         " for version with hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }

    public static void updateApplicationIcon(String applicationHashId, Object iconStream)
            throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        if( iconStream instanceof InputStream){
            InputStream iconInputStream = (InputStream) iconStream;
            try {
                int applicationId = getApplicationDAO().getApplicationId(dbConnection, applicationHashId, tenantId);
                getApplicationDAO().updateApplicationIcon(dbConnection, iconInputStream, applicationId);
                dbConnection.commit();
            } catch (AppCloudException e) {
                String msg = "Error while updating the application icon for application with hash id : "
                        + applicationHashId;
                log.error(msg, e);
                throw new AppCloudException(msg, e);
            } catch (SQLException e) {
                String msg = "Error while committing the transaction when updating the application icon for application " +
                             "with hash id : " + applicationHashId;
                log.error(msg, e);
                throw new AppCloudException(msg, e);
            } finally {
                try {
                    iconInputStream.close();
                } catch (IOException e) {
                    log.error("Error occurred while closing input stream for application with hash id : " +
                              applicationHashId);
                } finally {
                    DBUtil.closeConnection(dbConnection);
                }
            }
        } else {
            String msg = "Cannot read the provided icon stream for application with hash id : " + applicationHashId;
            log.error(msg);
            throw new AppCloudException(msg);
        }
    }


    /**
     * Method for getting all apptypes.
     *
     * @return
     * @throws AppCloudException
     */
    public static ApplicationType[] getAllAppTypes() throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        try {

            List<ApplicationType> applicationTypeList = getApplicationDAO().getAllApplicationTypes(dbConnection);
            return applicationTypeList.toArray(new ApplicationType[applicationTypeList.size()]);
        } catch (AppCloudException e) {
            String msg = "Error while getting all application types";
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }


    /**
     * Method for getting all runtimes for a given application type.
     *
     * @param appType application type
     * @return
     * @throws AppCloudException
     */
    public static ApplicationRuntime[] getAllRuntimesForAppType(String appType)
            throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        try {

            List<ApplicationRuntime> runtimes = getApplicationDAO().getRuntimesForAppType(dbConnection, appType);
            return runtimes.toArray(new ApplicationRuntime[runtimes.size()]);
        } catch (AppCloudException e) {
            String msg = "Error while getting all runtimes for application type for type: " + appType;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }

    /**
     * Method for updating application status.
     *
     * @param status status of application
     * @return
     * @throws AppCloudException
     */
    public static boolean updateVersionStatus(String versionHashId, String status) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        boolean isUpdateSuccess = false;
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            isUpdateSuccess = getApplicationDAO().updateVersionStatus(dbConnection, status, versionHashId, tenantId);
            dbConnection.commit();
        } catch (AppCloudException e) {
            String msg = "Error while updating version status with status : " + status + " for version with hash id : "
                    + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } catch (SQLException e) {
            String msg = "Error while committing the transaction when updating version status with status : " + status +
                         " for version with hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }

        return isUpdateSuccess;
    }

    /**
     * Method for delete an application completely.
     *
     * @param applicationHashId application hash id
     * @throws AppCloudException
     */
    public static void deleteApplication(String applicationHashId) throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            getApplicationDAO().deleteAllDeploymentOfApplication(dbConnection, applicationHashId, tenantId);
            getApplicationDAO().deleteApplication(dbConnection, applicationHashId, tenantId);
            dbConnection.commit();
        } catch (AppCloudException e) {
            String msg = "Error while deleting application with hash id : " + applicationHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } catch (SQLException e) {
            String msg = "Error while commiting transaction for deleting application with hash id : " +
                    applicationHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }


    public static void deleteVersion(String versionHashId) throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            getApplicationDAO().deleteDeployment(dbConnection, versionHashId, tenantId);
            getApplicationDAO().deleteVersion(dbConnection, versionHashId, tenantId);
            dbConnection.commit();
        } catch (AppCloudException e) {
            String msg = "Error while deleting the version with hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } catch (SQLException e) {
            String msg = "Error while committing the transaction when deleting the version with hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }

    public static void addDeployment(String versionHashId, Deployment deployment)throws AppCloudException {
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        Connection dbcConnection = DBUtil.getDBConnection();
        try {
            getApplicationDAO().addDeployment(dbcConnection, versionHashId, deployment, tenantId);
            dbcConnection.commit();
        } catch (AppCloudException e) {
            String msg = "Error while adding deployment for version with hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } catch (SQLException e) {
            String msg = "Error while committing transaction when adding deployment for version with hash id : " +
                         versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbcConnection);
        }

    }

    public static Deployment getDeployment(String versionHashId)throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            return getApplicationDAO().getDeployment(dbConnection, versionHashId, tenantId);
        } catch (AppCloudException e) {
            String msg = "Error while getting deployment for version with hash id: " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }

    public static void deleteDeployment(String versionHashId)throws AppCloudException {
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        Connection dbConnection = DBUtil.getDBConnection();
        try {
            getApplicationDAO().deleteDeployment(dbConnection, versionHashId, tenantId);
            dbConnection.commit();
        } catch (AppCloudException e) {
            String msg = "Error while deleting deployment for version with hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } catch (SQLException e) {
            String msg = "Error while committing transaction when deleting deployment for version with hash id : " +
                         versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }

    public static Transport[] getTransportsForRuntime (int runtimeId) throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        try {
            List<Transport> transports = getApplicationDAO().getTransportsForRuntime(dbConnection, runtimeId);
            return transports.toArray(new Transport[transports.size()]);
        } catch (AppCloudException e) {
            String msg = "Error while getting transports for runtime with id : " + runtimeId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }

    public static ApplicationRuntime getRuntimeById (int runtimeId) throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        try {
            return getApplicationDAO().getRuntimeById(dbConnection, runtimeId);
        } catch (AppCloudException e) {
            String msg = "Error while getting runtime by id for id : " + runtimeId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }
	
	public static int getApplicationCount() throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            return getApplicationDAO().getApplicationCount(dbConnection, tenantId);
        } catch (AppCloudException e) {
            String msg = "Error while getting application count";
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }
    /**
     * Get container service proxy by version hash id.
     *
     * @param versionHashId
     * @return
     * @throws AppCloudException
     */
    public static List<ContainerServiceProxy> getContainerServiceProxyByVersion(String versionHashId)
            throws AppCloudException {
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        Connection dbConnection = DBUtil.getDBConnection();
        try {
            return getApplicationDAO().getContainerServiceProxyByVersion(dbConnection, versionHashId, tenantId);
        } catch (AppCloudException e) {
            String msg = "Error while getting container service proxy with version hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }

    /**
     * Update container service proxy service by version hash id.
     *
     * @param versionHashId
     * @param host_url
     * @return
     * @throws AppCloudException
     */
    public static boolean updateContainerServiceProxyService(String versionHashId, String host_url)
            throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        boolean isUpdateSuccess = false;
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            isUpdateSuccess = getApplicationDAO().updateContainerServiceProxy(dbConnection, versionHashId, host_url, tenantId);
            dbConnection.commit();
        } catch (AppCloudException e) {
            String msg = "Error while updating the container service proxy with version hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } catch (SQLException e) {
            String msg = "Error while committing transaction for updating the container service proxy with version " +
                    "hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }

        return isUpdateSuccess;
    }

    /**
     * Update default version field with mapped version for custom url
     *
     * @param applicationHashId
     * @param defaultVersionName
     * @return
     * @throws AppCloudException
     */
    public static boolean updateDefaultVersion(String applicationHashId, String defaultVersionName)
            throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        boolean isUpdatedSuccess = false;
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            isUpdatedSuccess = getApplicationDAO().updateDefaultVersion(dbConnection, applicationHashId, defaultVersionName, tenantId);
            dbConnection.commit();
        } catch (AppCloudException e) {
            String msg = "Error while updating default version with application hash id : " + applicationHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } catch (SQLException e) {
            String message = "Error while committing transaction for updating default version with application hash " +
                    "id : " + applicationHashId;
            throw new AppCloudException(message, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }

        return isUpdatedSuccess;
    }

    public static Version[] getApplicationVersionsByRunningTimePeriod(int numberOfHours) throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        try {
            return getApplicationDAO().getApplicationVersionsByRunningTimePeriod(dbConnection, numberOfHours);
        } catch (AppCloudException e) {
            String msg = "Error while getting application version by running time period for " + numberOfHours +
                    " numberOfHours.";
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }

	public static int getMaxAppCountForWhiteListedTenants(int tenantID) throws AppCloudException {
		Connection dbConnection = DBUtil.getDBConnection();

		try {
			return getApplicationDAO().getWhiteListedTenantMaxAppCount(dbConnection, tenantID);
		} catch (AppCloudException e) {
            String msg = "Error while getting maximum application count for whitelisted tenant for tenant id: " +
                    tenantID;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
			DBUtil.closeConnection(dbConnection);
		}
	}

    public static List<Version> getAllVersionsOfApplication(String applicationHashId) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            return getApplicationDAO().getAllVersionsOfApplication(dbConnection, applicationHashId, tenantId);
        } catch (AppCloudException e) {
            String msg = "Error while getting versions list for application with hash id : " + applicationHashId;
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }

    }

	public static void whiteListApplicationVersion(String versionHashId) throws AppCloudException {
		Connection dbConnection = DBUtil.getDBConnection();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
		try {
			getApplicationDAO().whiteListApplicationVersion(dbConnection, versionHashId, tenantId);
			dbConnection.commit();
		} catch (AppCloudException e){
			String msg = "Error whitelisting application version hash id : " + versionHashId;
            log.error(msg, e);
			throw new AppCloudException(msg, e);
		} catch (SQLException e) {
			String msg = "Error while committing transaction for whitelisting application version hash id : " +
                    versionHashId;
            log.error(msg, e);
			throw new AppCloudException(msg, e);
		} finally {
			DBUtil.closeConnection(dbConnection);
		}
	}

    public static void whiteListTenant(int tenantId, int maxAppCount, int maxDatabaseCount) throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        try {
            getApplicationDAO().whiteListTenant(dbConnection, tenantId, maxAppCount, maxDatabaseCount);
            dbConnection.commit();
        } catch (AppCloudException e) {
            String msg = "Error whitelisting tenant for tenant id : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } catch (SQLException e) {
            String msg = "Error while committing transaction for whitelisting tenant for tenant id : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }

    public static boolean updateContainerSpecification(String versionHashId, int memory, int cpu)
            throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        boolean isUpdatedSuccess = false;
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            isUpdatedSuccess = getApplicationDAO().updateContainerSpecification(dbConnection, versionHashId, memory, cpu, tenantId);
            dbConnection.commit();
        } catch (AppCloudException e) {
            String msg = "Error while updating container specification with application hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } catch (SQLException e) {
            String message = "Error while committing transaction for updating container specification with " +
                    "application hash id : " + versionHashId;
            throw new AppCloudException(message, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }

        return isUpdatedSuccess;
    }

    public static int getMaxDatabaseCountForWhiteListedTenants(int tenantID) throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();

        try {
            return getApplicationDAO().getWhiteListedTenantMaxDatabaseCount(dbConnection, tenantID);
        } catch (AppCloudException e) {
            String msg = "Error while getting maximum database count for whitelisted tenant for tenant id: " + tenantID;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }

    public static void whiteListMaxDatabaseCount(int tenantId, int maxDatabaseCount) throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        try {
            getApplicationDAO().whiteListMaxDatabaseCount(dbConnection, tenantId, maxDatabaseCount);
            dbConnection.commit();
        } catch (AppCloudException e) {
            String msg = "Error whitelisting maximum database count for tenant id : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } catch (SQLException e) {
            String msg = "Error while committing transaction for whitelisting maximum database count for tenant id : "
                    + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }

    public static void whiteListMaxAppCount(int tenantId, int maxAppCount) throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        try {
            getApplicationDAO().whiteListMaxAppCount(dbConnection, tenantId, maxAppCount);
            dbConnection.commit();
        } catch (AppCloudException e) {
            String msg = "Error whitelisting maximum application count for tenant id : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } catch (SQLException e) {
            String msg = "Error while committing transaction for whitelisting maximum application count for " +
                    "tenant id : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeConnection(dbConnection);
        }
    }

    public static ApplicationDAO getApplicationDAO() {
        if (applicationDAO == null) {
            applicationDAO = new ApplicationDAO();
        }
        return applicationDAO;
    }

}
