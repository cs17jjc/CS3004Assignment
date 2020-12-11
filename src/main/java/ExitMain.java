import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;

public class ExitMain {

    static boolean alive = true;

    public static void main(String[] args) throws IOException, InterruptedException {


        Socket serverSocket = new Socket("127.0.0.1",25566);

        PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

        while(alive) {

            if(userInput.ready()){
                switch(userInput.readLine()){
                    case "c": //Car has exited
                        out.println("EXIT"); //Notify server
                        switch (in.readLine()) { //Wait for server response
                                case "OK":
                                    System.out.println(LocalTime.now() + " Car exited");
                                    break;
                                case "ERR":
                                    System.out.println(LocalTime.now() + " No cars in car park");
                        }
                        break;
                    case "q": //Quit
                        alive = false;
                        break;
                }
            }

        }

        out.println("QUIT");
        Thread.sleep(50);

        if(!serverSocket.isClosed()){
            serverSocket.close();
        }
    }

}
