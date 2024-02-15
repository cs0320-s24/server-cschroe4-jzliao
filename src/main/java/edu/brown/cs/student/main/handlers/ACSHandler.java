package edu.brown.cs.student.main.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.broadband.ACSAPIUtilities;
import edu.brown.cs.student.main.broadband.Broadband;
import edu.brown.cs.student.main.caching.ACSRequester;
import edu.brown.cs.student.main.caching.CacheACSRequester;
import edu.brown.cs.student.main.caching.Requester;
import edu.brown.cs.student.main.exceptions.EmptyResponseException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ACSHandler implements Route {

  private HashMap<String, String> stateMap;
  private boolean stateMapFetched;

  public ACSHandler() {
    this.stateMapFetched = false;
  }

  //TODO: REALLY IMPORTANT!!!! >:( Add date and time to the response map broadband
  @Override
  public Object handle(Request request, Response response) throws Exception {
    String stateName = request.queryParams("stateName");
    String countyName = request.queryParams("countyName");

    // Creates a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // get the state number map
      if (!this.stateMapFetched) {
        this.stateMap = this.sendStateRequest();
        this.stateMapFetched = true;
      }

      String stateNum = this.stateMap.get(stateName.toLowerCase());

//      // todo this is where we diverge
//      // get the county number map
//
//      HashMap<String, String> countyMap = this.sendCountyRequest(stateNum);
//      String countyNum = countyMap.get(countyName.toLowerCase());
//
//      // Sends a request to the API and receives JSON back
//      String broadbandJson = this.sendBroadbandRequest(countyNum, stateNum);
//      // TODO reconverges

      Requester cacheACSRequester = new CacheACSRequester(new ACSRequester());
      String broadbandJson = cacheACSRequester.sendRequest(stateNum, countyName);

      // Deserializes JSON into an Activity
      Broadband broadband = ACSAPIUtilities.deserializeBroadband(broadbandJson);
      // Adds results to the responseMap
      responseMap.put("result", "success");
      responseMap.put("broadband", broadband);
      return new ACSHandler.ACSSuccessResponse(responseMap).serialize();
    } catch (URISyntaxException | IOException | InterruptedException | EmptyResponseException e) {
      e.printStackTrace();
      // TODO: is this verbose enough
      return new ACSFailureResponse(e.getMessage()).serialize();
    }
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
      throw new EmptyResponseException("Census data not found: adjust entered parameters");
    }
    return ACSAPIUtilities.deserializeCountyNum(countyJson);
  }

  private String sendBroadbandRequest(String countyNum, String stateNum)
      throws URISyntaxException, IOException, InterruptedException, EmptyResponseException {
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

    //        System.out.println(sentACSApiResponse);
    //        System.out.println(sentACSApiResponse.body());
    if (sentACSApiResponse.body().isEmpty()) {
      throw new EmptyResponseException("Census data not found: adjust entered parameters");
    }
    return sentACSApiResponse.body();
  }

  public record ACSSuccessResponse(String response_type, Map<String, Object> responseMap) {
    public ACSSuccessResponse(Map<String, Object> responseMap) {
      this("success", responseMap);
    }
    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      try {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ACSHandler.ACSSuccessResponse> adapter =
            moshi.adapter(ACSHandler.ACSSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }

  /** Response object to send if someone gave a CSV that couldn't be parsed */
  public record ACSFailureResponse(String response_type, String error_message) {
    public ACSFailureResponse(String errorMessage) {
      this("error", errorMessage);
    }

    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(ACSHandler.ACSFailureResponse.class).toJson(this);
    }
  }
}
