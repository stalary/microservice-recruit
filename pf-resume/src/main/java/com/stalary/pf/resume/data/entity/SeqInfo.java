package com.stalary.pf.resume.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * SeqInfo
 * 用于mongodb产生自增id
 * @author lirongqian
 * @since 2018/04/13
 */
@Data
@AllArgsConstructor
public class SeqInfo {

    /**
     * 主键
     */
    @Id
    private String id;

    /**
     * 集合名称
     */
    private String collName;

    /**
     * 序列值
     */
    @Field
    private long seqId;
}