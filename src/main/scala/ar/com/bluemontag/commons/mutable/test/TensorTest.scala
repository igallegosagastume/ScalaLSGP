package commons.mutable.test

import commons.mutable.model.Tensor
import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalactic.source.Position.apply

@RunWith(classOf[JUnitRunner])
class TensorTest extends FlatSpec with Matchers {

  "A Tensor" should "be able to store an retrieve a value" in {
    val tensor = new Tensor[Int](size = 10, nullElem = 0)
    tensor.setElemAt(5, 3, 0, 0)
    tensor.getElemAt(3, 0, 0) should be(5)
  }

  it should "find a value in the three possible axis" in {
    val tensor = new Tensor[Int](size = 10, nullElem = 0)
    tensor.setElemAt(5, 3, 4, 2)
    tensor.indexOfElem(5, 3, 4, 'z') should be(2)
    tensor.indexOfElem(5, 3, 2, 'y') should be(4)
    tensor.indexOfElem(5, 4, 2, 'x') should be(3)
  }

  it should "should return -1 if searchForElem does not found the elem" in {
    val tensor = new Tensor[Int](size = 10, nullElem = 0)
    tensor.setElemAt(5, 3, 4, 2)
    tensor.indexOfElem(5, 3, 5, 'z') should be(-1)
  }

  it should "throw IndexOutOfBoundsException if indexes are greater or equal than its size" in {
    val tensor = new Tensor[Int](size = 10, nullElem = 0)
    a[IndexOutOfBoundsException] should be thrownBy {
      tensor.getElemAt(10, 0, 0)
    }
  }
    
  it should "be pretty printed by the expression tensor.toString()" in {
    val tensor = new Tensor[Int](size=5, nullElem=0)
    System.out.println(tensor)
  }
  
  it should "be capable of storing other types, like Doubles or Strings" in {
    val tensor  = new Tensor[String](size=5, nullElem="-")
    tensor.setElemAt("Z", 0, 3, 3)
    System.out.println(tensor)
    
    val tensor2 = new Tensor[Double](size=5, nullElem=0.0)
    tensor2.setElemAt(5.4, 0, 0, 3)
    System.out.println(tensor2)
  }
}
