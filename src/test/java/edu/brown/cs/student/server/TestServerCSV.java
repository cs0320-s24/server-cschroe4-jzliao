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
  public void testLoadHandler() throws IOException {
    // basic case
    HttpURLConnection clientConnection1 = tryRequest("loadcsv?filename=data/census/RICityTownIncome2017-2021.csv&hasHeader=yes");
    Assert.assertEquals(200, clientConnection1.getResponseCode());
    Map<String,Object> response1 = this.adapter.fromJson(new Buffer().readFrom(clientConnection1.getInputStream()));
    Assert.assertEquals("success",response1.get("response_type"));

    // improper parameters
    HttpURLConnection clientConnection = tryRequest("loadcsv?filename=data/census/RICityTownIncome2017-2021.csv");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String,Object> response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error",response.get("response_type"));

    // no file inputted
    clientConnection = tryRequest("loadcsv?filename=&hasHeader=yes");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error",response.get("response_type"));

    // file not in data folder
    clientConnection = tryRequest("loadcsv?filename=test/testingUnreachableData/unreachable.txt&hasHeader=yes");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error",response.get("response_type")); //todo should I check the message?

    // file not found
    clientConnection = tryRequest("loadcsv?filename=data/census/imNotHereTeHe.csv");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error",response.get("response_type"));

    // loading two items + check if updated
    clientConnection = tryRequest("loadcsv?filename=data/census/RICityTownIncome2017-2021.csv&hasHeader=yes");
    Assert.assertEquals(200, clientConnection1.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("success",response.get("response_type"));
    //TODO i don't like this casting still :( look to livecode not about poorly designed api
    Map<String, String> responseMap = (Map<String, String>) response.get("responseMap");
    Assert.assertEquals(responseMap.get("filename"), "data/census/RICityTownIncome2017-2021.csv");

    clientConnection = tryRequest("loadcsv?filename=data/census/postsecondary_education.csv&hasHeader=yes");
    Assert.assertEquals(200, clientConnection1.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("success",response.get("response_type"));
    responseMap = (Map<String, String>) response.get("responseMap");
    Assert.assertEquals(responseMap.get("filename"), "data/census/postsecondary_education.csv");

    // load without parameters specified at all
    clientConnection = tryRequest("loadcsv");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
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
  public void testSearchWithSpaces() throws IOException {
    //search term has spaces
    HttpURLConnection clientConnection = tryRequest("searchcsv?searchTerm=American Indian or Alaska Native&identifier=*");
    //this is crashing it!! Works when converted to American%20Indian%20or%20Alaska%20Native
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("success", response.get("response_type"));
    Map<String, Object> responseMap = (Map<String, Object>) response.get("responseMap");
    List<List<String>> searchResults = (List<List<String>>) responseMap.get("Search Results");
    //["White","2020","2020","217156","Brown University", "691", "brown-university", "0.223552248", "Men", "1"]
    Assert.assertEquals(searchResults.get(0).get(0), "American Indian or Alaska Native");
  }

  @Test
  public void TestSearchWithoutLoading() throws IOException {
    // search without loading
    HttpURLConnection clientConnection = tryRequest("searchcsv?searchTerm=White&identifier=*");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error", response.get("response_type"));
  }

    // search without identifier

    // search with col num
    // search with too small/big col num

    // search with string col name
    // search with col name not found
    // search with col name but different case

    // search with identifier but hasHeader = false (should error)

    // search case when it should output >1 row

    // search case where identifier makes a difference

    // search without parameters specified at all
}
