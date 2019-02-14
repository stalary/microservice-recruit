
package com.stalary.pf.user.data.dto;

import com.stalary.pf.user.data.entity.UserEs;
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

    private List<UserEs> userList;
}