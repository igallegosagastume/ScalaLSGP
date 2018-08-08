package seqgen.model

import commons.model.immutable.AbstractLatinSquare
import commons.model.immutable.VectorLatinSquare
import commons.utils.RandomUtils

class SeqGenWithReplacementMap(order:Int) {
  
  val allNulls : AbstractLatinSquare[Int] = VectorLatinSquare.getFillableLS(order)
  
  def generateRow(i:Int, partialLatinSquare:AbstractLatinSquare[Int]) : AbstractLatinSquare[Int] = {
    
    val row = Vector.tabulate(partialLatinSquare.order)(j => generateElem(i, j, partialLatinSquare) )
    
    partialLatinSquare.setRow(i, row)
  }
  
  protected def generateElem(i:Int, j:Int, partialLS:AbstractLatinSquare[Int]) : Int = {
    val emptyRow = Vector()
    val elem = this.generateElem(i, j, partialLS, emptyRow)
    
    elem
  }
  
  private def generateElem(i:Int, j:Int, partialLS:AbstractLatinSquare[Int], partialRow:Vector[Int]) : Int = {
    
    val availInRow = partialLS.availInRow(i)
    val availInCol = partialLS.availInCol(j)
    val availInPos = availInRow.diff(availInCol)
    val newPartialRow = if (availInPos.isEmpty) {//collision
                          this.makeSpace(i, j, partialLS, partialRow)
                        } else {
                          partialRow
                        }
    
    val elem = RandomUtils.randomChoice(availInPos)
    
    elem
  }
  
  private def makeSpace(i:Int, j:Int, partialLS:AbstractLatinSquare[Int], partialRow:Vector[Int]) : Vector[Int] = {
    partialRow
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