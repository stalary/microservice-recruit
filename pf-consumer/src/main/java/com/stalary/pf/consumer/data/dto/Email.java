/**
 * @(#)Email.java, 2019-01-01.
 *
 * Copyright 2019 Stalary.
 */
package com.stalary.pf.consumer.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Email
 *
 * @author lirongqian
 * @since 2019/01/01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Email {

    private String email;

    private String title;

    private String content;
}