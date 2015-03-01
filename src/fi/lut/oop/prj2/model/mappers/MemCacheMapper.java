package fi.lut.oop.prj2.model.mappers;

import fi.lut.oop.prj2.model.Entity;
import fi.lut.oop.prj2.model.Mapper;
import fi.lut.oop.prj2.model.entities.AbstractEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Marek Salát
 * Date: 9.3.14
 * Time: 14:07
 */
public class MemCacheMapper<ID, E extends AbstractEntity<ID>> implements Mapper<ID, E> {
    Map<ID, E> db = new HashMap<ID, E>();

    @Override
    public E findOne(ID s) {
        synchronized (db){
            return db.get(s);
        }
    }

    @Override
    public List<E> findAll() {
        synchronized (db){
            return new ArrayList<E>(db.values());
        }
    }

    @Override
    public void add(E entity) {
        if(entity.getEntityState() == Entity.State.MANAGED)
            return;

        synchronized (db){
            db.put(entity.getId(), entity);
            entity.setState(Entity.State.MANAGED);
        }
    }


    @Override
    public void delete(E entity) {
        synchronized (db){
            db.remove(entity.getId());
            entity.setState(Entity.State.DELETED);
        }
    }

    @Override
    public String toString() {
        return "MemCacheUserMapper{" +
                "db=" + db +
                '}';
    }
}
