package fi.lut.oop.prj2.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
* User: Marek Salát
* Date: 11.3.14
* Time: 14:58
*/
public class InputReaderWorker extends Thread {

    private OnInputReady onInputReady;

    public interface OnInputReady{
        void onInputReady(String line);
    }

    public InputReaderWorker(OnInputReady onInputReady) {
        super();
        this.onInputReady = onInputReady;
    }

    @Override
    public void run() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true){
            String line = null;
            try {
                line = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(line == null || line.isEmpty()) continue;

            onInputReady.onInputReady(line);
        }
//        try {
//            in.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
