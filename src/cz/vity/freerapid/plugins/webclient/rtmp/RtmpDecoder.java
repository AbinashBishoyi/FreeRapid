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
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.util.logging.Logger;

public class RtmpDecoder extends CumulativeProtocolDecoder {

    private static final Logger logger = Logger.getLogger(RtmpDecoder.class.getName());

    @Override
    protected boolean doDecode(IoSession ioSession, ByteBuffer in, ProtocolDecoderOutput _unused) {
        return decode(in, RtmpSession.getFrom(ioSession));
    }

    public static boolean decode(ByteBuffer in, RtmpSession session) {

        if (!session.isServerHandshakeReceived()) {
            if (!Handshake.decodeServerResponse(in, session)) {
                return false;
            }
            session.setServerHandshakeReceived(true);
            logger.fine("server handshake processed, sending reply");
            session.send(Handshake.generateClientRequest2(session));
            session.send(new Invoke("connect", 3, session.getConnectParams()));
            return true;
        }

        final int position = in.position();
        Packet packet = new Packet();

        if (!packet.decode(in, session)) {
            in.position(position);
            return false;
        }

        if (!packet.isComplete()) { // but finished decoding chunk
            return true;
        }

        if (RtmpSession.DEBUG) {
            logger.finest("packet complete: " + packet);
        }

        ByteBuffer data = packet.getData();

        switch (packet.getHeader().getPacketType()) {
            case CHUNK_SIZE:
                int newChunkSize = data.getInt();
                session.setChunkSize(newChunkSize);
                logger.fine("new chunk size is: " + newChunkSize);
                break;
            case CONTROL_MESSAGE:
                short type = data.getShort();
                if (type == 6) {
                    int time = data.getInt();
                    data.rewind();
                    logger.fine("server ping: " + packet);
                    Packet pong = Packet.ping(7, time, -1); // 7 == pong type
                    logger.fine("client pong: " + pong);
                    session.send(pong);
                } else if (type == 0x001A) {
                    logger.fine("server swf verification request: " + packet);
                    byte[] swfv = session.getSwfVerification();
                    if (swfv == null) {
                        logger.warning("not sending swf verification response! connect parameters not set"
                                + ", server likely to stop responding");
                    } else {
                        Packet pong = Packet.swfVerification(session.getSwfVerification());
                        logger.fine("sending client swf verification response: " + pong);
                        session.send(pong);
                    }
                } else {
                    logger.finest("not handling unknown control message type: " + type + " " + packet);
                }
                break;
            case AUDIO_DATA:
            case VIDEO_DATA:
                session.getOutputWriter().write(packet);
                break;
            case FLV_DATA:
                session.getOutputWriter().writeFlvData(data);
                break;
            case NOTIFY:
                AmfObject notify = new AmfObject();
                notify.decode(data, false);
                String notifyMethod = notify.getFirstPropertyAsString();
                logger.fine("server notify: " + notify);
                if (notifyMethod.equals("onMetaData")) {
                    logger.fine("notify is 'onMetadata', writing metadata");
                    data.rewind();
                    session.getOutputWriter().write(packet);
                }
                break;
            case INVOKE:
                Invoke serverInvoke = new Invoke();
                serverInvoke.decode(packet);
                String methodName = serverInvoke.getMethodName();
                if (methodName.equals("_result")) {
                    session.getInvokeResultHandler().handle(serverInvoke, session);
                } else if (methodName.equals("onStatus")) {
                    AmfObject temp = serverInvoke.getSecondArgAsAmfObject();
                    String code = (String) temp.getProperty("code").getValue();
                    logger.fine("onStatus code: " + code);
                    if (code.equals("NetStream.Failed")
                            || code.equals("NetStream.Play.Failed") || code.equals("NetStream.Play.Stop")) {
                        logger.fine("disconnecting");
                        session.getDecoderOutput().disconnect();
                    }
                } else {
                    logger.fine("unhandled server invoke: " + serverInvoke);
                }
                break;
            case BYTES_READ:
            case SERVER_BANDWIDTH:
            case CLIENT_BANDWIDTH:
                logger.fine("ignoring received packet: " + packet.getHeader());
                break;
            default:
                throw new RuntimeException("unknown packet type: " + packet.getHeader());
        }

        return true;
    }

}