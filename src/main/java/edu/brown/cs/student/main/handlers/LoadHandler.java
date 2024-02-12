package edu.brown.cs.student.main.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Parser;
import edu.brown.cs.student.main.creators.ListCreator;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoadHandler implements Route {
  private List<List<String>> data;

  public LoadHandler(){

  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String filename = request.queryParams("filename");
    Map<String, Object> responseMap = new HashMap<>();
    Reader reader = new FileReader(filename); // could throw file not found exception
    Parser<List<String>> parser = new Parser<>(reader, new ListCreator());
    this.data = parser.parseCSV();


    return null;
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
  public record LoadFailureResponse(String response_type) {
    public LoadFailureResponse() {
      this("error");
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
