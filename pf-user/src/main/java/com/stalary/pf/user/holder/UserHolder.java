package com.stalary.pf.user.holder;

import com.stalary.pf.user.data.dto.User;

/**
 * UserHolder
 *
 * @author lirongqian
 * @since 2018/04/16
 */
public class UserHolder {

    private static final ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public static User get() {
        return userThreadLocal.get();
    }

    public static void set(User user) {
        userThreadLocal.set(user);
    }

    public static void remove() {
        userThreadLocal.remove();
    }
}