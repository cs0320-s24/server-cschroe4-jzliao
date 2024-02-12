package edu.brown.cs.student.main.creators;

import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.rowObjects.Star;
import java.util.List;

/** StarCreator makes each row into a Star object */
public class StarCreator implements CreatorFromRow<Star> {
  /**
   * create takes the inputted list of strings and returns them as a Star
   *
   * @param row from parser
   * @return a row in the format of a string
   * @throws FactoryFailureException throws if it can't make a star from the row
   */
  @Override
  public Star create(List<String> row) throws FactoryFailureException {
    if (row.size() != 5) {
      throw new FactoryFailureException("row format doesn't fit star data", row);
    }
    return new Star(row.get(0), row.get(1), row.get(2), row.get(3), row.get(4));
  }
}
