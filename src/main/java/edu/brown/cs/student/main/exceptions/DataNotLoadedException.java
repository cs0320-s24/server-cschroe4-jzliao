package edu.brown.cs.student.main.exceptions;

/** Exception to indicate that there was an error in loading data */
public class DataNotLoadedException extends Exception {

  public DataNotLoadedException(String message) {
    super(message);
  }
}
