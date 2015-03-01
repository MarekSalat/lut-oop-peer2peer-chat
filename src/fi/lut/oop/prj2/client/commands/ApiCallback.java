package fi.lut.oop.prj2.client.commands;

import fi.lut.oop.prj2.client.ChatRequest;
import fi.lut.oop.prj2.client.Peer2PeerChatProtocol;

/**
* User: Marek Salát
* Date: 11.3.14
* Time: 13:49
*/
public interface ApiCallback {
    void onApiCall(Peer2PeerChatProtocol sender, ChatRequest request);
    public String getCommandName();
    public boolean isThisCommandDataInputValid(String input);
}
