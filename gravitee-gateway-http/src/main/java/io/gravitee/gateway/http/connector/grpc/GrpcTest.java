package io.gravitee.gateway.http.connector.grpc;

import io.grpc.*;
import io.grpc.internal.TransportFrameUtil;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.StreamObserver;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.grpc.VertxChannelBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.function.Consumer;

public class GrpcTest {


    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        /*
        ManagedChannel channel = VertxChannelBuilder
                .forAddress(vertx, "localhost", 50051)
                .usePlaintext()
                .build();

        MethodDescriptor<Buffer, Buffer> descriptor = MethodDescriptor.newBuilder(new MethodDescriptor.Marshaller<Buffer>() {
            @Override
            public InputStream stream(Buffer obj) {
                return new ByteArrayInputStream(obj.getBytes());
            }

            @Override
            public Buffer parse(InputStream inputStream) {
                System.out.println("req-parse");
                return null;
            }
        }, new MethodDescriptor.Marshaller<Buffer>() {
            @Override
            public InputStream stream(Buffer o) {
                System.out.println("resp-stream");
                return null;
            }

            @Override
            public Buffer parse(InputStream inputStream) {
                System.out.println("resp-parse");
                return null;
            }
        }).setType(MethodDescriptor.MethodType.UNARY).setFullMethodName("helloworld.Greeter/SayHello").build();

        vertx.createHttpServer(new HttpServerOptions().setUseAlpn(true))
                .requestHandler(new Handler<HttpServerRequest>() {
                    @Override
                    public void handle(HttpServerRequest req) {
                        System.out.println("Handle request !");

                        clientCall.start(new ClientCall.Listener<Buffer>() {
                            @Override
                            public void onHeaders(Metadata headers) {
                                System.out.println("onHeaders");
                                System.out.println(headers);
                                super.onHeaders(headers);
                            }

                            @Override
                            public void onMessage(Buffer message) {
                                System.out.println("onMessage");
                                System.out.println(message);
                                super.onMessage(message);
                            }

                            @Override
                            public void onClose(Status status, Metadata trailers) {
                                System.out.println("onClose");
                                System.out.println(status);
                                super.onClose(status, trailers);
                            }

                            @Override
                            public void onReady() {
                                System.out.println("onReady");
                                super.onReady();
                            }
                        }, new Metadata());

                        req.handler(new Handler<Buffer>() {
                            @Override
                            public void handle(Buffer data) {
                                System.out.println(data);


                                ClientCalls.asyncUnaryCall(
                                        channel.newCall(descriptor, CallOptions.DEFAULT),
                                        data,
                                        new StreamObserver<Buffer>() {
                                            @Override
                                            public void onNext(Buffer value) {
                                                System.out.println("onNext");
                                                System.out.println(value);
                                            }

                                            @Override
                                            public void onError(Throwable t) {
                                                System.out.println("onError");
                                                System.out.println(t);
                                            }

                                            @Override
                                            public void onCompleted() {
                                                System.out.println("onCompleted");
                                            }
                                        }
                                );


                                //    clientCall.sendMessage(data);
                            }
                        });

                        req.endHandler(new Handler<Void>() {
                            @Override
                            public void handle(Void event) {
                            //    clientCall.halfClose();
                            //    clientCall.request(1);
                            }
                        });
                    }
                })
                .listen(8082);
        */


        HttpClientOptions options = new HttpClientOptions()
                .setProtocolVersion(HttpVersion.HTTP_2)
                .setUseAlpn(true)
                .setHttp2ClearTextUpgrade(false)
                .setTrustAll(true);


        HttpClient client = vertx.createHttpClient(options);

