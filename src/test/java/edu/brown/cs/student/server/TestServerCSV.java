package edu.brown.cs.student.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.broadband.Broadband;
import edu.brown.cs.student.main.datasource.acs.ACSDatasource;
import edu.brown.cs.student.main.datasource.csv.CSVDatasource;
import edu.brown.cs.student.main.datasource.csv.LocalCSVSource;
import edu.brown.cs.student.main.handlers.ACSHandler;
import edu.brown.cs.student.main.handlers.LoadHandler;
import edu.brown.cs.student.main.handlers.SearchHandler;
import edu.brown.cs.student.main.handlers.ViewHandler;
import edu.brown.cs.student.mocks.MockACSDatasource;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;
import spark.Spark;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestServerCSV {
  private JsonAdapter<Map<String, Object>> adapter;
  private final Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);

  @BeforeAll
  public static void setupOnce() {
    // Pick an arbitrary free port
    Spark.port(0);
    // Eliminate logger spam in console for test suite
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root
  }
  @BeforeEach
  public void setup(){
    //set up the mock data source for testing w/ out sending api requests every time
    CSVDatasource csvState = new LocalCSVSource();
    Spark.get("loadcsv", new LoadHandler(csvState));
    Spark.get("viewcsv", new ViewHandler(csvState));
    Spark.get("searchcsv", new SearchHandler(csvState));
    Spark.awaitInitialization();

    Moshi moshi = new Moshi.Builder().build();
    this.adapter = moshi.adapter(this.mapStringObject);
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints after each test
    Spark.unmap("loadcsv");
    Spark.unmap("viewcsv");
    Spark.unmap("searchcsv");
    Spark.awaitStop();
  }


  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:"+ Spark.port()+"/"+apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    //clientConnection.setRequestProperty("Content-Type", "application/json");
    //clientConnection.setRequestProperty("Accept", "application/json");
    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }
  @Test
  public void testLoadHandlerBasic() throws IOException {
    // basic case
    HttpURLConnection clientConnection = tryRequest("loadcsv?filename=data/census/RICityTownIncome2017-2021.csv&hasHeader=yes");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("success", response.get("response_type"));

    clientConnection.disconnect();
  }

  @Test
  public void testImproperParametersLoad() throws IOException {
    // improper parameters
    HttpURLConnection clientConnection = tryRequest("loadcsv?filename=data/census/RICityTownIncome2017-2021.csv");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error", response.get("response_type"));
    clientConnection.disconnect();
  }
  @Test
  public void testNoFileLoad() throws IOException {
    // no file inputted
    HttpURLConnection clientConnection = tryRequest("loadcsv?filename=&hasHeader=yes");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error", response.get("response_type"));
    clientConnection.disconnect();
  }

  @Test
  public void testOutsideDataFolderLoad() throws IOException {
    // file not in data folder
    HttpURLConnection clientConnection = tryRequest("loadcsv?filename=test/testingUnreachableData/unreachable.txt&hasHeader=yes");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error", response.get("response_type")); //todo should I check the message?
    Assert.assertEquals(response.get("error_message"), "Error: file must not be outside of the /data/ directory");
    clientConnection.disconnect();
  }

  @Test
  public void testFileNotFoundLoad() throws IOException {
    // file not found
    HttpURLConnection clientConnection = tryRequest("loadcsv?filename=data/census/imNotHereTeehee.csv&hasHeader=yes");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error", response.get("response_type"));
    Assert.assertEquals(response.get("error_message"), "data/census/imNotHereTeehee.csv (No such file or directory)");
    clientConnection.disconnect();
  }

  @Test
  public void testLoadTwoItems() throws IOException {
    // loading two items + check if updated
    HttpURLConnection clientConnection = tryRequest("loadcsv?filename=data/census/RICityTownIncome2017-2021.csv&hasHeader=yes");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("success", response.get("response_type"));
    //TODO i don't like this casting still :( look to livecode not about poorly designed api
    Map<String, Object> responseMap = (Map<String, Object>) response.get("responseMap");
    Assert.assertEquals(responseMap.get("filename"), "data/census/RICityTownIncome2017-2021.csv");

    clientConnection = tryRequest("loadcsv?filename=data/census/postsecondary_education.csv&hasHeader=yes");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("success", response.get("response_type"));
    responseMap = (Map<String, Object>) response.get("responseMap");
    Assert.assertEquals(responseMap.get("filename"), "data/census/postsecondary_education.csv");
    clientConnection.disconnect();
  }

  @Test
  public void testNoParametersSpecifiedLoad() throws IOException {
    // load without parameters specified at all
    HttpURLConnection clientConnection = tryRequest("loadcsv");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error",response.get("response_type"));
    clientConnection.disconnect();
  }

  @Test
  public void testViewHandler() throws IOException {
    // basic case
    HttpURLConnection clientConnection = tryRequest("loadcsv?filename=data/census/RICityTownIncome2017-2021.csv&hasHeader=yes");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    clientConnection = tryRequest("viewcsv");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String,Object> response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("success",response.get("response_type"));

    //todo:check that its from the correct CSV

    // view after re-loading, make sure it changes
    clientConnection = tryRequest("loadcsv?filename=data/census/postsecondary_education.csv&hasHeader=yes");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("success",response.get("response_type"));
    //todo:check that its from the correct CSV. THIS IS WHAT we need to work on

    clientConnection.disconnect();
  }

  @Test
  public void testViewWithoutLoad() throws IOException {
    // view without loading
    HttpURLConnection clientConnection = tryRequest("viewcsv");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String,Object> response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error",response.get("response_type"));
  }



  @Test
  public void testSearchHandler() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filename=data/census/postsecondary_education.csv&hasHeader=yes");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("success", response.get("response_type"));

    // basic case
    clientConnection = tryRequest("searchcsv?searchTerm=White&identifier=*");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("success", response.get("response_type"));
    Map<String, Object> responseMap = (Map<String, Object>) response.get("responseMap");
    List<List<String>> searchResults = (List<List<String>>) responseMap.get("Search Results");
    //["White","2020","2020","217156","Brown University", "691", "brown-university", "0.223552248", "Men", "1"]
    Assert.assertEquals(searchResults.get(0).get(0), "White");

    // search term not found
    clientConnection = tryRequest("searchcsv?searchTerm=HindANDSeek&identifier=*");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error", response.get("response_type"));

    // search without identifier
    clientConnection = tryRequest("searchcsv?searchTerm=American%20Indian%20or%20Alaska%20Native&identifier=*");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("success", response.get("response_type"));

  }

  @Test
  public void testSearchWithoutLoading() throws IOException {
    // search without loading
    HttpURLConnection clientConnection = tryRequest("searchcsv?searchTerm=White&identifier=*");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error", response.get("response_type"));
  }

  @Test
  public void testNoSearchIdentifier() throws IOException{
    // loading csv to be used in test
    HttpURLConnection clientConnection = tryRequest("loadcsv?filename=data/census/postsecondary_education.csv&hasHeader=yes");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("success", response.get("response_type"));

    // search without identifier
    clientConnection = tryRequest("searchcsv?searchTerm=Brown%20University&identifier=");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error", response.get("response_type"));
    Assert.assertEquals("Please input all parameter values", response.get("error_message"));
  }

  @Test
  public void testColNum() throws IOException{

    // loading csv to be used in test
    HttpURLConnection clientConnection = tryRequest("loadcsv?filename=data/census/postsecondary_education.csv&hasHeader=yes");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("success", response.get("response_type"));

    // search with col num
    clientConnection = tryRequest("searchcsv?searchTerm=2&identifier=9");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("success", response.get("response_type"));
    Map<String, Object> responseMap = (Map<String, Object>) response.get("responseMap");
    List<List<String>> searchResults = (List<List<String>>) responseMap.get("Search Results");
    Assert.assertEquals(searchResults.get(0).get(5), "235");
    Assert.assertEquals(searchResults.get(1).get(5), "95");

    // search with too small/big col num
    clientConnection = tryRequest("searchcsv?searchTerm=2&identifier=-1");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error", response.get("response_type"));
    Assert.assertEquals("Column index invalid", response.get("error_message"));

    clientConnection = tryRequest("searchcsv?searchTerm=2&identifier=1000");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error", response.get("response_type"));
    Assert.assertEquals("Column index invalid", response.get("error_message"));
  }

  @Test
  public void testStringCol() throws IOException{
    // loading csv to be used in test
    HttpURLConnection clientConnection = tryRequest("loadcsv?filename=data/census/postsecondary_education.csv&hasHeader=yes");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("success", response.get("response_type"));

    // search with string col name
    clientConnection = tryRequest("searchcsv?searchTerm=2&identifier=ID%20Sex");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("success", response.get("response_type"));
    Map<String, Object> responseMap = (Map<String, Object>) response.get("responseMap");
    List<List<String>> searchResults = (List<List<String>>) responseMap.get("Search Results");
    Assert.assertEquals(searchResults.get(0).get(5), "235");
    Assert.assertEquals(searchResults.get(1).get(5), "95");

    // search with col name not found
    clientConnection = tryRequest("searchcsv?searchTerm=2&identifier=random");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error", response.get("response_type"));
    Assert.assertEquals("Column index invalid", response.get("error_message"));

    // search with col name but different case
    clientConnection = tryRequest("searchcsv?searchTerm=2&identifier=Id%20sex");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("success", response.get("response_type"));
    responseMap = (Map<String, Object>) response.get("responseMap");
    searchResults = (List<List<String>>) responseMap.get("Search Results");
    Assert.assertEquals(searchResults.get(0).get(5), "235");
    Assert.assertEquals(searchResults.get(1).get(5), "95");
  }

  @Test
  public void testNoHeader() throws IOException{
    // loading csv to be used in test
    HttpURLConnection clientConnection = tryRequest("loadcsv?filename=data/census/postsecondary_education.csv&hasHeader=false");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("success", response.get("response_type"));

    // search with String identifier but hasHeader = false (should error)
    clientConnection = tryRequest("searchcsv?searchTerm=2&identifier=ID%20Sex");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error", response.get("response_type"));
    Assert.assertEquals("Header name given but indicated no header present", response.get("error_message"));

    // search with int identifier but hasHeader = false (should still work)
    clientConnection = tryRequest("searchcsv?searchTerm=2&identifier=9");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("success", response.get("response_type"));
    Map<String, Object> responseMap = (Map<String, Object>) response.get("responseMap");
    List<List<String>> searchResults = (List<List<String>>) responseMap.get("Search Results");
    Assert.assertEquals(searchResults.get(0).get(5), "235");
    Assert.assertEquals(searchResults.get(1).get(5), "95");

    // search indicating no header, should return header in search results
    clientConnection = tryRequest("searchcsv?searchTerm=IPEDS%20Race&identifier=*");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("success", response.get("response_type"));
    responseMap = (Map<String, Object>) response.get("responseMap");
    searchResults = (List<List<String>>) responseMap.get("Search Results");
    Assert.assertEquals(searchResults.get(0).get(0), "IPEDS Race");
  }

  @Test
  public void testOutputMultipleRowsSearch() throws IOException {
    // search case when it should output >1 row

    // loading csv to be used in test
    HttpURLConnection clientConnection = tryRequest("loadcsv?filename=data/census/postsecondary_education.csv&hasHeader=yes");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("success", response.get("response_type"));

    //make the search
    clientConnection = tryRequest("searchcsv?searchTerm=Brown%20University&identifier=*");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("success", response.get("response_type"));
    Map<String, Object> responseMap = (Map<String, Object>) response.get("responseMap");
    List<List<String>> searchResults = (List<List<String>>) responseMap.get("Search Results");
    //Assert.assertEquals(searchResults.get(0).get(5), "235");
    //Assert.assertEquals(searchResults.get(1).get(5), "95");
    //todo assert the right number of elements returned
  }


  @Test
  public void testIdentifierResult(){
    // search case where identifier makes a difference
  }


    // search without parameters specified at all
}
