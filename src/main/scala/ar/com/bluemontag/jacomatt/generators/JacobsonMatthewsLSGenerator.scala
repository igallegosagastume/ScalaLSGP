package ar.com.bluemontag.jacomatt.generators

import ar.com.bluemontag.commons.LatinSquare
import ar.com.bluemontag.commons.generators.AbstractLSGenerator
import ar.com.bluemontag.jacomatt.model.OptimizedIncidenceCube

class JacobsonMatthewsLSGenerator(override val order: Int) extends AbstractLSGenerator(order) {
  
  override def generateLS() : LatinSquare[Int] = {
    
    var cube = new OptimizedIncidenceCube(order)
		cube.shuffle()
		
		cube
  }
}