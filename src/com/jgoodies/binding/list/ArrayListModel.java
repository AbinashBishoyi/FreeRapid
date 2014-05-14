package com.jgoodies.binding.list;

import java.util.Collection;

/**
 * This class was created for backward compatibility with an older version of JGoodies binding library.</br>
 * This class is used by XMLdecoder to load file lists. </br>
 * FRD itself should use the newer version of this class.
 *
 *
 * @author Vity
 */
@Deprecated
public class ArrayListModel<E> extends com.jgoodies.common.collect.ArrayListModel<E> {
    public ArrayListModel() {
    }

    public ArrayListModel(int initialCapacity) {
        super(initialCapacity);
    }

    public ArrayListModel(Collection<? extends E> c) {
        super(c);
    }
}
