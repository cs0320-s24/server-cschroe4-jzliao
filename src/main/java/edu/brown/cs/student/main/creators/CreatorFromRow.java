package edu.brown.cs.student.main.creators;

import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import java.util.List;

/**
 * This interface defines a method that allows your CSV parser to convert each row into an object of
 * some arbitrary passed type.
 *
 * <p>Your parser class constructor should take a second parameter of this generic interface type.
 */
public interface CreatorFromRow<T> {
  T create(List<String> row) throws FactoryFailureException;
}
