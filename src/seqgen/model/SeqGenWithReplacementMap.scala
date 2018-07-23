package seqgen.model

import commons.model.immutable.AbstractLatinSquare
import commons.model.immutable.VectorLatinSquare
import commons.utils.RandomUtils

class SeqGenWithReplacementMap(order:Int) {
  
  val allNulls : AbstractLatinSquare[Int] = VectorLatinSquare.getFillableLS(order)
  
  def generateRow(i:Int, partialLatinSquare:AbstractLatinSquare[Int]) : AbstractLatinSquare[Int] = {
    
    val row = Vector.tabulate(partialLatinSquare.order)(col => generateElem(col, partialLatinSquare) )
    
    partialLatinSquare.setRow(i, row)
  }
  
  def generateElem(col:Int, partialLS:AbstractLatinSquare[Int]) : Int = {
    val elem = RandomUtils.randomChoice(partialLS.availInCol(col))//TODO continue here
    
    elem
  }
  
  /**
   * PRECONDITION: The LS has been filled up to the (row-1)-th row (inclusive).
   * 
   *  The result at i will contain the available elements in column i
   */
  def generateReplacementMap(row:Int, partialLatinSquare: AbstractLatinSquare[Int]) : Vector[Vector[Int]] = {
    
    Vector.tabulate(partialLatinSquare.order)(col => partialLatinSquare.availInCol(col))
    
  }//generateReplacementMap
  
}

object SeqGenWithReplacementMap {
  
  def main(args:Array[String]) = {
    val ls = VectorLatinSquare.getFillableLS(5)
    
    
    val ls2 = ls.setRow(0, Vector.tabulate(5)(i => i))
    
//    print(ls2)
    
    val generator = new SeqGenWithReplacementMap(5)
    
//    println(generator.generateReplacementMap(0, ls2))
    
    println(generator.generateRow(1, ls2))
    
  }
}