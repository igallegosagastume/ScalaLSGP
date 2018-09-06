/**
 * Creation date: 19/08/2018
 * 
 */

/**
 * @author igallegosagas
 * 
 * @email ignaciogallego@gmail.com
 * 
 * @tags latin square generation
 * 
 */
package ar.com.bluemontag.jacomatt.model

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import ar.com.bluemontag.commons.utils.RandomUtils
import ar.com.bluemontag.commons.immutable.model.OrderedTriple;
import ar.com.bluemontag.commons.utils.DrawingOptions
import ar.com.bluemontag.commons.mutable.model.AbstractLatinSquare
import ar.com.bluemontag.commons.mutable.model.Tensor
import scala.collection.mutable.ArraySeq

/**
 *  This is a more efficient implementation of a Jacobson & Matthews' incidence cube.
 *  
 *  It has 3 views of the same cube, to save time when looking for the 1's and 0's along each axis.
 *  
 * 
 * @author Ignacio Gallego Sagastume
 * @email ignaciogallego@gmail.com
 * 
 * @tags Java Latin Square generation
 */

class OptimizedIncidenceCube(override val order:Int) extends IncidenceCube(order:Int) {
	val nullInt:Int = RandomUtils.getNullElem() //a number outside the scope of symbols
	val minus0 :Int = RandomUtils.getMinus0Elem()
	
	//Each view is stored as a two-dimensional array, 
	//to avoid sequential searches for "1" elements along the cube
	//the lists store all possible values of the third coordinate
	val xyMatrix :ArraySeq[ArraySeq[ArraySeq[Int]]] = new ArraySeq(order)
	val yzMatrix :ArraySeq[ArraySeq[ArraySeq[Int]]] = new ArraySeq(order)
	val xzMatrix :ArraySeq[ArraySeq[ArraySeq[Int]]] = new ArraySeq(order)
	
	//inicialization
  var drawingOptions = new DrawingOptions()
	var lastSymbol = -1
  for (i <-0 to (order -1)) {//for all rows
    xyMatrix(i) = new ArraySeq(order)
    yzMatrix(i) = new ArraySeq(order)
    xzMatrix(i) = new ArraySeq(order)
    
    for (j <- 0 to (order - 1) ) {
      xyMatrix(i)(j) = ArraySeq(nullInt, nullInt, nullInt) //maximum of 3 elements in the row or column (-z, z, t)
      yzMatrix(i)(j) = ArraySeq(nullInt, nullInt, nullInt) //maximum of 3 elements in the row or column (-z, z, t)
      xzMatrix(i)(j) = ArraySeq(nullInt, nullInt, nullInt) //maximum of 3 elements in the row or column (-z, z, t)
    }
  }
	//cyclic LS at first
	for (i <-0 to (order -1)) {//for all rows
    lastSymbol = (lastSymbol + 1) % order;
    
    for (j <- 0 to (order - 1) ) {
    
      this.xyzStore(i, j, lastSymbol);
      lastSymbol = (lastSymbol + 1) % order;
    }
  }
	
	def getEmptySpace(arr:ArraySeq[Int]) : Int = {
		arr.indexOf(nullInt);
	}
	
	/**
	 * Stores an 1 at position (x,y,z) in the incidence cube.
	 *  This is done by 
	 * @param x
	 * @param y
	 * @param z
	 */
	protected def xyzStore(x:Int, y:Int, z:Int) : Unit = {
		this.add(xyMatrix(x)(y), z);
		this.add(yzMatrix(y)(z), x);
		this.add(xzMatrix(x)(z), y);
	}
	
	private def add(arr:ArraySeq[Int], elem:Int) :Int = {
		var idx = arr.indexOf(minus(elem));//look for the negative element
		if (idx>=0) { //if -element is found
			arr(idx) = nullInt;//-elem+elem = 0
			return idx;
		} else {
			idx = this.getEmptySpace(arr);//look for empty space for the new element
			if (idx == -1) {//if full , fail
				return -1;
			} else {//add the new element
				arr(idx) = elem;
				return idx;//if successful, returns the index of the new element
			}
		}
	}
	
