package fi.lut.oop.prj2;

import fi.lut.oop.prj2.client.Peer2PeerConsoleChat;
import fi.lut.oop.prj2.model.entities.User;
import junit.framework.Assert;
import org.junit.Test;

/**
 * User: Marek Salát
 * Date: 13.3.14
 * Time: 15:37
 */
public class MainTest {

    private static void startChatAndWait(Peer2PeerConsoleChat chat) throws InterruptedException {
        chat.start();

        while (chat.peer2PeerChat == null)
            Thread.sleep(500);
    }


    @Test
    public void testEchoAndRouteTables() throws Exception {
        Peer2PeerConsoleChat chat1 = new Peer2PeerConsoleChat();
        Peer2PeerConsoleChat chat2 = new Peer2PeerConsoleChat();

        startChatAndWait(chat1);
        startChatAndWait(chat2);

        chat2.peer2PeerChat.callChatApiCallbacksForCommand("echo localhost:"+chat1.user.port);

        chat1.peer2PeerChat.callChatApiCallbacksForCommand("show");
        chat2.peer2PeerChat.callChatApiCallbacksForCommand("show");

        chat1.peer2PeerChat.callChatApiCallbacksForCommand(chat2.user.getId() + " jak se mas?");
        chat2.peer2PeerChat.callChatApiCallbacksForCommand(chat1.user.getId() + " mam se dobre :)");

        User chat1User = chat1.user;
        User chat2UserInRouteTable = chat2.userDb.findAll().get(0);

        Assert.assertEquals(chat1User.getId(), chat2UserInRouteTable.getId());
        Assert.assertEquals(chat1User.port, chat2UserInRouteTable.port);
    }

    @Test
    public void testGetRouteTable() throws Exception {
        Peer2PeerConsoleChat chat1 = new Peer2PeerConsoleChat();
        Peer2PeerConsoleChat chat2 = new Peer2PeerConsoleChat(chat1.user.address, chat1.user.port);

        startChatAndWait(chat1);
        startChatAndWait(chat2);

        Assert.assertEquals(chat1.userDb.findAll().size(), 1);
        Assert.assertEquals(chat2.userDb.findAll().size(), 1);
    }

    @Test
    public void testMakeOfflineOnline() throws Exception {
        Peer2PeerConsoleChat chat1 = new Peer2PeerConsoleChat();
        Peer2PeerConsoleChat chat2 = new Peer2PeerConsoleChat(chat1.user.address, chat1.user.port);
        Peer2PeerConsoleChat chat3 = new Peer2PeerConsoleChat(chat2.user.address, chat2.user.port);

        startChatAndWait(chat1);
        startChatAndWait(chat2);
        startChatAndWait(chat3);

        Assert.assertEquals(chat1.userDb.findAll().size(), 2);
        Assert.assertEquals(chat2.userDb.findAll().size(), 2);
        Assert.assertEquals(chat3.userDb.findAll().size(), 2);

        chat2.peer2PeerChat.callChatApiCallbacksForCommand("offline");

        Assert.assertEquals(chat2.user.state, User.State.OFFLINE);
        Assert.assertEquals(chat1.userDb.findOne(chat2.user.getId()).state, User.State.OFFLINE);
        Assert.assertEquals(chat3.userDb.findOne(chat2.user.getId()).state, User.State.OFFLINE);

        chat2.peer2PeerChat.callChatApiCallbacksForCommand("online");

        Assert.assertEquals(chat2.user.state, User.State.ONLINE);
        Assert.assertEquals(chat1.userDb.findOne(chat2.user.getId()).state, User.State.ONLINE);
        Assert.assertEquals(chat3.userDb.findOne(chat2.user.getId()).state, User.State.ONLINE);
    }

    @Test
    public void testMessageForAll() throws InterruptedException {
        Peer2PeerConsoleChat chat1 = new Peer2PeerConsoleChat();
        Peer2PeerConsoleChat chat2 = new Peer2PeerConsoleChat(chat1.user.address, chat1.user.port);
        Peer2PeerConsoleChat chat3 = new Peer2PeerConsoleChat(chat2.user.address, chat2.user.port);

        startChatAndWait(chat1);
        startChatAndWait(chat2);
        startChatAndWait(chat3);

        System.out.flush();

        chat1.peer2PeerChat.callChatApiCallbacksForCommand("all how are you?");
    }

    @Test
    public void testGroupMessage() throws InterruptedException {
        Peer2PeerConsoleChat chat1 = new Peer2PeerConsoleChat();
        Peer2PeerConsoleChat chat2 = new Peer2PeerConsoleChat(chat1.user.address, chat1.user.port);
        Peer2PeerConsoleChat chat3 = new Peer2PeerConsoleChat(chat2.user.address, chat2.user.port);

        startChatAndWait(chat1);
        startChatAndWait(chat2);
        startChatAndWait(chat3);

        System.out.flush();

        chat1.peer2PeerChat.callChatApiCallbacksForCommand("new group prdel " + chat2.user.getId() + " " + chat3.user.getId());

        Assert.assertEquals(chat1.groupDb.findAll().size(), 1);
        Assert.assertEquals(chat1.groupDb.findAll().get(0).getId(), "prdel");
        Assert.assertEquals(chat1.groupDb.findAll().get(0).getUsers().size(), 2);

        chat1.peer2PeerChat.callChatApiCallbacksForCommand("show groups");
        chat1.peer2PeerChat.callChatApiCallbacksForCommand("group prdel hello world");

        Thread.sleep(1000);

        Assert.assertEquals(1,          chat2.groupDb.findAll().size());
        Assert.assertEquals("prdel",  chat2.groupDb.findAll().get(0).getId());
        Assert.assertEquals(2,          chat2.groupDb.findAll().get(0).getUsers().size());

        Assert.assertEquals(1,          chat3.groupDb.findAll().size());
        Assert.assertEquals("prdel",  chat3.groupDb.findAll().get(0).getId());
        Assert.assertEquals(2,          chat3.groupDb.findAll().get(0).getUsers().size());
    }
}
