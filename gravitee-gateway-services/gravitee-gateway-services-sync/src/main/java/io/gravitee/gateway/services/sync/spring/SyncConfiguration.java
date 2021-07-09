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
package io.gravitee.gateway.services.sync.spring;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import io.gravitee.gateway.dictionary.model.Dictionary;
import io.gravitee.gateway.handlers.api.definition.Api;
import io.gravitee.gateway.services.sync.SyncManager;
import io.gravitee.gateway.services.sync.boot.LocalBootstrapService;
import io.gravitee.gateway.services.sync.cache.configuration.LocalCacheConfiguration;
import io.gravitee.gateway.services.sync.synchronizer.ApiSynchronizer;
import io.gravitee.gateway.services.sync.synchronizer.DictionarySynchronizer;
import io.gravitee.repository.management.model.ApiKey;
import io.reactivex.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author Titouan COMPIEGNE (titouan.compiegne at graviteesource.com)
 * @author GraviteeSource Team
 */
@Configuration
@Import({
        LocalCacheConfiguration.class
})
public class SyncConfiguration {

    public static final int PARALLELISM = Runtime.getRuntime().availableProcessors() * 2;

    @Bean
    public SyncManager syncManager() {
        return new SyncManager();
    }

    @Bean("syncExecutor")
    public ThreadPoolExecutor syncExecutor() {
        final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(PARALLELISM, PARALLELISM, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactory() {
                    private int counter = 0;

                    @Override
                    public Thread newThread(@NonNull Runnable r) {
                        return new Thread(r, "gio.sync-" + counter++);
                    }
                });

        threadPoolExecutor.allowCoreThreadTimeOut(true);

        return threadPoolExecutor;
    }

    @Bean
    public ApiSynchronizer apiSynchronizer() {
        return new ApiSynchronizer();
    }

    @Bean
    public DictionarySynchronizer dictionarySynchronizer() {
        return new DictionarySynchronizer();
    }

    @Bean("apiMap")
    public IMap<String, Api> apiMap(HazelcastInstance hzInstance) {
        return hzInstance.getMap("apis");
    }

    @Bean("apiKeyMap")
    public IMap<String, ApiKey> apiKeyMap(HazelcastInstance hzInstance) {
        return hzInstance.getMap("apikeys");
    }

    @Bean("subscriptionMap")
    public IMap<String, Object> subscriptionMap(HazelcastInstance hzInstance) {
        return hzInstance.getMap("subscriptions");
    }

    @Bean("dictionaryMap")
    public IMap<String, Dictionary> dictionaryMap(HazelcastInstance hzInstance) {
        return hzInstance.getMap("dictionaries");
    }

    @Bean
    public LocalBootstrapService localBootstrapService() {
        return new LocalBootstrapService();
    }
}
