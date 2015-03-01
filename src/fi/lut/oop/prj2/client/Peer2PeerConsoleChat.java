package fi.lut.oop.prj2.client;

import fi.lut.oop.prj2.model.entities.User;
import fi.lut.oop.prj2.model.mappers.GroupMapper;
import fi.lut.oop.prj2.model.mappers.UserMapper;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * User: Marek Salát
 * Date: 12.3.14
 * Time: 13:40
 *
 * Peer2Peer Console Chat application.
 */
public class Peer2PeerConsoleChat extends Thread  {

    public User user;
    public User boostPeer;
    public Peer2PeeContext context;
    public Peer2PeerChatProtocol protocol;
    public Peer2PeerChat peer2PeerChat;
    public UserMapper userDb;
    public GroupMapper groupDb;

    public Peer2PeerConsoleChat() {
        this(Utils.randomString(5), 0, null, 0);
    }

    public Peer2PeerConsoleChat(String boostPeerAddress, int boostPeerPort) {
        this(Utils.randomString(5), 0, boostPeerAddress, boostPeerPort);
    }

    public Peer2PeerConsoleChat(String name, int port, String boostPeerAddress, int boostPeerPort) {
        super();

        if(port <= 0)
            port = getAvailablePort();

        user = new User(name);
        user.address = "localhost";
        user.port = port;

        if(boostPeerAddress == null || boostPeerAddress.isEmpty() || boostPeerPort <= 0)
            return;

        this.boostPeer = new User("boostPeer");
        boostPeer.address = boostPeerAddress;
        boostPeer.port =  boostPeerPort;
    }

    private int getAvailablePort(){
        int sport = 8080;
        try {
            ServerSocket s = new ServerSocket(0);
            sport = s.getLocalPort();
            s.close();
        } catch (IOException e) { }
        return sport;
    }

    @Override
    public void run() {
        userDb = new UserMapper.MemCacheUserMapper();
        groupDb = new GroupMapper.MemCacheGroupMapper();

        protocol = new Peer2PeerChatProtocol(user, boostPeer, userDb);

        try {
            protocol.init();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }

        context = new Peer2PeeContext(user, boostPeer, userDb, protocol, groupDb);
        peer2PeerChat = new Peer2PeerChat(context);

        // telnet:GetRoutingTable|>
        // telnet:Echo|>
        // telnet:Message|> jak se mame?

        peer2PeerChat.start();
        System.out.println("|--| " + user.getId()+" running and listening at port " + user.port);

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                peer2PeerChat.callChatApiCallbacksForCommand("offline");
            }
        });
    }
}
