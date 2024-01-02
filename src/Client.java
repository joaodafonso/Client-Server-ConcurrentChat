import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    public static void main(String[] args) {

        try {

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Socket clientSocket = new Socket("localhost", 8009);

            Thread thread = new Thread(new UpdateChat(clientSocket));
            executorService.submit(thread);
            thread.start();

            System.out.println(clientSocket.isConnected());


            while (true) {

                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                String terminal = in.readLine();

                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                out.write(terminal + "\n");
                out.flush();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static class UpdateChat implements Runnable{

        private Socket clientSocket;
        private String msg;
        public UpdateChat(Socket clientSocket){
            this.clientSocket = clientSocket;

        }


        @Override
        public void run() {
            try {
                while (true) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    msg = bufferedReader.readLine();

                    if (!msg.isEmpty()) {
                        System.out.println(msg);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
