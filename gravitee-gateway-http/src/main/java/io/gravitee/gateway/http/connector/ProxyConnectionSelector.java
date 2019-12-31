package io.gravitee.gateway.http.connector;

import io.gravitee.common.http.HttpHeaders;
import io.gravitee.common.http.HttpMethod;
import io.gravitee.definition.model.EndpointType;
import io.gravitee.definition.model.endpoint.HttpEndpoint;
import io.gravitee.gateway.api.proxy.ProxyRequest;
import io.gravitee.gateway.http.connector.grpc.GrpcProxyConnection;
import io.gravitee.gateway.http.connector.http.Http2ProxyConnection;
import io.gravitee.gateway.http.connector.http.HttpProxyConnection;
import io.gravitee.gateway.http.connector.ws.WebSocketProxyConnection;
import io.netty.handler.codec.http.HttpHeaderValues;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class ProxyConnectionSelector {

    public static AbstractProxyConnection select(HttpEndpoint endpoint, ProxyRequest request) {
        if (endpoint.getType() == EndpointType.HTTP) {
            String connectionHeader = request.headers().getFirst(HttpHeaders.CONNECTION);
            String upgradeHeader = request.headers().getFirst(HttpHeaders.UPGRADE);

            boolean websocket = request.method() == HttpMethod.GET &&
                    HttpHeaderValues.UPGRADE.contentEqualsIgnoreCase(connectionHeader) &&
                    HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase(upgradeHeader);

            return websocket ?
                    new WebSocketProxyConnection(endpoint, request) :
                    new HttpProxyConnection(endpoint, request);
        } else if (endpoint.getType() == EndpointType.HTTP2) {
            return new Http2ProxyConnection(endpoint, request);
        } else if (endpoint.getType() == EndpointType.GRPC
                && request.method() == HttpMethod.POST) {
            return new GrpcProxyConnection(endpoint, request);
        }

        return new HttpProxyConnection(endpoint, request);
    }
}
