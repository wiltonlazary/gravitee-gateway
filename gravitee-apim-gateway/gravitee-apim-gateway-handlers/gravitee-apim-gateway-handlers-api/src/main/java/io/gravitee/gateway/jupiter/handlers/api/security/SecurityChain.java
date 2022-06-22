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
package io.gravitee.gateway.jupiter.handlers.api.security;

import static io.gravitee.common.http.HttpStatusCode.UNAUTHORIZED_401;
import static io.reactivex.Completable.defer;

import io.gravitee.gateway.handlers.api.definition.Api;
import io.gravitee.gateway.jupiter.api.ExecutionFailure;
import io.gravitee.gateway.jupiter.api.context.RequestExecutionContext;
import io.gravitee.gateway.jupiter.handlers.api.security.handler.SecurityPlan;
import io.gravitee.gateway.jupiter.handlers.api.security.handler.SecurityPlanFactory;
import io.gravitee.gateway.jupiter.policy.PolicyManager;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link SecurityChain} is a special chain dedicated to execute policy associated with plans.
 * The security chain is responsible to create {@link SecurityPlan} for each plan of the api and executed them in order.
 * Only the first {@link SecurityPlan} that can handle the current request is executed.
 * The result of the security chain execution depends on this {@link SecurityPlan} execution.
 *
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
public class SecurityChain {

    public static final String SKIP_SECURITY_CHAIN = "skip-security-chain";
    protected static final String PLAN_UNRESOLVABLE = "GATEWAY_PLAN_UNRESOLVABLE";
    protected static final String UNAUTHORIZED_MESSAGE = "Unauthorized";
    protected static final Single<Boolean> TRUE = Single.just(true), FALSE = Single.just(false);
    private static final Logger log = LoggerFactory.getLogger(SecurityChain.class);
    private final Flowable<SecurityPlan> chain;

    public SecurityChain(Api api, PolicyManager policyManager) {
        chain =
            Flowable.fromIterable(
                api
                    .getPlans()
                    .stream()
                    .map(plan -> SecurityPlanFactory.forPlan(plan, policyManager))
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingInt(SecurityPlan::order))
                    .collect(Collectors.toList())
            );
    }

    /**
     * Executes the security chain by executing all the {@link SecurityPlan}s in an ordered sequence.
     * It's up to each {@link SecurityPlan} to provide its order. The lower is the order, the highest priority is.
     * The result of the security chain execution depends on the first {@link SecurityPlan} able to execute the request.
     * If no {@link SecurityPlan} has been executed because there is no {@link SecurityPlan} in the chain or none of them can execute the request,
     * then the chain is interrupted with a 401 response status and the {@link Completable} returns an error.
     *
     * @param ctx the current execution context.
     * @return a {@link Completable} that completes if the request has been successfully handled by a {@link SecurityPlan} or returns
     * an error if no {@link SecurityPlan} can execute the request or the {@link SecurityPlan} failed.
     */
    public Completable execute(RequestExecutionContext ctx) {
        return defer(
            () -> {
                if (!Objects.equals(true, ctx.getAttribute(SKIP_SECURITY_CHAIN))) {
                    return chain
                        .flatMapSingle(policy -> continueChain(ctx, policy), false, 1)
                        .any(Boolean::booleanValue)
                        .flatMapCompletable(
                            securityHandled -> {
                                if (!securityHandled) {
                                    return ctx.interruptWith(
                                        new ExecutionFailure(UNAUTHORIZED_401).key(PLAN_UNRESOLVABLE).message(UNAUTHORIZED_MESSAGE)
                                    );
                                }
                                return Completable.complete();
                            }
                        )
                        .doOnSubscribe(disposable -> log.debug("Executing security chain"));
                }

                log.debug("Skipping security chain because it has been explicitly required");
                return Completable.complete();
            }
        );
    }

    private Single<Boolean> continueChain(RequestExecutionContext ctx, SecurityPlan securityPlan) {
        return securityPlan
            .canExecute(ctx)
            .flatMap(
                canExecute -> {
                    if (canExecute) {
                        return securityPlan.execute(ctx).andThen(TRUE);
                    }
                    return FALSE;
                }
            );
    }
}
