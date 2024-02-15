package edu.brown.cs.student;

import org.junit.jupiter.api.Test;

public class TestServerCSV {

  @Test
  public void testLoadHandler() {
    // basic case

    // no file inputted

    // file not in data folder

    // file not found

    // loading two items + check if updated

    // load without parameters specified at all
  }

  @Test
  public void testViewHandler() {
    // basic case

    // view without loading

    // view after re-loading, make sure it changes
  }

  @Test
  public void testSearchHandler() {
    // basic case

    // search without loading

    // search term not found

    // search without identifier

    // search with col num
    // search with too small/big col num

    // search with string col name
    // search with col name not found
    // search with col name but different case

    // search with identifier but hasHeader = false (should error)

    // search case when it should output >1 row

    // search case where identifier makes a difference

    // search without parameters specified at all
  }
}
