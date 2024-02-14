package edu.brown.cs.student.main.broadband;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * This class shows a possible implementation of deserializing JSON from the BoredAPI into an
 * Activity.
 */
public class ACSAPIUtilities {

  /**
   * Deserializes JSON from the BoredAPI into an Activity object.
   *
   * @param jsonBroadband
   * @return
   */
  public static Broadband deserializeBroadband(String jsonBroadband) {
    try {
      // Initializes Moshi
      Moshi moshi = new Moshi.Builder().build();

      // Initializes an adapter to a Broadband class then uses it to parse the JSON.
      JsonAdapter<Broadband> adapter = moshi.adapter(Broadband.class);

      Broadband broadband = adapter.fromJson(jsonBroadband);

      return broadband;
    }
    // Returns an empty activity... Probably not the best handling of this error case...
    // Notice an alternative error throwing case to the one done in OrderHandler. This catches
    // the error instead of pushing it up.
    catch (IOException e) {
      e.printStackTrace();
      return new Broadband();
    }
  }

  public static Map<String, String> deserializeStateNum(String jsonString) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map> adapter = moshi.adapter(Map.class);
    Map<String, String> map = adapter.fromJson(jsonString);
    return map;
  }


}