	private def remove(arr:ArraySeq[Int], elem:Int) : Int = {
		var idx = arr.indexOf(elem);//look for the element to remove
		if (idx>=0) { // if element is found
			arr(idx) = nullInt;
			return idx;			
		} else {//if elem is not found
			idx = this.getEmptySpace(arr);
			if (idx == -1) {//if full, fail
				return -1;
			} else {
				arr(idx) = minus(elem);//add the negative
				return idx;
			}
		}
		
	}
	
	protected def xyzRemove(x:Int, y:Int, z:Int) {
		this.remove(xyMatrix(x)(y), z);		
		this.remove(yzMatrix(y)(z), x);//if exists, removes
		this.remove(xzMatrix(x)(z), y);
	}
	
	/**
	 * Returns the negative, and -0 if it is 0
	 */
	protected def minus(a:Int) : Int = {
		if (a == 0)
			return minus0
		else if (a == minus0) 
			return 0
		else return (-a)
	}
	
	def coordOf(x:Int, y:Int, z:Int) : Int = {
		if (xyMatrix(x)(y).contains(z)) {
			return 1;
		} else if (xyMatrix(x)(y).contains( minus(z))) {
			return -1;
		} else {
			return 0;
		}
	}
	
	override def plusOneZCoordOf(x:Int, y:Int) : Int = {
		var firstPositive = xyMatrix(x)(y).indexWhere(_ >= 0)
		if (firstPositive >= 0)
			return xyMatrix(x)(y)(firstPositive);
		else
			return -1;
	}
	override def secondPlusOneZCoordOf(x:Int, y:Int) : Int = {
	  val firstPositive = xyMatrix(x)(y).indexWhere(_ >= 0)
		var sndPositive = xyMatrix(x)(y).indexWhere(_ >= 0, firstPositive) 
		if (sndPositive>=0)
			return xyMatrix(x)(y)(sndPositive);
		else
			return -1;
	}
	
	override def plusOneXCoordOf(y:Int, z:Int) : Int = {
		var firstPositive = yzMatrix(y)(z).indexWhere(_ >= 0)
		if (firstPositive >= 0)
			return yzMatrix(y)(z)(firstPositive);
		else
			return -1;
	}
	override def plusOneYCoordOf(x:Int, z:Int) : Int = {
		var firstPositive = xzMatrix(x)(z).indexWhere(_ >= 0)
		if (firstPositive>=0)
			return xzMatrix(x)(z)(firstPositive);
		else
			return -1;
	}
	
	override def minusOneCoordOf(x:Int, y:Int) : Int = {
//		var firstNegative = xyMatrix(x)(y).indexWhere(_ < 0)
//		if (firstNegative>=0)
//			return xyMatrix(x)(y)(firstNegative);
//		else
//			return -1;
	  val z = this.indexOfFirstNegativeElem(xyMatrix(x)(y))
		if (z >= 0)
			return (xyMatrix(x)(y)(z).abs)
		else
			return -1
	}
	
	protected def indexOfFirstNegativeElem(arr:ArraySeq[Int]) : Int = {
		this.indexOfFirstNegativeElemStartingAt(arr, 0)
	}
	
	/**
	 * To search for a "negative" element, must skip the nullInts (that are a negative constant)
	 */
	protected def indexOfFirstNegativeElemStartingAt(arr:ArraySeq[Int], index:Int) : Int = {
		var i = -1
		var found : Boolean = false
		var j = index
		while (j < arr.length && !found) {
			found = ((arr(j)< 0) && (arr(j) != nullInt)) //found
			if (found)
				i = j //save the index
			j = j + 1
		}
		return i
	}
	
