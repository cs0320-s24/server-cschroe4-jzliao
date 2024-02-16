package edu.brown.cs.student.main.handlers;

import com.google.common.util.concurrent.UncheckedExecutionException;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.broadband.ACSAPIUtilities;
import edu.brown.cs.student.main.datasource.acs.ACSDatasource;
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

public class ACSHandler<T> implements Route {

  private ACSDatasource<T> datasource;
  private String dataType;

  public ACSHandler(
      ACSDatasource datasource,
      String dataType) { // todo maybe take in a requester? and datasource?
    this.datasource = datasource;
    this.dataType = dataType;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String stateName = request.queryParams("stateName");
    String countyName = request.queryParams("countyName");

    if (stateName == null || countyName == null) {
      return new ACSFailureResponse("Missing one or more parameters").serialize();
    }

    if (stateName.isEmpty() || countyName.isEmpty()) {
      return new ACSFailureResponse("Empty parameter(s)").serialize();
    }

    // Creates a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();
    try {
      T data = this.datasource.sendRequest(stateName, countyName);

      // Adds results to the responseMap
      responseMap.put("result", "success");
      responseMap.put(this.dataType, data);

      return new ACSHandler.ACSSuccessResponse(responseMap).serialize();
    } catch (URISyntaxException | IOException | InterruptedException | EmptyResponseException e) {
      return new ACSFailureResponse(e.getMessage()).serialize();
    } catch (UncheckedExecutionException e) {
      return new ACSFailureResponse(e.getCause().getMessage()).serialize();
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
