package com.stalary.pf.message.repo;

import com.stalary.pf.message.data.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Stalary
 * @description
 * @date 2018/4/17
 */
@Transactional(rollbackFor = Exception.class)
@Repository
public interface MessageRepo extends JpaRepository<MessageEntity, Long> {

    /**
     * 通过toId查找接收的站内信
     * @param toId
     * @return
     */
    List<MessageEntity> findByToIdOrderByCreateTimeDesc(Long toId);

    /**
     * 通过fromId查找发送的站内信
     */
    List<MessageEntity> findByFromId(Long fromId);

    /**
     * 设置状态为已读
     * @param id
     */
    @Modifying
    @Query("update MessageEntity m set m.readState=true where m.id=?1")
    void read(Long id);

    /**
     * 查找收到的未读通知
     * @param fromId
     * @param readState
     * @return
     */
    List<MessageEntity> findByToIdAndReadState(Long fromId, Boolean readState);
}
