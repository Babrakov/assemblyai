import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class RestApiCheck {

    public static void main(String[] args) throws Exception {

        final String API_KEY = System.getenv().get("API_KEY");
        final String apiUrl = "https://api.assemblyai.com/v2/transcript";

        Transcript transcript = new Transcript();
        transcript.setAudio_url("https://github.com/Babrakov/assemblyai/raw/main/ENG.mp3");
        Gson gson = new Gson();
        String jsonResult = gson.toJson(transcript);


        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(new URI(apiUrl))
                .header("Authorization",API_KEY)
                .POST(BodyPublishers.ofString(jsonResult))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> postResponse = httpClient.send(postRequest, BodyHandlers.ofString());

        System.out.println(postResponse.body());

        transcript = gson.fromJson(postResponse.body(),Transcript.class);

        System.out.println(transcript.getId());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI(apiUrl + "/" + transcript.getId()))
                .header("Authorization",API_KEY)
                .build();

        while(true) {
            HttpResponse<String> getResponse = httpClient.send(getRequest, BodyHandlers.ofString());
            transcript = gson.fromJson(getResponse.body(), Transcript.class);

            System.out.println(transcript.getStatus());

            if ("completed".equals(transcript.getStatus()) || "error".equals(transcript.getStatus())) {
                break;
            }

            Thread.sleep(1000);
        }

        System.out.println("Transcription completed!");
        System.out.println(transcript.getText());

    }

}
