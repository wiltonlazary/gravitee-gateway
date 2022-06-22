/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.rest.api.service;

import io.gravitee.rest.api.model.InstallationEntity;
import io.gravitee.rest.api.model.InstallationStatus;
import java.util.Map;

/**
 * @author Florent CHAMFROY (florent.chamfroy at graviteesource.com)
 * @author GraviteeSource Team
 */
public interface InstallationService {
    String COCKPIT_INSTALLATION_ID = "COCKPIT_INSTALLATION_ID";
    String COCKPIT_INSTALLATION_STATUS = "COCKPIT_INSTALLATION_STATUS";
    String PLANS_DATA_UPGRADER_STATUS = "PLANS_DATA_UPGRADER_V2_STATUS";
    String APPLICATION_API_KEY_MODE_UPGRADER_STATUS = "API_KEY_MODE_UPGRADER_STATUS";
    String API_KEY_SUBSCRIPTIONS_UPGRADER_STATUS = "API_KEY_SUBSCRIPTIONS_UPGRADER_STATUS";
    String ORPHAN_CATEGORY_UPGRADER_STATUS = "ORPHAN_CATEGORY_UPGRADER_STATUS";
    String API_LOGGING_CONDITION_UPGRADER = "API_LOGGING_CONDITION_UPGRADER";
    String DEFAULT_ROLES_UPGRADER_STATUS = "DEFAULT_ROLES_UPGRADER_STATUS";
    String ALERTS_ENVIRONMENT_UPGRADER = "ALERTS_ENVIRONMENT_UPGRADER";
    String COMMAND_ORGANIZATION_UPGRADER = "COMMAND_ORGANIZATION_UPGRADER";
    String PLANS_FLOWS_UPGRADER_STATUS = "PLANS_FLOWS_UPGRADER_STATUS";

    /**
     * Get the current installation.
     *
     * @return the current installation or an {@link io.gravitee.rest.api.service.exceptions.InstallationNotFoundException} exception.
     */
    InstallationEntity get();

    /**
     * Get or initialize the installation. A new installation will be created only if none exists.
     *
     * @return the created or already existing installation.
     */
    InstallationEntity getOrInitialize();

    /**
     * Set additional information of the current installation.
     *
     * @param additionalInformation the list of additional information to set on the existing installation.
     *
     * @return the updated installation
     */
    InstallationEntity setAdditionalInformation(Map<String, String> additionalInformation);

    /**
     * Get installation status, regarding cockpit.
     *
     * @return One of these values: PENDING, ACCEPTED, REJECTED, DELETED
     */
    InstallationStatus getInstallationStatus();
}
