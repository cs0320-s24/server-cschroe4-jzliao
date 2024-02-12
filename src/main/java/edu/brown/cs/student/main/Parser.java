package edu.brown.cs.student.main;

import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.exceptions.MalformedDataException;
import edu.brown.cs.student.main.creators.CreatorFromRow;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The Parser class is used to create a list of rows from a csv. The type representing the rows is
 * left generic for the implementor to declare while instantiating this class
 *
 * @param <T> generic type T defining the return type of row
 */
public class Parser<T> {
  private CreatorFromRow<T> creator;
  private BufferedReader buffReader;

  /**
   * The Parser constructor enables the class to take in a given reader object of the user's choice
   * and a CreatorFromRow to dictate how the row is formed into the desired type.
   *
   * @param reader represents the Reader the user passes in
   * @param creator represents the CreatorFromRow to use to make each row the right type
   */
  public Parser(Reader reader, CreatorFromRow<T> creator) {
    // wrap the passed in reader in a BufferedReader object
    this.buffReader = new BufferedReader(reader);
    this.creator = creator;
  }

  /**
   * The parserCSV method parses a CSV into a List of rows. Each row is of generic type T, the given
   * creator turns each row from a list of strings to the desired type. It also checks for malformed
   * CSV data.
   *
   * @return a List<T> rows.
   * @throws FactoryFailureException if the creator fails
   * @throws IOException in the case of .readLine() hitting an error
   * @throws MalformedDataException if the rows have differing numbers of columns
   */
  public List<T> parseCSV() throws FactoryFailureException, IOException, MalformedDataException {
    String line;
    // The regex is set to the given example
    Pattern regexSplitCSVRow = Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");

    // create a list of rows of type T
    List<T> parsedRows = new ArrayList<>();
    int length = -1;
    while ((line = this.buffReader.readLine()) != null) {
      String[] rowArray = regexSplitCSVRow.split(line);
      List<String> row = Arrays.asList(rowArray);

      // check data validity
      if (length == -1) {
        length = row.size();
      } else if (length != row.size()) {
        throw new MalformedDataException("Malformed data - rows not all uniform length");
      }
      parsedRows.add(this.creator.create(row));
    }

    return parsedRows;
  }
}
