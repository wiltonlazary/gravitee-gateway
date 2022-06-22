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
package io.gravitee.gateway.jupiter.reactor.handler.message;

import io.gravitee.gateway.jupiter.api.message.Message;
import io.gravitee.gateway.jupiter.api.message.MessageFlow;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
public class DefaultMessageFlow implements MessageFlow {

    private Flowable<Message> flow;

    public DefaultMessageFlow(final Flowable<Message> flow) {
        this.flow = flow;
    }

    public Completable flow(final Flowable<Message> messageFlow) {
        flow = messageFlow;
        return Completable.complete();
    }

    public Completable onMessage(final FlowableTransformer<Message, Message> messagesTransformer) {
        return flow(flow.compose(messagesTransformer));
    }

    public Completable consume() {
        return Completable.defer(() -> flow.ignoreElements());
    }
}
