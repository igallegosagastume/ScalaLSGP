package commons.immutable.model

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

  def setRow(i: Int, row: Vector[T]): VectorLatinSquare[T] = {
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
    VectorLatinSquare.getCyclicLS(order)
  }

  /**
   * Creates a VectorLatinSquare[Int] without the Latin property (temporarily)
   */
  def getFillableLS(order: Int) = {
    val vector = Vector.tabulate(order, order) { (i, j) => RandomUtils.getNullElem() }
    new VectorLatinSquare(vector)
  }

  /**
   * Creates a VectorLatinSquare[Int] given the order of the LS
   */
  def getCyclicLS(order: Int): VectorLatinSquare[Int] = {

    var vector = Vector.tabulate(order, order) { (i, j) => ((i + j) % order) }

    new VectorLatinSquare(vector)
  }

  def main(args: Array[String]) {
    val ls = VectorLatinSquare(3)

    println(ls)

    val ls2 = ls.setValueAt(0, 1, 2)

    println(ls2)

    val ls3 = VectorLatinSquare.getCyclicLS(5)

    println(ls3)

  }
}