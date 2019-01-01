package com.stalary.pf.resume.service;

import com.stalary.pf.resume.repo.BaseRepo;

import java.util.List;

/**
 * BaseService
 *
 * @author lirongqian
 * @since 2018/04/14
 */
public abstract class BaseService<T, R extends BaseRepo<T, Long>>  {

    protected R repo;

    BaseService(R repo) {
        this.repo = repo;
    }

    public T findOne(Long id) {
        return repo.findById(id).get();
    }

    public T save(T entity) {
        return repo.save(entity);
    }

    public void save(List<T> entityList) {
        repo.saveAll(entityList);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    public void delete(T entity) {
        repo.delete(entity);
    }
}