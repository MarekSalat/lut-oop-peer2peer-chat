package fi.lut.oop.prj2.model;

import java.io.Serializable;

/**
 * User: Marek Salát
 * Student number: 0426412
 * Date: 9.3.14
 * Time: 13:11
 *
 * Base entity for all entities in application. Every entity should have some identifier
 */
public interface Entity<ID> extends Serializable{
    public enum State {
        CREATED,
        MANAGED,
        DELETED
    }

    State getEntityState();
    void setState(State s);

    ID getId();
}
