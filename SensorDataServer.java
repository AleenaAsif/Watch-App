import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SensorDataServer {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12345); // Use any available port
            System.out.println("Server listening on port 12345...");

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("Client connected: " + socket.getInetAddress());

                    // Create a new thread to handle the client
                    new Thread(new ClientHandler(socket)).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                StringBuilder sensorData = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sensorData.append(line).append("\n");
                }

                System.out.println("Received sensor data from client:\n" + sensorData.toString());

                // Process the sensor data and save it to a file
                saveDataToFile(sensorData.toString());

                // Close the socket
                socket.close();
                System.out.println("Client disconnected: " + socket.getInetAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void saveDataToFile(String data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("sensor_data.csv", true));

            String[] lines = data.split("\n");
            String sensorType = lines[0].trim();
            String[] values = new String[3];
            for (int i = 1; i < lines.length; i++) {
                String[] parts = lines[i].split(":");
                String axis = parts[0].trim();
                String value = parts[1].trim();
                switch (axis) {
                    case "X":
                        values[0] = value;
                        break;
                    case "Y":
                        values[1] = value;
                        break;
                    case "Z":
                        values[2] = value;
                        break;
                }
            }

            writer.write(sensorType + "," + values[0] + "," + values[1] + "," + values[2]);
            writer.newLine();
            writer.close();

            System.out.println("Sensor data saved to file in CSV format:\n" + data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
