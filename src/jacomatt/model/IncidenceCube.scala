package jacomatt.model

import commons.model.mutable.AbstractLatinSquare
import commons.model.mutable.Tensor
import commons.model.immutable.OrderedTriple
import commons.utils.RandomUtils
import javax.swing.JFrame
import commons.utils.DrawingOptions
import jacomatt.opengl.DrawIncidenceCube

import java.awt.Frame
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

import com.jogamp.opengl.util.FPSAnimator

import javax.media.opengl.awt.GLCanvas
import javax.swing.JFrame
import java.awt.Dimension

class IncidenceCube(order:Int) extends AbstractLatinSquare[Int](order:Int) {
  private val tensor = new Tensor[Int](size=order, nullElem=0)//uses a tensor of Int to store elements
  var proper : Boolean = true //it begins from a proper cube
  protected var improperCell : OrderedTriple[Int,Int,Int] = null    //no improper cell at the beggining
  protected val opts = new DrawingOptions
  
  init(true)
  
  def init(cyclic:Boolean) : Unit = {
    if (cyclic) {
			//initialize the cube with cyclic 1s
			var lastSymbol = -1
			for (i <- 0 to (order-1)) {//for all rows
				lastSymbol = (lastSymbol + 1) % order
				for (j <- 0 to (order-1)) {
					tensor.setElemAt(1, i, j, lastSymbol)
					lastSymbol = (lastSymbol + 1) % order
				}
			}
		}
  }
  
  
  /**
	 * Gets the value at specified row and column indexes.
	 */
	override def getValueAt(row:Int, column:Int):Int = {
    tensor.indexOfElem(1, row, column, 'z')
  }
	
	/**
	 * Sets the value at specified row and column indexes.
	 */
	override def setValueAt(row:Int, column:Int, value:Int):Unit = {
	  tensor.setElemAt(row, column, value, 1) //puts a bit ON , in the coordinates correspondant to the 3 values (x,y,z)
	}

	/**
	 * A LS implementation must know how to write to file.
	 * 
	 * @param fileName
	 */
	def writeToFile(fileName:String) : Unit = {
	  System.out.println("Incidence cube written to "+fileName)
	}
	
	/**
	 *  Check if the structure has repetitions in some row or column.
	 *  
	 */
	def preservesLatinProperty():Boolean = {
	  true
	}

	//*************** Jacobson & Matthews moves implementation
	/**
	 * This method is used by the OpenGL display method (that's why it's public)
	 */
	def plusOneZCoordOf(x:Int, y:Int) :Int = {
	  tensor.indexOfElem(1, x, y, 'z')
	}
	
	/**
	 * This method is used by the OpenGL display method (that's why it's public)
	 */
	def secondPlusOneZCoordOf(x:Int, y:Int) :Int = {
	  val pos = this.plusOneZCoordOf(x, y) + 1
	  tensor.indexOfElem(1, x, y, 'z', pos)
	}
	
	private def secondPlusOneXCoordOf(y:Int, z:Int) :Int = {
	  val pos = this.plusOneXCoordOf(y, z) + 1
	  tensor.indexOfElem(1, y, z, 'x', pos)
	}
	
	private def secondPlusOneYCoordOf(x:Int, z:Int) :Int = {
	  val pos = this.plusOneYCoordOf(x, z) + 1
	  tensor.indexOfElem(1, x, z, 'y', pos)
	}
	
	protected def plusOneXCoordOf(y:Int, z:Int) :Int = {
	  tensor.indexOfElem(1, y, z, 'x')
	}
	
	protected def plusOneYCoordOf(x:Int, z:Int) :Int = {
	  tensor.indexOfElem(1, x, z, 'y')
	}
	
	/**
	 * This method is used by the OpenGL display method (that's why it's public)
	 */
	def minusOneCoordOf(x:Int, y:Int) :Int = {
	  tensor.indexOfElem(-1, x, y, 'z')
	}
	
