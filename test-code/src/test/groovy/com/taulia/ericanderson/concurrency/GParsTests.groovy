package com.taulia.ericanderson.concurrency

import groovyx.gpars.GParsPool
import org.junit.Test

import java.util.concurrent.ForkJoinPool

/**
 * Created by eanderson on 4/25/16.
 */
class GParsTests {

  private volatile Integer total = 0

  @Test
  void testSimplePools() {

    //multiply numbers asynchronously
    GParsPool.withPool {
      final List result = [1, 2, 3, 4, 5].collectParallel {it * 2}
      assert ([2, 4, 6, 8, 10].equals(result))
    }

    GParsPool.withPool(5) {

       (0..9).eachParallel {
          println "works"

      }
    }

  }

}
