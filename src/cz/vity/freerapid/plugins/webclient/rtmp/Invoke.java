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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static cz.vity.freerapid.plugins.webclient.rtmp.Header.Type.LARGE;
import static cz.vity.freerapid.plugins.webclient.rtmp.Header.Type.MEDIUM;

public class Invoke {

    private static final Logger logger = Logger.getLogger(Invoke.class.getName());

    private String methodName;
    private int sequenceId;
    private int channelId;
    private int time;
    private int streamId = -1;
    private Object[] args;

    public Invoke() {
    }

    public Invoke(String methodName, int channelId, Object... args) {
        this.methodName = methodName;
        this.channelId = channelId;
        this.args = args;
    }

    public Invoke(int streamId, String methodName, int channelId, Object... args) {
        this(methodName, channelId, args);
        this.streamId = streamId;
    }

    public int getLastArgAsInt() {
        return new Double(args[args.length - 1].toString()).intValue();
    }

    public AmfObject getSecondArgAsAmfObject() { // TODO significance of first ?
        return (AmfObject) args[1];
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getSequenceId() {
        return sequenceId;
    }

    public String getMethodName() {
        return methodName;
    }

    @SuppressWarnings("unchecked")
    public Packet encode(RtmpSession session) {
        sequenceId = session.getNextInvokeId();
        session.getInvokedMethods().put(sequenceId, methodName);
        Header prevHeader = session.getPrevHeadersOut().get(channelId);
        Header.Type headerType = prevHeader == null ? LARGE : MEDIUM;
        Header header = new Header(headerType, channelId, Packet.Type.INVOKE);
        if (streamId != -1) {
            header.setStreamId(streamId);
        }
        List<Object> list = new ArrayList<Object>();
        list.add(methodName);
        list.add(sequenceId);
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                if (arg instanceof Map) {
                    list.add(new AmfObject((Map) arg));
                } else {
                    list.add(arg);
                }
            }
        } else {
            list.add(null);
        }
        header.setTime(time);
        ByteBuffer body = AmfProperty.encode(list.toArray());
        Packet packet = new Packet(header, body);
        session.getPrevHeadersOut().put(channelId, header);
        logger.fine("encoded invoke: " + toString());
        return packet;
    }

    public void decode(Packet packet) {
        channelId = packet.getHeader().getChannelId();
        streamId = packet.getHeader().getStreamId();
        AmfObject object = new AmfObject();
        object.decode(packet.getData(), false);
        List<AmfProperty> properties = object.getProperties();
        methodName = (String) properties.get(0).getValue();
        double temp = (Double) properties.get(1).getValue();
        sequenceId = (int) temp;
        if (properties.size() > 2) {
            int argsLength = properties.size() - 2;
            args = new Object[argsLength];
            for (int i = 0; i < argsLength; i++) {
                args[i] = properties.get(i + 2).getValue();
            }
        }
        logger.fine("decoded invoke: " + toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[method: ").append(methodName);
        sb.append(", sequenceId: ").append(sequenceId);
        if (streamId != -1) {
            sb.append(", streamId: ").append(streamId);
        }
        sb.append(", args: ").append(Arrays.toString(args)).append(']');
        return sb.toString();
    }

}