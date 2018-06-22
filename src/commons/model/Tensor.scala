package commons.model

import scala.collection.mutable.ArraySeq


class Tensor[T](val size: Int, var nullElem: T) {// error: traits or objects may not have parameters (type parameters are OK)

  private var elements: ArraySeq[ArraySeq[ArraySeq[T]]] = new ArraySeq(size)

  //initialization:
  for (i <- 0 to (size - 1)) {
    elements(i) = new ArraySeq(size)
    for (j <- 0 to (size - 1)) {
      elements(i)(j) = new ArraySeq(size)
      for (k <- 0 to (size - 1)) {
        elements(i)(j)(k) = nullElem
      }
    }
  }

  def getElemAt(x: Int, y: Int, z: Int): T = {
    elements(x)(y)(z)
  }

  def setElemAt(elem: T, x: Int, y: Int, z: Int) {
    elements(x)(y)(z) = elem
  }
  
  private def sum1ToCood(axis: Char, x: Int, y: Int, z: Int): (Int, Int, Int) = axis match {
    case 'x' => 
                var x1 = x+1
              (x1, y, z)
    case 'y' => var y1 = y+1
              (x, y1, z)
    case 'z' => var z1 = z+1
              (x, y, z1)
    case others => throw new Exception("axis parameter must be 'x', 'y' or 'z' Char")
  }

  private def initTuple(coord1: Int, coord2: Int, axis: Char, fromPos: Int): (Int, Int, Int) = axis match {
    case 'x' => (fromPos, coord1, coord2)
    case 'y' => (coord1, fromPos, coord2)
    case 'z' => (coord1, coord2, fromPos)
    case others => throw new Exception("axis parameter must be 'x', 'y' or 'z' Char")
  }

  private def returnCoord(axis: Char, x: Int, y: Int, z: Int): Int = axis match {
    case 'x' => x
    case 'y' => y
    case 'z' => z
    case others => throw new Exception("axis parameter must be 'x', 'y' or 'z' Char")
  }

  def indexOfElem(elem: T, coord1: Int, coord2: Int, axis: Char): Int = {
    return this.indexOfElem(elem, coord1, coord2, axis, 0)
  }
  /**
   * It returns the index of the corresponding "axis" that holds "elem"
   */
  def indexOfElem(elem: T, coord1: Int, coord2: Int, axis: Char, fromPos: Int): Int = {

    var (x: Int, y: Int, z: Int) = initTuple(coord1, coord2, axis, fromPos)

    if (x == size || y == size || z == size) //if outside the box of any coord
      return -1

    while (getElemAt(x, y, z) != elem) { //   cube[x][y][z]!=1) {
      var (q:Int, r:Int, t:Int) = sum1ToCood(axis, x, y, z)
      //unnecesary , but some restriction applies here
      x = q
      y = r
      z = t
      if (x == size || y == size || z == size) //if outside the box of any coord
        return -1
    }
    returnCoord(axis, x, y, z)
  }

  override def toString(): String = {
    var sb = new StringBuffer
    //begin row
    var i = size-1
    while (i >=0) {
      //begin column
      for (j <- 0 to (size - 1)) {
        sb.append("{")
        for (k <- 0 to (size - 1)) {
          sb.append(elements(i)(j)(k))
          sb.append(" ")
        }
        sb.append("} ")
      }
      sb.append("\n")
      i = i - 1
    }

    sb.toString();
  }
}