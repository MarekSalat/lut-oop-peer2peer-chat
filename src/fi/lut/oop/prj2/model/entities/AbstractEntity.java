package fi.lut.oop.prj2.model.entities;

import fi.lut.oop.prj2.model.Entity;

/**
 * User: Marek Salát
 * Date: 9.3.14
 * Time: 13:19
 */
public class AbstractEntity<ID> implements Entity<ID> {
    private State state = State.CREATED;
    private ID id;

    @Override
    public State getEntityState() {
        return state;
    }

    @Override
    public void setState(State s) {
        this.state = s;
    }

    @Override
    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }
}
