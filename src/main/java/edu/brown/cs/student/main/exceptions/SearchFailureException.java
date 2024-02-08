package edu.brown.cs.student.main.exceptions;

/** Exception to indicate the keyword was not found */
public class SearchFailureException extends Exception {

  public SearchFailureException(String message) {
    super(message);
  }
}
