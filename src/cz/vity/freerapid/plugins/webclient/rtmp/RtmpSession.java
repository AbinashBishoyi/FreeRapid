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

import org.apache.mina.common.IoSession;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RtmpSession {

    public static final boolean DEBUG = false;

    private static final Logger logger = Logger.getLogger(RtmpSession.class.getName());

    private static final String RTMP_SESSION_KEY = "RTMP_SESSION_KEY";

    private boolean serverHandshakeReceived;
    private boolean handshakeComplete;
    private Map<Integer, Header> prevHeadersIn = new ConcurrentHashMap<Integer, Header>();
    private Map<Integer, Header> prevHeadersOut = new ConcurrentHashMap<Integer, Header>();
    private Map<Integer, Packet> prevPacketsIn = new ConcurrentHashMap<Integer, Packet>();
    private Map<Integer, String> invokedMethods = new ConcurrentHashMap<Integer, String>();
    private int chunkSize = 128;
    private int nextInvokeId;
    private int bytesReadLastSent;
    private Map<String, Object> connectParams;
    private String playName;
    private int playStart;
    private int playDuration = -2;
    private OutputWriter outputWriter;
    private DecoderOutput decoderOutput;
    private String host;
    private int port;
    private InvokeResultHandler invokeResultHandler = new DefaultInvokeResultHandler();
    private boolean encrypted;
    private KeyAgreement keyAgreement;
    private byte[] clientPublicKey;
    private Cipher cipherIn;
    private Cipher cipherOut;
    private int swfSize;
    private byte[] swfHash;
    private byte[] swfVerification;
    private byte[] clientDigest;
    private byte[] serverDigest;
    private byte[] serverResponse;

    public RtmpSession() {
    }

    public RtmpSession(String host, int port, String app, String playName) {
        this(host, port, app, playName, false);
    }

    public RtmpSession(String host, int port, String app, String playName, boolean encrypted) {
        initConnectParams(host, port, app, playName, encrypted);
    }

    public RtmpSession(String url) {
        Pattern pattern = Pattern.compile("(rtmp.?)://([^/:]+)(:[0-9]+)?/([^/]+)/(.*)");
        Matcher matcher = pattern.matcher(url);
        if (!matcher.matches()) {
            throw new RuntimeException("invalid url: " + url);
        }
        logger.finest("parsing url: " + url);
        String protocol = matcher.group(1);
        logger.finest("protocol = '" + protocol + "'");
        String hostString = matcher.group(2);
        logger.finest("host = '" + hostString + "'");
        String portString = matcher.group(3);
        if (portString == null) {
            logger.finest("port is null in url, will use default 1935");
        } else {
            portString = portString.substring(1); // skip the ':'
            logger.finest("port = '" + portString + "'");
        }
        String appString = matcher.group(4);
        logger.finest("app = '" + appString + "'");
        String playString = matcher.group(5);
        logger.finest("play = '" + playString + "'");
        int portInt = portString == null ? 1935 : Integer.parseInt(portString);
        boolean encrypted = protocol.equalsIgnoreCase("rtmpe");
        initConnectParams(hostString, portInt, appString, playString, encrypted);
    }

    private void initConnectParams(String host, int port, String app, String playName, boolean encrypted) {
        this.host = host;
        this.port = port;
        this.playName = playName;
        if (encrypted) {
            this.encrypted = true;
        }
        String tcUrl = (encrypted ? "rtmpe://" : "rtmp://") + host + ":" + port + "/" + app;
        connectParams = new HashMap<String, Object>();
        connectParams.put("objectEncoding", 0);
        connectParams.put("app", app);
        connectParams.put("flashVer", "WIN 9,0,124,2");
        connectParams.put("fpad", false);
        connectParams.put("tcUrl", tcUrl);
        connectParams.put("audioCodecs", 1639);
        connectParams.put("videoFunction", 1);
        connectParams.put("capabilities", 15);
        connectParams.put("videoCodecs", 252);

        this.outputWriter = new FlvStreamWriter(playStart);
    }

    public static RtmpSession getFrom(IoSession ioSession) {
        return (RtmpSession) ioSession.getAttribute(RTMP_SESSION_KEY);
    }

    public void putInto(IoSession ioSession) {
        ioSession.setAttribute(RTMP_SESSION_KEY, this);
    }

    public void send(Handshake handshake) {
        decoderOutput.write(handshake);
    }

    public void send(Packet packet) {
        decoderOutput.write(packet);
    }

    public void send(Invoke invoke) {
        send(invoke.encode(this));
    }

    public String resultFor(Invoke invoke) {
        return getInvokedMethods().get(invoke.getSequenceId());
    }

    public int getNextInvokeId() {
        return ++nextInvokeId;
    }

    public void setSwfHash(String swfHash) {
        this.swfHash = Utils.fromHex(swfHash);
    }

    public void initSwfVerification(String pathToLocalSwfFile) {
        initSwfVerification(new File(pathToLocalSwfFile));
    }

    public void initSwfVerification(File localSwfFile) {
        logger.fine("initializing swf verification data for: " + localSwfFile.getAbsolutePath());
        byte[] bytes = Utils.readAsByteArray(localSwfFile);
        logger.fine("swf size: " + bytes.length);
        byte[] hash = Utils.sha256(bytes, Handshake.CLIENT_CONST);
        logger.fine("swf hash: " + Utils.toHex(hash));
        swfSize = bytes.length;
        swfHash = hash;
    }

    //==========================================================================

    public byte[] getServerResponse() {
        return serverResponse;
    }

    public void setServerResponse(byte[] serverResponse) {
        this.serverResponse = serverResponse;
    }

    public boolean isHandshakeComplete() {
        return handshakeComplete;
    }

    public void setHandshakeComplete(boolean handshakeComplete) {
        this.handshakeComplete = handshakeComplete;
    }

    public byte[] getServerDigest() {
        return serverDigest;
    }

    public void setServerDigest(byte[] serverDigest) {
        this.serverDigest = serverDigest;
    }

    public byte[] getClientDigest() {
        return clientDigest;
    }

    public void setClientDigest(byte[] clientDigest) {
        this.clientDigest = clientDigest;
    }

    public byte[] getSwfVerification() {
        return swfVerification;
    }

    public void setSwfVerification(byte[] swfVerification) {
        this.swfVerification = swfVerification;
    }

    public int getSwfSize() {
        return swfSize;
    }

    public void setSwfSize(int swfSize) {
        this.swfSize = swfSize;
    }

    public byte[] getSwfHash() {
        return swfHash;
    }

    public void setSwfHash(byte[] swfHash) {
        this.swfHash = swfHash;
    }

    public Cipher getCipherIn() {
        return cipherIn;
    }

    public void setCipherIn(Cipher cipherIn) {
        this.cipherIn = cipherIn;
    }

    public Cipher getCipherOut() {
        return cipherOut;
    }

    public void setCipherOut(Cipher cipherOut) {
        this.cipherOut = cipherOut;
    }

    public byte[] getClientPublicKey() {
        return clientPublicKey;
    }

    public void setClientPublicKey(byte[] clientPublicKey) {
        this.clientPublicKey = clientPublicKey;
    }

    public KeyAgreement getKeyAgreement() {
        return keyAgreement;
    }

    public void setKeyAgreement(KeyAgreement keyAgreement) {
        this.keyAgreement = keyAgreement;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public InvokeResultHandler getInvokeResultHandler() {
        return invokeResultHandler;
    }

    public void setInvokeResultHandler(InvokeResultHandler invokeResultHandler) {
        this.invokeResultHandler = invokeResultHandler;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getPlayStart() {
        return playStart;
    }

    public void setPlayStart(int playStart) {
        this.playStart = playStart;
    }

    public DecoderOutput getDecoderOutput() {
        return decoderOutput;
    }

    public void setDecoderOutput(DecoderOutput decoderOutput) {
        this.decoderOutput = decoderOutput;
    }

    public OutputWriter getOutputWriter() {
        return outputWriter;
    }

    public void setOutputWriter(OutputWriter outputWriter) {
        this.outputWriter = outputWriter;
    }

    public int getPlayDuration() {
        return playDuration;
    }

    public void setPlayDuration(int playDuration) {
        this.playDuration = playDuration;
    }

    public String getPlayName() {
        return playName;
    }

    public void setPlayName(String playName) {
        this.playName = playName;
    }

    public Map<String, Object> getConnectParams() {
        return connectParams;
    }

    public void setConnectParams(Map<String, Object> connectParams) {
        this.connectParams = connectParams;
    }

    public int getBytesReadLastSent() {
        return bytesReadLastSent;
    }

    public void setBytesReadLastSent(int bytesReadLastSent) {
        this.bytesReadLastSent = bytesReadLastSent;
    }

    public Map<Integer, String> getInvokedMethods() {
        return invokedMethods;
    }

    public boolean isServerHandshakeReceived() {
        return serverHandshakeReceived;
    }

    public void setServerHandshakeReceived(boolean serverHandshakeReceived) {
        this.serverHandshakeReceived = serverHandshakeReceived;
    }

    public Map<Integer, Header> getPrevHeadersIn() {
        return prevHeadersIn;
    }

    public Map<Integer, Header> getPrevHeadersOut() {
        return prevHeadersOut;
    }

    public Map<Integer, Packet> getPrevPacketsIn() {
        return prevPacketsIn;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

}