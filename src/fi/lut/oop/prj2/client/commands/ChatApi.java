package fi.lut.oop.prj2.client.commands;

import fi.lut.oop.prj2.client.ChatRequest;
import fi.lut.oop.prj2.client.Peer2PeerChatProtocol;
import fi.lut.oop.prj2.model.entities.User;
import fi.lut.oop.prj2.model.mappers.UserMapper;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Marek Salát
 * Date: 11.3.14
 * Time: 13:39
 */
public class ChatApi {

    //    enum CommandType {
    //        ECHO,
    //        GET_ROUTES_TABLE, /* -> */ SET_ROUTES_TABLE,
    //        ONLINE,
    //        OFFLINE,
    //        MESSAGE,
    //        GROUP_MSG,
    //    }

    public static abstract class AbstractApiCallback implements ApiCallback {
        @Override
        public String getCommandName() {
            return this.getClass().getSimpleName();
        }

        @Override
        public boolean isThisCommandDataInputValid(String input) {
            return getCommandName().equals(input);
        }
    }

    public static class Echo extends  AbstractApiCallback {
        @Override
        public void onApiCall(Peer2PeerChatProtocol sender, ChatRequest request) {

            request.fromUser.port = Integer.parseInt(request.commandData);

            ChatRequest response = new ChatRequest(
                sender.getCurrentUser(),
                null,
                getCommandName(),
                "",
                ""
            );

            try {
                sender.sendRequestWithoutResponse(request.socket, response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public  static class SendEcho extends  AbstractApiCallback {
        @Override
        public void onApiCall(Peer2PeerChatProtocol sender, ChatRequest request) {
            User toUser = request.toUser;

            if(toUser == null){
                String param = request.commandData.replaceFirst("echo ", "");

                toUser = new User("");
                String[] split = param.split(":");
                toUser.address = split[0];
                toUser.port = Integer.parseInt(split[1]);
            }

            ChatRequest chatRequest = new ChatRequest(
                    request.fromUser,
                    toUser,
                    Echo.class.getSimpleName(),
                    String .valueOf(sender.getCurrentUser().port),
                    ""
            );

            try {
                ChatRequest response = sender.sendRequest(chatRequest);
                System.out.println("<--| " + response.toString());
//                sender.callApiCallback(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean isThisCommandDataInputValid(String input) {
            return input.matches("^echo [aA0-zZ9.]+:[0-9]+$");
        }
    }

    public static class UpdateRouteTable extends AbstractApiCallback {

        @Override
        public void onApiCall(Peer2PeerChatProtocol sender, ChatRequest callRequest) {
            User boostPeer = callRequest.toUser;

            if(boostPeer == null){
                String param = callRequest.commandData.replaceFirst(getCommandName()+" ", "");

                String[] split = param.split(":");
                String peerAddress = split[0];
                int peerport = Integer.parseInt(split[1]);

                boostPeer = new User("");
                boostPeer.address = peerAddress;
                boostPeer.port = peerport;
            }

            ChatRequest request = new ChatRequest(
                    callRequest.fromUser,
                    boostPeer,
                    GetRoutingTable.class.getSimpleName(),
                    String.valueOf(sender.getCurrentUser().port),
                    ""
            );

            ChatRequest response = null;
            try {
                response = sender.sendRequest(request);
            } catch (IOException e) {
                //e.printStackTrace();
            }

            if(response == null)
                System.err.println("|--| Routing table cannot be initialized. Try command type '"+getCommandName()+" host:port'");

            System.out.println("<--| " + response);

            sender.callApiCallback(SetRouteTable.class.getSimpleName(), response);

        }

        @Override
        public boolean isThisCommandDataInputValid(String input) {
            return input.matches("^"+getCommandName()+" [aA0-zZ9.]+:[0-9]+$");
        }
    }

    public static class SetRouteTable extends AbstractApiCallback {
        @Override
        public void onApiCall(Peer2PeerChatProtocol sender, ChatRequest request) {
            if(!"OK".equals(request.commandData))
                return;

            String[] clients = request.content.split(";");
            for(String client : clients){
                String[] split = client.split(":");
                User user = new User();
                user.setId(split[0]);
                user.address = split[1];
                user.port = Integer.parseInt(split[2]);
                user.state = User.State.valueOf(split[3]);

                UserMapper db = sender.getRouteTable();
                User one = db.findOne(user.getId());

                if(user.getId().equals(sender.getCurrentUser().getId())) continue;
                if(one != null ) {
                    one.state = user.state;
                    continue;
                }

                db.add(user);

                ApiCallback callback = new SendEcho();
                callback.onApiCall(sender, new ChatRequest(
                        sender.getCurrentUser(),
                        user,
                        SendEcho.class.getSimpleName(),
                        "",
                        ""
                ));
            }
        }

        @Override
        public boolean isThisCommandDataInputValid(String input) {
            return super.isThisCommandDataInputValid(input);
        }
    }

    public static class GetRoutingTable extends AbstractApiCallback {
        @Override
        public void onApiCall(Peer2PeerChatProtocol sender, ChatRequest request) {
            request.fromUser.port = Integer.parseInt(request.commandData);

            String data = createData(sender.getRouteTable());

            ChatRequest response = new ChatRequest(
                    sender.getCurrentUser(),
                    null,
                    "SetRouteTable",
                    "OK",
                    data
            );

            try {
                sender.sendRequestWithoutResponse(request.socket, response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String createData(UserMapper routeTable) {
            boolean added = false;
            StringBuilder sb = new StringBuilder();

            List<User> findAll = routeTable.findAll();
            for (int i = 0; i < findAll.size(); i++) {
                User user = findAll.get(i);
                added = true;

                sb.append(user.getId() + ":" + user.address + ":" + user.port + ":" + user.state);
                if (i != findAll.size()-1)
                    sb.append(";");
            }

            if(!added)
                sb.append("NONE");

            return sb.toString();
        }
    }

    public static class Message extends AbstractApiCallback {
        @Override
        public void onApiCall(Peer2PeerChatProtocol sender, ChatRequest request) {
            // incoming message
//            System.out.println("|--| " + request.fromUser.getId()+"<"+request.content);
        }
    }

    /****/

    public static class SendMessage extends  AbstractApiCallback{

        final String PATTERN = "^[\\w]+ .*$";
        private final UserMapper db;

        public SendMessage(UserMapper db) {
            this.db = db;
        }

        protected String getUserNameFromCommand(String line){
            return line.replaceFirst(" .*", "");
        }

        protected String getContentFromCommand(String line){
            return line.replaceFirst("^[\\w]+ ", "");
        }

        @Override
        public void onApiCall(Peer2PeerChatProtocol sender, ChatRequest request) {
            int len = db.findAll().size();
            if(len <= 0) {
                System.err.println("|--| There is no one to send this message :(");
                return;
            }

            String nameOfTargetUser = getUserNameFromCommand(request.commandData);
            String msgContent = getContentFromCommand(request.commandData);

            List<User> toUsers;

            if("all".equals(nameOfTargetUser))
                toUsers = db.findAll();
            else {
                toUsers = new ArrayList<User>();
                User toUser = db.findOne(nameOfTargetUser);

                if(toUser == null){
                    System.err.println("|--| No such user :(");
                    return;
                }
                toUsers.add(toUser);
            }

            for (User toUser : toUsers){
                ChatRequest msgRequest = new ChatRequest(
                        sender.getCurrentUser(),
                        toUser,
                        Message.class.getSimpleName(),
                        "",
                        msgContent
                );

                try {
                    sender.sendRequestWithoutResponse(msgRequest);
                }
                catch (ConnectException e){
                    msgRequest.toUser.state = User.State.OFFLINE;
                    System.err.println("|--| Cannot create connection to user " + nameOfTargetUser);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public boolean isThisCommandDataInputValid(String input) {
            if(input.matches("^all .*")) return true;
            if(!input.matches(PATTERN)) return false;
            return db.findOne(getUserNameFromCommand(input)) != null;
        }
    }

    public static class UserList extends AbstractApiCallback {
        @Override
        public void onApiCall(Peer2PeerChatProtocol sender, ChatRequest request) {
            StringBuilder sb = new StringBuilder();

            String param = request.commandData.replaceFirst("show", "");
            param = param.replaceFirst(" ", "");

            List<User> all = sender.getRouteTable().findAll();

            boolean added = false;
            for(User user : all){
                if(param.isEmpty() || user.state.toString().equals(param.toUpperCase())){
                    added = true;
                    sb.append("|--| " + user.getId()+" is " + user.state + " " + user.address + ":" +user.port+"\n" );
                }
            }

            if(!added)
                sb.append("|--| None");

            System.out.println(sb.toString());
        }

        @Override
        public boolean isThisCommandDataInputValid(String input) {
            return input.matches("^show$|^show (online|offline|unknown)$");
        }
    }


   public static class ChangeVisibilityState extends AbstractApiCallback {
       @Override
       public void onApiCall(Peer2PeerChatProtocol sender, ChatRequest request) {
            User.State state = User.State.valueOf(request.commandData.toUpperCase());

           User currentUser = sender.getCurrentUser();
           currentUser.state = state;

            for(User user: sender.getRouteTable().findAll()){
                if(user.state != User.State.ONLINE) continue;

                try {
                    sender.sendRequest(new ChatRequest(
                            currentUser,
                            user,
                            SetRouteTable.class.getSimpleName(),
                            "OK",
                            currentUser.getId()+":"+currentUser.address+":"+currentUser.port+":"+state
                    ));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
       }

       @Override
       public boolean isThisCommandDataInputValid(String input) {
           return input.matches("^(offline|online)$");
       }
   }
}
