package cz.vity.freerapid.plugins.webclient.utils;

import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author VitasekL
 */
public class JsonMapperTest {

    @Test
    public void testDeserialize() throws Exception {
        final JsonMapper jsonMapper = new JsonMapper();
        final JsonValue deserialized = jsonMapper.deserialize("{\"value\": 10, \"stringValue\":\"blabla\"}", JsonValue.class);
        Assert.assertEquals(10, deserialized.value);
        Assert.assertEquals("blabla", deserialized.stringValue);
    }

    @Test(expected = PluginImplementationException.class)
    public void testDeserializeWithException() throws Exception {
        final JsonMapper jsonMapper = new JsonMapper();
        final JsonValue deserialized = jsonMapper.deserialize("{\"valu: 10, \"stringValue\":\"blabla\"}", JsonValue.class);
    }

    public static class JsonValue {
        private int value;
        private String stringValue;

        public JsonValue() {

        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getStringValue() {
            return stringValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }
    }
}