	override def doPlusMinus1Move(t:OrderedTriple[Int,Int,Int], x1:Int, y1:Int, z1:Int) {
		//changes in chosen sub-cube
		//sum 1 to the selected "0" cell
		this.xyzStore(t.x, t.y, t.z);
		this.xyzStore(t.x, y1, z1);
		this.xyzStore(x1, y1, t.z);
		this.xyzStore(x1, t.y, z1);
				
		//subtract 1 to the "1" cell
		this.xyzRemove(t.x, t.y, z1);
		this.xyzRemove(t.x, y1, t.z);
		this.xyzRemove(x1, t.y, t.z);
		this.xyzRemove(x1, y1, z1);
	}
	
	override def moveFromProper() {
		var t:OrderedTriple[Int,Int,Int] = this.select0Cell();
		
		var x1 = this.plusOneXCoordOf(t.y, t.z);
		var z1 = this.plusOneZCoordOf(t.x, t.y);
		var y1 = this.plusOneYCoordOf(t.x, t.z);
		
		this.doPlusMinus1Move(t, x1, y1, z1);
				
		//check if improper
		//(only one cell can be -1)
		if (this.coordOf(x1, y1, z1) == -1) {
			proper = false;
			improperCell = new OrderedTriple(x1, y1, z1);
		}
	}
	
	override def moveFromImproper() : Unit = {
		//get the improper cell:
		var t:OrderedTriple[Int,Int,Int] = this.improperCell;
		
		var x1 = this.choosePlusOneXCoordOf(t.y, t.z);
		var y1 = this.choosePlusOneYCoordOf(t.x, t.z);
		var z1 = this.choosePlusOneZCoordOf(t.x, t.y);
		
		this.doPlusMinus1Move(t, x1, y1, z1)
		
		//this is the only cell that can result -1
		if (this.coordOf(x1, y1, z1) == -1) {
			this.proper = false;
			this.improperCell = new OrderedTriple(x1, y1, z1);
		} else {
			proper = true;
			improperCell = null;
		}
	}
	
	override def choosePlusOneZCoordOf(x:Int, y:Int) : Int = {
		var takeFirst = (RandomUtils.pickAnInt(2) == 1);
		var z = xyMatrix(x)(y).indexWhere(_ >= 0)
		if (z == -1)
			return -1;
		if (takeFirst)
			return xyMatrix(x)(y)(z); 
		else {
			z = xyMatrix(x)(y).indexWhere(_ >= 0, z+1);
			if (z == -1)
				return -1;
			return xyMatrix(x)(y)(z);
		}
	}
	override def choosePlusOneXCoordOf(y:Int, z:Int) : Int = {
		var takeFirst = (RandomUtils.pickAnInt(2) == 1);
		var x = yzMatrix(y)(z).indexWhere(_ >= 0)
		if (x == -1)
			return -1;
		if (takeFirst)
			return yzMatrix(y)(z)(x);
		else {
			x = yzMatrix(y)(z).indexWhere(_ >= 0, x+1);
			if (x == -1)
				return -1;
			return yzMatrix(y)(z)(x);
		}
	}
	override def choosePlusOneYCoordOf(x:Int, z:Int) : Int = {
		var takeFirst = (RandomUtils.pickAnInt(2) == 1);
		var y = xzMatrix(x)(z).indexWhere(_ >= 0)
		if (y == -1)
			return -1;
		if (takeFirst)
			return xzMatrix(x)(z)(y); 
		else {
			y = xzMatrix(x)(z).indexWhere(_ >= 0, y+1);
			if (y == -1)
				return -1;
			return xzMatrix(x)(z)(y);
		}
	}

	override def select0Cell() : OrderedTriple[Int,Int,Int] = {
		var x = RandomUtils.pickAnInt(order);
		var y = RandomUtils.pickAnInt(order);
		var z = RandomUtils.pickAnInt(order);
		
		while (this.coordOf(x, y, z)!=0) {
			x = RandomUtils.pickAnInt(order);
			y = RandomUtils.pickAnInt(order);
			z = RandomUtils.pickAnInt(order);
		}
		return new OrderedTriple(x,y,z);
	}
	
	override def getDrawingOptions() : DrawingOptions = {
		return drawingOptions;
	}

	def setDrawingOptions(drawingOptions:DrawingOptions) : Unit = {
		this.drawingOptions = drawingOptions;
	}
}

