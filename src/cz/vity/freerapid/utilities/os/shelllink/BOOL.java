package cz.vity.freerapid.utilities.os.shelllink;

import com4j.ComEnum;

enum BOOL implements ComEnum {
    /**
     * <p>
     * The value of this constant is 1
     * </p>
     */
    APITRUE(1),
    /**
     * <p>
     * The value of this constant is 0
     * </p>
     */
    APIFALSE(0);

    private final int value;

    BOOL(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
