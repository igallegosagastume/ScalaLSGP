package ar.com.bluemontag.commons.immutable.model

import ar.com.bluemontag.commons.utils.RandomUtils
import scala.collection.mutable.Set
import ar.com.bluemontag.commons.LatinSquare

abstract class ImmutableLatinSquare[T] (protected val elements: Vector[Vector[T]]) extends LatinSquare[T](elements.size) {

//  val order: Int = elements.size
  val allNumsVector : Vector[Int] = Vector.tabulate(order){ i => i }
  
  
  def getRow(x:Int) : Vector[T] = {//TODO create type Row
    this.elements(x)
  }
  
  def getCol(y:Int) : Vector[T] = {
    this.elements.map(row => row(y))
  }
  
  def setRow(i:Int, row:Vector[T]) : ImmutableLatinSquare[T]
  
  def availInRow(x:Int) : Vector[Int] = {
    val row = this.getRow(x)
    
    allNumsVector.diff(row)
  }
  
  def availInCol(y:Int) = {
    val col = this.getCol(y)
    
    allNumsVector.diff(col)
  }

}//end class
