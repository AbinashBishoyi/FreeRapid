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

import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import static cz.vity.freerapid.plugins.webclient.rtmp.AmfProperty.Type.*;

public class AmfProperty {

    private static final Logger logger = Logger.getLogger(AmfProperty.class.getName());

    public static enum Type implements ByteToEnum.Convert {

        NUMBER(0x00),
        BOOLEAN(0x01),
        STRING(0x02),
        OBJECT(0x03),
        NULL(0x05),
        UNDEFINED(0x06),
        MAP(0x08),
        ARRAY(0x0A),
        DATE(0x0B),
        LONG_STRING(0x0C),
        UNSUPPORTED(0x0D);

        private final byte value;

        private Type(int byteValue) {
            this.value = (byte) byteValue;
        }

        public byte byteValue() {
            return value;
        }

        private static ByteToEnum<Type> converter = new ByteToEnum<Type>(Type.values());

        public static Type parseByte(byte b) {
            return converter.parseByte(b);
        }

        @Override
        public String toString() {
            return converter.toString(this);
        }

    }

    private Type type;
    private String name;
    private Object value;

    public AmfProperty() {
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public AmfProperty(Object o) {
        this(null, o);
    }

    public AmfProperty(String name, Object o) {
        this.name = name;
        value = o;
        if (o instanceof Number) {
            type = NUMBER;
            value = Double.parseDouble(o.toString()); // converts int also
        } else if (o instanceof Boolean) {
            type = BOOLEAN;
        } else if (o instanceof String) {
            type = STRING;
        } else if (o instanceof AmfObject) {
            type = OBJECT;
        } else if (o == null) {
            type = NULL;
        } else if (o instanceof Map) {
            type = MAP;
        } else {
            throw new RuntimeException("unexpected parameter type: " + o.getClass());
        }
    }

    private static String decodeString(ByteBuffer in) {
        short size = in.getShort();
        byte[] bytes = new byte[size];
        in.get(bytes);
        return new String(bytes); // TODO UTF-8 ?
    }

    private static void encodeString(ByteBuffer out, String value) {
        byte[] bytes = value.getBytes(); // TODO UTF-8 ?
        out.putShort((short) bytes.length);
        out.put(bytes);
    }

    public static void encode(ByteBuffer out, Object... values) {
        for (Object value : values) {
            AmfProperty temp = new AmfProperty(value);
            temp.encode(out);
        }
    }

    public static ByteBuffer encode(Object... values) {
        ByteBuffer out = ByteBuffer.allocate(1024);
        encode(out, values);
        return out;
    }


    public void decode(ByteBuffer in, boolean decodeName) {
        if (decodeName) {
            name = decodeString(in);
        }
        type = Type.parseByte(in.get());
        switch (type) {
            case NUMBER:
                value = in.getDouble();
                break;
            case BOOLEAN:
                value = (in.get() == 0x01) ? true : false;
                break;
            case STRING:
                value = decodeString(in);
                break;
            case OBJECT:
                logger.finest("decoding nested object");
                AmfObject object = new AmfObject();
                object.decode(in, true);
                value = object;
                break;
            case NULL:
                break;
            case ARRAY:
                int arraySize = in.getInt();
                logger.finest("decoding nested array of size: " + arraySize);
                AmfObject array = new AmfObject();
                for (int i = 0; i < arraySize; i++) {
                    AmfProperty prop = new AmfProperty();
                    prop.decode(in, false);
                    array.add(prop);
                }
                value = array;
                break;
            case MAP:
                in.getInt(); // will always be 0
                logger.finest("decoding map (name value pairs)");
                AmfObject map = new AmfObject();
                map.decode(in, true);
                value = map;
                break;
            case DATE:
                value = new Date((long) in.getDouble()); // TODO UTC offset
                in.getShort(); // consume the timezone
                break;
            case LONG_STRING:
                int stringSize = in.getInt();
                byte[] bytes = new byte[stringSize];
                in.get(bytes);
                value = new String(bytes); // TODO UTF-8 ?
                break;
            case UNDEFINED:
            case UNSUPPORTED:
                break;
            default:
                throw new RuntimeException("unknown type");
        }
        logger.finest("decoded property: " + toString());
    }

    public void encode(ByteBuffer out) {
        logger.finest("encoding property: " + toString());
        if (name != null) {
            encodeString(out, name);
        }
        out.put(type.value);
        switch (type) {
            case NUMBER:
                out.putDouble((Double) value);
                break;
            case BOOLEAN:
                int bool = (Boolean) value ? 0x01 : 0x00;
                out.put((byte) bool);
                break;
            case STRING:
                encodeString(out, (String) value);
                break;
            case NULL:
                break;
            case OBJECT:
                logger.finest("encoding nested object");
                AmfObject object = (AmfObject) value;
                object.encode(out);
                break;
            case MAP:
                logger.finest("encoding nested map");
                out.putInt(0);
                AmfObject map = (AmfObject) value;
                for (AmfProperty prop : map.getProperties()) {
                    prop.encode(out);
                }
                break;
            case ARRAY:
                logger.finest("encoding nested array");
                AmfObject array = (AmfObject) value;
                out.putInt(array.getProperties().size());
                for (AmfProperty prop : array.getProperties()) {
                    prop.encode(out);
                }
                break;
            default:
                // ignoring other types client doesn't require for now
                throw new RuntimeException("unexpected type: " + type);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[').append(type).append(" ");
        if (name != null) {
            sb.append(name).append(": ");
        }
        sb.append(value).append(']');
        return sb.toString();
    }

}