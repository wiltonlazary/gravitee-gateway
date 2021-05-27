package io.gravitee.gateway.standalone.websocket;

import io.gravitee.gateway.standalone.AbstractGatewayTest;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public abstract class AbstractWebSocketGatewayTest extends AbstractGatewayTest {

    static {
        System.setProperty("vertx.disableWebsockets", Boolean.FALSE.toString());
    }
}
