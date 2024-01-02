import com.sun.corba.se.spi.orbutil.threadpool.Work;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.*;

public class Server {

    public static void main(String[] args) {

        try {

            ServerSocket serverSocket = new ServerSocket(8009);

            ExecutorService executor = Executors.newFixedThreadPool(4);
            Server server = new Server();

            while (true) {

                Socket clientSocket = serverSocket.accept();

                System.out.println(clientSocket.isConnected());

                WorkerServer workerServer = server.new WorkerServer(clientSocket);
                server.clientList.add(workerServer);

                executor.submit(workerServer);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private LinkedList<WorkerServer> clientList = new LinkedList<>();
    //private LinkedList<WorkerServer> workerServers = new LinkedList<>();

    private class WorkerServer implements Runnable {

        private Socket clientSocket;
        private String username;

        public WorkerServer(Socket clientSocket) {
            this.clientSocket = clientSocket;

        }

        public void chooseUsername() {
            try {
                BufferedWriter bWriter =  new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                bWriter.write("Choose your username: " + "\n");
                bWriter.flush();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                username = bufferedReader.readLine() + ": ";
                System.out.println(username);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        @Override
        public void run() {
            try {
                chooseUsername();
                while (true) {
                BufferedReader bufferedReader;
                PrintWriter printWriter;

                bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String message = bufferedReader.readLine();

                if (message.equals("/quit")) {
                    bufferedReader.close();
                    clientSocket.close();
                    clientList.remove(clientSocket);
                }

                System.out.println(clientList.size());

                    for (WorkerServer p : clientList) {
                        if (p.clientSocket.isClosed()) {
                            clientList.remove(p);
                            p.clientSocket.close();
                            continue;
                        }
                        printWriter = new PrintWriter(p.clientSocket.getOutputStream());
                        printWriter.write(username + message + "\n");
                        printWriter.flush();
                    }
                }

            } catch (IOException ex) {
                System.out.println("Exceção");
                throw new RuntimeException(ex);
            }
        }
    }
}
