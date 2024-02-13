package edu.brown.cs.student.main.creators;

import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import java.util.List;

/**
 * FaultyCreator is a creator made to use in a testing suite. It automatically throws a
 * FactoryFailureException
 */
public class FaultyCreator implements CreatorFromRow<Integer> {
  /**
   * Auto throws FactoryFailureException
   *
   * @param row from parser
   * @return N/A
   * @throws FactoryFailureException to signify it failed
   */
  @Override
  public Integer create(List<String> row) throws FactoryFailureException {
    throw new FactoryFailureException("couldn't create Integer from row", row);
  }
}
