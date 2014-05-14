package cz.vity.freerapid.gui.managers.utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Vity
 */
public class SimpleCache<K, T> {
    private Map<K, T> cache;

    public SimpleCache(final int cacheSize) {
        cache = new LinkedHashMap<K, T>(cacheSize + 1, .75F, true) {
            // This method is called just after a new entry has been added
            public boolean removeEldestEntry(Map.Entry eldest) {
                return size() > cacheSize;
            }
        };
        cache = Collections.synchronizedMap(cache);
    }

//    public synchronized Map.Entry<K, T> getAllItems() {
//        return cache.entrySet();
//    }
//
//    public synchronized void addItem(T item) {
//        cache.put(item.hashCode(), item);
//    }

}
