package fi.lut.oop.prj2.client;

import fi.lut.oop.prj2.model.entities.User;

import java.net.Socket;
import java.util.regex.Pattern;

/**
* User: Marek Salát
* Date: 11.3.14
* Time: 11:13
*/
public class ChatRequest {
    static public final String HEADER_REGEXP = "^([^:]*:){1,2}[^:]*\\|>";

    public User fromUser = new User();
    public User toUser;

    public String command;
    public String commandData;
    public String content;
    public Socket socket;

    public ChatRequest() {
    }

    public ChatRequest(String rawRequest) {
        setFromString(rawRequest);
    }

    public ChatRequest(User fromUser, User toUser, String command, String commandData, String content) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.command = command;
        this.content = content;
        this.commandData = commandData;
    }

    public static boolean isRawRequestValid(String request){
        return request.matches(HEADER_REGEXP+".*");
    }

    public void setFromString(String rawRequest){
        assert isRawRequestValid(rawRequest) : "Header has incorrect format";

        content = rawRequest.replaceFirst(HEADER_REGEXP, "");
        String header = rawRequest.replaceFirst("\\|>"+ Pattern.quote(content), "");
        String[] headerItems = header.split(":");

        fromUser.setId(headerItems[0]);
        command = headerItems[1];
        commandData = headerItems.length >= 3 ? headerItems[2] : "";
    }

    @Override
    public String toString() {
        String data = commandData != null && !commandData.isEmpty() ? ":"+commandData : "";
        return fromUser.getId()+":"+command+data+"|>"+content;
    }
}
