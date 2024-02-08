package edu.brown.cs.student.main.creators;

import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.interfaces.CreatorFromRow;
import java.util.List;

/** ListCreator makes each row a list of strings */
public class ListCreator implements CreatorFromRow<List<String>> {
  /**
   * create takes the inputted list of strings and returns them because they are already in the
   * right format.
   *
   * @param row from parser
   * @return a row in the format of a list of strings
   * @throws FactoryFailureException (does not throw bc there is no way for it to fail)
   */
  @Override
  public List<String> create(List<String> row) throws FactoryFailureException {
    return row;
  }
}
