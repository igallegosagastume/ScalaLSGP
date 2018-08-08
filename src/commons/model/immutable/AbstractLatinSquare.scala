package commons.model.immutable

import commons.utils.RandomUtils

abstract class AbstractLatinSquare[T] protected (protected val elements: Vector[Vector[T]]) {

  val order: Int = elements.size
  val allNumsVector : Vector[Int] = Vector.tabulate(order){ i => i }
  val allNumsSet : Set[Int] = (0 to order).toSet
  
  /**
   * Get an element in the square
   */
  def getValueAt(row: Int, column: Int): T

  /**
   * return a new LS with a modified element (left the impl for concrete subclasses)
   */
  def setValueAt(row: Int, column: Int, value: T): AbstractLatinSquare[T]

  
  def getRow(x:Int) : Vector[T] = {//TODO create type Row
    this.elements(x)
  }
  
  def getCol(y:Int) : Vector[T] = {
    this.elements.map(row => row(y))
  }
  
  def setRow(i:Int, row:Vector[T]) : AbstractLatinSquare[T]
  
  def availInRow(x:Int) : Vector[Int] = {
    val row = this.getRow(x)
    
    allNumsVector.diff(row)
  }
  
  def availInCol(y:Int) = {
    val col = this.getCol(y)
    
    allNumsVector.diff(col)
  }
  /**
   * A LS must know how to write to file.
   *
   * @param fileName
   */
  def writeToFile(fileName: String): String = {
    "LS not written to file " + fileName + " (not implemented yet)."
  }

  /**
   * A LS must know how to print an instance.
   *
   */
  override def toString: String = {
    val sb = new StringBuffer();
    sb.append("Latin Square of order " + this.order + ":\n");

    var x = order - 1
    while (x >= 0) { //the first upper line is the n-1 x coordinate
      for (y <- 0 to (order - 1)) {
        try {
          val elem = this.getValueAt(x, y)

          if (elem.equals(RandomUtils.getNullElem())) 
            sb.append("--  ")
          else {
            sb.append(elem)
            sb.append("    ".substring(elem.toString().length()))
          }
        } catch {
          case e: Exception => sb.append("--  ")
        }
      } //end row
      sb.append("\n")
      x = x - 1
    }
    sb.toString()
  }

  /**
   * A LS implementation must know how to compare with other LSs.
   *
   */
  def equals(ls2: AbstractLatinSquare[T]): Boolean = {

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

object AbstractLatinSquare {

}