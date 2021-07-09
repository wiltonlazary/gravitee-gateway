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
package io.gravitee.gateway.services.sync.boot;

import com.hazelcast.core.IMap;
import com.hazelcast.core.MapEvent;
import com.hazelcast.map.impl.MapListenerAdapter;
import io.gravitee.common.service.AbstractService;
import io.gravitee.gateway.dictionary.DictionaryManager;
import io.gravitee.gateway.dictionary.model.Dictionary;
import io.gravitee.gateway.handlers.api.definition.Api;
import io.gravitee.gateway.handlers.api.manager.ApiManager;
import io.gravitee.node.api.cluster.ClusterManager;
import io.gravitee.repository.management.model.ApiKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.CompletableFuture.runAsync;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
public class LocalBootstrapService extends AbstractService<LocalBootstrapService> {

    private final Logger logger = LoggerFactory.getLogger(LocalBootstrapService.class);

    @Value("${services.sync.localBootstrap.path:${gravitee.home}/data}")
    protected String bootstrapPath;

    @Autowired
    @Qualifier("apiMap")
    private IMap<String, Api> apis;

    @Autowired
    @Qualifier("apiKeyMap")
    private IMap<String, ApiKey> apiKeys;

    @Autowired
    @Qualifier("dictionaryMap")
    private IMap<String, Dictionary> dictionaries;

    @Autowired
    @Qualifier("subscriptionMap")
    // /!\ Note: subscription map contains Object not Subscription.
    private IMap<String, Object> subscriptions;

    @Autowired
    private ApiManager apiManager;

    @Autowired
    private DictionaryManager dictionaryManager;

    @Autowired
    @Qualifier("syncExecutor")
    private ThreadPoolExecutor executorService;

    @Autowired
    private ClusterManager clusterManager;

    LocalBackupMap<String, Api> backupApis;
    LocalBackupMap<String, ApiKey> backupApiKeys;
    LocalBackupMap<String, Dictionary> backupDictionaries;
    LocalBackupMap<String, Object> backupSubscriptions;

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        // Load from local storage only if current instance is master. Other instances will retrieve all data from the cluster itself.
        initializeBackups();

        List<CompletableFuture<?>> futures = new ArrayList<>();
        apis.forEach((s, api) -> runAsync(() -> apiManager.register(api), executorService));
        dictionaries.forEach((s, dictionary) -> runAsync(() -> dictionaryManager.deploy(dictionary), executorService));

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
    }

    private void initializeBackups() {

        try {
            backupApis = new LocalBackupMap<>("apis", bootstrapPath, apis);
            backupApiKeys = new LocalBackupMap<>("api-keys", bootstrapPath, apiKeys);
            backupDictionaries = new LocalBackupMap<>("dictionaries", bootstrapPath, dictionaries);
            backupSubscriptions = new LocalBackupMap<>("subscriptions", bootstrapPath, subscriptions);

            CompletableFuture.allOf(runAsync(() -> backupApis.initialize(clusterManager.isMasterNode())),
                    runAsync(() -> backupApiKeys.initialize(clusterManager.isMasterNode())),
                    runAsync(() -> backupDictionaries.initialize(clusterManager.isMasterNode())),
                    runAsync(() -> backupSubscriptions.initialize(clusterManager.isMasterNode()))).get();
        } catch (Exception e) {
            logger.error("An error occurred during initialization for local bootstrap feature.", e);
        }
    }

    @Override
    protected void doStop() throws Exception {
        // Run a last backup before stopping.
        backup();

        backupApis.cleanup();
        backupApiKeys.cleanup();
        backupDictionaries.cleanup();
        backupSubscriptions.cleanup();

        super.doStop();
    }

    public void backup() {

        try {
            CompletableFuture.allOf(runAsync(() -> backupApis.backup()),
                    runAsync(() -> backupApiKeys.backup()),
                    runAsync(() -> backupSubscriptions.backup()),
                    runAsync(() -> backupDictionaries.backup())).get();
        } catch (Exception e) {
            logger.error("An error occurred during backup for local bootstrap feature.", e);
        }
    }
}
