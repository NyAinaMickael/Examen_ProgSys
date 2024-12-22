package sock;
import java.io.*;
import java.net.*;
public class ServerStopper{
    public static void stopServer(String host,int port) 
    {
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) 
        {
            //Envoi d'une demande d'arret au serveur
            String message = "stop";
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
