package cz.vity.freerapid.plugins.webclient.utils;

import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @author VitasekL
 */
public class JsonMapperTest {

    @Test
    public void testDeserialize() throws Exception {
        final JsonMapper jsonMapper = new JsonMapper();
        final JsonValue deserialized = jsonMapper.deserialize("{\"value\": 10, \"stringValue\":\"blabla\"}", JsonValue.class);
        final Map des2 = jsonMapper.deserialize("{\\\"id\\\": 592818, \\\"eid\\\": \\\"GoBga_-yK3aqyPQGbLwfKg\\\", \\\"title\\\": \\\"The Prophet Apple\\\", \\\"video_type\\\": \\\"episode\\\", \\\"content_id\\\": \\\"60336517\\\", \\\"categories\\\": null, \\\"original_premiere_date\\\": \\\"2014-02-19T00:00:00Z\\\", \\\"is_subscriber_only\\\": false, \\\"is_web_only\\\": false, \\\"is_auth_valid\\\": false, \\\"is_html5_enabled\\\": false, \\\"description\\\": \\\"Struck by lightning, Apple becomes a miracle child complete with healing powers and followers, which Rudi embraces for all its perks. When Apple\\\\u2019s faithful become overwhelming, Rudi must searches for a way to disperse them.\\\", \\\"copyright\\\": \\\"CHISUP\\\", \\\"season_number\\\": 1, \\\"episode_number\\\": 12, \\\"programming_type\\\": \\\"Full Episode\\\", \\\"poster_url\\\": null, \\\"rating\\\": 3.60606, \\\"duration\\\": 1317.38, \\\"has_captions\\\": true, \\\"video_captions\\\": [], \\\"released_at\\\": \\\"2014-02-04T23:15:07Z\\\", \\\"content_rating\\\": \\\"TV-14\\\", \\\"content_rating_reason\\\": \\\"V\\\", \\\"studio\\\": null, \\\"available_at\\\": \\\"2014-02-19T05:00:00Z\\\", \\\"expires_at\\\": null, \\\"auth_name\\\": null, \\\"tune_in_information\\\": \\\"Watch the full series\\\\\\\\nOnly on Hulu\\\", \\\"embed_permitted\\\": true, \\\"show_id\\\": 15960, \\\"show\\\": {\\\"id\\\": 15960, \\\"name\\\": \\\"Mother Up\\\", \\\"canonical_name\\\": \\\"mother-up\\\", \\\"rating\\\": 3.921556433917775, \\\"link_description\\\": null, \\\"seasons_count\\\": 1, \\\"episodes_count\\\": 13, \\\"games_count\\\": 0, \\\"clips_count\\\": 21, \\\"film_clips_count\\\": 0, \\\"feature_films_count\\\": 0, \\\"is_movie\\\": false, \\\"is_subscriber_only\\\": false, \\\"is_coppa\\\": false, \\\"genre\\\": \\\"Comedy\\\", \\\"genres\\\": \\\"Comedy|Animation and Cartoons\\\", \\\"videos_count\\\": 34, \\\"art_copyright\\\": null, \\\"description\\\": \\\"Mother Up! is an animated series chronicling the misguided attempts at parenthood of Rudi Wilson (Longoria), a disgraced former music exec, as she transitions from the towers of Manhattan to the carpool line of suburbia, where she finds herself alone and hopelessly under-equipped to manage her two kids and new life.\\\"}, \\\"company\\\": {\\\"id\\\": 492, \\\"name\\\": \\\"Hulu Original Series\\\", \\\"canonical_name\\\": \\\"hulu-original-series\\\", \\\"key_art_url\\\": \\\"http://ib1.huluim.com/company_key_art/492?size=1600x600&region=US\\\", \\\"network_logo_url\\\": null}, \\\"cr_directors\\\": [], \\\"cr_countries\\\": [], \\\"video_game_id\\\": null, \\\"video_game\\\": null}".replace("\\\"", "\""), Map.class);
        Assert.assertEquals(10, deserialized.value);
        Assert.assertEquals("blabla", deserialized.stringValue);
        Assert.assertEquals("GoBga_-yK3aqyPQGbLwfKg", des2.get("eid"));
        Assert.assertEquals("The Prophet Apple", des2.get("title"));
        Assert.assertEquals("Mother Up", ((Map) des2.get("show")).get("name"));
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
