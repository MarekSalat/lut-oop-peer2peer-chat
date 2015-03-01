package fi.lut.oop.prj2.client.commands;

import fi.lut.oop.prj2.client.ChatRequest;
import fi.lut.oop.prj2.client.Peer2PeerChatProtocol;
import fi.lut.oop.prj2.client.Utils;
import fi.lut.oop.prj2.model.entities.Group;
import fi.lut.oop.prj2.model.entities.User;
import fi.lut.oop.prj2.model.mappers.GroupMapper;
import fi.lut.oop.prj2.model.mappers.UserMapper;

import java.io.IOException;
import java.util.List;

/**
 * User: Marek Salát
 * Date: 19.3.14
 * Time: 13:07
 */
public class GroupChatApi {



    private UserMapper users;
    private GroupMapper groups;

    public CreateGroup createGroup = new CreateGroup();
    public SendGroupMessage sendGroupMessage = new SendGroupMessage();
    public GroupMessage groupMessage = new GroupMessage();
    public ShowGroup showGroup = new ShowGroup();


    public GroupChatApi(UserMapper users, GroupMapper groups) {
        this.users = users;
        this.groups = groups;
    }

    public class CreateGroup extends ChatApi.AbstractApiCallback {

        @Override
        public void onApiCall(Peer2PeerChatProtocol sender, ChatRequest request) {
            String param = request.commandData.replaceFirst("new group ", "");
            String[] split = param.split(" ", 2);
            String groupName = split[0];
            String[] names = split[1].split(" ");

            if(groups.findOne(groupName) != null){
                System.err.println("|--| Group "+groupName+" already exists (" + Utils.join(" ", groups.findOne(groupName).getNames())+").");
                return;
            }

            Group group = new Group(groupName);
            int someUser = 0;
            for (int i = 0, namesLength = names.length; i < namesLength; i++) {
                String name = names[i];

                User user = users.findOne(name);
                if(user == null){
                    System.err.println("|--| User " + name + " has not been found and it cannot be part of this group.");
                    continue;
                }
                someUser++;
                group.addUser(user);
            }

            if(someUser < 2){
                System.err.println("|--| Group has to have more than 2 users");
                return;
            }

            groups.add(group);
        }

        @Override
        public boolean isThisCommandDataInputValid(String input) {
            return input.matches("new group [\\w]+( [\\w]+){2,}");
        }
    }

    public class ShowGroup extends ChatApi.AbstractApiCallback {

        @Override
        public void onApiCall(Peer2PeerChatProtocol sender, ChatRequest request) {
            boolean someGroup = false;
            for(Group group : groups.findAll()){
                someGroup = true;
                System.out.println("|--| " + group.getId() + " | " + Utils.join(" ", group.getNames()));
            }

            if(!someGroup)
                System.err.println("|--| no group");
        }

        @Override
        public boolean isThisCommandDataInputValid(String input) {
            return input.matches("show groups");
        }
    }

    public class SendGroupMessage extends ChatApi.AbstractApiCallback{

        @Override
        public void onApiCall(Peer2PeerChatProtocol sender, ChatRequest input) {
            Group group = getGroupFromCommand(input.commandData);

            if (group == null) {
                System.err.println("|--| This group does not exits.");
                return;
            }

            String message = getMessageFromCommand(input.commandData);

            List<User> all = group.getUsers();
            String names = Utils.join(" ", group.getNames());

            for(User user : all){
                ChatRequest request = new ChatRequest(
                    sender.getCurrentUser(),
                    user,
                    GroupMessage.class.getSimpleName(),
                    group.getId() + " " + names,
                    message
                );

                try {
                    sender.sendRequestWithoutResponse(request);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public boolean isThisCommandDataInputValid(String input) {
            return input.matches("group [\\w]+ .+");
        }

        protected String[] getParamsFromCommand(String command){
            String param = command.replaceFirst("group ", "");
            String[] params = param.split(" ", 2);
            return params;
        }

        protected Group getGroupFromCommand(String command){
            return groups.findOne( getParamsFromCommand(command)[0] );
        }

        protected String getMessageFromCommand(String command){
            return getParamsFromCommand(command)[1];
        }
    }

    public class GroupMessage extends ChatApi.AbstractApiCallback {

        @Override
        public void onApiCall(Peer2PeerChatProtocol sender, ChatRequest request) {
            Group group = getGroupFromCommand(sender, request);
        }

        private Group getGroupFromCommand(Peer2PeerChatProtocol sender, ChatRequest request) {
            String[] params = request.commandData.split(" ");
            String groupName = params[0];

            Group group = groups.findOne(groupName);

            if(group == null){
                group = new Group(params[0]);

                for (int i = 1; i < params.length; i++) {
                    String name = params[i];

                    if(name.equals(sender.getCurrentUser().getId())) continue;

                    User user = users.findOne(name);
                    if(user == null) {
                        System.err.println("|--| I do not know user called '" + name + "', he will be skipped.");
                        continue;
                    }

                    group.addUser(user);
                }

                group.addUser(request.fromUser);
                groups.add(group);
            }

            return group;
        }
    }
}
