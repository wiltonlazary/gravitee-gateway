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
package io.gravitee.gateway.jupiter.reactor.processor;

import io.gravitee.gateway.jupiter.api.hook.ProcessorHook;
import io.gravitee.gateway.jupiter.core.processor.Processor;
import io.gravitee.gateway.jupiter.core.processor.ProcessorChain;
import io.gravitee.gateway.jupiter.core.tracing.TracingHook;
import io.gravitee.gateway.jupiter.reactor.processor.notfound.NotFoundProcessor;
import io.gravitee.gateway.jupiter.reactor.processor.notfound.NotFoundReporterProcessor;
import io.gravitee.gateway.jupiter.reactor.processor.responsetime.ResponseTimeProcessor;
import io.gravitee.gateway.report.ReporterService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.env.Environment;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
public class NotFoundProcessorChainFactory {

    private final Environment environment;
    private final ReporterService reporterService;
    private final boolean logEnabled;
    private final List<ProcessorHook> processorHooks = new ArrayList<>();
    private ProcessorChain processorChain;

    public NotFoundProcessorChainFactory(
        final Environment environment,
        final ReporterService reporterService,
        boolean logEnabled,
        boolean tracing
    ) {
        this.environment = environment;
        this.reporterService = reporterService;
        this.logEnabled = logEnabled;
        if (tracing) {
            processorHooks.add(new TracingHook("processor"));
        }
    }

    public ProcessorChain processorChain() {
        if (processorChain == null) {
            initProcessorChain();
        }
        return processorChain;
    }

    private void initProcessorChain() {
        List<Processor> processorList = new ArrayList<>();

        processorList.add(new NotFoundProcessor(environment));
        processorList.add(new ResponseTimeProcessor());
        processorList.add(new NotFoundReporterProcessor(reporterService, logEnabled));

        processorChain = new ProcessorChain("processor-chain-not-found", processorList);
        processorChain.addHooks(processorHooks);
    }
}
