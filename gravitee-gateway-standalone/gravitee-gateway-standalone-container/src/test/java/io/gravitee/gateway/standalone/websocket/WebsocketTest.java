package io.gravitee.gateway.standalone.websocket;

import io.gravitee.gateway.standalone.AbstractGatewayTest;
import io.gravitee.gateway.standalone.junit.annotation.ApiDescriptor;
import io.gravitee.gateway.standalone.junit.rules.ApiDeployer;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@ApiDescriptor("/io/gravitee/gateway/standalone/websocket/teams.json")
public class WebsocketTest extends AbstractGatewayTest {

    {
        System.setProperty("vertx.disableWebsockets", Boolean.FALSE.toString());
    }

    @Rule
    public final TestRule chain = RuleChain.outerRule(new ApiDeployer(this));

    @Test
    public void simple_websocket_request() throws InterruptedException {
        Vertx vertx = Vertx.vertx();

        HttpServer httpServer = vertx.createHttpServer();
        httpServer
                .requestHandler(new Handler<HttpServerRequest>() {
                    @Override
                    public void handle(HttpServerRequest event) {

                        event.toWebSocket(new Handler<AsyncResult<ServerWebSocket>>() {
                            @Override
                            public void handle(AsyncResult<ServerWebSocket> result) {
                                System.out.println("je suis ici: " + result);
                                result.result().accept();

                                result.result().writePing(Buffer.buffer("coucou"));
                            }
                        });

                    }
                })
        .listen(16664);

        // Wait for result
        CountDownLatch latch = new CountDownLatch(1);

        HttpClient httpClient = vertx.createHttpClient(new HttpClientOptions()
                .setDefaultPort(8082)
                .setDefaultHost("localhost"));

        httpClient.webSocket("/test", new Handler<AsyncResult<WebSocket>>() {
            @Override
            public void handle(AsyncResult<WebSocket> event) {
                if (event.failed()) {
                    Assert.fail();
                } else {
                    final WebSocket webSocket = event.result();
                    webSocket.frameHandler(new Handler<WebSocketFrame>() {
                        @Override
                        public void handle(WebSocketFrame frame) {
                            System.out.println("je suis là: " +frame);
                            latch.countDown();
                        }
                    });
                }
            }
        });

        Assert.assertTrue(latch.await(10, TimeUnit.SECONDS));
        httpServer.close();
    }
}
