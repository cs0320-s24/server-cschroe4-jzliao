package edu.brown.cs.student.main.datasource.acs;

import edu.brown.cs.student.main.broadband.ACSAPIUtilities;
import edu.brown.cs.student.main.exceptions.EmptyResponseException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class StateDatasource implements ACSDatasource{
    @Override
    public String sendRequest(String stateNum, String countyName) throws URISyntaxException, IOException, EmptyResponseException, InterruptedException {
        // https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*
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
        if (stateJson.isEmpty()) {
            throw new EmptyResponseException("Census data not found: adjust entered parameters");
        }
        return stateJson;
    }
}
