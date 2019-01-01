package com.stalary.pf.resume.utils;

import com.stalary.pf.resume.data.entity.SeqInfoEntity;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * IdUtil
 *
 * @author lirongqian
 * @since 2018/04/14
 */
public class IdUtil {

    /**
     * 获取下一个id并更新表
     * @param collName
     * @param mongo
     * @return
     */
    public static Long getNextIdAndUpdate(String collName, MongoTemplate mongo) {
        Query query = new Query(Criteria.where("collName").is(collName));
        Update update = new Update();
        update.inc("seqId", 1);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(true);
        options.returnNew(true);
        SeqInfoEntity seq = mongo.findAndModify(query, update, options, SeqInfoEntity.class);
        return seq.getSeqId();
    }

    /**
     * 获取下一个id
     * @param collName
     * @param mongo
     * @return
     */
    public static Long getNextId(String collName, MongoTemplate mongo) {
        Query query = new Query(Criteria.where("collName").is(collName));
        SeqInfoEntity seq = mongo.findOne(query, SeqInfoEntity.class);
        // 当无数据时返回0
        return (seq == null ? 0 : seq.getSeqId()) + 1;
    }
}