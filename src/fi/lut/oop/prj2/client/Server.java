package fi.lut.oop.prj2.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
* User: Marek Salát
* Date: 11.3.14
* Time: 14:57
 *
 * Congruent server. It is possible to register callback on socket accepted.
*/
class Server extends Thread{

    public interface OnServerAccept{
        void onServerAccept(Socket socket);
    }

    int port;

    OnServerAccept onServerAccept;

    public Server(int port, OnServerAccept onServerAccept){
        this.port = port;
        this.onServerAccept = onServerAccept;
    }

    @Override
    public void run(){
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while(true){
                final Socket clientSocket = serverSocket.accept();
                new Thread(){
                    @Override
                    public void run(){
                        try {
                            onServerAccept.onServerAccept(clientSocket);

                            if(!clientSocket.isClosed())
                                clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }

        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + port + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

}
