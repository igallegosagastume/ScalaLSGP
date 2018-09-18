package ar.com.bluemontag.commons.generators

import ar.com.bluemontag.commons.LatinSquare
import ar.com.bluemontag.commons.immutable.model.VectorLatinSquare

class AbstractLSGenerator(val order:Int) {
  
  
  def generateLS() : LatinSquare[Int] = {
    VectorLatinSquare.getCyclicLS(order)  //default value of a generator is a cyclic vector LS
  }
  
}