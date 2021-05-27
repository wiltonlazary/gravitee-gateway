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
