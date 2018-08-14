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
        val emptyPath = Vector()
        val partialRowWithElem = partialRow :+ elem
        val availInRow = partialLS.allNumsVector.diff(partialRowWithElem)
//        val newPartialRow = this.eliminateRepetitionForElem(elem, j, partialLS, partialRowWithElem, availInRow, emptyPath)
//        val availInRow = partialLS.allNumsVector.diff(partialRow)
        val newPartialRow = this.makeRoomForElem(elem, elem, partialRow.indexOf(elem), partialLS, partialRow, availInRow, emptyPath)
        //now elem fits in position (i,j)
        this.generateRowWithPartialRow(i, j + 1, partialLS, newPartialRow)
      }
    }
  }

  private def makeRoomForElem(elem: Int, old: Int, idxOld: Int,
                              partialLS: AbstractLatinSquare[Int],
                              currentRow: Vector[Int],
                              availInRow: Vector[Int],
                              path: Vector[Int]): Vector[Int] = {
    
    //calculate available to choose
    val availInCol = partialLS.availInCol(idxOld)
    val available = (availInCol).diff(path).diff(Vector(elem)) //must be in available in col but not in path, and must not use elem

    if (available.isEmpty) {
      //Path no good, begin again
      //				path = new HashSet<Integer>();
      //				avail.addAll(map.get(idx_old));//cannot avoid addAll
//      val emptyPath = Vector()
//      return this.makeRoomForElem(elem, old, idxOld, partialLS, availInRow, path)
      throw new Exception("No good path... failure")
    }
    //choose
    val newElem = RandomUtils.randomChoice(available);
    val idxNew = currentRow.indexOf(newElem); //index of this elem before replacement because it will be repeated

    //make the replacement:
    val newRow = currentRow.updated(idxOld, newElem) //replace

    //store in path
    val newPath: Vector[Int] = path :+ newElem

    //remove from available in row
    val newAvailInRow = availInRow.diff(Vector(newElem))

    //			availableInCol[idx_old].add(old);
    //			availableInCol[idx_old].remove(newElem);

    val finished = (availInRow.contains(elem) && idxNew == -1) //the element is now available in currentRow and there are no repetitions

    //			idx_old = idx_new;
    //			old = newElem;
    if (finished) {
      return currentRow
    } else {
      return this.makeRoomForElem(elem, newElem, idxNew, partialLS, newRow, newAvailInRow, newPath)
    }
  }
  
//  private def eliminateRepetitionForElem(newIndex:Int, lastIndex:Int, partialLS:AbstractLatinSquare[Int], partialRow:Vector[Int], path:Vector[Int]) : Vector[Int] = {
//    
//    val newPos = partialRow.indexOf(lastElem)
//    
//    if ((newPos == lastIndex) && 
//        () ) {
//      return partialRow
//    }
//    
//    val newPath :Vector[Int] = path :+ lastElem
//
//    val availInCol = partialLS.availInCol(newPos)
//    
////    val availInRow = partialLS.allNumsVector.diff(partialRow)
//    
//    val available = (availInCol).diff(newPath) //must be in available but not in path
//    
//    if (available.isEmpty) {
//      throw new Exception("Bad path")
//    }
//    //take a new element for the position
//    val newElem = RandomUtils.randomChoice(available) 
//    //make the replacement:
//    val newRow = partialRow.updated(newPos, newElem)
//        
//    eliminateRepetitionForElem(newElem, partialLS, newRow, newPath)
//  }
  
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

//    val ls3 = ls2.setRow(1, Vector(2,0,1,4,3))
//
//    val partialRow = Vector(1,2,0,2)
//    
//    val newRow = generator.eliminateRepetitionForElem(2, ls3, partialRow, Vector())
//    
//    print(ls3.setRow(2, newRow))
  }
}