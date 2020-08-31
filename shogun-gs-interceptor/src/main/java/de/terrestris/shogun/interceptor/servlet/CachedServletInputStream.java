package de.terrestris.shogun.interceptor.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

/**
 * An inputstream which reads the cached request body and has mutable
 * request URI and params.
 *
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 * http://stackoverflow.com/questions/10210645/http-servlet-request-lose-params-from-post-body-after-read-it-once
 */
public class CachedServletInputStream extends ServletInputStream {

    private ByteArrayInputStream input;

    /**
     * Create a new input stream from the cached request body
     */
    public CachedServletInputStream(ByteArrayOutputStream cachedBytes) {
        input = new ByteArrayInputStream(cachedBytes.toByteArray());
    }

    @Override
    public int read() {
        return input.read();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
    }
}
