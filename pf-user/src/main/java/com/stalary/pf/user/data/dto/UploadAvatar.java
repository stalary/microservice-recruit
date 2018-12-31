/**
 * @(#)UploadAvatar.java, 2019-01-01.
 *
 * Copyright 2019 Stalary.
 */
package com.stalary.pf.user.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UploadAvatar
 *
 * @author lirongqian
 * @since 2019/01/01
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UploadAvatar {

    private Long userId;

    private String avatar;
}