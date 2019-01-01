package com.stalary.pf.user.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @model SendInfo
 * @description 投递信息
 * @field sendList 岗位列表(见SendResume)
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendInfo {

    private List<SendResume> sendList;
}