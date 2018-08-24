package commons.immutable.test

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import ar.com.bluemontag.commons.immutable.model.VectorLatinSquare

@RunWith(classOf[JUnitRunner])
class VectorLSTest extends FlatSpec with Matchers {

  "A vector LS" should "print itself in the console" in {

    val ls = VectorLatinSquare(6)
    
    println(ls)
  }
  
}