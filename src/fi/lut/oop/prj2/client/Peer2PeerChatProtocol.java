package fi.lut.oop.prj2.client;

import fi.lut.oop.prj2.client.commands.ApiCallback;
import fi.lut.oop.prj2.client.commands.ChatApi;
import fi.lut.oop.prj2.model.entities.User;
import fi.lut.oop.prj2.model.mappers.UserMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Marek Salát
 * Date: 10.3.14
 * Time: 21:58
 *
 * It handles incoming request from other client. It is able to send request with ot without response.
 * Api is extensible via ChatApiCallback. Some basic API is provided in ChatApi class.
 * Remember to call init() before using this class.
 *
 * It only delegates incoming request to the relevant callback
 */
public class Peer2PeerChatProtocol {

    public interface OnNewUser {
        public void onNewUser(Peer2PeerChatProtocol sender, User user);
    }

    protected Map<String, ApiCallback> apiCallbacks = new HashMap<String, ApiCallback>();
    protected List<OnNewUser> onNewUsersCallbacks = new ArrayList<OnNewUser>();

    protected UserMapper routeTable;
    protected User currentUser;
    protected User boostPeer;

    public Peer2PeerChatProtocol(User currentUser, User boostPeer, UserMapper routeTable) {
        this.currentUser = currentUser;
        this.boostPeer = boostPeer;
        this.routeTable = routeTable;


        this.addApiCallback(new ChatApi.UpdateRouteTable());
        this.addApiCallback(new ChatApi.SetRouteTable());
        this.addApiCallback(new ChatApi.GetRoutingTable());
        this.addApiCallback(new ChatApi.Echo());
        this.addApiCallback(new ChatApi.Message());
    }

    public void addApiCallback(ApiCallback callback) {
        apiCallbacks.put(callback.getCommandName(), callback);
    }

    public void callApiCallback(String name, ChatRequest request){
        ApiCallback callback = apiCallbacks.get(name);
        if(callback == null) {
            System.err.println("|--| No api found for " + name);
            return;
        };
        callback.onApiCall(this, request);
    }

    public void init() throws Exception {
        if(boostPeer == null) return;

        String initName = new ChatApi.UpdateRouteTable().getCommandName();
        callApiCallback(initName, new ChatRequest(
            currentUser,
            null,
            "",
            boostPeer.address+":"+boostPeer.port,
            ""
        ));
    }

    public void onIncomingRequest(Socket clientSocket){
        String rawRequest;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while ((rawRequest = in.readLine()) != null) {
                break;
            }

            System.out.println("<--| " + rawRequest);

            ChatRequest request = rawRequest2ChatRequest(clientSocket, rawRequest);
            if(request == null) return;

            request.socket = clientSocket;

            callApiCallback(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void callApiCallback(ChatRequest request){
        callApiCallback(request.command, request);
    }

    public ChatRequest rawRequest2ChatRequest(Socket socket, String rawRequest){
        if(!ChatRequest.isRawRequestValid(rawRequest)) return null;

        ChatRequest request = new ChatRequest(rawRequest);
        User fromUser = routeTable.findOne(request.fromUser.getId());

        if(fromUser == null){
            fromUser = new User(request.fromUser.getId());
            fromUser.address = socket.getInetAddress().getHostAddress();
            fromUser.port = socket.getPort();
            fromUser.state = User.State.ONLINE;

            onNewUser(fromUser);
        }

        request.fromUser = fromUser;

        request.fromUser.state = User.State.ONLINE;
        request.toUser =  currentUser;

        return request;
    }

    public static Socket createSocket(User user) throws IOException {
        return new Socket(user.address, user.port);
    }

    public ChatRequest sendRequest(ChatRequest request) throws IOException {
        return sendRequest(request, true);
    }

    public void sendRequestWithoutResponse(ChatRequest request) throws IOException {
        sendRequest(request, false);
    }

    private ChatRequest sendRequest(ChatRequest request, boolean waitForResponse) throws IOException {
        assert request.fromUser != null;
        assert request.toUser != null;

        String  rawResponse;
        Socket socket = null;
        try {
            socket = request.socket != null ? request.socket : createSocket(request.toUser);
            rawResponse = sendRequest(socket, request.toString(), waitForResponse);

            if(rawResponse == null) return null;

            return rawRequest2ChatRequest(socket, rawResponse);
        }
        finally {
            if(request.socket == null && socket != null)
                socket.close();
        }
    }

    private void onNewUser(User user) {
        if(user == null) return;

        routeTable.add(user);
    }

    public void sendRequestWithoutResponse(Socket socket, String content) throws IOException {
        sendRequest(socket, content, false);
    }

    public String sendRequest(Socket socket, String content, boolean waitForResponse) throws IOException {
        assert socket != null;
        assert content != null;

        String rawResponse;
        PrintWriter out = null;
        BufferedReader in = null;

        System.out.println("|--> " + content);

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader( new InputStreamReader(socket.getInputStream()));

        out.println(content);

        if(!waitForResponse) return null;

        while ((rawResponse = in.readLine()) != null) {
            break;
        }

        return rawResponse;
    }

    public UserMapper getRouteTable() {
        return routeTable;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public User getBoostPeer() {
        return boostPeer;
    }
}
