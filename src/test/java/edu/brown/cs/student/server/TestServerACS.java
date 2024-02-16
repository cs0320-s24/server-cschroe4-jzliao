package edu.brown.cs.student.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.broadband.Broadband;
import edu.brown.cs.student.main.datasource.acs.ACSDatasource;
import edu.brown.cs.student.main.datasource.acs.BroadbandDatasource;
import edu.brown.cs.student.main.datasource.acs.CacheBroadbandDatasource;
import edu.brown.cs.student.main.handlers.ACSHandler;
import edu.brown.cs.student.mocks.MockACSDatasource;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.brown.cs.student.mocks.MockMultiACSDatasource;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import spark.Spark;

public class TestServerACS {
  private JsonAdapter<Map<String, Object>> adapter;
  private JsonAdapter<ACSHandler.ACSSuccessResponse> acsSuccessAdapter;
  private JsonAdapter<Broadband> broadBandAdapter;
  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);

  /**
   * Sets up the port for server tests
   */
  @BeforeClass
  public static void setupOnce() {
    // Pick an arbitrary free port
    Spark.port(0);
    // Eliminate logger spam in console for test suite
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root
  }

  /**
   * Creates a mock datasource, ACSHandler, and sets the broadband endpoint. Also creates adapters
   */
  @BeforeEach
  public void setup() {
    // set up the mock data source for testing w/ out sending api requests every time
    ACSDatasource<Broadband> mockDatasource =
        new MockACSDatasource(
            new Broadband("Hartford County, Connecticut", "86.2", "Fri Feb 16 01:16:01 EST 2024"));
    Spark.get("broadband", new ACSHandler<Broadband>(mockDatasource, "broadband"));
    Spark.awaitInitialization();

    Moshi moshi = new Moshi.Builder().build();
    this.adapter = moshi.adapter(this.mapStringObject);
    this.acsSuccessAdapter = moshi.adapter(ACSHandler.ACSSuccessResponse.class);
    this.broadBandAdapter = moshi.adapter(Broadband.class); // ?? maybe ??
  }

  /**
   * Stops listening on broadband endpoint after each test
   */
  @AfterEach
  public void teardown() {
    Spark.unmap("broadband");
    Spark.awaitStop();
  }

  /** helper method that sets up a handler and adapters using the real cached datasource */
  public void setupACS(ACSDatasource<Broadband> datasource) {
    // teardown setup from BeforeAll
    Spark.unmap("broadband");
    Spark.awaitStop();

    Spark.get("broadband", new ACSHandler<Broadband>(datasource, "broadband"));
    Spark.awaitInitialization();
  }

  /**
   * Makes a get request to local host at the specified port number given an api call string
   * @param apiCall
   * @return the HttpURLConnection
   * @throws IOException
   */
  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Tests a basic example of querying for broadband data from the mock datasource and ensures returned
   * result map contains the correct info. Uses the ACSSuccessResponse adapter to compare the results of
   * the SuccessResponse to expected values.
   * @throws IOException
   */
  @Test
  public void testBasicGearupMethod() throws IOException {
    // test the basic workings of the ACS server
    // Set up the request, make the request
    HttpURLConnection loadConnection =
        tryRequest("broadband?stateName=Connecticut&countyName=Hartford%20County");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    Assert.assertEquals(loadConnection.getResponseCode(), 200);
    // Get the expected response: a success
    ACSHandler.ACSSuccessResponse response =
        this.acsSuccessAdapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    Assert.assertEquals("success", response.responseMap().get("result"));

    Assert.assertNotNull(response);
    Map<String, Object> result = (Map<String, Object>) response.responseMap().get("broadband");
    Assert.assertEquals("Hartford County, Connecticut", result.get("name"));
    Assert.assertEquals("Fri Feb 16 01:16:01 EST 2024", result.get("dateFetched"));
    Assert.assertEquals("86.2", result.get("percent"));

    loadConnection.disconnect();
  }

  /**
   * Tests calling the API by storing the entire responseMap and comparing its results
   * @throws IOException
   */
  @Test
  public void testingBasicLivecodeMethod() throws IOException {
    HttpURLConnection loadConnection =
        tryRequest("broadband?stateName=Connecticut&countyName=Hartford%20County");
    Map<String, Object> response =
        this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    Map<String, Object> responseMap = (Map<String, Object>) response.get("responseMap");
    Map<String, Object> resultBroadband = (Map<String, Object>) responseMap.get("broadband");
    Assert.assertEquals("Hartford County, Connecticut", resultBroadband.get("name"));
    Assert.assertEquals("Fri Feb 16 01:16:01 EST 2024", resultBroadband.get("dateFetched"));
    Assert.assertEquals("86.2", resultBroadband.get("percent"));
    loadConnection.disconnect();
  }

  /**
   * idk
   * TODO for caden: what is this test doing?
   * @throws IOException
   */
  @Test
  public void testViewACSHandlerConnections() throws IOException {
    HttpURLConnection clientConnection1 =
        tryRequest("broadband?stateName=Connecticut&countyName=Hartford%20County");
    Assert.assertEquals(200, clientConnection1.getResponseCode());
    HttpURLConnection clientConnection =
        tryRequest("broadband?stateName=Connecticut&countyName=Hartford");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response =
        this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error", response.get("response_type"));

    clientConnection.disconnect();
  }

  /**
   * Tests inputting incorrect values for the countyName parameter
   * @throws IOException
   */
  @Test
  public void testWrongCounty() throws IOException {
    this.setupACS(new CacheBroadbandDatasource(new BroadbandDatasource(), 60));
    // empty string
    HttpURLConnection clientConnection = tryRequest("broadband?stateName=Connecticut&countyName=");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response =
        this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error", response.get("response_type"));
    Assert.assertEquals("Empty parameter(s)", response.get("error_message"));

    // county does not exist
    clientConnection = tryRequest("broadband?stateName=Connecticut&countyName=Hartford");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error", response.get("response_type"));
    Assert.assertEquals("Data not found for given county", response.get("error_message"));
    clientConnection.disconnect();
  }

  /**
   * Tests inputting incorrect values for the stateNum parameter. Also tests the ACSHandler with
   * different Datasource (BroadbandDatasource)
   * @throws IOException
   */
  @Test
  public void testWrongState() throws IOException {
    this.setupACS(new BroadbandDatasource());
    // empty string
    HttpURLConnection clientConnection =
        tryRequest("broadband?stateName=test&countyName=Hartford%20County");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response =
        this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error", response.get("response_type"));
    Assert.assertEquals("Data not found for given state", response.get("error_message"));

    // state does not exist
    clientConnection = tryRequest("broadband?stateName=test&countyName=Hartford%20County");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error", response.get("response_type"));
    Assert.assertEquals("Data not found for given state", response.get("error_message"));
    clientConnection.disconnect();
  }

  /**
   * Tests inputting no parameters to the api call, should return an error message
   * @throws IOException
   */
  @Test
  public void testNoParams() throws IOException {
    // no parameters specified at all
    HttpURLConnection clientConnection = tryRequest("broadband?stateName=&countyName=");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response =
        this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error", response.get("response_type"));
    Assert.assertEquals("Empty parameter(s)", response.get("error_message"));
    clientConnection.disconnect();

    // no state parameter
    clientConnection = tryRequest("broadband?stateName=&countyName=County");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error", response.get("response_type"));
    Assert.assertEquals("Empty parameter(s)", response.get("error_message"));
    clientConnection.disconnect();

    // no county parameter
    clientConnection = tryRequest("broadband?stateName=Connecticut&countyName=");
    Assert.assertEquals(200, clientConnection.getResponseCode());
    response = this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Assert.assertEquals("error", response.get("response_type"));
    Assert.assertEquals("Empty parameter(s)", response.get("error_message"));
    clientConnection.disconnect();
  }

  /**
   * Test caching - tests making a query and seeing if it is in the cache, with single and multiple queries
   * @throws IOException
   * @throws ExecutionException
   */
  @Test
  public void testCached() throws IOException, ExecutionException {
    // setup for the cached broadband with mocked data
    HashMap<String, Broadband> dataMap = new HashMap<>();
    dataMap.put("Strafford County,New Hampshire",
            new Broadband("Strafford County, New Hampshire", "2.5", "Fri Feb 16 01:16:01 EST 2024"));
    dataMap.put("Hartford County,Connecticut",
            new Broadband("Hartford County, Connecticut", "86.2", "Fri Feb 16 01:16:01 EST 2024"));
    CacheBroadbandDatasource datasource = new CacheBroadbandDatasource(new MockMultiACSDatasource(dataMap), 60);
    this.setupACS(datasource);

    //make a query, then make it again and check if it has hit the cache
    HttpURLConnection clientConnection = tryRequest("broadband?stateName=Connecticut&countyName=Hartford%20County");
    Assert.assertEquals(clientConnection.getResponseCode(), 200);
    Assert.assertTrue(datasource.inCache("Hartford County,Connecticut", dataMap.get("Hartford County,Connecticut")));
    Assert.assertEquals(datasource.getStatsMap().get("missCount"), 1);

    clientConnection = tryRequest("broadband?stateName=Connecticut&countyName=Hartford%20County");
    Assert.assertEquals(clientConnection.getResponseCode(), 200);
    Assert.assertTrue(datasource.inCache("Hartford County,Connecticut", dataMap.get("Hartford County,Connecticut")));
    Assert.assertEquals(datasource.getStatsMap().get("hitCount"), 1);

    //check the new response presented is updated
    Map<String, Object> response =
            this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Map<String, Object> responseMap = (Map<String, Object>) response.get("responseMap");
    Map<String, Object> resultBroadband = (Map<String, Object>) responseMap.get("broadband");
    Assert.assertEquals("Hartford County, Connecticut", resultBroadband.get("name"));
    Assert.assertEquals("Fri Feb 16 01:16:01 EST 2024", resultBroadband.get("dateFetched"));
    Assert.assertEquals("86.2", resultBroadband.get("percent"));


    // test if the cache can hold more than one item
    clientConnection = tryRequest("broadband?stateName=New%20Hampshire&countyName=Strafford%20County");
    Assert.assertEquals(clientConnection.getResponseCode(), 200);
    Assert.assertTrue(datasource.inCache("Strafford County,New Hampshire", dataMap.get("Strafford County,New Hampshire")));
    Assert.assertEquals(datasource.getStatsMap().get("hitCount"), 1);
    Assert.assertEquals(datasource.getStatsMap().get("missCount"), 2);

    clientConnection = tryRequest("broadband?stateName=New%20Hampshire&countyName=Strafford%20County");
    Assert.assertEquals(clientConnection.getResponseCode(), 200);
    Assert.assertTrue(datasource.inCache("Strafford County,New Hampshire", dataMap.get("Strafford County,New Hampshire")));
    Assert.assertEquals(datasource.getStatsMap().get("hitCount"), 2);
    Assert.assertEquals(datasource.getStatsMap().get("missCount"), 2);

    // checks if the response from the API returns the request information
    response =
            this.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    responseMap = (Map<String, Object>) response.get("responseMap");
    resultBroadband = (Map<String, Object>) responseMap.get("broadband");
    Assert.assertEquals("Strafford County, New Hampshire", resultBroadband.get("name"));
    Assert.assertEquals("Fri Feb 16 01:16:01 EST 2024", resultBroadband.get("dateFetched"));
    Assert.assertEquals("2.5", resultBroadband.get("percent"));
  }

  /**
   * Tests eviction by setting the cache duration to two seconds, making a request, waiting a second, making another
   * request, waiting another second, then verifying the results. It makes sure that the first request has been
   * evicted, but the second one remains in the cache. It then waits another second, then makes sure the second one
   * was properly evicted.
   */
  @Test
  public void testTwoEvicted() throws IOException, InterruptedException, ExecutionException {
    // setup for the cached broadband with mocked data
    HashMap<String, Broadband> dataMap = new HashMap<>();
    dataMap.put("Strafford County,New Hampshire",
            new Broadband("Strafford County, New Hampshire", "2.5", "Fri Feb 16 05:16:01 EST 2024"));
    dataMap.put("Hartford County,Connecticut",
            new Broadband("Hartford, Connecticut", "86.2", "Fri Feb 16 01:16:01 EST 2024"));
    CacheBroadbandDatasource datasource = new CacheBroadbandDatasource(new MockMultiACSDatasource(dataMap), 2);
    this.setupACS(datasource);

    // making the first request:
    HttpURLConnection clientConnection = tryRequest("broadband?stateName=Connecticut&countyName=Hartford%20County");
    Assert.assertEquals(clientConnection.getResponseCode(), 200);
    Assert.assertEquals(datasource.getStatsMap().get("evictionCount"), 0);

    // sleep!
    Thread.sleep(1000);

    // making the second request
    clientConnection = tryRequest("broadband?stateName=New%20Hampshire&countyName=Strafford%20County");
    Assert.assertEquals(clientConnection.getResponseCode(), 200);

    // sleep again!
    Thread.sleep(1010);

    // check if first request has been evicted
    Assert.assertFalse(datasource.inCache("Hartford County,Connecticut", dataMap.get("Hartford County,Connecticut")));
    Assert.assertEquals(datasource.getStatsMap().get("evictionCount"), 1);
    // check that the second request is still in the cache
    Assert.assertTrue(datasource.inCache("Strafford County,New Hampshire", dataMap.get("Strafford County,New Hampshire")));

    // sleep!!
    Thread.sleep(1010);

    // check that second request is now evicted as well
    Assert.assertFalse(datasource.inCache("Strafford County,New Hampshire", dataMap.get("Strafford County,New Hampshire")));
    Assert.assertEquals(datasource.getStatsMap().get("evictionCount"), 2);
    Assert.assertFalse(datasource.inCache("Hartford County,Connecticut", dataMap.get("Hartford County,Connecticut")));
  }

  // hit, wait for eviction, then query again (should be a miss)
  @Test
  public void testQueryAfterEviction() throws IOException, InterruptedException, ExecutionException {
    // setup for the cached broadband with mocked data
    HashMap<String, Broadband> dataMap = new HashMap<>();
    dataMap.put("Strafford County,New Hampshire",
            new Broadband("Strafford County, New Hampshire", "2.5", "Fri Feb 16 05:16:01 EST 2024"));
    dataMap.put("Hartford County,Connecticut",
            new Broadband("Hartford, Connecticut", "86.2", "Fri Feb 16 01:16:01 EST 2024"));
    CacheBroadbandDatasource datasource = new CacheBroadbandDatasource(new MockMultiACSDatasource(dataMap), 1);
    this.setupACS(datasource);

    // making the request:
    HttpURLConnection clientConnection = tryRequest("broadband?stateName=Connecticut&countyName=Hartford%20County");
    Assert.assertEquals(clientConnection.getResponseCode(), 200);
    Assert.assertEquals(datasource.getStatsMap().get("evictionCount"), 0);
    Assert.assertTrue(datasource.inCache("Hartford County,Connecticut", dataMap.get("Hartford County,Connecticut")));

    Thread.sleep(1010);

    Assert.assertFalse(datasource.inCache("Hartford County,Connecticut", dataMap.get("Hartford County,Connecticut")));


    clientConnection = tryRequest("broadband?stateName=Connecticut&countyName=Hartford%20County");
    Assert.assertEquals(clientConnection.getResponseCode(), 200);
    Assert.assertEquals(datasource.getStatsMap().get("evictionCount"), 1);
    Assert.assertEquals(datasource.getStatsMap().get("hitCount"), 0);
    Assert.assertEquals(datasource.getStatsMap().get("missCount"), 2);
  }

  /**
   * Tests calling a search for a location that is not found in the datasource - should count as a miss
   * @throws IOException
   */
  @Test
  public void testFailedSearch() throws IOException{
    // setup for the cached broadband with mocked data
    HashMap<String, Broadband> dataMap = new HashMap<>();
    dataMap.put("Strafford County,New Hampshire",
            new Broadband("Strafford County, New Hampshire", "2.5", "Fri Feb 16 05:16:01 EST 2024"));
    dataMap.put("Hartford County,Connecticut",
            new Broadband("Hartford, Connecticut", "86.2", "Fri Feb 16 01:16:01 EST 2024"));
    CacheBroadbandDatasource datasource = new CacheBroadbandDatasource(new MockMultiACSDatasource(dataMap), 2);
    this.setupACS(datasource);

    // failed search query (should be a miss)
    HttpURLConnection clientConnection = tryRequest("broadband?stateName=random&countyName=random");
    Assert.assertEquals(clientConnection.getResponseCode(), 200);
    Assert.assertEquals(datasource.getStatsMap().get("missCount"), 1);
  }
}
