/*
 * Copyright 2002-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cz.vity.freerapid.plugins.webclient.rtmp;

import cz.vity.freerapid.utilities.LogUtils;
import org.apache.mina.common.CloseFuture;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.transport.socket.nio.SocketConnector;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class RtmpClient extends IoHandlerAdapter {

    private static final Logger logger = Logger.getLogger(RtmpClient.class.getName());

    private final RtmpSession session;
    private final SocketConnector connector;
    private IoSession ioSession;

    public RtmpClient(RtmpSession session) {
        this.session = session;
        connector = new SocketConnector();
        connector.getFilterChain().addLast("crypto", new RtmpeIoFilter());
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new RtmpCodecFactory()));
    }

    public InputStream getStream() {
        return session.getOutputWriter().getStream();
    }

    public void connect() {
        connector.connect(new InetSocketAddress(session.getHost(), session.getPort()), this);
    }

    @Override
    public void sessionOpened(IoSession ioSession) {
        this.ioSession = ioSession;
        session.setDecoderOutput(new MinaIoSessionOutput(this));
        session.putInto(ioSession);
        logger.fine("session opened, starting handshake");
        ioSession.write(Handshake.generateClientRequest1(session));
    }

    @Override
    public void exceptionCaught(IoSession ioSession, Throwable cause) throws Exception {
        LogUtils.processException(logger, cause);
        disconnect();
    }

    public void disconnect() {
        try {
            logger.fine("disconnecting, bytes read: " + ioSession.getReadBytes());
            connector.setWorkerTimeout(0);
            CloseFuture future = ioSession.close();
            logger.fine("closing connection, waiting for thread exit");
            future.join();
            logger.fine("connection closed successfully");
        } finally {
            session.getOutputWriter().close();
        }
    }

    private static class RtmpCodecFactory implements ProtocolCodecFactory {

        private ProtocolEncoder encoder = new RtmpEncoder();
        private ProtocolDecoder decoder = new RtmpDecoder();

        public ProtocolDecoder getDecoder() {
            return decoder;
        }

        public ProtocolEncoder getEncoder() {
            return encoder;
        }
    }

    /**
     * implementation used for connecting to a network stream
     */
    private static class MinaIoSessionOutput implements DecoderOutput {

        private RtmpClient client;

        public MinaIoSessionOutput(RtmpClient client) {
            this.client = client;
        }

        public void write(Object packet) {
            client.ioSession.write(packet);
        }

        public void disconnect() {
            client.disconnect();
        }
    }
}