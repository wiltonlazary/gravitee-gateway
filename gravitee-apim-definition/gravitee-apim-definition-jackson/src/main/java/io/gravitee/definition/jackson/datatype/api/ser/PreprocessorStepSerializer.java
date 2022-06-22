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
package io.gravitee.definition.jackson.datatype.api.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import io.gravitee.definition.model.debug.PreprocessorStep;
import java.io.IOException;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
public class PreprocessorStepSerializer extends StdScalarSerializer<PreprocessorStep> {

    public PreprocessorStepSerializer(Class<PreprocessorStep> t) {
        super(t);
    }

    @Override
    public void serialize(PreprocessorStep preprocessorStep, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        if (preprocessorStep.getAttributes() != null) {
            jgen.writeObjectField("attributes", preprocessorStep.getAttributes());
        }

        if (preprocessorStep.getHeaders() != null) {
            jgen.writeObjectField("headers", preprocessorStep.getHeaders());
        }
        jgen.writeEndObject();
    }
}
