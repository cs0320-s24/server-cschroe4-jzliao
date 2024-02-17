package edu.brown.cs.student.main.exceptions;

/**
 * Exception to indicate that a query for broadband data come up empty
 */
public class EmptyResponseException extends Exception {
  public EmptyResponseException(String message) {
    super(message);
  }
}
