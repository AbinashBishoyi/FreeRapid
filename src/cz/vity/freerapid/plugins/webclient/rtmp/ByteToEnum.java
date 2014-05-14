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

import java.util.HashMap;
import java.util.Map;

/**
 * a little bit of code reuse, would have been cleaner if enum types
 * could extend some other class - we implement an interface instead
 * and have to construct a static instance in each enum type we use
 */
public class ByteToEnum<T extends Enum<T> & ByteToEnum.Convert> {

    public interface Convert {
        byte byteValue();
    }

    private Map<Byte, T> map;

    public ByteToEnum(T[] values) {
        map = new HashMap<Byte, T>(values.length);
        for (T t : values) {
            map.put(t.byteValue(), t);
        }
    }

    public T parseByte(byte b) {
        T t = map.get(b);
        if (t == null) {
            throw new RuntimeException("bad byte: " + Utils.toHex(b));
        }
        return t;
    }

    public String toString(T t) {
        return t.name() + "(0x" + Utils.toHex(t.byteValue()) + ")";
    }

}