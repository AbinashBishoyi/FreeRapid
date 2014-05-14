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

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoFilterAdapter;
import org.apache.mina.common.IoSession;

import java.util.logging.Logger;

public class RtmpeIoFilter extends IoFilterAdapter {

    private static final Logger logger = Logger.getLogger(RtmpeIoFilter.class.getName());

    @Override
    public void messageReceived(NextFilter nextFilter, IoSession ioSession, Object message) throws Exception {
        RtmpSession session = RtmpSession.getFrom(ioSession);
        final int bytesReadSoFar = (int) ioSession.getReadBytes();    // TODO what if bigger than int ?
        if (bytesReadSoFar > session.getBytesReadLastSent() + 600 * 1024) {
            logger.fine("sending bytes read " + bytesReadSoFar);
            session.send(Packet.bytesRead(bytesReadSoFar));
            session.setBytesReadLastSent(bytesReadSoFar);
        }
        if (!session.isEncrypted() || !session.isHandshakeComplete() || !(message instanceof ByteBuffer)) {
            nextFilter.messageReceived(ioSession, message);
            return;
        }
        ByteBuffer buf = (ByteBuffer) message;
        int initial = buf.position();
        byte[] encrypted = new byte[buf.remaining()];
        buf.get(encrypted);
        byte[] plain = session.getCipherIn().update(encrypted);
        buf.position(initial);
        buf.put(plain);
        buf.position(initial);
        nextFilter.messageReceived(ioSession, buf);
    }

    @Override
    public void filterWrite(NextFilter nextFilter, IoSession ioSession, WriteRequest writeRequest) throws Exception {
        RtmpSession session = RtmpSession.getFrom(ioSession);
        if (!session.isEncrypted() || !session.isHandshakeComplete()) {
            nextFilter.filterWrite(ioSession, writeRequest);
            return;
        }
        ByteBuffer buf = (ByteBuffer) writeRequest.getMessage();
        if (!buf.hasRemaining()) {
            // ignore empty buffers
            nextFilter.filterWrite(ioSession, writeRequest);
        } else {
            int initial = buf.position();
            byte[] plain = new byte[buf.remaining()];
            buf.get(plain);
            byte[] encrypted = session.getCipherOut().update(plain);
            buf.position(initial);
            buf.put(encrypted);
            buf.position(initial);
            nextFilter.filterWrite(ioSession, new WriteRequest(buf, writeRequest.getFuture()));
        }
    }

}