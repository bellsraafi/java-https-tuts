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

        try {

            String createdPickupTaskID = new org.json.JSONObject(con.createPickupTask()).getString("_id");
            String createdDeliveryTaskID = new org.json.JSONObject(con.createDeliveryTask(createdPickupTaskID)).getString("_id");


            JSONObject requestData = new JSONObject();
            requestData.put("packingList", createdDeliveryTaskID);

            String res = con.updateTask(createdPickupTaskID, requestData);
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


    private String sendPost (String url, JSONObject data) throws IOException {

        HttpUrlConnectionExample httpUrlConnectionExample = new HttpUrlConnectionExample();

        org.json.JSONObject tokenResponse = new org.json.JSONObject(httpUrlConnectionExample.getBearerToken());
        String token = tokenResponse.getString("token");


        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestProperty("Authorization", "Bearer "+ token);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
        wr.write(data.toString());
        wr.close();

        return responseProcessor(con);
    }

    private String createPickupTask() throws IOException {
        HttpUrlConnectionExample httpUrlConnectionExample = new HttpUrlConnectionExample();

        String url = "https://api-lomis-deliver-dev.ehealthafrica.org/v2/tasks/new";

        JSONObject requestData = new JSONObject();
        requestData.put("customerID", "CUS00109");
        requestData.put("driverID", "org.couchdb.user:admin@vvv.com");
        requestData.put("dueDate", "2019-04-30T18:00:00+01:00");
        requestData.put("taskNote", "");
        requestData.put("taskType", "pickup");
        requestData.put("linkedTask", "");
        requestData.put("taskGroup", "");
        requestData.put("status", "upcoming");
        requestData.put("lag", "");

        return httpUrlConnectionExample.sendPost(url, requestData);

    }

    private String createDeliveryTask(String taskID) throws IOException {
        HttpUrlConnectionExample httpUrlConnectionExample = new HttpUrlConnectionExample();

        String url = "https://api-lomis-deliver-dev.ehealthafrica.org/v2/tasks/new";

        JSONObject requestData = new JSONObject();
        requestData.put("customerID", "CUS00108");
        requestData.put("driverID", "org.couchdb.user:admin@vvv.com");
        requestData.put("dueDate", "2019-04-30T18:15:00+01:00");
        requestData.put("taskNote", "");
        requestData.put("taskType", "delivery");
        requestData.put("linkedTask", taskID);
        requestData.put("taskGroup", "");
        requestData.put("status", "upcoming");
        requestData.put("lag", "");

        return httpUrlConnectionExample.sendPost(url, requestData);

    }

    private String updateTask(String taskID, JSONObject requestData) throws IOException {
        HttpUrlConnectionExample httpUrlConnectionExample = new HttpUrlConnectionExample();

        String url = "https://api-lomis-deliver-dev.ehealthafrica.org/v2/tasks/task/"+ taskID +"/edit";

        return httpUrlConnectionExample.sendPost(url, requestData);
    }

    private String getBearerToken() throws IOException {
        String url = "https://api-lomis-deliver-dev.ehealthafrica.org/ums/auth";

        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        JSONObject cred = new JSONObject();
        cred.put("username", "admin@vvv.com");
        cred.put("password", "123456");

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
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
