package edu.brown.cs.student;

import edu.brown.cs.student.main.csvFunctions.Searcher;
import edu.brown.cs.student.main.exceptions.ColNotFoundException;
import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.exceptions.MalformedDataException;
import edu.brown.cs.student.main.exceptions.SearchFailureException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

/** This test suite tests the functionality of the Search class */
public class TestSearch {

  /** tests search with s simple csv and normal inputs */
//  @Test
//  public void testBasicSearch() {
//    try {
//      Reader reader = new FileReader("./data/stars/ten-star.csv");
//      Searcher searcher = new Searcher(reader, true);
//      List<List<String>> actual = searcher.search("Sol");
//      List<List<String>> expected = List.of(List.of("0", "Sol", "0", "0", "0"));
//      Assert.assertEquals(expected, actual);
//    } catch (FactoryFailureException
//        | SearchFailureException
//        | MalformedDataException
//        | IOException e) {
//      Assert.fail(e.getMessage());
//    }
//  }
//
//  /** With basic CSV file and with a string column identifier */
//  @Test
//  public void testBasicSearchWithColIDString() {
//    try {
//      Reader reader = new FileReader("./data/stars/ten-star.csv");
//      Searcher searcher = new Searcher(reader, true);
//      List<List<String>> actual = searcher.search("Sol", "ProperName");
//      List<List<String>> expected = List.of(List.of("0", "Sol", "0", "0", "0"));
//      Assert.assertEquals(expected, actual);
//    } catch (FactoryFailureException
//        | SearchFailureException
//        | ColNotFoundException
//        | MalformedDataException
//        | IOException e) {
//      Assert.fail(e.getMessage());
//    }
//  }
//
//  /** With basic CSV file and with an index column identifier */
//  @Test
//  public void testBasicSearchWithColIDNumber() {
//    try {
//      Reader reader = new FileReader("./data/stars/ten-star.csv");
//      Searcher searcher = new Searcher(reader, true);
//      List<List<String>> actual = searcher.search("87666", "0");
//      List<List<String>> expected =
//          List.of(List.of("87666", "Barnard's Star", "-0.01729", "-1.81533", "0.14824"));
//      Assert.assertEquals(expected, actual);
//    } catch (FactoryFailureException
//        | SearchFailureException
//        | ColNotFoundException
//        | MalformedDataException
//        | IOException e) {
//      Assert.fail(e.getMessage());
//    }
//  }
//
//  /** With basic CSV file and with bad column identifiers */
//  @Test
//  public void testBadColID() {
//    try {
//      Reader reader = new FileReader("./data/stars/ten-star.csv");
//      Searcher searcher = new Searcher(reader, true);
//      Assert.assertThrows(
//          ColNotFoundException.class,
//          () -> {
//            searcher.search("87666", "K");
//          });
//      Assert.assertThrows(
//          ColNotFoundException.class,
//          () -> {
//            searcher.search("hi", "-1");
//          });
//      Assert.assertThrows(
//          ColNotFoundException.class,
//          () -> {
//            searcher.search("nope", "100");
//          });
//    } catch (FactoryFailureException | MalformedDataException | IOException e) {
//      Assert.fail(e.getMessage());
//    }
//  }
//
//  /** With basic CSV file when the keyword doesn't exist in the file */
//  @Test
//  public void testKeywordNotFound() {
//    try {
//      Reader reader = new FileReader("./data/stars/ten-star.csv");
//      Searcher searcher = new Searcher(reader, true);
//      Assert.assertThrows(
//          SearchFailureException.class,
//          () -> {
//            searcher.search("Harry Potter");
//          });
//      Assert.assertThrows(
//          SearchFailureException.class,
//          () -> {
//            searcher.search("Harry Potter", "ProperName");
//          });
//    } catch (FactoryFailureException | MalformedDataException | IOException e) {
//      Assert.fail(e.getMessage());
//    }
//  }
//
//  /**
//   * With basic CSV file when the user tries to use a column identifier string but says there are no
//   * headers
//   */
//  @Test
//  public void testIllegalArgument() {
//    try {
//      Reader reader = new FileReader("./data/stars/ten-star.csv");
//      Searcher searcher = new Searcher(reader, false);
//      Assert.assertThrows(
//          IllegalArgumentException.class,
//          () -> {
//            searcher.search("Harry Potter", "Hogwarts");
//          });
//    } catch (FactoryFailureException | MalformedDataException | IOException e) {
//      Assert.fail(e.getMessage());
//    }
//  }
//
//  /** With malformed CSV file */
//  @Test
//  public void testMalformedData() {
//    try {
//      Reader reader = new FileReader("./data/malformed/malformed_signs.csv");
//      Assert.assertThrows(
//          MalformedDataException.class,
//          () -> {
//            new Searcher(reader, true);
//          });
//    } catch (FileNotFoundException e) {
//      Assert.fail("file not found");
//    }
//  }
//
//  /**
//   * With larger CSV file and also tests if a row has the same value twice if it will only count the
//   * row once
//   */
//  @Test
//  public void testIncomeByRace() {
//    try {
//      Reader reader = new FileReader("./data/census/income_by_race.csv");
//      Searcher searcher = new Searcher(reader, true);
//      int actual = searcher.search("2020").size();
//      int expected = 40;
//      Assert.assertEquals(expected, actual);
//      actual = searcher.search("Providence County, RI", "Geography").size();
//      expected = 79;
//      Assert.assertEquals(expected, actual);
//    } catch (FactoryFailureException
//        | SearchFailureException
//        | MalformedDataException
//        | ColNotFoundException
//        | IOException e) {
//      Assert.fail(e.getMessage());
//    }
//  }
//
//  /** With larger CSV file and also with numbers and special characters to search for */
//  @Test
//  public void testDolRipEarnings() {
//    try {
//      Reader reader = new FileReader("./data/census/dol_ri_earnings_disparity.csv");
//      Searcher searcher = new Searcher(reader, true);
//      List<String> actual = searcher.search("$1,058.47").get(0);
//      Assert.assertEquals("White", actual.get(1));
//    } catch (FactoryFailureException
//        | SearchFailureException
//        | MalformedDataException
//        | IOException e) {
//      Assert.fail(e.getMessage());
//    }
//  }
}
