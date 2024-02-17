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

/**
 * In our program, this class dictates how calls to the broadband endpoint are handled. Generally, though, it is
 * able to handle requests for different types of data. It takes in a datasource of generic type T and sends requests
 * to the datasource given state and county names. It then uses the returned information to return a response to
 * the querier.
 * @param <T> type of data that is being queried
 */
public class ACSHandler<T> implements Route {

  private ACSDatasource<T> datasource;
  private String dataType;

  /**
   * Constructor for the class. Sets the datasource and dataType variables
   * @param datasource Datasource that returns the information given parameters
   * @param dataType type of data returned - in our implementation, this is Broadband data
   */
  public ACSHandler(
      ACSDatasource<T> datasource,
      String dataType) {
    this.datasource = datasource;
    this.dataType = dataType;
  }

  /**
   * Uses the request parameters so that the datasource can request data. In our implementation, it uses the state
   * and county names inputted by the user to send a request for Broadband data. Adds this data to a response, but
   * returns a failed response if any errors arise.
   * @param request
   * @param response
   * @return either a ACSSuccessResponse or a ACSFailureResponse
   * @throws Exception
   */
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

  /**
   * Record that represents a succesful response. Returned to querier in handle(). Stores a response map and
   * has serializing capabilities
   * @param response_type
   * @param responseMap
   */
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

  /**
   * Response object that is returned when an error arises in fetching data
   * @param response_type set as error
   * @param error_message
   */
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
