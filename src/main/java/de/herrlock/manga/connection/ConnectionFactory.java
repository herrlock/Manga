package de.herrlock.manga.connection;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import de.herrlock.manga.util.Constants;

public abstract class ConnectionFactory {

    private final int timeout;

    public URLConnection getConnection(URL url) throws IOException {
        URLConnection con = getRawConnection(url);
        con.setConnectTimeout(this.timeout);
        con.setReadTimeout(this.timeout);
        return con;
    }

    protected abstract URLConnection getRawConnection(URL url) throws IOException;

    public ConnectionFactory(String timeout) {
        int _timeout = Constants.PARAM_TIMEOUT_DEFAULT;
        try {
            if (timeout != null && !"".equals(timeout)) {
                _timeout = Integer.parseInt(timeout);
            }
        }
        catch (NumberFormatException ex) {
            // do nothing, default will be set
        }

        this.timeout = _timeout;
    }

}
