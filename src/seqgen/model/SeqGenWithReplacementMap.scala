package seqgen.model

import commons.model.immutable.AbstractLatinSquare
import commons.model.immutable.VectorLatinSquare
import commons.utils.RandomUtils

class SeqGenWithReplacementMap(order:Int) {
  
  val allNulls : AbstractLatinSquare[Int] = VectorLatinSquare.getFillableLS(order)
  
  
  def generateLS() : VectorLatinSquare[Int] = {
   
    val ls0 = VectorLatinSquare.getFillableLS(order) //get empty matrix
    
    val rows = (0 to (order - 1)).toList
    
    rows.foldLeft(ls0){ (lsi, i) => this.generateRow(i, lsi) }
  }
  
  def generateRow(i:Int, partialLatinSquare:VectorLatinSquare[Int]) : VectorLatinSquare[Int] = {
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
        
        val availInRow = partialLS.allNumsVector.diff(partialRow)
        
        val idxOfFirstElem = partialRow.indexOf(elem) //search for the root of the problem (elem is already in row)
        
        val newPartialRow = this.makeRoomForElem(elem, elem, idxOfFirstElem, partialLS, partialRowWithElem, availInRow, emptyPath)
        
        //now elem fits in position (i,j), continue with column (j + 1)
        this.generateRowWithPartialRow(i, j + 1, partialLS, newPartialRow)
      }
    }
  }

  private def makeRoomForElem(elem: Int, old: Int, idxOld: Int,
                              partialLS: AbstractLatinSquare[Int],
                              currentRow: Vector[Int],
                              availInRow: Vector[Int],
                              path: Vector[Int]): Vector[Int] = {
    
    //calculate available to choose at idxOld
    val availInCol = partialLS.availInCol(idxOld)
    val available = (availInCol).diff(path).diff(Vector(elem)) //must be in available in col but not in path, and must not use elem

    if (available.isEmpty) {
//      Path no good, begin again
//      
//      val emptyPath = Vector()
//      avail.addAll(map.get(idx_old));//cannot avoid addAll
      println("No good path for partial row:")
      println(partialLS)
      println(currentRow)
      throw new Exception("No good path... failure")
    }
    //choose a new element for inxOld position
    val newElem = RandomUtils.randomChoice(available);
    val idxNew = currentRow.indexOf(newElem); //index of this newElem before replacement because it will be repeated

    //make the replacement:
    val newRow = currentRow.updated(idxOld, newElem) //replace and generate a repetition of elem newElem

    //store in path
    val newPath: Vector[Int] = path :+ newElem

    //remove from available in row
    val newAvailInRow = availInRow.diff(Vector(newElem))

    //			availableInCol[idx_old].add(old);
    //			availableInCol[idx_old].remove(newElem);

    val finished = (idxNew == -1) //elem is now available in currentRow and there are no repetitions

    //			idx_old = idx_new;
    //			old = newElem;
    if (finished) {
      return newRow
    } else {
      return this.makeRoomForElem(elem, newElem, idxNew, partialLS, newRow, newAvailInRow, newPath)
    }
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
}
  
object SeqGenWithReplacementMap {
  
  def main(args:Array[String]) = {
//    val generator = new SeqGenWithReplacementMap(5)
//    val ls = VectorLatinSquare.getFillableLS(5)
//    
//    val ls2 = ls.setRow(0, Vector.tabulate(5)(i => i))
//    
//    val ls3 = generator.generateRow(1, ls2)
//    
//    val ls4 = generator.generateRow(2, ls3)
//    
//    val ls5 = generator.generateRow(3, ls4)
//    
//    val ls6 = generator.generateRow(4, ls5)
//    
//    println(ls6)
    
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
    var i = 0
    while (i < 100) { 
      val generator = new SeqGenWithReplacementMap(7)
      val ls = generator.generateLS()
      println(ls)
      if (!ls.preservesLatinProperty()) {
        println("Does not preserve Latin Property")
        i = 100
      }
      i = i + 1
    }
  }
}