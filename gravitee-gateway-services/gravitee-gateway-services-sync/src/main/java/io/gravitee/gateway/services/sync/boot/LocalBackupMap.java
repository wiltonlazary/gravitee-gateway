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

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapEvent;
import com.hazelcast.map.impl.MapListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
public class LocalBackupMap<K, V> {
    private final Logger logger = LoggerFactory.getLogger(LocalBackupMap.class);

    private final String name;
    private final String backupPath;
    private final IMap<K, V> map;
    private final AtomicBoolean stale;
    private String listenerId;

    public LocalBackupMap(String name, String backupPath, IMap<K, V> map) {
        this.name = name;
        this.backupPath = (backupPath + "/").replaceAll("//", "/");
        this.map = map;
        this.stale = new AtomicBoolean();
    }

    /**
     * Loads data from the file system and feed the map.
     */
    public void initialize(boolean loadFromStorage) {
        if (loadFromStorage) {
            loadFromStorage();
        }

        // Add listener to detect changes on the map.
        this.listenerId = map.addEntryListener(new MapListenerAdapter<K, V>() {
            @Override
            public void onEntryEvent(EntryEvent event) {
                stale.set(true);
            }
        }, false);
    }

    public void backup() {
        // Backup only if we detect that map content has changed.
        if (stale.getAndSet(false)) {
            try {
                File backupFile = new File(backupPath + "gio-" + name + ".ser");
                FileOutputStream fos = new FileOutputStream(backupFile);

                FileChannel channel = fos.getChannel();
                FileLock lock = channel.lock();

                ObjectOutputStream oos = new ObjectOutputStream(fos);

                oos.writeObject(new HashMap<>(map));
                oos.flush();
                lock.release();

                oos.close();
                fos.close();
            } catch (Exception e) {
                logger.error("An error occurred when trying to backup {} map to local storage.", name, e);
            }
        }
    }

    public void cleanup() {
        if (listenerId != null) {
            this.map.removeEntryListener(listenerId);
        }
    }

    private void loadFromStorage() {
        try {
            File backupFile = new File(backupPath + "gio-" + name + ".ser");

            if (!backupFile.exists()) {
                return;
            }

            FileInputStream fis = new FileInputStream(backupFile);
            ObjectInputStream ois = new ObjectInputStream(fis);

            Map<K, V> mapFromStorage = (Map<K, V>) ois.readObject();

            ois.close();
            fis.close();

            this.map.putAll(mapFromStorage);
        } catch (Exception e) {
            logger.error("An error occurred when trying to load {} map from local storage.", name, e);
        }
    }
}
