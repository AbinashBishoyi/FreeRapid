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
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class AmfObject {

    private static final Logger logger = Logger.getLogger(AmfObject.class.getName());

    private List<AmfProperty> properties = new ArrayList<AmfProperty>();

    public AmfObject() {
    }

    public AmfObject(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public List<AmfProperty> getProperties() {
        return properties;
    }

    public AmfProperty getProperty(String name) {
        for (AmfProperty property : properties) {
            if (name.equals(property.getName())) {
                return property;
            }
        }
        return null;
    }

    public String getFirstPropertyAsString() {
        return (String) properties.get(0).getValue();
    }

    public void put(Object value) {
        put(null, value);
    }

    public void put(String name, Object value) {
        add(new AmfProperty(name, value));
    }

    public void add(AmfProperty property) {
        properties.add(property);
    }

    public void decode(ByteBuffer in, boolean decodeName) {
        while (in.remaining() >= 3) {
            byte[] endMarker = new byte[3];
            in.get(endMarker);
            if (endMarker[0] == 0x00 && endMarker[1] == 0x00 && endMarker[2] == 0x09) {
                logger.finest("decoded end marker: [00 00 09]");
                return;
            }
            in.position(in.position() - 3);
            AmfProperty property = new AmfProperty();
            property.decode(in, decodeName);
            add(property);
        }
    }

    public static ByteBuffer encode(Object... values) {
        AmfObject o = new AmfObject();
        for (Object value : values) {
            o.put(value);
        }
        ByteBuffer body = ByteBuffer.allocate(1024);
        o.encode(body);
        return body;
    }

    public void encode(ByteBuffer out) {
        for (AmfProperty property : properties) {
            property.encode(out);
        }
        logger.finest("encoding end marker: [00 00 09]");
        out.put(new byte[]{0x00, 0x00, 0x09});
    }

    @Override
    public String toString() {
        return properties.toString();
    }

}