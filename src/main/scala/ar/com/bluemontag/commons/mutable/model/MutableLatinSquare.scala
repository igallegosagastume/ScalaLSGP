package ar.com.bluemontag.commons.mutable.model

import ar.com.bluemontag.commons.LatinSquare

/**
 * This is the abstract class that represent all implementation of a Latin Square.
 */
abstract class MutableLatinSquare[T](override val order:Int) extends LatinSquare[T](order) {
  
	/**
	 * Sets the value at specified row and column indexes.
	 */
	def setValueAt(row:Int, column:Int, value:T):Unit


}