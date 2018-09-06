package ar.com.bluemontag.seqgen.generators

import ar.com.bluemontag.commons.immutable.model.ImmutableLatinSquare
import ar.com.bluemontag.commons.immutable.model.VectorLatinSquare
import ar.com.bluemontag.commons.utils.RandomUtils
import ar.com.bluemontag.commons.utils.ControlStructures
import scala.Vector
import ar.com.bluemontag.commons.generators.AbstractLSGenerator

/**
 * @author igallegosagas
 * 
 */
class ReplacementChainLSGenerator(override val order: Int) extends AbstractLSGenerator(order) {
  val allNulls: ImmutableLatinSquare[Int] = VectorLatinSquare.getFillableLS(order)

  override def generateLS(): ImmutableLatinSquare[Int] = {

    val ls0 = VectorLatinSquare.getFillableLS(order) //get empty matrix

    val rows = (0 to (order - 1)).toList

    rows.foldLeft(ls0) { (lsi, i) => this.generateRow(i, lsi) }
  }

  def generateRow(i: Int, partialLatinSquare: VectorLatinSquare[Int]): VectorLatinSquare[Int] = {
    val emptyRow = Vector()

    val row = this.generateRowWithPartialRow(i, 0, partialLatinSquare, emptyRow) //Vector.tabulate(partialLatinSquare.order)(j => generateElem(i, j, partialLatinSquare) )

    partialLatinSquare.setRow(i, row)
  }

  protected def generateRowWithPartialRow(i: Int, j: Int, partialLS: ImmutableLatinSquare[Int], partialRow: Vector[Int]): Vector[Int] = {

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

        val newPartialRow = ControlStructures.retryUntilSucceed(
                                                  this.makeRoomForElem(elem, idxOfFirstElem,
                                                                       partialLS, partialRowWithElem,
                                                                       availInRow, emptyPath)
                                                               )
        
        //now elem fits in position (i,j), continue with column (j + 1)
        this.generateRowWithPartialRow(i, j + 1, partialLS, newPartialRow)
      }
    }
  }
  
  private def makeRoomForElem(elem: Int, prevIdx: Int,
                              partialLS: ImmutableLatinSquare[Int],
                              currentRow: Vector[Int],
                              availInRow: Vector[Int],
                              path: Vector[Int]): Vector[Int] = {
  
    //calculate available to choose at idxOld
    val availInCol = partialLS.availInCol(prevIdx)
    val available = (availInCol).diff(path).diff(Vector(elem)) //must be in available in col but not in path, and must not use elem

    if (available.isEmpty) {
      // Path no good, begin again
      throw new Exception("No good path... failure")
    }
    //choose a new element for inxOld position
    val newElem = RandomUtils.randomChoice(available);
    val idxNew = currentRow.indexOf(newElem); //index of this newElem before replacement because it will be repeated

    //make the replacement:
    val newRow = currentRow.updated(prevIdx, newElem) //replace and generate a repetition of elem newElem

    //store in path
    val newPath: Vector[Int] = path :+ newElem

    //remove from available in row
    val newAvailInRow = availInRow.diff(Vector(newElem))

    if (idxNew == -1) { //elem is now available in row and there are no repetitions
      return newRow
    } else {
      return this.makeRoomForElem(elem, idxNew, partialLS, newRow, newAvailInRow, newPath)
    }
  }

  private def generateElem(i: Int, j: Int, partialLS: ImmutableLatinSquare[Int], partialRow: Vector[Int]): Int = {
    val availInRow = partialLS.allNumsVector.diff(partialRow)
    val availInCol = partialLS.availInCol(j)
    val availInPos = availInRow.intersect(availInCol)

    if (availInPos.isEmpty) {
      RandomUtils.randomChoice(availInCol) //there is a collision here, allow a repetition in row
    } else {
      RandomUtils.randomChoice(availInPos)
    }
  }

} //end class

object ReplacementChainLSGenerator {

  def main(args: Array[String]) = {
    var i = 0
    while (i < 100) {
      println(i)
      val generator = new ReplacementChainLSGenerator(7)
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