	protected def doPlusMinus1Move(t:OrderedTriple[Int,Int,Int], x1:Int, y1:Int, z1:Int) :Unit = {
	  ++(t.x, t.y, t.z)      //sum 1 to the selected "0" cell
		++(t.x, y1,  z1)
		++(x1,  y1,  t.z)
		++(x1,  t.y, z1)
		
		--(t.x, t.y, z1)       //subtract 1 to the "1" cell	
		--(t.x, y1,  t.z)
		--(x1,  t.y, t.z)
		--(x1,  y1,  z1)
	}
	
	
  private def ++(x: Int, y: Int, z: Int) : Unit = {
    tensor.setElemAt(tensor.getElemAt(x, y, z)+1, x, y, z)
  }
  
  private def --(x: Int, y: Int, z: Int) : Unit = {
    tensor.setElemAt(tensor.getElemAt(x, y, z)-1, x, y, z)
  }
	
	protected def moveFromProper() :Unit = {
		val t:OrderedTriple[Int,Int,Int] = this.select0Cell();
		
		val x1:Int = this.plusOneXCoordOf(t.y, t.z);
		val z1:Int = this.plusOneZCoordOf(t.x, t.y);
		val y1:Int = this.plusOneYCoordOf(t.x, t.z);
		
		//changes in chosen sub-cube
		this.doPlusMinus1Move(t, x1, y1, z1);
		
		//check if improper
		//(only one cell can be -1)
		if (tensor.getElemAt(x1, y1, z1) ==(-1)) {
			proper = false;
			improperCell = new OrderedTriple(x1, y1, z1);
		}
	}
	
	protected def moveFromImproper() {
		//get the improper cell:
		val t = this.improperCell;
		
		val x1:Int = this.choosePlusOneXCoordOf(t.y, t.z)
		val y1:Int = this.choosePlusOneYCoordOf(t.x, t.z)
		val z1:Int = this.choosePlusOneZCoordOf(t.x, t.y)
		
		//changes in chosen sub-cube
		this.doPlusMinus1Move(t, x1, y1, z1);

		//this is the only cell that can result -1
		if (tensor.getElemAt(x1, y1, z1)==(-1)) {
			this.proper = false;
			this.improperCell = new OrderedTriple(x1, y1, z1);
		} else {
			proper = true;
			improperCell = null;
		}
	}
	
	def doOneMove() : Unit = {
	  if (this.proper) {
				this.moveFromProper();
			} else {
				this.moveFromImproper();
			}
	}
	
	protected def choosePlusOneZCoordOf(x:Int, y:Int) : Int = {
		val takeFirst:Boolean = (RandomUtils.pickAnInt(2)==1);
		
		if (takeFirst) {
		  this.plusOneZCoordOf(x, y)
		} else {
		  this.secondPlusOneZCoordOf(x, y)
		}
	}
	
	protected def choosePlusOneXCoordOf(y:Int, z:Int) : Int = {
		val takeFirst:Boolean = (RandomUtils.pickAnInt(2)==1);
		
		if (takeFirst) {
		  this.plusOneXCoordOf(y, z)
		} else {
		  this.secondPlusOneXCoordOf(y, z)
		}
	}
	
	protected def choosePlusOneYCoordOf(x:Int, z:Int) : Int = {
		val takeFirst:Boolean = (RandomUtils.pickAnInt(2)==1);
		
		if (takeFirst) {
		  this.plusOneYCoordOf(x, z)
		} else {
		  this.secondPlusOneYCoordOf(x, z)
		}
	}

	
	protected def select0Cell() : OrderedTriple[Int,Int,Int] = {
		var x:Int = RandomUtils.pickAnInt(order)
		var y:Int = RandomUtils.pickAnInt(order)
		var z:Int = RandomUtils.pickAnInt(order)
		
		while (tensor.getElemAt(x, y, z)!=0) {
			x = RandomUtils.pickAnInt(order);
			y = RandomUtils.pickAnInt(order);
			z = RandomUtils.pickAnInt(order);
		}
		return new OrderedTriple(x,y,z)
	}
	
