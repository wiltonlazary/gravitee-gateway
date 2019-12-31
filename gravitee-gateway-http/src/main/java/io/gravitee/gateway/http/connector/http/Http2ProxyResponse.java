package io.gravitee.gateway.http.connector.http;

import io.gravitee.common.http.HttpHeaders;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.api.handler.Handler;
import io.gravitee.gateway.api.http2.HttpFrame;
import io.gravitee.gateway.api.proxy.ProxyResponse;
import io.gravitee.gateway.api.stream.ReadStream;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class Http2ProxyResponse extends HttpProxyResponse {

    private final HttpProxyResponse delegate;

    private Handler<HttpFrame> customFrameHandler;

    public Http2ProxyResponse(HttpProxyResponse delegate) {
        super(null);
        this.delegate = delegate;
    }

    @Override
    public int status() {
        return delegate.status();
    }

    @Override
    public String reason() {
        return delegate.reason();
    }

    @Override
    public HttpHeaders headers() {
        return delegate.headers();
    }

    @Override
    public ProxyResponse bodyHandler(Handler<Buffer> bodyHandler) {
        return delegate.bodyHandler(bodyHandler);
    }

    @Override
    public Handler<Buffer> bodyHandler() {
        return delegate.bodyHandler();
    }

    @Override
    public ProxyResponse endHandler(Handler<Void> endHandler) {
        return delegate.endHandler(endHandler);
    }

    @Override
    public Handler<Void> endHandler() {
        return delegate.endHandler();
    }

    @Override
    public ReadStream<Buffer> pause() {
        return delegate.pause();
    }

    @Override
    public ReadStream<Buffer> resume() {
        return delegate.resume();
    }

    @Override
    public ProxyResponse customFrameHandler(Handler<HttpFrame> frameHandler) {
        this.customFrameHandler = frameHandler;
        return this;
    }

    @Override
    public void writeCustomFrame(HttpFrame frame) {
        this.customFrameHandler.handle(frame);
    }

    @Override
    public boolean connected() {
        return delegate.connected();
    }

    @Override
    public HttpHeaders trailers() {
        return delegate.trailers();
    }
}
