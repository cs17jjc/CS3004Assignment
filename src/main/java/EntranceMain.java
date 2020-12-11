import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Queue;

public class EntranceMain {

    static boolean alive = true;
    static IntegerLock carsInQueue = new IntegerLock(0);

    public static void main(String[] args) throws IOException, InterruptedException {

        Socket serverSocket = new Socket("127.0.0.1",25565);
        Thread serverThread = new Thread(() -> serverHandler(serverSocket));
        serverThread.start();

        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

        while(alive && !serverSocket.isClosed()) {

            if(userInput.ready()){
                switch(userInput.readLine()){
                    case "c": //Car has entered
                        carsInQueue.lock();
                        carsInQueue.value ++;
                        System.out.println(LocalTime.now() + " Car joined the queue, size: " + carsInQueue.value);
                        carsInQueue.unlock();
                        break;
                    case "q": //Quit
                        alive = false;
                }
            }

        }

        Thread.sleep(50);//Wait for QUIT message to be sent before closing socket
        if(!serverSocket.isClosed()){
            serverSocket.close();
        }

    }

    public static void serverHandler(Socket serverSocket){

        try{
            PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

            while(alive){

                carsInQueue.lock();
                if(carsInQueue.value > 0){
                    carsInQueue.unlock();

                    out.println("ENTER");
                    while (!in.readLine().equals("SPACE")){} //Wait for server to allow access

                    carsInQueue.lock();
                    carsInQueue.value --;
                    System.out.println(LocalTime.now() + " Car left the queue, size: " + carsInQueue.value);
                }
                carsInQueue.unlock();
            }
            out.println("QUIT");

        } catch (SocketException e){
            System.out.println(LocalTime.now() + " Socket Closed");
            alive = false;
        } catch (Exception e){
            e.printStackTrace();
            alive = false;

        }
    }

}
