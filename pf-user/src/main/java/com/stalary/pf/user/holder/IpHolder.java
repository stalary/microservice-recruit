
package com.stalary.pf.user.holder;

/**
 * IpHolder
 *
 * @author lirongqian
 * @since 2019/03/01
 */
public class IpHolder {

    private static final ThreadLocal<String> ipThreadLocal = new ThreadLocal<>();

    public static String get() {
        return ipThreadLocal.get();
    }

    public static void set(String ip) {
        ipThreadLocal.set(ip);
    }

    public static void remove() {
        ipThreadLocal.remove();
    }
}