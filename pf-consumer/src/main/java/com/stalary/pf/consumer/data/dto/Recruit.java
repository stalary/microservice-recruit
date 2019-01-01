package com.stalary.pf.consumer.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @model Recruit
 * @description 招聘信息
 * @field hrId 关联的hrId
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recruit {

    private Long hrId;
}