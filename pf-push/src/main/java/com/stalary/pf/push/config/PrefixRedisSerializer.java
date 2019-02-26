
package com.stalary.pf.push.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.nio.charset.Charset;

/**
 * PrefixRedisSerializer
 *
 * @author lirongqian
 * @since 2018/10/21
 */
@Component
@Slf4j
public class PrefixRedisSerializer implements RedisSerializer<String> {

    private String prefix = "pf";

    private final Charset charset;

    public PrefixRedisSerializer() {
        this(Charset.forName("UTF8"));
    }

    public PrefixRedisSerializer(Charset charset) {
        Assert.notNull(charset, "Charset must not be null!");
        this.charset = charset;
    }


    @Override
    public String deserialize(byte[] bytes) {
        String saveKey = new String(bytes, charset);
        int indexOf = saveKey.indexOf(prefix);
        if (indexOf > 0) {
            log.warn("key缺少前缀");
        } else {
            saveKey = saveKey.substring(indexOf + prefix.length() + 1);
        }
        return saveKey;
    }

    @Override
    public byte[] serialize(String string) {
        String key = prefix + ":" + string;
        return key.getBytes(charset);
    }
}
