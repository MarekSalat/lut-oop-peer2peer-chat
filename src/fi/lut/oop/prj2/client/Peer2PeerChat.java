package fi.lut.oop.prj2.client;

import fi.lut.oop.prj2.client.commands.ApiCallback;
import fi.lut.oop.prj2.client.commands.ChatApi;
import fi.lut.oop.prj2.client.commands.GroupChatApi;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Marek Salát
 * Date: 10.3.14
 * Time: 18:23
 *
 * It handles input command from user. Api is extensible via ChatApiCallback. Some basic api is provided in ChatApi class.
 *
 * It only delegates user commands to the all relevant callbacks.
 */
public class Peer2PeerChat extends Thread implements InputReaderWorker.OnInputReady, Server.OnServerAccept{

    protected Map<String, ApiCallback> apiCallbacks = new HashMap<String, ApiCallback>();
    protected Peer2PeeContext context;
    protected GroupChatApi groupChatApi;

    public Peer2PeerChat(Peer2PeeContext context) {
        this.context = context;

        addApiCallback(new ChatApi.SendEcho());
        addApiCallback(new ChatApi.UpdateRouteTable());
        addApiCallback(new ChatApi.SendMessage(context.userDb));
        addApiCallback(new ChatApi.UserList());
        addApiCallback(new ChatApi.ChangeVisibilityState());

        groupChatApi = new GroupChatApi(context.userDb, context.groupDb);
        context.protocol.addApiCallback(groupChatApi.groupMessage);

        addApiCallback(groupChatApi.createGroup);
        addApiCallback(groupChatApi.showGroup);
        addApiCallback(groupChatApi.sendGroupMessage);
        addApiCallback(groupChatApi.groupMessage);
    }

    public void addApiCallback(ApiCallback callback) {
        apiCallbacks.put(callback.getCommandName(), callback);
    }

    @Override
    public void run() {
        new Server(context.currentUser.port, this).start();
        new InputReaderWorker(this).start();
    }

    @Override
    public void onServerAccept(Socket socket) {
        context.protocol.onIncomingRequest(socket);
    }

    @Override
    public void onInputReady(String line) {
        callChatApiCallbacksForCommand(line);
    }

    public void callChatApiCallbacksForCommand(String line) {
        List<ApiCallback> callbacks = getApiCallbacksForCommand(line);
        if(callbacks.isEmpty()){
            System.err.println("|--| No command found :(");
            return;
        }

        for(ApiCallback callback : callbacks){
            callback.onApiCall(this.context.protocol, new ChatRequest(
                this.context.currentUser,
                null,
                callback.getCommandName(),
                line,
                ""
            ));
        }
    }

    public boolean isChatApiCommand(String line) {
        return !getApiCallbacksForCommand(line).isEmpty();
    }

    public List<ApiCallback> getApiCallbacksForCommand(String line){
        List<ApiCallback> list = new ArrayList<ApiCallback>();
        for(ApiCallback callback: apiCallbacks.values()){
            if(!callback.isThisCommandDataInputValid(line))
                continue;
            list.add(callback);
        }
        return list;
    }
}
