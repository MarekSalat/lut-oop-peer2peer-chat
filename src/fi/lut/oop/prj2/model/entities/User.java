package fi.lut.oop.prj2.model.entities;

import fi.lut.oop.prj2.client.Utils;

/**
 * User: Marek Salát
 * Date: 9.3.14
 * Time: 13:19
 */
public class User extends AbstractEntity<String> {
    public static enum State {
        UNKNOWN,
        ONLINE,
        OFFLINE,
    }

    public String address;
    public int port;
    public State state = State.UNKNOWN;

    public User() {
        setId(Utils.randomString(5));
    }

    public User(String name) {
        setId(name);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + getId() + '\'' + ", " +
                "state='" + state + '\'' + ", " +
                "address='" + address + ":" + port + '\'' + ", " +
                "state='" + getEntityState() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (port != user.port) return false;
        if (address != null ? !address.equals(user.address) : user.address != null) return false;
        if (state != user.state) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + port;
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }
}
