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

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.logging.Logger;

public class FlvStreamWriter implements OutputWriter {

    private static final Logger logger = Logger.getLogger(FlvStreamWriter.class.getName());

    private final WriterStatus status;
    private final WritableByteChannel channel;
    private final PipedInputStream in;
    private final PipedOutputStream out;
    private final ByteBuffer buffer;

    public FlvStreamWriter(int seekTime) {
        status = new WriterStatus(seekTime);
        try {
            out = new PipedOutputStream();
            channel = Channels.newChannel(out);
            in = new PipedInputStream(out, 1024);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        buffer = ByteBuffer.allocate(1024);
        buffer.setAutoExpand(true);
        writeHeader();
    }

    public InputStream getStream() {
        return in;
    }

    public void close() {
        try {
            channel.close();
            in.close();
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        status.logFinalVideoDuration();
    }

    private void writeHeader() {
        buffer.put((byte) 0x46); // F
        buffer.put((byte) 0x4C); // L
        buffer.put((byte) 0x56); // V
        buffer.put((byte) 0x01); // version
        buffer.put((byte) 0x05); // flags: audio + video
        buffer.putInt(0x09); // header size = 9
        buffer.putInt(0); // previous tag size, here = 0
        buffer.flip();
        write(buffer);
    }

    public synchronized void write(Packet packet) {
        Header header = packet.getHeader();
        int time = status.getChannelAbsoluteTime(header);
        write(header.getPacketType(), packet.getData(), time);
    }

    public synchronized void writeFlvData(ByteBuffer data) {
        while (data.hasRemaining()) {
            Packet.Type packetType = Packet.Type.parseByte(data.get());
            int size = Utils.readInt24(data);
            int timestamp = Utils.readInt24(data);
            status.updateVideoChannelTime(timestamp);
            data.getInt(); // 4 bytes of zeros (reserved)
            byte[] bytes = new byte[size];
            data.get(bytes);
            ByteBuffer temp = ByteBuffer.wrap(bytes);
            write(packetType, temp, timestamp);
            data.getInt(); // FLV tag size (size + 11)
        }
    }

    public synchronized void write(Packet.Type packetType, ByteBuffer data, final int time) {
        if (RtmpSession.DEBUG) {
            logger.finest(String.format("writing FLV tag %s %s %s", packetType, time, data));
        }
        buffer.clear();
        buffer.put(packetType.byteValue());
        final int size = data.limit();
        Utils.writeInt24(buffer, size);
        Utils.writeInt24(buffer, time);
        buffer.putInt(0); // 4 bytes of zeros (reserved)
        buffer.flip();
        write(buffer);
        write(data);
        //==========
        buffer.clear();
        buffer.putInt(size + 11); // previous tag size
        buffer.flip();
        write(buffer);
    }

    private void write(ByteBuffer buffer) {
        try {
            channel.write(buffer.buf());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}