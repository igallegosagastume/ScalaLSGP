package seqgen.model

import commons.model.immutable.AbstractLatinSquare
import commons.model.immutable.VectorLatinSquare
import commons.utils.RandomUtils

class SeqGenWithReplacementMap(order:Int) {
  
  val allNulls : AbstractLatinSquare[Int] = VectorLatinSquare.getFillableLS(order)
  
  def generateRow(i:Int, partialLatinSquare:AbstractLatinSquare[Int]) : AbstractLatinSquare[Int] = {
    val emptyRow = Vector()
    
    val row = this.generateRowWithPartialRow(i, 0, partialLatinSquare, emptyRow)//Vector.tabulate(partialLatinSquare.order)(j => generateElem(i, j, partialLatinSquare) )
    
    partialLatinSquare.setRow(i, row)
  }

  protected def generateRowWithPartialRow(i: Int, j: Int, partialLS: AbstractLatinSquare[Int], partialRow: Vector[Int]): Vector[Int] = {

    if (partialRow.size == partialLS.order) {
      partialRow
    } else {
      val elem = this.generateElem(i, j, partialLS, partialRow)

      if (!partialRow.contains(elem)) {
        this.generateRowWithPartialRow(i, j + 1, partialLS, partialRow :+ elem)
      } else {
//        val newPartialRow = this.makeSpaceForElem(elem, i, j, partialLS, partialRow)
        //now elem fits in position (i,j)
        this.generateRowWithPartialRow(i, j + 1, partialLS, partialRow :+ elem)
      }
    }
  }
  
  private def makeSpaceForElem(elem:Int, inPos:Int, partialLS:AbstractLatinSquare[Int], partialRow:Vector[Int]) : Vector[Int] = {
    val pos = partialRow.indexOf(elem)
    
    if ((pos == -1) && (partialRow.distinct.size==partialRow.size) ) {//elem is not in partialRow && partialRow has not repetitions
      partialRow
    } else {
      val possibleInColumnPos = partialLS.availInCol(pos)
      val actualElem = Vector(partialRow(pos))
      val newElem = RandomUtils.randomChoice(possibleInColumnPos.diff(actualElem))
      val newRow = partialRow.updated(pos, newElem)
      makeSpaceForElem(elem, inPos, partialLS, newRow, partialRow(pos), Vector())
    }
  }
  
  private def makeSpaceForElem(elem:Int, inPos:Int, partialLS:AbstractLatinSquare[Int], partialRow:Vector[Int], lastIndex:Int, path:Vector[Int]) :Vector[Int] = {
    
    
    
    path
  }
  
  private def generateElem(i: Int, j: Int, partialLS: AbstractLatinSquare[Int], partialRow: Vector[Int]): Int = {
    val availInRow = partialLS.allNumsVector.diff(partialRow)
    val availInCol = partialLS.availInCol(j)
    val availInPos = availInRow.intersect(availInCol)

    if (availInPos.isEmpty) {
      RandomUtils.randomChoice(availInCol) //there is a collision here, allow a repetition in row 
    } else {
      RandomUtils.randomChoice(availInPos)
    }
  }
  
  /**
   * PRECONDITION: The LS has been filled up to the (row-1)-th row (inclusive).
   * 
   *  The result at i will contain the available elements in column i
   */
  def generateReplacementMap(partialLatinSquare: AbstractLatinSquare[Int]) : Vector[Vector[Int]] = {
    
    Vector.tabulate(partialLatinSquare.order)(col => partialLatinSquare.availInCol(col))
    
  }//generateReplacementMap
  
}

object SeqGenWithReplacementMap {
  
  def main(args:Array[String]) = {
    val generator = new SeqGenWithReplacementMap(5)
    val ls = VectorLatinSquare.getFillableLS(5)
    
    val ls2 = ls.setRow(0, Vector.tabulate(5)(i => i))
    
    val ls3 = generator.generateRow(1, ls2)
    
    val ls4 = generator.generateRow(2, ls3)
    
    val ls5 = generator.generateRow(3, ls4)
    
    val ls6 = generator.generateRow(4, ls5)
    
    println(ls6)
    
//    val ls11 = ls.setRow(0, Vector(0,1,2,3,4))
//    val ls22 = ls11.setRow(1, Vector(1,3,0,2, RandomUtils.getNullElem()))
//    
//    println(ls22)
//    print(generator.makeSpaceForElem(4, 1, 4, ls22, Vector(1,3,0,2)))
    
  }
}