package test

import jacomatt.model.IncidenceCube
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class GraphicJacoMattTest extends FlatSpec with Matchers {

  "An incidence cube" should "know how to print itself in the console" in {
    
    var ic = new IncidenceCube(20)
    ic.shuffle()
    System.out.println(ic)
    
  }
  
  "An incidence cube" should "graphic itself in 3D and allow user interaction" in {
  
    var ic = new IncidenceCube(20)
    
    ic.shuffle()
    ic.drawIncidenceCube()
    
    Thread.sleep(30000);
  }
}