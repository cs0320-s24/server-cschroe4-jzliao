package edu.brown.cs.student.main.csvFunctions;

import edu.brown.cs.student.main.exceptions.ColNotFoundException;
import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.exceptions.MalformedDataException;
import edu.brown.cs.student.main.exceptions.SearchFailureException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Searcher class can find and return a list of rows containing a keyword in a CSV file. It
 * creates a parser to separate the CSV for more manageable searching.
 */
public class Searcher {
  private List<List<String>> rowsToSearch;
  private Boolean headerIncluded;

  /**
   * Pass in a reader to use for parsing and indicate whether a header is included in the CSV file.
   * This method creates the parser and sets the list of rows.
   *
   * @param headerIncluded indicates whether there is a header in the CSV or not
   * @throws FactoryFailureException if the parser cannot correctly make a row
   * @throws MalformedDataException if the given CSV data is not correctly formed
   * @throws IOException if reading from the CSV while parsing fails
   */
  public Searcher(List<List<String>> rowList, Boolean headerIncluded)
      throws FactoryFailureException, MalformedDataException, IOException {
    //    Parser<List<String>> parser = new Parser<>(reader, new ListCreator());
    this.rowsToSearch = rowList;
    this.headerIncluded = headerIncluded;
  }

  /**
   * This version of search that only takes one parameter searches all columns of a CSV for a
   * keyword. Not case-sensitive
   *
   * @param keyWord to search for
   * @return any matching rows fai
   * @throws SearchFailureException if the value couldn't be found
   */
  public List<List<String>> search(String keyWord) throws SearchFailureException {
    List<List<String>> matchedRows = new ArrayList<>();
    for (List<String> row : this.rowsToSearch) {
      for (String value : row) {
        // compare the values - not case sensitive
        value = value.trim().replaceAll("\"", "").trim();
        if (keyWord.equalsIgnoreCase(value)) {
          matchedRows.add(row);
          break;
        }
      }
    }
    if (!matchedRows.isEmpty()) {
      return matchedRows;
    }
    throw new SearchFailureException("Keyword not found");
  }

  /**
   * Search method when called with two parameters searches only within a desired column
   *
   * @param keyWord is the search term to look for
   * @param col represents the optional column identifier
   * @return a row that matches the search.
   */
  public List<List<String>> search(String keyWord, String col)
      throws SearchFailureException, ColNotFoundException {
    int colNum;
    List<List<String>> matchedRows = new ArrayList<>();
    if (col.matches("\\d+")) {
      // if it's a number
      colNum = Integer.parseInt(col);
    } else {
      // col is the header name
      if (!this.headerIncluded) {
        throw new SearchFailureException("Header name given but indicated no header present");
      }
      List<String> headers = this.rowsToSearch.get(0);
      colNum = this.findColIndex(headers, col);
    }
    return this.findInCol(colNum, keyWord);
  }

  /**
   * findInCol is a helper method to find a keyword in a column once the index of the column has
   * been calculated
   *
   * @param colNum index of column
   * @param keyWord to search for
   * @return a list of rows containing the keyword
   * @throws SearchFailureException is the keyword isn't found
   * @throws ColNotFoundException if the column is invalid
   */
  private List<List<String>> findInCol(int colNum, String keyWord)
      throws SearchFailureException, ColNotFoundException {
    List<List<String>> matchedRows = new ArrayList<>();
    for (List<String> row : this.rowsToSearch) {
      if (colNum < row.size()) {
        String value = row.get(colNum);

        // Accounts for when an element is encapsulated by quotation marks
        value = value.trim().replaceAll("\"", "");
        if (keyWord.equalsIgnoreCase(value)) {
          matchedRows.add(row);
        }
      } else {
        throw new ColNotFoundException("Column index invalid");
      }
    }
    if (!matchedRows.isEmpty()) {
      return matchedRows;
    }
    throw new SearchFailureException("Keyword not found");
  }

  /**
   * Helper method to calculate a columns index with an identifier
   *
   * @param row to search through
   * @param identifier to search for
   * @return the column index int
   * @throws ColNotFoundException if the column couldn't be calculated
   */
  private int findColIndex(List<String> row, String identifier) throws ColNotFoundException {
    int index = 0;
    for (String value : row) {
      if (identifier.equalsIgnoreCase(value)) {
        return index;
      }
      index++;
    }
    throw new ColNotFoundException("Column index invalid");
  }
}
