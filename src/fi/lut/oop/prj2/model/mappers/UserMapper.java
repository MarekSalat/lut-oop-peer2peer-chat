package fi.lut.oop.prj2.model.mappers;

import fi.lut.oop.prj2.model.Mapper;
import fi.lut.oop.prj2.model.entities.User;

import java.util.Iterator;

/**
 * User: Marek Salát
 * Date: 9.3.14
 * Time: 13:18
 */
public interface UserMapper extends Mapper<String, User> {

    public class MemCacheUserMapper extends MemCacheMapper<String, User> implements UserMapper{
        public User findByAddresAndPort(String address, int port) {
            User user = null;

            for (Iterator<User> iterator = findAll().iterator(); iterator.hasNext(); ) {
                user = iterator.next();
                if (address.equals(user.address) && port == user.port) break;
            }

            return user;
        }
    }
}
