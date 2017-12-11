package commons.model

class Matrix[T](val n: Int) {

  var elements: Array[Array[T]];

  def initialize(elem:T): Unit = {

    for (i <- 0 to (n - 1)) {
      for (j <- 0 to (n - 1)) {
          elements(i)(j) = elem
      }
    }
  }
  
  override def apply(i:Int,j:Int) {
    elements(i)(j)
  }
  
  def set(i:Int,j:Int,elem:T) {
    elements(i)(j) = elem
  }
}