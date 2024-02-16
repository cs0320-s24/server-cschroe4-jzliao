package edu.brown.cs.student.main.datasource.acs;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.main.broadband.Broadband;
import edu.brown.cs.student.main.exceptions.EmptyResponseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

public class CacheBroadbandDatasource implements ACSDatasource {

      private final LoadingCache<String, Broadband> cache;
      private final ACSDatasource<Broadband> wrappedDatasource;
      public CacheBroadbandDatasource(ACSDatasource<Broadband> wrappedDatasource, int cacheDuration){
          this.wrappedDatasource = wrappedDatasource;
          this.cache = CacheBuilder.newBuilder() //TODO: take in a builder instead!! strategy pattern
                  .maximumSize(10)
                  .expireAfterWrite(cacheDuration, TimeUnit.MINUTES)
                  .recordStats()
                  .build(
                          // Strategy pattern: how should the cache behave when
                          // it's asked for something it doesn't have?
                          new CacheLoader<>() {
                              @Override
                              public Broadband load(String key) throws URISyntaxException, IOException, EmptyResponseException, InterruptedException {
                                  System.out.println("called load for: "+ key);
                                  // If this isn't yet present in the cache, load it:
                                  String countyName = key.split(",")[0];
                                  String stateNum = key.split(",")[1];
                                  return wrappedDatasource.sendRequest(stateNum, countyName);
                              }
                          });
      }

  @Override
  public Broadband sendRequest(String stateNum, String countyName) {
      String target = countyName + "," + stateNum;
      Broadband result = this.cache.getUnchecked(target);
      //uncomment the below line for testing
      //System.out.println(this.cache.stats());
      return result;
  }
}
