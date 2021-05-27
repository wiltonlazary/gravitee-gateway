package io.gravitee.gateway.standalone.container;

import io.gravitee.gateway.standalone.GatewayContainer;
import io.gravitee.node.container.NodeFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

public class GatewayTestContainer extends GatewayContainer {

    @Override
    protected List<Class<?>> annotatedClasses() {
        List<Class<?>> classes = super.annotatedClasses();
        classes.add(GatewayTestConfiguration.class);
        return classes;
    }

    @Configuration
    public static class GatewayTestConfiguration {

        @Bean
        public NodeFactory node() {
            return new NodeFactory(GatewayTestNode.class);
        }
    }
}
