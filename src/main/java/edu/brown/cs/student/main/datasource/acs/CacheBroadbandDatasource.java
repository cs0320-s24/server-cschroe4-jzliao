package edu.brown.cs.student.main.datasource.acs;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.main.broadband.Broadband;
import edu.brown.cs.student.main.exceptions.EmptyResponseException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CacheBroadbandDatasource implements ACSDatasource {

  private final LoadingCache<String, Broadband> cache;
  private final ACSDatasource<Broadband> wrappedDatasource;

  public CacheBroadbandDatasource(ACSDatasource<Broadband> wrappedDatasource, long cacheDuration) {
    this.wrappedDatasource = wrappedDatasource;
    this.cache =
        CacheBuilder.newBuilder() // TODO: take in a builder instead!! strategy pattern
            .maximumSize(10)
            .expireAfterWrite(cacheDuration, TimeUnit.SECONDS)
            .recordStats()
            .build(
                // Strategy pattern: how should the cache behave when
                // it's asked for something it doesn't have?
                new CacheLoader<>() {
                  @Override
                  public Broadband load(String key)
                      throws URISyntaxException, IOException, EmptyResponseException,
                          InterruptedException {
                    // If this isn't yet present in the cache, load it:
                    String countyName = key.split(",")[0];
                    String stateName = key.split(",")[1];
                    return wrappedDatasource.sendRequest(stateName, countyName);
                  }
                });
  }

  @Override
  public Broadband sendRequest(String stateName, String countyName) {
    String target = countyName + "," + stateName;
    Broadband result = this.cache.getUnchecked(target);
    return result;
  }

  public boolean inCache(String key, Broadband broadband) throws ExecutionException {
      if(!this.cache.asMap().containsKey(key)){
          return false;
      }
      return this.cache.asMap().get(key).equals(broadband);

  }

  public Map<String, Long> getStatsMap(){
      Map<String,Long> statsMap = new HashMap<>();
      statsMap.put("hitCount", this.cache.stats().hitCount());
      statsMap.put("missCount", this.cache.stats().missCount());
      statsMap.put("evictionCount", this.cache.stats().evictionCount());
      return statsMap;
  }
}
