package edu.brown.cs.student;

import edu.brown.cs.student.main.Parser;
import edu.brown.cs.student.main.creators.FaultyCreator;
import edu.brown.cs.student.main.creators.ListCreator;
import edu.brown.cs.student.main.creators.StarCreator;
import edu.brown.cs.student.main.creators.StringCreator;
import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.exceptions.MalformedDataException;
import edu.brown.cs.student.main.rowObjects.Star;
import java.io.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

/** This testing suite tests the parser */
public class TestParser {

  /** Tests parsing with a simple ListCreator */
  @Test
  public void testWithListCreator() {
    try {
      Reader reader = new FileReader("./data/stars/ten-star.csv");
      Parser<List<String>> parser = new Parser<>(reader, new ListCreator());
      List<List<String>> rows = parser.parseCSV();
      Assert.assertEquals(rows.get(1).get(0), "0");
      Assert.assertEquals(rows.get(5).get(1), "96 G. Psc");
    } catch (FileNotFoundException e) {
      System.out.println("failed to open file");
      Assert.fail();
    } catch (FactoryFailureException | IOException | MalformedDataException e) {
      Assert.fail(e.getMessage());
    }
  }

  /** Tests parsing an empty file */
  @Test
  public void testEmptyFile() {
    try {
      Reader reader = new FileReader("./data/emptyFiles/empty.csv");
      Parser<List<String>> parser = new Parser<>(reader, new ListCreator());
      List<List<String>> rows = parser.parseCSV();
      Assert.assertEquals(rows.size(), 0);
    } catch (FileNotFoundException e) {
      System.out.println("failed to open file");
      Assert.fail();
    } catch (FactoryFailureException | IOException | MalformedDataException e) {
      Assert.fail(e.getMessage());
    }
  }

  /** Tests parsing with malformed CSV file */
  @Test
  public void testMalformedData() {
    try {
      Reader reader = new FileReader("./data/malformed/malformed_signs.csv");
      Parser<List<String>> parser = new Parser<>(reader, new ListCreator());
      Assert.assertThrows(
          MalformedDataException.class,
          () -> {
            parser.parseCSV();
          });
    } catch (FileNotFoundException e) {
      System.out.println("failed to open file");
      Assert.fail();
    }
  }

  /** Tests parsing using a StringReader */
  @Test
  public void testStringReader() {
    String myStringCSV = "Street,Apt Number,Zip Code";
    try {
      Reader reader = new StringReader(myStringCSV);
      Parser<List<String>> parser = new Parser<>(reader, new ListCreator());
      List<List<String>> rows = parser.parseCSV();
      Assert.assertEquals(rows.get(0).get(0), "Street");
      Assert.assertEquals(rows.get(0).get(1), "Apt Number");
    } catch (FactoryFailureException | IOException | MalformedDataException e) {
      Assert.fail(e.getMessage());
    }
  }

  /** Tests parsing with a simple StringCreator */
  @Test
  public void testWithStringCreator() {
    try {
      Reader reader = new FileReader("./data/stars/ten-star.csv");
      Parser<String> parser = new Parser<>(reader, new StringCreator());
      List<String> rows = parser.parseCSV();
      Assert.assertEquals(rows.get(1), "0,Sol,0,0,0");
      Assert.assertEquals(rows.get(9), "87666,Barnard's Star,-0.01729,-1.81533,0.14824");
    } catch (FileNotFoundException e) {
      System.out.println("failed to open file");
      Assert.fail();
    } catch (FactoryFailureException | IOException | MalformedDataException e) {
      Assert.fail(e.getMessage());
    }
  }

  /** Tests parsing with a simple StarCreator */
  @Test
  public void testWithStarCreator() {
    try {
      Reader reader = new FileReader("./data/stars/ten-star.csv");
      Parser<Star> parser = new Parser<>(reader, new StarCreator());
      List<Star> rows = parser.parseCSV();
      Assert.assertEquals(rows.get(1).getStarID(), "0");
      Assert.assertEquals(rows.get(9).getStarID(), "87666");
    } catch (FileNotFoundException e) {
      System.out.println("failed to open file");
      Assert.fail();
    } catch (FactoryFailureException | IOException | MalformedDataException e) {
      Assert.fail(e.getMessage());
    }
  }

  /** Tests parsing when the StarCreator will fail */
  @Test
  public void testWithStarCreatorFail() {
    try {
      Reader reader = new FileReader("./data/census/dol_ri_earnings_disparity.csv");
      Parser<Star> parser = new Parser<>(reader, new StarCreator());
      Assert.assertThrows(
          FactoryFailureException.class,
          () -> {
            parser.parseCSV();
          });
    } catch (FileNotFoundException e) {
      System.out.println("failed to open file");
      Assert.fail();
    }
  }

  /** Tests parsing with a simple faulty creator */
  @Test
  public void testListBadCreator() {
    try {
      Reader reader = new FileReader("./data/stars/ten-star.csv");
      Parser<Integer> parser = new Parser<>(reader, new FaultyCreator());
      Assert.assertThrows(
          FactoryFailureException.class,
          () -> {
            parser.parseCSV();
          });
    } catch (FileNotFoundException e) {
      Assert.fail("failed to open file");
    }
  }
}
