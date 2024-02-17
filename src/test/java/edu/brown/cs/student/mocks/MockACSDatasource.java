package edu.brown.cs.student.mocks;

import edu.brown.cs.student.main.broadband.Broadband;
import edu.brown.cs.student.main.datasource.acs.ACSDatasource;
import edu.brown.cs.student.main.exceptions.EmptyResponseException;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * This class is a mock datasource that stores a single, constant piece of data: the broadband that
 * was passed in upon instantiation. It replaces a datasource that queries the ACS API and is for
 * testing purposes.
 */
public class MockACSDatasource implements ACSDatasource<Broadband> {
  private final Broadband constantData;

  /**
   * Constructor for the class. Takes in a Broadband and stores it
   *
   * @param constantData
   */
  public MockACSDatasource(Broadband constantData) {
    this.constantData = constantData;
  }

  /**
   * Called when Handler is requesting Broadband data. Returns the constant data stored
   *
   * @param stateName
   * @param countyName
   * @return Broadband stored in the datasource
   * @throws URISyntaxException
   * @throws IOException
   * @throws EmptyResponseException
   * @throws InterruptedException
   */
  @Override
  public Broadband sendRequest(String stateName, String countyName)
      throws URISyntaxException, IOException, EmptyResponseException, InterruptedException {
    String name = countyName + ", " + stateName;
    if (!name.equals(this.constantData.getName())) {
      throw new EmptyResponseException("County or state data not found");
    }
    return constantData;
  }
}
