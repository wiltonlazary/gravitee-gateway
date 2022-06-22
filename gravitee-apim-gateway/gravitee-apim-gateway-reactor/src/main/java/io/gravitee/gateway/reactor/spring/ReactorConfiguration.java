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
package io.gravitee.gateway.reactor.spring;

import static io.gravitee.gateway.jupiter.reactor.processor.transaction.TransactionProcessorFactory.DEFAULT_REQUEST_ID_HEADER;
import static io.gravitee.gateway.jupiter.reactor.processor.transaction.TransactionProcessorFactory.DEFAULT_TRANSACTION_ID_HEADER;

import io.gravitee.common.event.EventManager;
import io.gravitee.common.http.IdGenerator;
import io.gravitee.common.utils.Hex;
import io.gravitee.common.utils.UUID;
import io.gravitee.gateway.core.component.ComponentProvider;
import io.gravitee.gateway.env.GatewayConfiguration;
import io.gravitee.gateway.jupiter.reactor.DefaultHttpRequestDispatcher;
import io.gravitee.gateway.jupiter.reactor.HttpRequestDispatcher;
import io.gravitee.gateway.jupiter.reactor.handler.DefaultEntrypointResolver;
import io.gravitee.gateway.jupiter.reactor.handler.EntrypointResolver;
import io.gravitee.gateway.jupiter.reactor.processor.NotFoundProcessorChainFactory;
import io.gravitee.gateway.jupiter.reactor.processor.PlatformProcessorChainFactory;
import io.gravitee.gateway.reactor.Reactor;
import io.gravitee.gateway.reactor.handler.ReactorHandlerFactory;
import io.gravitee.gateway.reactor.handler.ReactorHandlerFactoryManager;
import io.gravitee.gateway.reactor.handler.ReactorHandlerRegistry;
import io.gravitee.gateway.reactor.handler.context.provider.NodeTemplateVariableProvider;
import io.gravitee.gateway.reactor.handler.impl.DefaultReactorHandlerRegistry;
import io.gravitee.gateway.reactor.impl.DefaultReactor;
import io.gravitee.gateway.reactor.processor.RequestProcessorChainFactory;
import io.gravitee.gateway.reactor.processor.ResponseProcessorChainFactory;
import io.gravitee.gateway.reactor.processor.transaction.TraceContextProcessorFactory;
import io.gravitee.gateway.reactor.processor.transaction.TransactionProcessorFactory;
import io.gravitee.gateway.report.ReporterService;
import io.gravitee.node.api.Node;
import io.gravitee.plugin.alert.AlertEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
@Configuration
public class ReactorConfiguration {

    private static final String HEX_FORMAT = "hex";

    //    @Autowired
    //    protected io.gravitee.gateway.reactor.handler.EntrypointResolver entrypointResolver;
    //
    //    @Autowired
    //    @Qualifier("v3RequestProcessorChainFactory")
    //    private RequestProcessorChainFactory requestProcessorChainFactory;
    //
    //    @Autowired
    //    @Qualifier("v3ResponseProcessorChainFactory")
    //    private ResponseProcessorChainFactory responseProcessorChainFactory;

    @Bean
    public Reactor v3Reactor(
        io.gravitee.gateway.reactor.handler.EntrypointResolver entrypointResolver,
        @Qualifier("v3RequestProcessorChainFactory") RequestProcessorChainFactory requestProcessorChainFactory,
        @Qualifier("v3ResponseProcessorChainFactory") ResponseProcessorChainFactory responseProcessorChainFactory
    ) {
        // DefaultReactor bean must be kept while we are still supporting v3 execution mode.
        return new DefaultReactor(entrypointResolver, requestProcessorChainFactory, responseProcessorChainFactory);
    }

    @Bean
    public io.gravitee.gateway.reactor.handler.EntrypointResolver v3EntrypointResolver(ReactorHandlerRegistry reactorHandlerRegistry) {
        // V3 EntrypointResolver bean must be kept while we are still supporting v3 execution mode.
        return new io.gravitee.gateway.reactor.handler.impl.DefaultEntrypointResolver(reactorHandlerRegistry);
    }

    @Bean
    public IdGenerator idGenerator(@Value("${handlers.request.format:uuid}") String requestFormat) {
        if (HEX_FORMAT.equals(requestFormat)) {
            return new Hex();
        } else {
            return new UUID();
        }
    }

