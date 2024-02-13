package edu.brown.cs.student.main.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.brown.cs.student.main.datasource.csv.CSVDatasource;
import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.exceptions.MalformedDataException;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoadHandler implements Route {
  private List<List<String>> data;
  private CSVDatasource state;

  public LoadHandler(CSVDatasource state){
    this.state = state;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String filename = request.queryParams("filename");
    Map<String, Object> responseMap = new HashMap<>();
    if (!filename.contains("data/")) { //todo should this have ./ needed
      return new LoadFailureResponse("Error: file must not be outside of the /data/ directory").serialize();
    }

    try{
      this.state.parseDataset(filename);
    } catch (MalformedDataException | IOException | FactoryFailureException e){
      return new LoadFailureResponse(e.getMessage()).serialize();
    }
    responseMap.put("filename", filename);
    return new LoadSuccessResponse(responseMap).serialize();
  }

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
