package io.gravitee.gateway.http.connector.grpc;

import io.gravitee.common.http.HttpStatusCode;
import io.gravitee.common.util.Version;
import io.gravitee.definition.model.endpoint.HttpEndpoint;
import io.gravitee.gateway.api.proxy.ProxyRequest;
import io.gravitee.gateway.core.proxy.EmptyProxyResponse;
import io.gravitee.gateway.http.connector.http.Http2ProxyConnection;
import io.netty.channel.ConnectTimeoutException;
import io.vertx.core.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

public class GrpcProxyConnection extends Http2ProxyConnection {

    private final Logger LOGGER = LoggerFactory.getLogger(GrpcProxyConnection.class);

    private static final String GRPC_CONTENT_TYPE = "application/grpc";
    private static final String GRPC_TRAILERS_TE = "trailers";
    private static final String GRPC_USER_AGENT = "grpc-gio-gateway/" + Version.RUNTIME_VERSION;

    public GrpcProxyConnection(HttpEndpoint endpoint, ProxyRequest proxyRequest) {
        super(endpoint, proxyRequest);
    }

    @Override
    protected HttpClientRequest doRequest(HttpClient httpClient, int port, String host, String uri) {
        HttpClientRequest clientRequest = httpClient.request(HttpMethod.POST, port, host, uri);

        clientRequest.setTimeout(endpoint.getHttpClientOptions().getReadTimeout());

        // Always set chunked mode for gRPC transport
        clientRequest.setChunked(true);

        clientRequest.headers().set(HttpHeaders.CONTENT_TYPE, GRPC_CONTENT_TYPE);
        clientRequest.headers().set(HttpHeaders.USER_AGENT, GRPC_USER_AGENT);
        clientRequest.headers().set(io.gravitee.common.http.HttpHeaders.TE, GRPC_TRAILERS_TE);

        clientRequest.handler(new io.vertx.core.Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse response) {
                handleUpstreamResponse(response);
                sendToClient(proxyResponse);
            }
        });

        clientRequest.connectionHandler(connection -> {
            connection.exceptionHandler(ex -> {
                // I don't want to fill my logs with error
            });
        });

        clientRequest.exceptionHandler(event -> {
            if (!isCanceled() && !isTransmitted()) {
                proxyRequest.metrics().setMessage(event.getMessage());

                if (this.timeoutHandler() != null
                        && (event instanceof ConnectException ||
                        event instanceof TimeoutException ||
                        event instanceof NoRouteToHostException ||
                        event instanceof UnknownHostException)) {
                    handleConnectTimeout(event);
                } else {
                    sendToClient(new EmptyProxyResponse(
                            ((event instanceof ConnectTimeoutException) || (event instanceof TimeoutException)) ?
                                    HttpStatusCode.GATEWAY_TIMEOUT_504 : HttpStatusCode.BAD_GATEWAY_502));
                }
            }
        });

        return clientRequest;
    }
}
