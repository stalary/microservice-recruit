package com.stalary.pf.resume.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Skill
 *
 * @author lirongqian
 * @since 2018/04/13
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Skill extends BaseEntity {

    /**
     * 技能名称
     */
    private String name;

    /**
     * 熟练程度，分为了解，熟悉，掌握，精通，分别对应1，2，3，4(为了方便计算分数)
     */
    private int level;

    /**
     * 存储简历id
     */
    private long resumeId;

}