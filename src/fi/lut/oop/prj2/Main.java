package fi.lut.oop.prj2;

import fi.lut.oop.prj2.client.Peer2PeerConsoleChat;
import fi.lut.oop.prj2.client.Utils;

public class Main {

    private static String help = "Peer2Peer console chat by Marek Salat" +
            "\nparams: [name:port [peerHostAddress:port]]" +
            "\ncommands:" +
            "\n\t show|show online|show offline\t shows all user in chat with theirs status" +
            "\n\t show groups\t will show all groups you are part of" +
            "\n" +
            "\n\t online|offline\t makes you online or offline" +
            "\n" +
            "\n\t name message\t sends message to user with name" +
            "\n\t all message\t sends message to all users" +
            "\n\t group name\t\t sends message to group with name" +
            "\n" +
            "\n\t new group names\t creates new chat group with given names separated by space(new group nameOfGroup name1 name2 ...)" +
            "\n\t echo host:post\t\t sends echo message" +
            "\n" +
            "\n\t UpdateRouteTable host:post\t tries to update route table" +
            "";

    public static void main(String[] args) {
        if(args.length == 1 && args[0].matches("-h|--help")){
            System.out.println(help);
            return;
        }

        String name = Utils.randomString(5);
        int port = 0;

        String boostPeerAddress = null;
        int boostPeerPort = 0;

        if(args.length >= 1){
            if(!args[0].matches("[\\w]+(:[0-9]+)?")){
                System.err.println(help);
                return;
            }

            String[] userInfo = args[0].split(":");
            name = userInfo[0];

            if(userInfo.length == 2){
                port = Integer.parseInt(userInfo[1]);
            }
        }

        if(args.length == 2){
            if(!args[0].matches("[\\w]+(:[0-9]+)?") || !args[1].matches("[\\w.]+:[0-9]+")) {
                System.err.println(help);
                return;
            }

            String [] boostPeerInfo = args[1].split(":");

            boostPeerAddress = boostPeerInfo[0];
            boostPeerPort = Integer.parseInt(boostPeerInfo[1]);
        }

        new Peer2PeerConsoleChat(name, port, boostPeerAddress, boostPeerPort)
                .start();
    }
}
