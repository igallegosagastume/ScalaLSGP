package commons.model.immutable

abstract class AbstractLS[T] protected (private val elements: Vector[Vector[T]], protected val nullElem: T) {

  val order: Int = elements.size
  /**
   * Get an element in the square
   */
  def getValueAt(row: Int, column: Int): T
  
  /**
   * return a new LS with a modified element (left the impl for concrete subclasses)
   */
  def setValueAt(row: Int, column: Int, value:T) : ImmutableAbstractLS[T]
  
  
  /**
   * A LS must know how to write to file.
   *
   * @param fileName
   */
  def writeToFile(fileName: String) : String = {
    "LS not written to file "+fileName+" (not implemented yet)."
  }

  /**
   * A LS must know how to print an instance.
   *
   */
  override def toString: String = {
    val sb = new StringBuffer();
    sb.append("Latin Square of order " + order + ":\n");

    var x = order - 1
    while (x >= 0) { //the first upper line is the n-1 x coordinate
      for (y <- 0 to (order - 1)) {
        try {
          val elem = this.getValueAt(x, y);
          sb.append(elem);
          sb.append("    ".substring(elem.toString().length()));

        } catch {
          case e: Exception => sb.append("--  ");
        }
      } //end row
      sb.append("\n");
      x = x - 1
    }
    sb.toString()
  }

  /**
   * A LS implementation must know how to compare with other LSs.
   *
   */
  def equals(ls2: ImmutableAbstractLS[T]): Boolean = {

    if (order != ls2.order)
      false

    for (i <- 0 to order) {
      for (j <- 0 to order) {
        if (this.getValueAt(i, j) != ls2.getValueAt(i, j)) {
          return false;
        }
      }
    }
    return true;
  }
}

object ImmutableAbstractLS {
  
  
  
}