    @Bean
    public io.gravitee.gateway.jupiter.reactor.processor.transaction.TransactionProcessorFactory transactionHandlerFactory(
        @Value("${handlers.request.transaction.header:" + DEFAULT_TRANSACTION_ID_HEADER + "}") String transactionHeader,
        @Value("${handlers.request.request.header:" + DEFAULT_REQUEST_ID_HEADER + "}") String requestHeader
    ) {
        return new io.gravitee.gateway.jupiter.reactor.processor.transaction.TransactionProcessorFactory(transactionHeader, requestHeader);
    }

    @Bean
    public PlatformProcessorChainFactory globalProcessorChainFactory(
        io.gravitee.gateway.jupiter.reactor.processor.transaction.TransactionProcessorFactory transactionHandlerFactory,
        @Value("${handlers.request.trace-context.enabled:false}") boolean traceContext,
        ReporterService reporterService,
        AlertEventProducer eventProducer,
        Node node,
        @Value("${http.port:8082}") String httpPort,
        @Value("${services.tracing.enabled:false}") boolean tracing
    ) {
        return new PlatformProcessorChainFactory(
            transactionHandlerFactory,
            traceContext,
            reporterService,
            eventProducer,
            node,
            httpPort,
            tracing
        );
    }

    @Bean
    public HttpRequestDispatcher httpRequestDispatcher(
        EventManager eventManager,
        GatewayConfiguration gatewayConfiguration,
        ReactorHandlerRegistry reactorHandlerRegistry,
        EntrypointResolver entrypointResolver,
        IdGenerator idGenerator,
        ComponentProvider globalComponentProvider,
        RequestProcessorChainFactory v3RequestProcessorChainFactory,
        ResponseProcessorChainFactory v3ResponseProcessorChainFactory,
        PlatformProcessorChainFactory platformProcessorChainFactory,
        NotFoundProcessorChainFactory notFoundProcessorChainFactory,
        @Value("${services.tracing.enabled:false}") boolean tracingEnabled
    ) {
        return new DefaultHttpRequestDispatcher(
            eventManager,
            gatewayConfiguration,
            reactorHandlerRegistry,
            entrypointResolver,
            idGenerator,
            globalComponentProvider,
            v3RequestProcessorChainFactory,
            v3ResponseProcessorChainFactory,
            platformProcessorChainFactory,
            notFoundProcessorChainFactory,
            tracingEnabled
        );
    }

    @Bean
    public EntrypointResolver entrypointResolver(ReactorHandlerRegistry reactorHandlerRegistry) {
        return new DefaultEntrypointResolver(reactorHandlerRegistry);
    }

    @Bean
    public ReactorHandlerRegistry reactorHandlerManager(ReactorHandlerFactoryManager reactorHandlerFactoryManager) {
        return new DefaultReactorHandlerRegistry(reactorHandlerFactoryManager);
    }

    @Bean
    public ReactorHandlerFactoryManager reactorHandlerFactoryManager(ReactorHandlerFactory reactorHandlerFactory) {
        return new ReactorHandlerFactoryManager(reactorHandlerFactory);
    }

    @Bean
    public TransactionProcessorFactory v3TransactionHandlerFactory() {
        return new io.gravitee.gateway.reactor.processor.transaction.TransactionProcessorFactory();
    }

    @Bean
    public TraceContextProcessorFactory v3TraceContextProcessorFactory() {
        return new TraceContextProcessorFactory();
    }

    @Bean
    public io.gravitee.gateway.reactor.processor.RequestProcessorChainFactory v3RequestProcessorChainFactory() {
        return new io.gravitee.gateway.reactor.processor.RequestProcessorChainFactory();
    }

    @Bean
    public io.gravitee.gateway.reactor.processor.ResponseProcessorChainFactory v3ResponseProcessorChainFactory() {
        return new io.gravitee.gateway.reactor.processor.ResponseProcessorChainFactory();
    }

    @Bean
    public io.gravitee.gateway.reactor.processor.NotFoundProcessorChainFactory v3NotFoundProcessorChainFactory() {
        return new io.gravitee.gateway.reactor.processor.NotFoundProcessorChainFactory();
    }

    @Bean
    public NotFoundProcessorChainFactory notFoundProcessorChainFactory(
        Environment environment,
        ReporterService reporterService,
        @Value("${handlers.notfound.log.enabled:false}") boolean logEnabled,
        @Value("${services.tracing.enabled:false}") boolean tracingEnabled
    ) {
        return new NotFoundProcessorChainFactory(environment, reporterService, logEnabled, tracingEnabled);
    }

    @Bean
    public NodeTemplateVariableProvider nodeTemplateVariableProvider(Node node, GatewayConfiguration gatewayConfiguration) {
        return new NodeTemplateVariableProvider(node, gatewayConfiguration);
    }
}
