package ar.com.bluemontag.commons

import ar.com.bluemontag.commons.utils.RandomUtils

/**
 * Common methods to all latin squares
 */
abstract class LatinSquare[T](val order: Int) {

  /**
   * Get an element in the square
   */
  def getValueAt(row: Int, column: Int): T

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
   * A LS must know how to write to file.
   *
   * @param fileName
   */
  def writeToFile(fileName: String): String = {
    "LS not written to file " + fileName + " (not implemented yet)."
  }

  /**
   * A LS implementation must know how to compare with other LSs.
   *
   */
  def equals(ls2: LatinSquare[T]): Boolean = {

    if (order != ls2.order)
      false

    for (i <- 0 to (order - 1)) {
      for (j <- 0 to (order - 1)) {
        if (this.getValueAt(i, j) != ls2.getValueAt(i, j)) {
          return false
        }
      }
    }
    true
  }

  /**
   *  Check if the structure has repetitions in some row or column.
   *
   */
  def preservesLatinProperty(): Boolean = {
    var symbols: Set[T] = Set[T]();

    //row verification
    for (i <- 0 to (order - 1)) {
      symbols = Set[T]();
      for (j <- 0 to (order - 1)) {
        symbols += this.getValueAt(i, j);
      }
      if (symbols.size != order)
        return false;
    }
    //column verification
    for (j <- 0 to (order - 1)) {
      symbols = Set[T]();
      for (i <- 0 to (order - 1)) {
        symbols += this.getValueAt(i, j);
      }
      if (symbols.size != order)
        return false;
    }
    return true;
  }
}