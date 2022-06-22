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
package io.gravitee.gateway.jupiter.handlers.api.processor.cors;

import io.gravitee.definition.model.Cors;
import io.gravitee.gateway.jupiter.core.processor.Processor;
import java.util.regex.Pattern;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public abstract class AbstractCorsRequestProcessor implements Processor {

    static final String ALLOW_ORIGIN_PUBLIC_WILDCARD = "*";

    static final String JOINER_CHAR_SEQUENCE = ", ";

    boolean isOriginAllowed(final Cors cors, final String origin) {
        if (origin == null) {
            return false;
        }

        boolean allowed =
            cors.getAccessControlAllowOrigin().contains(ALLOW_ORIGIN_PUBLIC_WILDCARD) ||
            cors.getAccessControlAllowOrigin().contains(origin);

        if (allowed) {
            return true;
        } else if (cors.getAccessControlAllowOriginRegex() != null && !cors.getAccessControlAllowOriginRegex().isEmpty()) {
            for (Pattern pattern : cors.getAccessControlAllowOriginRegex()) {
                if (pattern.matcher(origin).matches()) {
                    return true;
                }
            }
        }
        return false;
    }
}