        vertx.createHttpServer(new HttpServerOptions().setUseAlpn(true))
                .requestHandler(new Handler<HttpServerRequest>() {
                    @Override
                    public void handle(HttpServerRequest req) {
                        HttpClientRequest request = client.request(
                                HttpMethod.POST,
                                50051,
                                "localhost",
                                req.path());

                        req.customFrameHandler(new Handler<HttpFrame>() {
                            @Override
                            public void handle(HttpFrame event) {
                                System.out.println(event);
                            }
                        });

                        request.connectionHandler(new Handler<HttpConnection>() {
                            @Override
                            public void handle(HttpConnection connection) {
                                System.out.println(connection);

                                connection.closeHandler(new Handler<Void>() {
                                    @Override
                                    public void handle(Void event) {
                                        System.out.println("under connection: close");
                                    }
                                });

                                connection.exceptionHandler(new Handler<Throwable>() {
                                    @Override
                                    public void handle(Throwable event) {
                                        System.out.println("under connection");
                                        System.out.println(event);
                                    }
                                });
                                connection.goAwayHandler(new Handler<GoAway>() {
                                    @Override
                                    public void handle(GoAway event) {
                                        System.out.println("under connection");
                                        System.out.println("GoAway");
                                        System.out.println(event);
                                    }
                                });
                            }
                        });

                        request.continueHandler(new Handler<Void>() {
                            @Override
                            public void handle(Void event) {
                                System.out.println("continue");
                            }
                        });


                        request.exceptionHandler(new Handler<Throwable>() {
                            @Override
                            public void handle(Throwable event) {
                                System.out.println(event);
                            }
                        });

                        Metadata headers = new Metadata();
                        headers.put(Metadata.Key.of("grpc-accept-encoding", Metadata.ASCII_STRING_MARSHALLER), "gzip");
                        byte[][] http2Headers = TransportFrameUtil.toHttp2Headers(headers);

                        // Required for GRPC
                        request.setChunked(true);

                        request.headers().set(HttpHeaders.CONTENT_TYPE, "application/grpc");
                        request.headers().set(HttpHeaders.USER_AGENT, "grpc-gravitee-gateway");
                        request.headers().set("te", "trailers");

                        request.handler(new Handler<HttpClientResponse>() {
                            @Override
                            public void handle(HttpClientResponse response) {
                                req.response().setChunked(true);

                                System.out.println(response);
//                                System.out.println(response.trailers());
//                                req.response().trailers();

                                response.exceptionHandler(new Handler<Throwable>() {
                                    @Override
                                    public void handle(Throwable event) {
                                        System.out.println("response - exception");
                                        System.out.println(event);
                                    }
                                });

                                // Handle headers
                                response.headers().forEach(new Consumer<Map.Entry<String, String>>() {
                                    @Override
                                    public void accept(Map.Entry<String, String> header) {
                                        req.response().headers().set(header.getKey(), header.getValue());
                                    }
                                });

                                response.endHandler(new Handler<Void>() {
                                    @Override
                                    public void handle(Void event) {
                                        System.out.println("response - trailers");

                                        // Handle headers
                                        response.trailers().forEach(new Consumer<Map.Entry<String, String>>() {
                                            @Override
                                            public void accept(Map.Entry<String, String> header) {
                                                req.response().trailers().set(header.getKey(), header.getValue());
                                            }
                                        });

                                        System.out.println("response - end");
                                        req.response().end();
                                    }
                                });

                                response.handler(new Handler<Buffer>() {
                                    @Override
                                    public void handle(Buffer event) {
                                        System.out.println("response - event");
                                        System.out.println(event);
                                        req.response().write(event);
                                    }
                                });
                            }
                        });

                        req.handler(new Handler<Buffer>() {
                            @Override
                            public void handle(Buffer event) {
                                System.out.println("request - write");
                                System.out.println(event);
                                request.write(event);
                            }
                        });

                        req.endHandler(new Handler<Void>() {
                            @Override
                            public void handle(Void event) {
                                System.out.println("request - end");
                                request.end();
                            }
                        });

                        request.exceptionHandler(new Handler<Throwable>() {
                            @Override
                            public void handle(Throwable event) {
                                System.out.println(event);
                            }
                        });


                    }
                })
                .listen(8082);
    }
}
