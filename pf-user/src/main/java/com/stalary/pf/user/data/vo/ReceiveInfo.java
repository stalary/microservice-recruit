package com.stalary.pf.user.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @model ReceiveInfo
 * @description 简历列表
 * @field receiveList 收到的简历列表(见ReceiveResume)
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiveInfo {

    private List<ReceiveResume> receiveList = new ArrayList<>();
}