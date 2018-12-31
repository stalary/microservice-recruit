package com.stalary.pf.message.data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * @author stalary
 * @model Message
 * @description 站内信对象
 * @field id id
 * @field fromId 发送方id，系统发送则id为0
 * @field toId 接收方id
 * @field title 站内信标题
 * @field content 内容
 * @field readState 是否已阅
 * @field updateTime 更新时间
 * @field createTime 创建时间
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "message")
@Entity
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fromId;

    private Long toId;

    private String title;

    private String content;

    private Boolean readState = false;

    @JsonIgnore
    @UpdateTimestamp
    private Date updateTime;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}