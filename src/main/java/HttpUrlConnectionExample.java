import org.json.simple.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpUrlConnectionExample {

    public static void main ( String[] args) {
        HttpUrlConnectionExample con = new HttpUrlConnectionExample();

        String url = "https://api-lomis-deliver-dev.ehealthafrica.org/v2/admin-levels";
        try {
            String res = con.sendGet(url);
            System.out.println(res);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String sendGet (String url) throws IOException {

        HttpUrlConnectionExample httpUrlConnectionExample = new HttpUrlConnectionExample();

        org.json.JSONObject tokenResponse = new org.json.JSONObject(httpUrlConnectionExample.getBearerToken());
        String token = tokenResponse.getString("token");


        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer "+ token);


        return responseProcessor(con);

    }

    private void sendPost () {

    }

    private String getBearerToken() throws IOException {
        String url = "https://api-lomis-deliver-dev.ehealthafrica.org/ums/auth";

        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        JSONObject cred = new JSONObject();
        cred.put("username", "ums_admin");
        cred.put("password", "lomis_deliver");

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
//        con.setDoInput(true);
        con.setDoOutput(true);

        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
        wr.write(cred.toString());
        wr.close();

        return responseProcessor(con);
    }

    public String responseProcessor (HttpsURLConnection connection) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK){
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)
            );
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line +"\n");
            }
            bufferedReader.close();

            String response = stringBuilder.toString();

            return response;
        }
        else {
            throw new RuntimeException(connection.getResponseMessage());
        }
    }
}