	/**
	 * Redefinition of the method toString() for Incidence Cubes.
	 * 
	 * This method writes the structure into a String, taking into account the special case when the cube
	 *  is improper: that is, it has for the same (x,y) three values of z, one negative and two positives. 
	 * 
	 * @author ignacio
	 * 
	 * @return String
	 */
	override def toString() :String = {
//		tensor.toString()
		if (proper) {
			return toStringOfProper()
		}
		val sb = new StringBuffer()
		sb.append("Incidence cube of size "+order+":\n")
		var x = order-1
		while (x >=0) {//the first upper line is the n-1 x coordinate
			for (y <-0 to (order-1)) {
				try {
					var z:String = "";
					
					var coordZ = this.minusOneCoordOf(x, y);
					
					if (coordZ!=(-1)) {//if has negative (3 elements)
						z = "{";
						
						if(coordZ>=0) 
							z+="-"+Integer.toString(coordZ)
						else
							z+=Integer.toString(coordZ)
						
						var z2:Int = this.plusOneZCoordOf(x, y)
						var z3:Int = this.secondPlusOneZCoordOf(x, y)
						
						z+= ","+Integer.toString(z2);
						z+= ","+Integer.toString(z3)+"}";
						
					} else {//IF NOT, it has only one element
						coordZ = this.plusOneZCoordOf(x, y);
						z = ""+coordZ;
						
					}
					
					sb.append(z); 
					sb.append("           ".substring(z.length()));//the blank string is necessary to avoid StringIndexOutOfBounds
					
				} catch {
				  case e:Exception => 
				      sb.append("ERROR IN COORD")
				}
			}
			sb.append("\n");
			x = x - 1
		}
		return sb.toString();
		tensor.toString()
	}

	def toStringOfProper() :String = {
		val sb = new StringBuffer();
		sb.append("Incidence cube of size "+order+":\n");
		
		var x = order-1
		while (x >=0) {//the first upper line is the n-1 x coordinate
			for (y <- 0 to (order-1)) {
				try {
					val elem = this.getValueAt(x, y);
					sb.append(elem); 
					sb.append("    ".substring(elem.toString().length()));
					
				} catch {
				  case e:Exception =>sb.append("--  ");
				}
			}//end row
			sb.append("\n");
			x = x - 1
		}
		sb.toString()//+ "\n\n"+tensor.toString();
		
		
	}

	/**
	 * Mixes the initial ls into a ic uniformly distributed, doing at least (n^3)/8 iterations,
	 * so each element has a chance to be moved (see Brown). 
	 * This is because each +-1-movement moves 8 elements and there are n^3 elements. 
	 * 
	 * @return int , the number of iterations that took to get a proper ic
	 */
	def shuffle() :Int = {
	  var iterations = 0
		while (iterations < math.pow(order, 3) || !this.proper ) {
			doOneMove()
			iterations = iterations +1
		}
		return iterations;
	}
	
	def drawIncidenceCube() {
		var canvas = new GLCanvas();
		
//		canvas.setPreferredSize(new Dimension(10, 10));
		val renderer = new DrawIncidenceCube(this);
		canvas.addGLEventListener(renderer);
		canvas.addKeyListener(renderer);
		canvas.setFocusable(true); // To receive key event
		canvas.requestFocus();

		// Create a animator that drives canvas' display() at the specified FPS.
		val animator = new FPSAnimator(canvas, opts.getFPS(), true);

		// Create the top-level container frame
		val jframe = new JFrame(); // Swing's JFrame or AWT's Frame
		jframe.getContentPane().add(canvas);
		jframe.addWindowListener(new WindowAdapter() {
			override def windowClosing(e : WindowEvent) {
				// Use a dedicate thread to run the stop() to ensure that the
				// animator stops before program exits.
				new Thread() {
					override def run() {
						animator.stop(); // stop the animator loop
						System.exit(0);
					}
				}.start();
			}
		});
		
		
		jframe.setTitle(opts.getWindowTitle());
		jframe.pack();
		jframe.setVisible(true);
		if (opts.isFullScreen())
			jframe.setExtendedState(Frame.MAXIMIZED_BOTH); // full screen mode
		else
			jframe.setBounds(opts.getFrameXPosition(), opts.getFrameYPosition(),
							opts.getFrameWidth(), opts.getFrameHeight());
	
		animator.start(); // start the animation loop
	}

  def getDrawingOptions():DrawingOptions = {
     this.opts
  }

}
