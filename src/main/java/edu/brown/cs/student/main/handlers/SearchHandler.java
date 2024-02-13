package edu.brown.cs.student.main.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.csvFunctions.Searcher;
import edu.brown.cs.student.main.datasource.csv.CSVData;
import edu.brown.cs.student.main.datasource.csv.CSVDatasource;
import edu.brown.cs.student.main.exceptions.ColNotFoundException;
import edu.brown.cs.student.main.exceptions.DataNotLoadedException;
import edu.brown.cs.student.main.exceptions.SearchFailureException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchHandler implements Route {

  private CSVDatasource state;

  public SearchHandler(CSVDatasource state) {
    this.state = state;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String searchTerm = request.queryParams("searchTerm");
    String identifier = request.queryParams("identifier");

    try {
      CSVData dataset = this.state.getDataset();
      List<List<String>> rowsList = dataset.dataset();

      Searcher searcher = new Searcher(rowsList, dataset.hasHeader());
      List<List<String>> result;
      if (identifier == null ){
        result = searcher.search(searchTerm);
      } else {
        result = searcher.search(searchTerm, identifier);
      }

      Map<String, Object> responseMap = new HashMap<>();
      responseMap.put("Search Results: ", result);
      responseMap.put("Search Term: ", searchTerm);
      return new SearchSuccessResponse(responseMap).serialize();
    } catch (SearchFailureException | DataNotLoadedException | ColNotFoundException e) {
      return new SearchFailureResponse(e.getMessage()).serialize();
    }

    /*TODO:
       - parameters: copy logic from main
       - also the current searcher makes its own parser so how should we bypass that or change the structure?
       - handle column identifiers
       - we might have to ask the user if the csv has headers when we load
       - return a record with a response map containing each of the matching rows.
           - should we format this data a bit more? maybe put each row in separately?

     Notes:
       - i changed parseAndSearch() in Main so the Searcher now takes in a list
       - also i commented out the old search tests

    */

  }

  public record SearchSuccessResponse(String response_type, Map<String, Object> responseMap) {
    public SearchSuccessResponse(Map<String, Object> responseMap) {
      this("success", responseMap);
    }
    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      try {
        // Initialize Moshi which takes in this class and returns it as JSON!
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<SearchHandler.SearchSuccessResponse> adapter =
            moshi.adapter(SearchHandler.SearchSuccessResponse.class);
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
  public record SearchFailureResponse(String response_type, String error_message) {
    public SearchFailureResponse(String errorMessage) {
      this("error", errorMessage);
    }

    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(SearchHandler.SearchFailureResponse.class).toJson(this);
    }
  }
}
