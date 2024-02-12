package edu.brown.cs.student.main.creators;

import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.rowObjects.Town;
import java.util.List;

/** StarCreator makes each row into a Star object */
public class TownCreator implements CreatorFromRow<Town> {
  /**
   * create takes the inputted list of strings and returns them as a Star
   *
   * @param row from parser
   * @return a row in the format of a string
   * @throws FactoryFailureException throws if it can't make a star from the row
   */
  @Override
  public Town create(List<String> row) throws FactoryFailureException {
    if (row.size() != 4) {
      throw new FactoryFailureException("row format doesn't fit town data", row);
    }
    return new Town(row.get(0), row.get(1), row.get(2), row.get(3));
  }
}
