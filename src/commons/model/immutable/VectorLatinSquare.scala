package commons.model.immutable

class VectorLS[T] private (override val elements: Vector[Vector[T]]) extends AbstractLS[T](elements) {

  //val vec = Vector.fill(order, order)(nullElem)
  //Vector.tabulate(3,3){ (i,j) => 3*i+j+1 }

  /**
   * Gets the value at specified row and column indexes.
   */
  def getValueAt(row: Int, column: Int): T = {
    elements(row)(column)
  }

  def setValueAt(row: Int, column: Int, elem: T): VectorLS[T] = {
    val newRow: Vector[T] = this.elements(row).updated(column, elem)

    val newElements: Vector[Vector[T]] = this.elements.updated(row, newRow)

    new VectorLS(newElements)
  }

}

object VectorLS {

  /**
   * It creates a cyclic LS by default
   */
  def apply(order: Int) = {
    VectorLS.getCyclicLS(Range(0, order))
  }

  /**
   * Creates a VectorLS[Int] given a range of Int
   */
  def getCyclicLS(range: Range): VectorLS[Int] = {

    var elem = range.min
    var order = range.size
    var vector = Vector.tabulate(order, order){ (i,j) => ((i+j) % order) }

    new VectorLS(vector)
  }

  def main(args: Array[String]) {
    val ls = VectorLS(3)

    println(ls)

    val ls2 = ls.setValueAt(0, 1, 2)

    println(ls2)

    val ls3 = VectorLS.getCyclicLS(Range(2, 7))

    println(ls3)

  }
}