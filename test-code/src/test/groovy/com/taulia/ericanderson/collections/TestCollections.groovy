import org.junit.Test

class TestCollections {


  @Test
  void testCollect100Values() {

    def count = (1..100).collect {
      String value = it
      value
    }

    assert count.size() == 100

  }

}
