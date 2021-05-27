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
package io.gravitee.gateway.standalone.container;

import io.gravitee.common.component.LifecycleComponent;
import io.gravitee.gateway.report.impl.NodeMonitoringReporterService;
import io.gravitee.gateway.standalone.node.GatewayNode;
import io.gravitee.node.management.http.ManagementService;
import io.gravitee.node.monitoring.healthcheck.NodeHealthCheckService;
import io.gravitee.node.monitoring.infos.NodeInfosService;
import io.gravitee.node.monitoring.monitor.NodeMonitorService;
import io.gravitee.node.plugins.service.ServiceManager;
import io.gravitee.node.reporter.ReporterManager;
import io.gravitee.plugin.alert.AlertEventProducerManager;

import java.util.List;

public class GatewayTestNode extends GatewayNode {

    @Override
    public List<Class<? extends LifecycleComponent>> components() {
        List<Class<? extends LifecycleComponent>> components = super.components();

        components.remove(AlertEventProducerManager.class);
        components.remove(ServiceManager.class);
        components.remove(ManagementService.class);
        components.remove(ReporterManager.class);
        components.remove(NodeMonitoringReporterService.class);
        components.remove(NodeHealthCheckService.class);
        components.remove(NodeInfosService.class);
        components.remove(NodeMonitorService.class);

        return components;
    }
}
