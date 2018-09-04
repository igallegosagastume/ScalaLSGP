package ar.com.bluemontag.jacomatt.test

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalactic.source.Position.apply
import ar.com.bluemontag.jacomatt.model.EfficientIncidenceCube


@RunWith(classOf[JUnitRunner])
class GraphicJacoMattTest extends FlatSpec with Matchers {

  /*"An incidence cube" should "know how to print itself in the console" in {
    
    var ic = new IncidenceCube(20)
    ic.shuffle()
    System.out.println(ic)
    
  }
  
  it should "graphic itself in 3D and allow user interaction" in {
  
    var ic = new IncidenceCube(20)
    
    ic.shuffle()
    ic.drawIncidenceCube()
    
    Thread.sleep(30000);
  }*/
  
  "A efficient incidence cube" should "graphic itself in 3D and allow user interaction" in {
  
    var ic = new EfficientIncidenceCube(20)
    
    ic.shuffle()
    
    println(ic)
    ic.drawIncidenceCube()
    
    Thread.sleep(30000);
  }
}