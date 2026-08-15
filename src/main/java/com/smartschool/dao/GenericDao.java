package com.smartschool.dao;

import java.util.List;
import java.util.Optional;

/**
 * Generic DAO interface providing standard CRUD operations.
 * All entity-specific DAOs extend this interface.
 *
 * @param <T>  The entity type
 * @param <ID> The primary key type
 */
public interface GenericDao<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    T update(T entity);
    boolean delete(ID id);
    long count();
}
