package edu.brown.cs.student.mocks;

import edu.brown.cs.student.main.broadband.Broadband;
import edu.brown.cs.student.main.datasource.acs.ACSDatasource;
import edu.brown.cs.student.main.exceptions.EmptyResponseException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * This class is a mock datasource that stores a constant map of Broadbands. It replaces a
 * datasource that queries the ACS API and is for testing purposes.
 */
public class MockMultiACSDatasource implements ACSDatasource<Broadband> {

  private HashMap<String, Broadband> dataset;

  /**
   * Constructor for the class. Takes in a HashMap of Broadbands and stores them
   *
   * @param broadbandMap
   */
  public MockMultiACSDatasource(HashMap<String, Broadband> broadbandMap) {
    this.dataset = broadbandMap;
  }

  /**
   * Called when ACSHandler is trying to request Broadband data. Finds the Broadband corresponding
   * to the passed in names and returns it.
   *
   * @param stateName - state of the data
   * @param countyName - county of the data
   * @return the Broadband corresponding to the names passed in
   * @throws URISyntaxException
   * @throws IOException
   * @throws EmptyResponseException when the corresponding Broadband is not in the stored dataset
   * @throws InterruptedException
   */
  @Override
  public Broadband sendRequest(String stateName, String countyName)
      throws URISyntaxException, IOException, EmptyResponseException, InterruptedException {
    if (!this.dataset.containsKey(countyName + "," + stateName)) {
      throw new EmptyResponseException("Location not found");
    }
    return this.dataset.get(countyName + "," + stateName);
  }
}
