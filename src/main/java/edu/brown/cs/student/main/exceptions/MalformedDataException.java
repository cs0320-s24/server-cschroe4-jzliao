package edu.brown.cs.student.main.exceptions;

/** Exception to indicate the given data was malformed */
public class MalformedDataException extends Exception {

  public MalformedDataException(String message) {
    super(message);
  }
}
