package sock;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static final String LOG_FILE = "logs/server.log";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void log(String message) {
        logToFile("INFO", message);
    }

    public static void error(String message) {
        logToFile("ERROR", message);
    }

    private static void logToFile(String level, String message) {
        String timestamp = DATE_FORMAT.format(new Date());
        String formattedMessage = String.format("[%s] [%s] %s", timestamp, level, message);

        try (FileWriter fw = new FileWriter(LOG_FILE, true); PrintWriter pw = new PrintWriter(fw)) {
            pw.println(formattedMessage);
        } catch (IOException e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }
}
