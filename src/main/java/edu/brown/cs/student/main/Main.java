package edu.brown.cs.student.main;

import static java.lang.System.exit;

import edu.brown.cs.student.main.creators.StringCreator;
import edu.brown.cs.student.main.csvFunctions.Searcher;
import edu.brown.cs.student.main.exceptions.ColNotFoundException;
import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.exceptions.MalformedDataException;
import edu.brown.cs.student.main.exceptions.SearchFailureException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
    this.parseAndSearch(args, file);
  }

  /**
   * This method creates a new searcher, which in turn creates a new parser to parse and search
   * through the file given. This method assumes that the arguments have been checked for
   * correctness
   *
   * @param args An array of command line arguments
   * @param file The file to parse and search through
   */
  private void parseAndSearch(String[] args, File file) {
    Boolean headerIncluded = args[2].equalsIgnoreCase("true");
    try {
      Reader reader = new FileReader(file);
      // change inputted type T and inputted creator to change parsed row type
      Searcher searcher = new Searcher(new ArrayList<>(), headerIncluded); // CHANGED FIRST PARAM, USED TO BE READER
      List<List<String>> resultRows;
      if (args.length > 3) {
        resultRows = searcher.search(args[1], args[3]);
      } else {
        resultRows = searcher.search(args[1]);
      }
      StringCreator stringCreator = new StringCreator();
      for (List<String> row : resultRows) {
        System.out.println(stringCreator.create(row));
      }
    } catch (FileNotFoundException e) {
      System.out.println("Error: file not found");
      exit(0);
    } catch (FactoryFailureException
        | ColNotFoundException
        | SearchFailureException
        | MalformedDataException
        | IOException
        | IllegalArgumentException e) {
      System.out.println("Error: " + e.getMessage());
      exit(0);
    }
  }
}
