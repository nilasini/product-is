/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.wso2.carbon.is.migration.service.v530.migrator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.core.migrate.MigrationClientException;
import org.wso2.carbon.is.migration.service.Migrator;
import org.wso2.carbon.is.migration.service.v530.RegistryDataManager;

/**
 * Migrator implementation for email template.
 */
public class EmailTemplateDataMigrator extends Migrator{


    private static final Log log = LogFactory.getLog(EmailTemplateDataMigrator.class);

    @Override
    public void migrate() throws MigrationClientException {
        migrateEmailTemplateData();
    }



    public void migrateEmailTemplateData()  {
        RegistryDataManager registryDataManager = RegistryDataManager.getInstance();
        try {
            registryDataManager.migrateEmailTemplates(isIgnoreForInactiveTenants());
        } catch (Exception e) {
            log.error("Error while migrating email templates", e);
        }
    }
}
