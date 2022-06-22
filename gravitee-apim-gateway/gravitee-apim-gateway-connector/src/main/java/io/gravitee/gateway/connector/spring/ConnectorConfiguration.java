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
package io.gravitee.gateway.connector.spring;

import io.gravitee.gateway.connector.ConnectorRegistry;
import io.gravitee.gateway.connector.plugin.ConnectorRegistryImpl;
import io.gravitee.plugin.connector.ConnectorPluginManager;
import io.gravitee.plugin.connector.spring.ConnectorPluginConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
@Configuration
@Import(ConnectorPluginConfiguration.class)
public class ConnectorConfiguration {

    @Bean
    public ConnectorRegistry connectorRegistry(final ConnectorPluginManager connectorPluginManager) {
        return new ConnectorRegistryImpl(connectorPluginManager);
    }
}
