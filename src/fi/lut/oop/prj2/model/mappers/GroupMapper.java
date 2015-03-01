package fi.lut.oop.prj2.model.mappers;

import fi.lut.oop.prj2.model.Mapper;
import fi.lut.oop.prj2.model.entities.Group;

/**
 * User: Marek Salát
 * Date: 19.3.14
 * Time: 16:02
 */
public interface GroupMapper extends Mapper<String, Group> {

    public static class MemCacheGroupMapper extends MemCacheMapper<String, Group> implements GroupMapper {

    }
}
