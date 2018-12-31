package com.stalary.pf.gateway.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ProjectInfo
 *
 * @author lirongqian
 * @since 2018/04/09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectInfo {

    private Long projectId;

    private String key;
}