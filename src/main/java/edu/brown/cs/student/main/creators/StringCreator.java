package edu.brown.cs.student.main.creators;

import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import java.util.ArrayList;
import java.util.List;

/** StringCreator makes a given row into a string */
public class StringCreator implements CreatorFromRow<String> {
  /**
   * create takes the inputted list of strings and returns them as a string
   *
   * @param row from parser
   * @return a row in the format of a string
   * @throws FactoryFailureException (does not throw bc there is no way for it to fail)
   */
  @Override
  public String create(List<String> row) throws FactoryFailureException {
    ArrayList<String> myRow = new ArrayList<>(row);
    String rowAsString = myRow.get(0);
    myRow.remove(0);
    for (String element : myRow) {
      rowAsString = rowAsString + "," + element;
    }
    return rowAsString;
  }
}
