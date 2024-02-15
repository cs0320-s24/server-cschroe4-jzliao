package edu.brown.cs.student.main.caching;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.main.exceptions.EmptyResponseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class CacheACSRequester implements Requester {

      private final LoadingCache<String, String> cache;
      private final Requester wrappedRequester;

      public CacheACSRequester(Requester wrappedRequester){
          this.wrappedRequester = wrappedRequester;
          this.cache = CacheBuilder.newBuilder()
                  .maximumSize(10)
                  .expireAfterWrite(1, TimeUnit.MINUTES)
                  .recordStats()
                  .build(
                          // Strategy pattern: how should the cache behave when
                          // it's asked for something it doesn't have?
                          new CacheLoader<>() {
                              @Override
                              public String load(String key) throws URISyntaxException, IOException, EmptyResponseException, InterruptedException {
                                  System.out.println("called load for: "+ key);
                                  // If this isn't yet present in the cache, load it:
                                  String countyName = key.split(",")[0];
                                  String stateNum = key.split(",")[1];
                                  return wrappedRequester.sendRequest(stateNum, countyName);
                              }
                          });;
      }

  @Override
  public String sendRequest(String stateNum, String countyName) {
    // todo: find in cache
      // "get" is designed for concurrent situations; for today, use getUnchecked:
      String target = countyName + "," + stateNum;
      String result = cache.getUnchecked(target);
      // For debugging and demo (would remove in a "real" version):
      System.out.println(cache.stats());
      return result;
  }
}
