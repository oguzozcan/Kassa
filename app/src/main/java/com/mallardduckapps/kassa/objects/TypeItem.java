package com.mallardduckapps.kassa.objects;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 */
public class TypeItem {

    /**
     * A item representing a piece of content.
     */
    public final int type;
    public final String name;
    public final int resId;
    public final int typeId;

    public TypeItem(int type, String name, int resId, int typeId) {
        this.type = type;
        this.name = name;
        this.resId = resId;
        this.typeId = typeId;
    }

    @Override
    public String toString() {
        return name;
    }
}
