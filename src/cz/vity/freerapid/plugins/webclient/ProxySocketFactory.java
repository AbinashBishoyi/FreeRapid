/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.vity.freerapid.plugins.webclient;

import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import java.io.IOException;
import java.net.*;

/**
 * SocketFactory that creates Sockets using the given Proxy
 *
 * @author benpicco
 */
public class ProxySocketFactory implements ProtocolSocketFactory {

    Proxy proxy;

    public ProxySocketFactory(Proxy proxy) {
        this.proxy = proxy;
    }

    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort) throws IOException {
        Socket socket = new Socket(proxy);
        SocketAddress localaddr = new InetSocketAddress(localAddress, localPort);
        SocketAddress remoteaddr = new InetSocketAddress(host, port);
        socket.bind(localaddr);
        socket.connect(remoteaddr);
        return socket;
    }

    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort, HttpConnectionParams params) throws IOException {
        // We discard params
        if (params == null) {
            return createSocket(host, port, localAddress, localPort);
        }
        int timeout = params.getConnectionTimeout();

        if (timeout == 0) {
            return createSocket(host, port, localAddress, localPort);
        } else {
            Socket socket = new Socket(proxy);
            SocketAddress localaddr = new InetSocketAddress(localAddress, localPort);
            SocketAddress remoteaddr = new InetSocketAddress(host, port);
            socket.bind(localaddr);
            socket.connect(remoteaddr, timeout);
            return socket;
        }
    }

    public Socket createSocket(String host, int port) throws IOException {
        Socket psock = new Socket(proxy);

        InetSocketAddress final_addr = new InetSocketAddress(host, port);
        psock.connect(final_addr);

        return psock;
    }

    public String toString() {
        return proxy.toString();
    }
}
