package edu.brown.cs.student.main.datasource.acs;

import edu.brown.cs.student.main.broadband.ACSAPIUtilities;
import edu.brown.cs.student.main.broadband.Broadband;
import edu.brown.cs.student.main.exceptions.EmptyResponseException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.HashMap;

public class BroadbandDatasource implements ACSDatasource<Broadband> {
  private HashMap<String, String> stateMap;
  private boolean hasMap;

  public BroadbandDatasource() {
    this.hasMap = false;
  }

  private HashMap<String, String> sendStateRequest()
      throws URISyntaxException, IOException, InterruptedException, EmptyResponseException {
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
    return ACSAPIUtilities.deserializeStateNum(stateJson);
  }

  private HashMap<String, String> sendCountyRequest(String stateNum)
      throws URISyntaxException, IOException, InterruptedException, EmptyResponseException {
    // https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:06
    HttpRequest buildACSApiRequest =
        HttpRequest.newBuilder()
            .uri(
                new URI(
                    "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:"
                        + stateNum))
            .GET()
            .build();
    HttpResponse<String> sentCountyNumResponse =
        HttpClient.newBuilder()
            .build()
            .send(buildACSApiRequest, HttpResponse.BodyHandlers.ofString());

    String countyJson = sentCountyNumResponse.body();
    if (countyJson.isEmpty()) {
      throw new EmptyResponseException("Data not found for given state");
    }
    return ACSAPIUtilities.deserializeCountyNum(countyJson);
  }

  private String sendBroadbandRequest(String countyNum, String stateNum)
      throws EmptyResponseException, URISyntaxException, IOException, InterruptedException {
    // ex: county:*&in=state:06
    HttpRequest buildACSApiRequest =
        HttpRequest.newBuilder()
            .uri(
                new URI(
                    "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                        + countyNum
                        + "&in=state:"
                        + stateNum))
            .GET()
            .build();

    HttpResponse<String> sentACSApiResponse =
        HttpClient.newBuilder()
            .build()
            .send(buildACSApiRequest, HttpResponse.BodyHandlers.ofString());

    if (sentACSApiResponse.body().isEmpty()) {
      throw new EmptyResponseException("Data not found for given county");
    }
    return sentACSApiResponse.body();
  }

  @Override
  public Broadband sendRequest(String stateName, String countyName)
      throws URISyntaxException, IOException, EmptyResponseException, InterruptedException {
    // make the map if it doesn't exist yet
    if (!this.hasMap) {
      this.stateMap = this.sendStateRequest();
      this.hasMap = true;
    }
    if (!this.stateMap.containsKey(stateName.toLowerCase())) {
      throw new EmptyResponseException("Data not found for given state");
    }
    String stateNum = this.stateMap.get(stateName.toLowerCase());
    HashMap<String, String> countyMap = this.sendCountyRequest(stateNum);
    String countyNum = countyMap.get(countyName.toLowerCase());

    // Sends a request to the API and receives JSON back
    String broadbandJson = this.sendBroadbandRequest(countyNum, stateNum);
    return ACSAPIUtilities.deserializeBroadband(broadbandJson, new Date());
  }
}
