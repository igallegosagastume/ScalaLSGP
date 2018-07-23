package commons.model.immutable

import commons.utils.RandomUtils

class VectorLatinSquare[T] private (override val elements: Vector[Vector[T]]) extends AbstractLatinSquare[T](elements) {

  //val vec = Vector.fill(order, order)(nullElem)
  //Vector.tabulate(3,3){ (i,j) => 3*i+j+1 }

  /**
   * Gets the value at specified row and column indexes.
   */
  def getValueAt(i: Int, j: Int): T = {
    elements(i)(j)
  }

  def setValueAt(i: Int, j: Int, elem: T): VectorLatinSquare[T] = {
    
    val newRow: Vector[T] = this.elements(i).updated(j, elem)

    val newElements: Vector[Vector[T]] = this.elements.updated(i, newRow)

    new VectorLatinSquare(newElements)
  }

  def setRow(i: Int, row: Vector[T]): AbstractLatinSquare[T] = {
    
    val newElements: Vector[Vector[T]] = this.elements.updated(i, row)

   /* val newElements : Vector[Vector[T]] = for (
      (elem, index) <- this.elements.zipWithIndex;
      r = if (i==index)
              row
          else
              elem
    ) yield r*/

    new VectorLatinSquare(newElements)
  }

}

object VectorLatinSquare {

  /**
   * It creates a cyclic LS by default
   */
  def apply(order: Int) = {
    VectorLatinSquare.getCyclicLS(Range(0, order))
  }

  /**
   * Creates a VectorLatinSquare[Int] given a range of Int
   */
  def getCyclicLS(range: Range): VectorLatinSquare[Int] = {

    val elem = range.min
    val order = range.size
    val vector = Vector.tabulate(order, order) { (i, j) => ((i + j) % order) }

    new VectorLatinSquare(vector)
  }

  def getFillableLS(order: Int) = {
    val vector = Vector.tabulate(order, order) { (i, j) => RandomUtils.getNullElem() }
    new VectorLatinSquare(vector)
  }

  def main(args: Array[String]) {
    //    val ls = VectorLatinSquare(3)
    //
    //    println(ls)
    //
    //    val ls2 = ls.setValueAt(0, 1, 2)
    //
    //    println(ls2)
    //
    //    val ls3 = VectorLatinSquare.getCyclicLS(Range(2, 7))
    //
    //    println(ls3)

    val ls = VectorLatinSquare.getFillableLS(3)

    val ls2 = ls.setValueAt(0, 0, 0)
      .setValueAt(0, 1, 1)
      .setValueAt(0, 2, 2)
      .setValueAt(1, 0, 2)
      .setValueAt(1, 1, 0)
    //      .setValueAt(1, 2, 1)

    println(ls2)
    println(ls2.availInCol(0))
    println(ls2.availInCol(1))
    println(ls2.availInCol(2))

    println(ls2.availInRow(1))
  }
}