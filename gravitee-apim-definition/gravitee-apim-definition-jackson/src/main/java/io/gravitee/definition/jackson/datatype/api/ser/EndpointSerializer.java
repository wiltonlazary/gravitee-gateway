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
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import io.gravitee.definition.model.Endpoint;
import java.io.IOException;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class EndpointSerializer extends StdScalarSerializer<Endpoint> {

    public EndpointSerializer(Class<Endpoint> t) {
        super(t);
    }

    @Override
    public void serialize(Endpoint endpoint, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        doSerialize(endpoint, jgen, provider);
        jgen.writeEndObject();
    }

    @Override
    public void serializeWithType(Endpoint value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        serialize(value, g, provider);
    }

    protected void doSerialize(Endpoint endpoint, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
        jgen.writeStringField("name", endpoint.getName());
        jgen.writeStringField("target", endpoint.getTarget());
        jgen.writeNumberField("weight", endpoint.getWeight());
        jgen.writeBooleanField("backup", endpoint.isBackup());
        jgen.writeStringField("type", endpoint.getType());
        if (endpoint.getInherit() != null) {
            jgen.writeObjectField("inherit", endpoint.getInherit());
        }
        if (endpoint.getHealthCheck() != null) {
            jgen.writeObjectField("healthcheck", endpoint.getHealthCheck());
        }

        if (endpoint.getTenants() != null && !endpoint.getTenants().isEmpty()) {
            jgen.writeArrayFieldStart("tenants");
            endpoint
                .getTenants()
                .forEach(
                    tenant -> {
                        try {
                            jgen.writeString(tenant);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                );

            jgen.writeEndArray();
        }
    }
}
