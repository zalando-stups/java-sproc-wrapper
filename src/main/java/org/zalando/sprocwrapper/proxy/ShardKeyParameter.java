package org.zalando.sprocwrapper.proxy;

/**
 * @author jmussler
 */
class ShardKeyParameter {

    private final int pos;
    private final Class<?> type;

    public ShardKeyParameter(final int pos, final Class<?> type) {
        this.pos = pos;
        this.type = type;
    }

    public int getPos() {
        return pos;
    }

    public Class<?> getType() {
        return type;
    }
}
