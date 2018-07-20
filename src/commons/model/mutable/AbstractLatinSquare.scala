package commons.model.mutable

/**
 * This is the abstract class that represent all implementation of a Latin Square.
 */
abstract class AbstractLatinSquare[T](val order:Int) {
  
  /**
	 * Gets the value at specified row and column indexes.
	 */
	def getValueAt(row:Int, column:Int):T
	
	/**
	 * Sets the value at specified row and column indexes.
	 */
	def setValueAt(row:Int, column:Int, value:T):Unit

	/**
	 * A LS implementation must know how to write to file.
	 * 
	 * @param fileName
	 */
	def writeToFile(fileName:String)
	
	/**
	 * A LS implementation must know how to print an instance.
	 * 
	 */
	override def toString:String = {
	  "Latin Square of order "+order
	}
	
	/**
	 *  Check if the structure has repetitions in some row or column.
	 *  
	 */
	def preservesLatinProperty():Boolean;
	
	/**
	 * A LS implementation must know how to compare with other LSs.
	 * 
	 */
	def equals(ls2:AbstractLatinSquare[T]):Boolean = {
	
		if (order!=ls2.order) 
		  return false

		for (i <-0 to order) {
			for (j <-0 to order) {
				if (this.getValueAt(i, j)!=ls2.getValueAt(i, j)) {
					return false;
				}
			}
		}
		return true;
	}
}