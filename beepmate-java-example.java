
import java.io.*;
import java.net.*;
import java.nio.file.*;

public class BeepMateAPI {

    public static String sendMessage(String key, String phoneId, String message) throws IOException {
        // URL encode the message
        String encodedMessage = URLEncoder.encode(message, "UTF-8");
        String urlString = "https://beepmate.io/send?key=" + key + "&id=" + phoneId + "&msg=" + encodedMessage;

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Get the response
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }

    public static String sendFile(String key, String phoneId, String filePath) throws IOException {
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        String lineEnd = "\r\n";
        String twoHyphens = "--";

        URL url = new URL("https://beepmate.io/send");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        DataOutputStream request = new DataOutputStream(connection.getOutputStream());

        // Add key parameter
        request.writeBytes(twoHyphens + boundary + lineEnd);
        request.writeBytes("Content-Disposition: form-data; name=\"key\"" + lineEnd + lineEnd);
        request.writeBytes(key + lineEnd);

        // Add id parameter
        request.writeBytes(twoHyphens + boundary + lineEnd);
        request.writeBytes("Content-Disposition: form-data; name=\"id\"" + lineEnd + lineEnd);
        request.writeBytes(phoneId + lineEnd);

        // Add file
        File file = new File(filePath);
        String fileName = file.getName();

        request.writeBytes(twoHyphens + boundary + lineEnd);
        request.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"" + lineEnd);
        request.writeBytes("Content-Type: application/octet-stream" + lineEnd + lineEnd);

        // Read and write file content
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        request.write(fileBytes);
        request.writeBytes(lineEnd);

        // End of multipart form
        request.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
        request.flush();
        request.close();

        // Get the response
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }

    public static void main(String[] args) {
        String apiKey = "MyKey";
        String phoneId = "19291111111";

        try {
            // Send text message
            String msgResult = sendMessage(apiKey, phoneId, "Hello World");
            System.out.println("Message sent, response: " + msgResult);

            // Send file
            String fileResult = sendFile(apiKey, phoneId, "somefile.pdf");
            System.out.println("File sent, response: " + fileResult);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
