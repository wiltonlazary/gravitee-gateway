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
package io.gravitee.definition.jackson.datatype.api.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import io.gravitee.definition.model.debug.PreprocessorStep;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
public class PreprocessorStepDeserializer extends StdScalarDeserializer<PreprocessorStep> {

    public PreprocessorStepDeserializer(Class<PreprocessorStep> vc) {
        super(vc);
    }

    @Override
    public PreprocessorStep deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        PreprocessorStep preprocessorStep = new PreprocessorStep();

        JsonNode initialAttributesNode = node.get("attributes");
        if (initialAttributesNode != null) {
            preprocessorStep.setAttributes(
                (initialAttributesNode.traverse(jp.getCodec()).readValueAs(new TypeReference<Map<String, Object>>() {}))
            );
        }

        JsonNode initialHeadersNode = node.get("headers");
        if (initialHeadersNode != null && !initialHeadersNode.isEmpty()) {
            preprocessorStep.setHeaders(
                (initialHeadersNode.traverse(jp.getCodec()).readValueAs(new TypeReference<Map<String, List<String>>>() {}))
            );
        }

        return preprocessorStep;
    }
}
