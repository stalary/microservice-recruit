package com.stalary.pf.recruit.data.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stalary.pf.recruit.data.dto.SkillRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * @model RecruitEntity
 * @description 招聘信息
 * @field companyId 关联的公司id
 * @field hrId 关联的hrId
 * @field content 招聘内容
 * @field title 招聘标题
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recruit")
@Entity
public class RecruitEntity extends BaseEntity {

    private Long companyId;

    private Long hrId;

    private String content;

    private String title;

    @Transient
    private List<SkillRule> skillList;

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    private String skillStr;

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public void serializeFields() {
        this.skillStr = JSONObject.toJSONString(skillList);
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public void deserializeFields() {
        this.skillList = JSONObject.parseObject(skillStr, new TypeReference<List<SkillRule>>(){});
    }

}