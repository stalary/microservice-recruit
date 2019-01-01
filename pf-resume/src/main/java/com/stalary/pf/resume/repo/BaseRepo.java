package com.stalary.pf.resume.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * @author Stalary
 * @description
 * @date 2018/4/13
 */
@NoRepositoryBean
public interface BaseRepo<T, ID extends Serializable> extends MongoRepository<T, ID> {

}
