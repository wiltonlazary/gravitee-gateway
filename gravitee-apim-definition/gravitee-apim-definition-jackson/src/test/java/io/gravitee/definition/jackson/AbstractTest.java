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
package io.gravitee.definition.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gravitee.definition.jackson.datatype.GraviteeMapper;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public abstract class AbstractTest {

    protected JsonNode loadJson(String resource) throws IOException {
        return objectMapper().readTree(read(resource));
    }

    protected <T> T load(String resource, Class<T> type) throws IOException {
        return objectMapper().readValue(read(resource), type);
    }

    protected InputStream read(String resource) throws IOException {
        return this.getClass().getResourceAsStream(resource);
    }

    protected ObjectMapper objectMapper() {
        return new GraviteeMapper();
    }
}
