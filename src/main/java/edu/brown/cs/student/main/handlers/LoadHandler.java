package edu.brown.cs.student.main.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.datasource.csv.CSVDatasource;
import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.exceptions.MalformedDataException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This class dictates how calls to the loadcsv endpoint are handled. Returns Response objects to
 * querier based on whether data fetching was successful or not.
 */
public class LoadHandler implements Route {
  private CSVDatasource state;

  /**
   * Constructor for class. Takes in a datasource that stores the dataset and if the csv has been parsed yet
   * @param state
   */
  public LoadHandler(CSVDatasource state) {
    this.state = state;
  }

  /**
   * Uses parameters from the request to properly parse the inputted csv file. Stores the parsed data in
   * the datasource and returns a Response object
   * @param request
   * @param response
   * @return Response object describing the success of the fetch
   * @throws Exception
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    String filename = request.queryParams("filename");
    String hasHeader = request.queryParams("hasHeader");

    if (hasHeader == null || filename == null) {
      return new LoadFailureResponse("Parameters not fulfilled").serialize();
    }

    // setting the boolean indicating whether there is a header
    boolean header = false;
    if (hasHeader.equals("yes") || hasHeader.equals("true")) {
      header = true;
    } else if (hasHeader.equals("no") || hasHeader.equals("false")) {
      header = false;
    } else {
      return new LoadFailureResponse(
              "Invalid hasHeader value inputted. Please enter true/false or yes/no.")
          .serialize();
    }

    Map<String, Object> responseMap = new HashMap<>();
    if (!filename.contains("data/")) {
      return new LoadFailureResponse("Error: file must not be outside of the /data/ directory")
          .serialize();
    }

    try {
      this.state.parseDataset(filename, header);
    } catch (MalformedDataException | IOException | FactoryFailureException e) {
      return new LoadFailureResponse(e.getMessage()).serialize();
    }
    responseMap.put("filename", filename);
    return new LoadSuccessResponse(responseMap).serialize();
  }

  /**
   * Response object representing a successful load
   * @param response_type
   * @param responseMap
   */
  public record LoadSuccessResponse(String response_type, Map<String, Object> responseMap) {
    public LoadSuccessResponse(Map<String, Object> responseMap) {
      this("success", responseMap);
    }
    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      try {
        // Initialize Moshi which takes in this class and returns it as JSON!
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<LoadHandler.LoadSuccessResponse> adapter =
            moshi.adapter(LoadHandler.LoadSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        // For debugging purposes, show in the console _why_ this fails
        // Otherwise we'll just get an error 500 from the API in integration
        // testing.
        e.printStackTrace();
        throw e;
      }
    }
  }

  /** Response object to send if someone gave a CSV that couldn't be parsed */
  public record LoadFailureResponse(String response_type, String error_message) {
    public LoadFailureResponse(String errorMessage) {
      this("error", errorMessage);
    }

    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(LoadHandler.LoadFailureResponse.class).toJson(this);
    }
  }
}
