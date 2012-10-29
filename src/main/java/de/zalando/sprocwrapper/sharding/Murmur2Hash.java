package de.zalando.sprocwrapper.sharding;

/**
 * This is a very fast, non-cryptographic hash suitable for general hash-based lookup. See
 * http://murmurhash.googlepages.com/ for more details.
 *
 * <p>The C version of MurmurHash 2.0 found at that site was ported to Java by Andrzej Bialecki (ab at getopt org).</p>
 *
 * see also
 * http://javasourcecode.org/html/open-source/hbase/hbase-0.90.3/org/apache/hadoop/hbase/util/MurmurHash.java.html
 */
public class Murmur2Hash {
    public static int hash(final byte[] data, final int seed) {
        int m = 0x5bd1e995;
        int r = 24;

        int len = data.length;
        int h = seed ^ len;

        int len_4 = len >> 2;

        for (int i = 0; i < len_4; i++) {
            int i_4 = i << 2;
            int k = data[i_4 + 3];
            k = k << 8;
            k = k | (data[i_4 + 2] & 0xff);
            k = k << 8;
            k = k | (data[i_4 + 1] & 0xff);
            k = k << 8;
            k = k | (data[i_4 + 0] & 0xff);
            k *= m;
            k ^= k >>> r;
            k *= m;
            h *= m;
            h ^= k;
        }

        int len_m = len_4 << 2;
        int left = len - len_m;

        if (left != 0) {
            if (left >= 3) {
                h ^= (int) data[len - 3] << 16;
            }

            if (left >= 2) {
                h ^= (int) data[len - 2] << 8;
            }

            if (left >= 1) {
                h ^= (int) data[len - 1];
            }

            h *= m;
        }

        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;

        return h;
    }

    /* Testing ...
     * static int NUM = 1000;
     *
     * public static void main(String[] args) {
     * byte[] bytes = new byte[4];
     * for (int i = 0; i < NUM; i++) {
     *  bytes[0] = (byte)(i & 0xff);
     *  bytes[1] = (byte)((i & 0xff00) >> 8);
     *  bytes[2] = (byte)((i & 0xff0000) >> 16);
     *  bytes[3] = (byte)((i & 0xff000000) >> 24);
     *  System.out.println(Integer.toHexString(i) + " " + Integer.toHexString(hash(bytes, 1)));
     * }
     *} */
}
