import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ServerMain {

    static boolean alive = true;

    static int totalSpaces = 5;
    static IntegerLockOnValue numberOfCars = new IntegerLockOnValue(0, totalSpaces);

    public static void main(String[] args) throws IOException {

        ServerSocket entranceSocketServer = new ServerSocket(25565);
        ServerSocket exitSocketServer = new ServerSocket(25566);

        Thread entranceAcceptor = new Thread(() -> entranceSocketAcceptor(entranceSocketServer));
        Thread exitAcceptor = new Thread(() -> exitSocketAcceptor(exitSocketServer));
        entranceAcceptor.start();
        exitAcceptor.start();

        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

        while (alive){
            if(userInput.ready()){
                if ("q".equals(userInput.readLine())) {
                    alive = false;
                }
            }
        }

        entranceSocketServer.close();
        exitSocketServer.close();
    }

    public static void entranceSocketAcceptor(ServerSocket entranceSocketServer){
        while (alive){
            try {
                Socket entranceSocket = entranceSocketServer.accept();
                System.out.println(LocalTime.now() + " Accepted entrance socket");
                new Thread(() -> entranceSocketHandler(entranceSocket)).start();
            } catch (IOException e){
                if(e.getMessage().equals("socket closed")){
                    System.out.println(LocalTime.now() + " Socket Closed");
                } else {
                    System.out.println(LocalTime.now() + " Failed to create handler for entrance client on thread " + Thread.currentThread().getName());
                    e.printStackTrace();
                }
            }
        }
    }

    public static void exitSocketAcceptor(ServerSocket exitSocketServer){
        while (alive){
            try {
                Socket exitSocket = exitSocketServer.accept();
                System.out.println(LocalTime.now() + " Accepted exit socket");
                new Thread(() -> exitSocketHandler(exitSocket)).start();
            } catch (Exception e){
                if(e.getMessage().equals("socket closed")){
                    System.out.println(LocalTime.now() + " Socket Closed");
                } else {
                    System.out.println(LocalTime.now() + " Failed to create handler for entrance client on thread " + Thread.currentThread().getName());
                    e.printStackTrace();
                }
            }
        }
    }

    public static void entranceSocketHandler(Socket entranceSocket) {

        boolean threadAlive = true;
        boolean hasLock = false;

        try{
            PrintWriter out = new PrintWriter(entranceSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(entranceSocket.getInputStream()));

            while (alive && threadAlive){
                switch (in.readLine()){
                    case "QUIT":
                        threadAlive = false;
                        break;
                    case "ENTER":

                        numberOfCars.lockOnValue();
                        hasLock = true;
                        numberOfCars.value ++;
                        System.out.println(LocalTime.now() + " " + numberOfCars.value + " cars in car park.");

                        out.println("SPACE");
                        numberOfCars.unlock();
                        hasLock = false;
                }
            }

            entranceSocket.close();
        } catch (SocketException e){
            System.out.println(LocalTime.now() + " Socket closed");
        } catch (IOException | InterruptedException e){
            e.printStackTrace();
        }

        System.out.println(LocalTime.now() + " Closing entrance thread");
        if(hasLock){
            numberOfCars.unlock();
        }
    }

    public static void exitSocketHandler(Socket exitSocket) {

        boolean threadAlive = true;
        boolean hasLock = false;

        try{
            PrintWriter out = new PrintWriter(exitSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(exitSocket.getInputStream()));

            while (alive && threadAlive){
                switch (in.readLine()){
                    case "QUIT":
                        threadAlive = false;
                        break;
                    case "EXIT":
                        numberOfCars.lock();
                        hasLock = true;
                        if(numberOfCars.value > 0){
                            numberOfCars.value --;
                            System.out.println(LocalTime.now() + " " + numberOfCars.value + " cars in car park.");

                            out.println("OK");
                        } else {
                            out.println("ERR");
                        }
                        numberOfCars.unlock();
                        hasLock = false;
                }
            }

            exitSocket.close();
        } catch (SocketException e){
            System.out.println(LocalTime.now() + "Socket closed");
        } catch (IOException | InterruptedException e){
            e.printStackTrace();
        }

        System.out.println(LocalTime.now() + " Closing exit thread");
        if(hasLock){
            numberOfCars.unlock();
        }
    }

}
