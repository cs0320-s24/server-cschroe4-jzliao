package edu.brown.cs.student.main.exceptions;

/** Exception to indicate that a column in the csv was not found */
public class ColNotFoundException extends Exception {

  public ColNotFoundException(String message) {
    super(message);
  }
}
