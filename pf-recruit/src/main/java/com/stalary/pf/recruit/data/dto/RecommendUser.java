
package com.stalary.pf.recruit.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * RecommendUser
 *
 * @author lirongqian
 * @since 2019/02/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendUser {

    private Long recruitId;

    private List<UserInfo> userList;
}