package edu.brown.cs.student.main;

import static java.lang.System.exit;

import java.io.*;

/** The Main class of our project. This is where execution begins. */
public final class Main {
  /**
   * The initial method called when execution begins. Checks for correct input to the CLI Restricts
   * the user to using files in the data directory Creates a Parser and passes in a Reader and
   * CreatorFromRow of developers choice method?
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run(args);
  }

  /**
   * Constructor of Main. Not set up to do anything
   *
   * @param args An array of command line arguments
   */
  private Main(String[] args) {}

  /**
   * Run handles the arguments and checks that they are in the right format Additionally it checks
   * that the file is in the right directory before searching it
   *
   * @param args An array of command line arguments
   */
  private void run(String[] args) {
    if (args.length < 3 || args.length > 4) {
      System.out.println("Incorrect command usage");
      System.out.println(
          "Enter: <CSV filename> <value to search for> <is a header present? (true or false)> <optional: column identifier>");
      exit(0);
    }
    File file = new File(args[0]);
    // check that the file is in the right directory
    String filePath = file.getParent();
    if (!filePath.contains("/data/")) {
      System.out.println("Error: file must not be outside of the /data/ directory");
      exit(0);
    }
    // If arguments are correct then call parseAndSearchHelper
  }
}
