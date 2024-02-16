package edu.brown.cs.student.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.broadband.Broadband;
import edu.brown.cs.student.main.datasource.acs.ACSDatasource;
import edu.brown.cs.student.main.handlers.ACSHandler;
import edu.brown.cs.student.mocks.MockACSDatasource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;
import spark.Spark;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import okio.Buffer;

import java.util.Map;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestServerACS {
  private JsonAdapter<Map<String, Object>> adapter;
  private JsonAdapter<ACSHandler.ACSSuccessResponse> acsSuccessAdapter;
  private JsonAdapter<Broadband> broadBandAdapter;
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
    ACSDatasource<Broadband> mockDatasource = new MockACSDatasource(
            new Broadband(
                    "Hartford County, Connecticut",
                    "86.2",
                    "Fri Feb 16 01:16:01 EST 2024"));
    Spark.get("broadband", new ACSHandler(mockDatasource));
    Spark.awaitInitialization();

    Moshi moshi = new Moshi.Builder().build();
    this.adapter = moshi.adapter(this.mapStringObject);
    this.acsSuccessAdapter = moshi.adapter(ACSHandler.ACSSuccessResponse.class);
    this.broadBandAdapter = moshi.adapter(Broadband.class); //?? maybe ??
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints after each test
    Spark.unmap("broadband");
    Spark.awaitStop();
  }

  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:"+Spark.port()+"/"+apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    //clientConnection.setRequestProperty("Content-Type", "application/json");
    //clientConnection.setRequestProperty("Accept", "application/json");
    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  @Test
  public void testBasic() throws IOException {
    //test the basic workings of the ACS server

    /////////// LOAD DATASOURCE ///////////
    // Set up the request, make the request
    HttpURLConnection loadConnection = tryRequest("broadband?stateName=Connecticut&countyName=Hartford%20County");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    Assert.assertEquals(loadConnection.getResponseCode(), 200);
    // Get the expected response: a success
    //Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    //Assert.assertEquals("success", responseBody.get("response_type"));

    //Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    //Assert.assertEquals("success", responseBody.get("response_type"));

    ACSHandler.ACSSuccessResponse response =
            this.acsSuccessAdapter.fromJson(new
                    Buffer().readFrom(loadConnection.getInputStream()));
    Assert.assertEquals("success", response.responseMap().get("result"));

    Assert.assertNotNull(response);
    //TODO how to not have to cast?
    Map<String, Object> result = (Map<String, Object>) response.responseMap().get("broadband");
    Assert.assertEquals("Hartford County, Connecticut", result.get("name"));
    Assert.assertEquals("Fri Feb 16 01:16:01 EST 2024", result.get("dateFetched"));
    Assert.assertEquals("86.2", result.get("percent"));
//    Assert.assertEquals(
//            this.adapter.toJson(new Broadband("Hartford County, Connecticut", "86.2", "Fri Feb 16 01:16:01 EST 2024")),
//            responseBody.get("broadband"));
    // Notice we had to do something strange above, because the map is
    // from String to *Object*. Awkward testing caused by poor API design...

    loadConnection.disconnect();
  }

  @Test
  public void testingWithLivecodeMethods() throws IOException {
    HttpURLConnection loadConnection = tryRequest("broadband?stateName=Connecticut&countyName=Hartford%20County");
    Map<String,Object> response = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    Map<String, Object> responseMap = (Map<String, Object>) response.get("responseMap");
    Object result = responseMap.get("broadband");
    Broadband broadbandExpected = new Broadband(
            "Hartford County, Connecticut",
            "86.2",
            "Fri Feb 16 01:16:01 EST 2024");
  }

  @Test
  public void testViewACSHandlerConnections() throws IOException {
    HttpURLConnection clientConnection1 = tryRequest("broadband?stateName=Connecticut&countyName=Hartford%20County");
    Assert.assertEquals(200, clientConnection1.getResponseCode());
    HttpURLConnection clientConnection = tryRequest("broadband?stateName=Connecticut&countyName=Hartford");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String,Object> response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("success",response.get("response_type"));

    clientConnection.disconnect();
  }

  @Test
  public void testWrongCounty() {
    // empty string

    // county does not exist
  }

  @Test
  public void testWrongState() {
    // empty string

    // state does not exist
  }

  @Test
  public void testNoParams() {
    // no parameters specified at all
  }
}
