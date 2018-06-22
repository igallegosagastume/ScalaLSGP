package commons.model.immutable

class VectorLS[T] private (private val elements: Vector[Vector[T]], override val nullElem: T) extends ImmutableAbstractLS[T](elements, nullElem) {

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

    new VectorLS(newElements, this.nullElem)
  }

  //  override def toString: String = {
  //    val sb = new StringBuffer();
  //    sb.append("Latin Square of order " + order + ":\n");
  //
  //    //var x = order - 1
  //    for (x <- 0 to (order - 1)) {
  //      for (y <- 0 to (order - 1)) {
  //        try {
  //          val elem = this.getValueAt(x, y);
  //          sb.append(elem);
  //          sb.append("    ".substring(elem.toString().length()));
  //
  //        } catch {
  //          case e: Exception => sb.append("--  ");
  //        }
  //      } //end row
  //      sb.append("\n");
  //    }
  //    sb.toString()
  //  }
}

object VectorLS {

  def apply[T](order: Int, nullElem: T) = {
    val vec = Vector.fill(order, order)(nullElem)

    new VectorLS[T](vec, nullElem)
  }

  /**
   * Creates a VectorLS[Int] given a range of Int
   */
  def getCyclicLS(range: Range): VectorLS[Int] = {

    var elem = range.min
    var order = range.size
    var ls = VectorLS(order, 0)

    for (row <- 0 to (order - 1)) {
      elem = row + range.min
      for (column <- 0 to (order - 1)) {
        ls = ls.setValueAt(row, column, elem)
        elem = elem + 1

        if (elem == (range.max + 1)) {
          elem = range.min
        }

      }
    }
    ls
  }

  def main(args: Array[String]) {
    //    val ls = VectorLS(3, 0)
    //
    //    println(ls)
    //
    //    val ls2 = ls.setValueAt(0, 1, 2)
    //
    //    println(ls2)

    val ls3 = VectorLS.getCyclicLS(Range(2, 7))

    println(ls3)

  }
}