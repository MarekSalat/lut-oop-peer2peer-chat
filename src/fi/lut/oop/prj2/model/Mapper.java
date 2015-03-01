package fi.lut.oop.prj2.model;

import java.io.Serializable;
import java.util.List;

/**
 * User: Marek Salát
 * Student number: 0426412
 * Date: 9.3.14
 * Time: 13:11
 */
public interface Mapper<ID, E extends Entity> extends Serializable{
    E findOne(ID id);
    List<E> findAll();
    void add(E entity);
    void delete(E entity);
}
