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

/**
 * Datasource that makes requests for Broadband data. Sends requests to convert state and county
 * names into their respective number codes and queries the ACS API for broadband data.
 */
public class BroadbandDatasource implements ACSDatasource<Broadband> {
  private HashMap<String, String> stateMap;
  private boolean hasMap;

  /** Constructor that intializes hasMap boolean as false */
  public BroadbandDatasource() {
    this.hasMap = false;
  }

  /**
   * Populates stateMap, which maps each state name to its number code
   *
   * @return populated state map
   * @throws URISyntaxException
   * @throws IOException
   * @throws InterruptedException
   * @throws EmptyResponseException
   */
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

  /**
   * Populates map that stores each county and its corresponding number code
   *
   * @param stateNum code for the state to get counties for
   * @return populated map
   * @throws URISyntaxException
   * @throws IOException
   * @throws InterruptedException
   * @throws EmptyResponseException
   */
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

  /**
   * Sends request to ACS API for broadband data
   *
   * @param countyNum number code for county
   * @param stateNum number code for state
   * @return broadband data as a string
   * @throws EmptyResponseException
   * @throws URISyntaxException
   * @throws IOException
   * @throws InterruptedException
   */
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
