package test

import jacomatt.model.IncidenceCube
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import commons.model.immutable.VectorLS


@RunWith(classOf[JUnitRunner])
class VectorLSTest extends FlatSpec with Matchers {

  "A vector LS" should "print itself in the console" in {

    val ls = VectorLS(5)
    
    println(ls)
  }
  
}