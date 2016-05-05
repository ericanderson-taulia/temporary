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


  @Test
  void testEvery() {

    assert ![false,true,true].every { it }
    assert ![true,true,false].every { it }
    assert [true,true,true].every { it }

  }

}
