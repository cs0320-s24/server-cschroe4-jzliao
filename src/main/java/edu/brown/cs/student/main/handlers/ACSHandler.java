package edu.brown.cs.student.main.handlers;

import edu.brown.cs.student.main.broadband.ACSAPIUtilities;
import edu.brown.cs.student.main.broadband.Broadband;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ACSHandler implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        // If you are interested in how parameters are received, try commenting out and
        // printing these lines! Notice that requesting a specific parameter requires that parameter
        // to be fulfilled.
        // If you specify a queryParam, you can access it by appending ?parameterName=name to the
        // endpoint
        // ex. http://localhost:3232/activity?participants=num
        Set<String> params = request.queryParams();
        //     System.out.println(params);
        String stateName = request.queryParams("stateName");
        String countyName = request.queryParams("countyName");
        //     System.out.println(participants);

        // Creates a hashmap to store the results of the request
        Map<String, Object> responseMap = new HashMap<>();
        try {

            // get the state number
            this.sendStateRequest();

            // Sends a request to the API and receives JSON back
            String broadbandJson = this.sendRequest(countyName, 0); //TODO: convert stateName to corresponding int
            // Deserializes JSON into an Activity
            Broadband broadband = ACSAPIUtilities.deserializeBroadband(broadbandJson);
            // Adds results to the responseMap
            responseMap.put("result", "success");
            responseMap.put("broadband", broadband);
            return responseMap;
        } catch (Exception e) {
            e.printStackTrace();
            // This is a relatively unhelpful exception message. An important part of this sprint will be
            // in learning to debug correctly by creating your own informative error messages where Spark
            // falls short.
            responseMap.put("result", "Exception");
        }
        return responseMap;
    }

    private int sendStateRequest() throws URISyntaxException, IOException, InterruptedException {
        //https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*
        HttpRequest buildACSApiRequest =
                HttpRequest.newBuilder()
                        .uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*"))
                        .GET()
                        .build();

        HttpResponse<String> sentStateNumResponse =
                HttpClient.newBuilder()
                        .build()
                        .send(buildACSApiRequest, HttpResponse.BodyHandlers.ofString());

        String stateJson = sentStateNumResponse.body();
        System.out.println(ACSAPIUtilities.deserializeStateNum(stateJson));
        return 0;
    }

    private String sendRequest(String countyName, int stateNum)
            throws URISyntaxException, IOException, InterruptedException {
        //ex: county:*&in=state:06
        HttpRequest buildACSApiRequest =
                HttpRequest.newBuilder()
                        .uri(new URI("https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:" + countyName + "&in=state:" + stateNum))
                        .GET()
                        .build();

        // Send that API request then store the response in this variable. Note the generic type.
        HttpResponse<String> sentBoredApiResponse =
                HttpClient.newBuilder()
                        .build()
                        .send(buildACSApiRequest, HttpResponse.BodyHandlers.ofString());

        // What's the difference between these two lines? Why do we return the body? What is useful from
        // the raw response (hint: how can we use the status of response)?
        System.out.println(sentBoredApiResponse);
        System.out.println(sentBoredApiResponse.body());

        return sentBoredApiResponse.body();
    }
}
