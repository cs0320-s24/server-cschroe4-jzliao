package edu.brown.cs.student.main.broadband;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * This class shows a possible implementation of deserializing JSON from the BoredAPI into an
 * Activity.
 */
public class ACSAPIUtilities {

  /**
   * Deserializes JSON from the ACS API into a Broadband object.
   *
   * @param jsonBroadband json returned by request to ACS API
   * @return
   */
  public static Broadband deserializeBroadband(String jsonBroadband, Date date) throws IOException {
    // Initializes Moshi
    Moshi moshi = new Moshi.Builder().build();

    // Initializes an adapter to a Broadband class then uses it to parse the JSON.
    JsonAdapter<List> adapter = moshi.adapter(List.class);
    List<List<String>> countyInfo = adapter.fromJson(jsonBroadband);

    String name = countyInfo.get(1).get(0);
    String percentage = countyInfo.get(1).get(1);

    return new Broadband(name, percentage, date.toString());
  }

  /**
   * Deserializes JSON representing state names and their state codes and returns them as a map
   *
   * @param jsonString
   * @return state names and their corresponding codes as a map
   * @throws IOException
   */
  public static HashMap<String, String> deserializeStateNum(String jsonString) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<List> adapter = moshi.adapter(List.class);
    List<List<String>> stateList = adapter.fromJson(jsonString);
    if (stateList == null) {
      throw new IOException("No state found");
    }
    HashMap<String, String> stateMap = new HashMap<>();
    stateList.remove(0);
    for (List<String> state : stateList) {
      if (state.size() != 2) {
        throw new IOException("Error in deserializing state number");
      }
      stateMap.put(state.get(0).toLowerCase(), state.get(1));
    }
    return stateMap;
  }

  /**
   * Deserializes JSON respresenting county names and their corresponding codes for a certain state
   * and returns this info as a map
   *
   * @param jsonString
   * @return hashmap of county names and their corresponding number codes
   * @throws IOException
   */
  public static HashMap<String, String> deserializeCountyNum(String jsonString) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<List> adapter = moshi.adapter(List.class);
    List<List<String>> countyList = adapter.fromJson(jsonString);
    if (countyList == null) {
      throw new IOException("Error in county deserialization");
    }
    HashMap<String, String> countyMap = new HashMap<>();
    countyList.remove(0);
    for (List<String> row : countyList) {
      if (row.size() != 3) {
        throw new IOException("Error in county deserialization");
      }

      String county = row.get(0).split(",")[0];
      // Map of <county name, county code> with all lowercase for non case-sensitive searching
      countyMap.put(county.toLowerCase(), row.get(2));
    }
    return countyMap;
  }
}
