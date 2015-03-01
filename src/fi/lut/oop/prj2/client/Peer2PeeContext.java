package fi.lut.oop.prj2.client;

import fi.lut.oop.prj2.model.entities.User;
import fi.lut.oop.prj2.model.mappers.GroupMapper;
import fi.lut.oop.prj2.model.mappers.UserMapper;

/**
 * User: Marek Salát
 * Date: 11.3.14
 * Time: 15:32
 *
 * Envelop for context needed by chat api and chat protocol
 */
public class Peer2PeeContext {
    public User currentUser;
    public User boostPeerUser;
    public UserMapper userDb;
    public Peer2PeerChatProtocol protocol;
    public GroupMapper groupDb;

    public Peer2PeeContext(User currentUser, User boostPeerUser, UserMapper userDb, Peer2PeerChatProtocol protocol, GroupMapper groupDb) {
        this.currentUser = currentUser;
        this.boostPeerUser = boostPeerUser;
        this.userDb = userDb;
        this.protocol = protocol;
        this.groupDb = groupDb;
    }
}
