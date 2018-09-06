package ar.com.bluemontag.jacomatt.opengl

import java.awt.event.KeyEvent.VK_DOWN;
import java.awt.event.KeyEvent.VK_LEFT;
import java.awt.event.KeyEvent.VK_PAGE_DOWN;
import java.awt.event.KeyEvent.VK_PAGE_UP;
import java.awt.event.KeyEvent.VK_RIGHT;
import java.awt.event.KeyEvent.VK_SPACE;
import java.awt.event.KeyEvent.VK_UP;
import java.awt.event.KeyEvent.VK_PLUS
import java.awt.event.KeyEvent.VK_MINUS
import java.awt.event.KeyEvent._
import javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import javax.media.opengl.GL.GL_DEPTH_TEST;
import javax.media.opengl.GL.GL_LEQUAL;
import javax.media.opengl.GL.GL_LINE_STRIP;
import javax.media.opengl.GL.GL_NICEST;
import javax.media.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT;
import javax.media.opengl.fixedfunc.GLLightingFunc.GL_DIFFUSE;
import javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHT1;
import javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHTING;
import javax.media.opengl.fixedfunc.GLLightingFunc.GL_POSITION;
import javax.media.opengl.fixedfunc.GLLightingFunc.GL_SMOOTH;
import javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.media.opengl.GL2;
import javax.media.opengl.GL;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import com.jogamp.opengl.util.awt.TextRenderer;
import ar.com.bluemontag.jacomatt.model.IncidenceCube

class DrawIncidenceCube(protected var cube: IncidenceCube) extends GLEventListener with KeyListener {
  // inicializo el TextRenderer (solo una vez en toda la ejecucion, sino se cuelga!)
  protected var textRenderer: TextRenderer = new TextRenderer(new Font("SansSerif", Font.PLAIN, 12));
  protected var gl: GL2 = null
  protected var glu: GLU = null

  protected var quadric: GLUquadric = null

  protected var rotateAngleX: Float = -530.0F // rotational angle for x-axis in degree
  protected var rotateAngleY: Float = 21.0F // rotational angle for y-axis in degree
  protected var rotateAngleZ: Float = 269.0F // rotational angle for z-axis in degree
  
  protected var z = -100.0F // z-location
  protected var zIncrement = 1.0F // for zoom in/out

  protected var rotateSpeedX = 0.0F // rotational speed for x-axis
  protected var rotateSpeedY = 0.0F // rotational speed for y-axis
  protected var rotateSpeedZ = 0.0F // rotational speed for z-axis
  
  protected var rotateSpeedIncrement = 0.5F // adjusting rotational speed
  
  protected var n: Int = cube.order //dimension

  protected var right: Float = 8.0F //value that is overwritten later

  protected var textScaleFactor = 1.0F

  protected var lightOn: Boolean = false

  protected val maxVal: Float = 100.0F

  protected var gradientOnAxis:Char = 'x'
  
  // ------ Implement methods declared in GLEventListener ------
  /**
   * Called back immediately after the OpenGL context is initialized. Can be
   * used to perform one-time initialization. Run only once.
   */
  override def init(drawable: GLAutoDrawable) {
    glu = new GLU(); // get GL Utilities// for the GL Utility

    gl = drawable.getGL().getGL2(); // get the OpenGL graphics context
    gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // set background (clear) color
    gl.glClearDepth(1.0f); // set clear depth value to farthest
    gl.glEnable(GL_DEPTH_TEST); // enables depth testing
    gl.glDepthFunc(GL_LEQUAL); // the type of depth test to do
    gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best
    // perspective
    // correction
    gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out
    // lighting

    // Compute the scale factor of the largest string which will make
    // them all fit on the faces of the cube
    // Rectangle2D bounds = renderer.getBounds("0");
    right = 8.0F // (float) bounds.getWidth(); //8.0 es para escribir
    // siempre de a una letra
    textRenderer.setColor(0.0f, 0.0f, 1.0f, 1.0f);
    textRenderer.setSmoothing(false);
    // top = (float) bounds.getHeight();
    // left = 0.0F
    // bottom = 0.0F
    textScaleFactor = 1.0f / (right * 1.1f);

    // Set up the lighting for light named GL_LIGHT1

    // Ambient light does not come from a particular direction. Need some
    // ambient
    // light to light up the scene. Ambient's value in RGBA
    var lightAmbientValue: Array[Float] = Array(0.5F, 0.5F, 0.5F, 1.0F)
    // Diffuse light comes from a particular location. Diffuse's value in
    // RGBA
    var lightDiffuseValue: Array[Float] = Array(1.0F, 1.0F, 1.0F, 1.0F)
    // Diffuse light location xyz (in front of the screen).
    var lightDiffusePosition: Array[Float] = Array(0.0F, 0.0F, 2.0F, 1.0F)

    gl.glLightfv(GL_LIGHT1, GL_AMBIENT, lightAmbientValue, 0);
    gl.glLightfv(GL_LIGHT1, GL_DIFFUSE, lightDiffuseValue, 0);
    gl.glLightfv(GL_LIGHT1, GL_POSITION, lightDiffusePosition, 0);
    gl.glEnable(GL_LIGHT1);

    // Set up the Quadrics
    quadric = glu.gluNewQuadric(); // create a pointer to the Quadric object
    // ( NEW )
    glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH); // create smooth normals
    // ( NEW )
    glu.gluQuadricTexture(quadric, true); // create texture coords ( NEW )
  }

  /**
   * Call-back handler for window re-size event. Also called when the drawable
   * is first set to visible.
   */
  override def reshape(drawable: GLAutoDrawable, x: Int, y: Int, width: Int, heightP: Int) {
    var gl: GL2 = drawable.getGL().getGL2(); // get the OpenGL 2 graphics context
    var height = heightP

    if (height == 0)
      height = 1; // prevent divide by zero
    var aspect: Float = width / height;

    // Set the view port (display area) to cover the entire window
    gl.glViewport(0, 0, width, height);

    // Setup perspective projection, with aspect ratio matches viewport
    gl.glMatrixMode(GL_PROJECTION); // choose projection matrix
    gl.glLoadIdentity(); // reset projection matrix
    glu.gluPerspective(45.0, aspect, 0.1, 1500.0); // fovy, aspect, zNear,
    // zFar

    // Enable the model-view transform
    gl.glMatrixMode(GL_MODELVIEW);
    gl.glLoadIdentity(); // reset
  }

  /**
   * Called back by the animator to perform rendering.
   */
  override def display(drawable: GLAutoDrawable) {
    var gl: GL2 = drawable.getGL().getGL2(); // get the OpenGL 2 graphics context
    gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear color
    // and depth
    // buffers
    gl.glLoadIdentity(); // reset the model-view matrix

    // Check whether light shall be turn on (toggle via the 'L' key)
    // This LIGHTING is different form LIGHT1 (??)
    if (lightOn) {
      gl.glEnable(GL_LIGHTING);
    } else {
      gl.glDisable(GL_LIGHTING);
    }

    gl.glTranslatef(0.0f, 0.0f, z); // translate into the screen
    gl.glRotatef(rotateAngleX, 1.0f, 0.0f, 0.0f) // rotate about the x-axis
    gl.glRotatef(rotateAngleY, 0.0f, 1.0f, 0.0f) // rotate about the y-axis
    gl.glRotatef(rotateAngleZ, 0.0f, 0.0f, 1.0f)  // rotate about the z-axis
    
    
    this.drawSquareLimits(gl)

    //draws grid in blue
    val opts = this.cube.getDrawingOptions();
    for (x <- 0 to (n - 1))
      for (y <- 0 to (n - 1))
        for (z <- 0 to (n - 1)) {
          //drawCube1By1(gl, x, y, z);
        }

    //this.drawCube1By1(gl, 1.0F, 1.0F, 1.0F)

    //this loop draws the 1's in the incidence cube
//    var x = 0
//    var y = 0
//    var zp = cube.getValueAt(x, y)
//    this.drawPointPlus1(gl, x, y, zp)
//    
//    y = 1
//    zp = cube.getValueAt(x, y)
//    this.drawPointPlus1(gl, x, y, zp)
//    
//    y = 2 
//    zp = cube.getValueAt(x, y)
//    this.drawPointPlus1(gl, x, y, zp)
    for (xp <- 0 to (n - 1)) {
      for (yp <- 0 to (n - 1)) {
        var zp = cube.plusOneZCoordOf(xp, yp)
        if (zp != (-1)) { //if the point exists (it must)
          this.drawPointPlus1ColorAsFunction(gl, xp, yp, zp);
        }
        
        if (!cube.proper) {
          zp = cube.minusOneCoordOf(xp, yp);
          if (zp != (-1)) {
            //if cube is improper and this x,y contains a -1
            
            this.drawPointMinus1(gl, xp, yp, zp);
  
            zp = cube.secondPlusOneZCoordOf(xp, yp);
            if (zp != (-1))
              this.drawPointPlus1ColorAsFunction(gl, xp, yp, zp);
  
          } //if there is a -1
        }//if not proper
      } //for yp
    } //for xp
    //

    //		for (x <- 1 to 20) {
    //		  this.pointPlus1(gl, x, x, x)
    //		  this.pointPlus1(gl, x, x, 0.0F)
    //		  this.pointPlus1(gl, x, 0.0F, x)
    //		  this.pointPlus1(gl, 0.0F, x, x)
    //		}

    this.drawAxis(gl)
    
//    if (opts.isShowAxisLetters())
//      this.drawAxisLetters(gl)
    // Update the rotational position after each refresh, based on
    // rotational speed.
    rotateAngleX += rotateSpeedX
    rotateAngleY += rotateSpeedY
    rotateAngleZ += rotateSpeedZ
    
    if (rotateSpeedX!=0) {
      System.out.println("X="+rotateAngleX)
    }
    if (rotateSpeedY!=0) {
      System.out.println("Y="+rotateAngleY)
    }
    if (rotateSpeedZ!=0) {
      System.out.println("Z="+rotateAngleZ)
    }
  }

  protected def drawSquareLimits(gl: GL2) {

    gl.glColor3f(0.0f, 0.0f, 1.0f); // blue
    gl.glBegin(GL_LINE_STRIP);

    gl.glVertex3f(0.0f, 0.0f, 0.0f);
    gl.glVertex3f(n, 0.0f, 0.0f);
    gl.glVertex3f(n, n, 0.0f);
    gl.glVertex3f(0.0f, n, 0.0f);
    gl.glVertex3f(0.0f, 0.0f, 0.0f);

    gl.glVertex3f(0.0f, 0.0f, n);
    gl.glVertex3f(0.0f, n, n);
    gl.glVertex3f(0.0f, n, 0.0f);
    gl.glVertex3f(0.0f, 0.0f, 0.0f);

    gl.glVertex3f(n, 0.0f, 0.0f);
    gl.glVertex3f(n, 0.0f, n);
    gl.glVertex3f(0.0f, 0.0f, n);
    gl.glVertex3f(0.0f, n, n);
    gl.glVertex3f(n, n, n);
    gl.glVertex3f(n, 0.0f, n);
    gl.glVertex3f(0.0f, 0.0f, n);
    gl.glEnd();

    gl.glBegin(GL_LINE_STRIP);
    gl.glVertex3f(n, n, n);
    gl.glVertex3f(n, n, 0.0f);
    gl.glEnd();
  }

  protected def selectCube(gl: GL2, x: Float, y: Float, z: Float) {
    gl.glColor3f(1.0f, 0.0f, 1.0f); // red

    //z-line (4 lines to select cube)
    gl.glBegin(GL_LINE_STRIP);
    gl.glVertex3f(x, y, z);
    gl.glVertex3f(x, y, n); //0,0,z
    gl.glVertex3f(x, y + 1, n);

    gl.glVertex3f(x + 1, y + 1, n); //square in the border
    gl.glVertex3f(x + 1, y, n);
    gl.glVertex3f(x, y, n);
    gl.glVertex3f(x, y + 1, n);

    gl.glVertex3f(x, y + 1, 0);
    gl.glVertex3f(x, y, 0);
    gl.glVertex3f(x, y, z); //return to (x, y, z)

    gl.glVertex3f(x + 1, y, z);
    gl.glVertex3f(x + 1, y, n); //0,0,z
    gl.glVertex3f(x + 1, y + 1, n);
    gl.glVertex3f(x + 1, y + 1, 0);

    gl.glVertex3f(x + 1, y, 0); //square in the border
    gl.glVertex3f(x, y, 0);
    gl.glVertex3f(x, y + 1, 0);
    gl.glVertex3f(x + 1, y + 1, 0);

    gl.glVertex3f(x + 1, y, 0);
    gl.glVertex3f(x + 1, y, z);
    gl.glEnd();

    //y-line
    gl.glBegin(GL_LINE_STRIP);
    gl.glVertex3f(x, y, z);
    gl.glVertex3f(x, n, z);

    gl.glVertex3f(x + 1, n, z);
    gl.glVertex3f(x + 1, n, z + 1);
    gl.glVertex3f(x, n, z + 1);
    gl.glVertex3f(x, n, z);

    gl.glVertex3f(x, n, z + 1);
    gl.glVertex3f(x, 0, z + 1);
    gl.glVertex3f(x, 0, z);

    gl.glVertex3f(x + 1, 0, z);
    gl.glVertex3f(x + 1, 0, z + 1);
    gl.glVertex3f(x, 0, z + 1);
    gl.glVertex3f(x, 0, z);

    gl.glVertex3f(x, y, z); // return to (x, y, z)
    gl.glVertex3f(x + 1, y, z);
    gl.glVertex3f(x + 1, n, z);
    gl.glVertex3f(x + 1, n, z + 1);
    gl.glVertex3f(x + 1, 0, z + 1);
    gl.glVertex3f(x + 1, 0, z);
    gl.glVertex3f(x + 1, y, z);
    gl.glEnd();

    //x-line
    gl.glBegin(GL_LINE_STRIP);
    gl.glVertex3f(x, y, z);
    gl.glVertex3f(n, y, z);
    gl.glVertex3f(n, y, z + 1);

    gl.glVertex3f(n, y + 1, z + 1); //square in the end
    gl.glVertex3f(n, y + 1, z);
    gl.glVertex3f(n, y, z);
    gl.glVertex3f(n, y, z + 1);

    gl.glVertex3f(0, y, z + 1);
    gl.glVertex3f(0, y, z);

    gl.glVertex3f(0, y + 1, z); //square in the end
    gl.glVertex3f(0, y + 1, z + 1);
    gl.glVertex3f(0, y, z + 1);
    gl.glVertex3f(0, y, z);

    gl.glVertex3f(x, y, z); // return to (x, y, z)
    gl.glVertex3f(x, y + 1, z);
    gl.glVertex3f(n, y + 1, z);
    gl.glVertex3f(n, y + 1, z + 1);
    gl.glVertex3f(0, y + 1, z + 1);
    gl.glVertex3f(0, y + 1, z);
    gl.glVertex3f(x, y + 1, z);
    gl.glEnd();
  }

  protected def drawCube1By1Solid(gl: GL2, x: Float, y: Float, z: Float, red: Float, green: Float, blue: Float) {

    //cara de adelante
    gl.glBegin(GL2GL3.GL_QUADS); //apoyo el lapiz
    gl.glColor3f(red, green, blue);
    gl.glVertex3f(x + 0.0f, y + 0.0f, z + 1.0f);
    gl.glVertex3f(x + 1.0f, y + 0.0f, z + 1.0f);
    gl.glVertex3f(x + 1.0f, y + 1.0f, z + 1.0f);
    gl.glVertex3f(x + 0.0f, y + 1.0f, z + 1.0f);
    gl.glVertex3f(x + 0.0f, y + 0.0f, z + 1.0f);

    
    //cara inferior
    gl.glVertex3f(x + 1.0f, y + 0.0f, z + 1.0f);
    gl.glVertex3f(x + 1.0f, y + 0.0f, z + 0.0f);
    gl.glVertex3f(x + 0.0f, y + 0.0f, z + 0.0f);
    gl.glVertex3f(x + 0.0f, y + 0.0f, z + 1.0f);

    //cara lateral izquierda
    gl.glVertex3f(x + 0.0f, y + 0.0f, z + 0.0f);
    gl.glVertex3f(x + 0.0f, y + 1.0f, z + 0.0f);
    gl.glVertex3f(x + 0.0f, y + 1.0f, z + 1.0f);
    gl.glVertex3f(x + 0.0f, y + 0.0f, z + 1.0f);
    gl.glEnd(); //levanto el lapiz

    //cara de atras
    gl.glBegin(GL2GL3.GL_QUADS); //apoyo el lapiz
    gl.glColor3f(red, green, blue);
    gl.glVertex3f(x + 0.0f, y + 0.0f, z + 0.0f);
    gl.glVertex3f(x + 1.0f, y + 0.0f, z + 0.0f);
    gl.glVertex3f(x + 1.0f, y + 1.0f, z + 0.0f);
    gl.glVertex3f(x + 0.0f, y + 1.0f, z + 0.0f);
    gl.glVertex3f(x + 0.0f, y + 0.0f, z + 0.0f);
    gl.glEnd(); //levanto el lapiz

    //cara superior
    gl.glBegin(GL2GL3.GL_QUADS); //apoyo el lapiz
    gl.glColor3f(red, green, blue);
    gl.glVertex3f(x + 0.0f, y + 1.0f, z + 0.0f);
    gl.glVertex3f(x + 1.0f, y + 1.0f, z + 0.0f);
    gl.glVertex3f(x + 1.0f, y + 1.0f, z + 1.0f);
    gl.glVertex3f(x + 0.0f, y + 1.0f, z + 1.0f);
    gl.glVertex3f(x + 0.0f, y + 1.0f, z + 0.0f);
    gl.glEnd(); //levanto el lapiz

    //cara lateral derecha
    gl.glBegin(GL2GL3.GL_QUADS); //apoyo el lapiz
    gl.glColor3f(red, green, blue);
    gl.glVertex3f(x + 1.0f, y + 0.0f, z + 0.0f);
    gl.glVertex3f(x + 1.0f, y + 1.0f, z + 0.0f);
    gl.glVertex3f(x + 1.0f, y + 1.0f, z + 1.0f);
    gl.glVertex3f(x + 1.0f, y + 0.0f, z + 1.0f);
    gl.glVertex3f(x + 1.0f, y + 0.0f, z + 0.0f);
    gl.glEnd(); //levanto el lapiz
    
    
//    gl.glTranslatef(0.5f, 0.5f, 0.0f); // translate into the screen
//    gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f); // rotate 90 degrees y-axis
//    gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f)  // rotate 90 on x-axis
    
    if (cube.getDrawingOptions().isShowAxisLetters()) {
      var digit:String = String.valueOf(z.toInt)
    
      this.drawText(gl, digit, x, y, z, java.awt.Color.BLUE)
    }
//    gl.glRotatef(-180.0f, 0.0f, 1.0f, 0.0f); // rotate 90 degrees back
    
    
//    gl.glTranslatef(-0.50f,-0.5f,0.0f); // translate into the screen
//    gl.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f); // rotate 90 degrees back
    
//    gl.glLoadIdentity(); // reset projection matrix
//    gl.glRotatef(rotateAngleX, 1.0f, 0.0f, 0.0f) // rotate about the x-axis
//    gl.glRotatef(rotateAngleY, 0.0f, 1.0f, 0.0f) // rotate about the y-axis
//    gl.glRotatef(rotateAngleZ, 0.0f, 0.0f, 1.0f)  // rotate about the z-axis
//    
  }

  protected def drawPointMinus1(gl: GL2, x: Float, y: Float, z: Float) {
    this.drawCube1By1Solid(gl, x, y, z, 1, 0, 0);//red cube
  }

  protected def drawPointPlus1GradientColor(gl: GL2, x: Float, y: Float, z: Float) {
    //gradient colors as functions of coordinates
    val greenGrad: Float = (-y / n) + 1;
    val blueGrad: Float = (-z / n) + 1;
    val redGrad: Float = (-x / n) + 1;

    this.drawCube1By1Solid(gl, x, y, z, redGrad, greenGrad, blueGrad);
    this.drawTransparentCube1By1(gl, x, y, z);
  }

  protected def drawPointPlus1ColorAsFunctionOfY(gl: GL2, x: Float, y: Float, z: Float) {
    //gradient colors as functions of coordinates
    val greenGrad: Float = (-y / n) + 1;
    val blueGrad: Float = 0.5F
    val redGrad: Float = 0.2F

    this.drawCube1By1Solid(gl, x, y, z, redGrad, greenGrad, blueGrad);
    this.drawTransparentCube1By1(gl, x, y, z);
  }

  protected def drawPointPlus1ColorAsFunctionOfX(gl: GL2, x: Float, y: Float, z: Float) {
    //gradient colors as functions of coordinates
    val greenGrad: Float = (-x / n) + 1
    val blueGrad: Float = 0.5F
    val redGrad: Float = 0.2F

    this.drawCube1By1Solid(gl, x, y, z, redGrad, greenGrad, blueGrad);
    this.drawTransparentCube1By1(gl, x, y, z);
  }

  protected def drawPointPlus1ColorAsFunctionOfZ(gl: GL2, x: Float, y: Float, z: Float) {
    //gradient colors as functions of coordinates
    val greenGrad: Float = 0.2F
    val blueGrad: Float = 0.5F
    val redGrad: Float = (-z / n) + 1

    this.drawCube1By1Solid(gl, x, y, z, redGrad, greenGrad, blueGrad);
    this.drawTransparentCube1By1(gl, x, y, z);
  }
  
  protected def drawPointPlus1ColorAsFunction(gl: GL2, x:Float, y:Float, z:Float) {
    this.gradientOnAxis match {
      case 'x' => drawPointPlus1ColorAsFunctionOfX(gl,x,y,z)
      case 'y' => drawPointPlus1ColorAsFunctionOfY(gl,x,y,z)
      case 'z' => drawPointPlus1ColorAsFunctionOfZ(gl,x,y,z)
      case other => drawPointPlus1GradientColor(gl, x, y, z)
    }
  }
  protected def drawAxisLetters(gl: GL2) {
    // x-axis coordinates
    var digit: String = ""
    var x: Int = 0
    var car:Int = 0

    for (x <- 0 to (n - 1)) {
      car = x % 10;
      digit = Integer.toString(car);

      this.drawText(gl, digit, x, n, n, java.awt.Color.RED)
    }
    
    // y-axis coordinates
    gl.glRotatef(90.0f, 0.0f, 0.0f, 1.0f); // z-axis rotation
    for (y <- 0 to (n - 1)) {
      car = y % 10
      digit = Integer.toString(car)
      
      this.drawText(gl, digit, y, 0.0f, n, java.awt.Color.GREEN)
    }
    
//    for (x <- 0 to (n - 1)) {
//      var car: Int = x % 10;
//      digit = Integer.toString(car);
//
//      this.drawText(gl, digit, x, 0.0f, n);
//    }
    gl.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f); // rotate back
    gl.glRotatef(90.0f, 0.0f, 1.0f, 0.0f); // rotate 90 degrees y-axis
    
    // z-axis coordinates
    for (z <- 0 to (n - 1)) {
      var mz = z * (-1)
      car = z % 10
      digit = Integer.toString(car)
      this.drawText(gl, digit, mz-1, n, 0.0f, java.awt.Color.BLUE)
    }
//    for (x <- 0 to (n - 1)) {
//      var mx = x * (-1)
//      var car: Int = x % 10;
//      digit = Integer.toString(car);
//
//      this.drawText(gl, digit, mx-1, n, 0.0F);
//    }
//    
    gl.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f); // vuelvo el eje y como estaba

  }

  protected def drawText(gl: GL2, text: String, x: Float, y: Float, z: Float, color:java.awt.Color) {
    textRenderer.setColor(color)
    textRenderer.begin3DRendering();
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDisable(GL.GL_CULL_FACE);
    textRenderer.draw3D(text, x, y, z, textScaleFactor);
    textRenderer.end3DRendering();
  }

  protected def drawTransparentCube1By1(gl: GL2, x: Float, y: Float, z: Float) {
    // lineas que unen los dos cuadrados en azul
    gl.glBegin(GL_LINE_STRIP);
    gl.glColor3f(0.0f, 0.0f, 1.0f); // blue
    gl.glVertex3f(x + 0.0f, y + 0.0f, z + 0.0f);
    gl.glVertex3f(x + 0.0f, y + 0.0f, z + 1.0f);
    gl.glEnd();

    gl.glBegin(GL_LINE_STRIP);
    gl.glColor3f(0.0f, 0.0f, 1.0f); // blue
    gl.glVertex3f(x + 1.0f, y + 0.0f, z + 0.0f);
    gl.glVertex3f(x + 1.0f, y + 0.0f, z + 1.0f);
    gl.glEnd();

    gl.glBegin(GL_LINE_STRIP);
    gl.glColor3f(0.0f, 0.0f, 1.0f); // blue
    gl.glVertex3f(x + 0.0f, y + 1.0f, z + 0.0f);
    gl.glVertex3f(x + 0.0f, y + 1.0f, z + 1.0f);
    gl.glEnd();

    gl.glBegin(GL_LINE_STRIP);
    gl.glColor3f(0.0f, 0.0f, 1.0f); // blue
    gl.glVertex3f(x + 1.0f, y + 1.0f, z + 0.0f);
    gl.glVertex3f(x + 1.0f, y + 1.0f, z + 1.0f);
    gl.glEnd();

    gl.glBegin(GL_LINE_STRIP);
    gl.glColor3f(0.0f, 0.0f, 1.0f); // blue
    gl.glVertex3f(x + 0.0f, y + 0.0f, z + 1.0f);
    gl.glVertex3f(x + 1.0f, y + 0.0f, z + 1.0f);
    gl.glVertex3f(x + 1.0f, y + 1.0f, z + 1.0f);
    gl.glVertex3f(x + 0.0f, y + 1.0f, z + 1.0f);
    gl.glVertex3f(x + 0.0f, y + 0.0f, z + 1.0f);
    gl.glEnd();

    gl.glBegin(GL_LINE_STRIP);
    gl.glColor3f(0.0f, 0.0f, 1.0f); // blue
    gl.glVertex3f(x + 0.0f, y + 0.0f, z + 0.0f);
    gl.glVertex3f(x + 1.0f, y + 0.0f, z + 0.0f);
    gl.glVertex3f(x + 1.0f, y + 1.0f, z + 0.0f);
    gl.glVertex3f(x + 0.0f, y + 1.0f, z + 0.0f);
    gl.glVertex3f(x + 0.0f, y + 0.0f, z + 0.0f);
    gl.glEnd();

  }

  protected def drawAxis(gl: GL2) {
    
    //criteria to remember: X,Y,Z = R,G,B
    
    //x-axis in red
    gl.glBegin(GL_LINE_STRIP);
    gl.glColor3f(1.0f, 0.0f, 0.0f); // red
    gl.glVertex3f(0.0f, 0.0f, 0.0f);
    gl.glVertex3f(maxVal, 0.0f, 0.0f);
    gl.glEnd();

    //y-axis in green
    gl.glBegin(GL_LINE_STRIP);
    gl.glColor3f(0.0f, 1.0f, 0.0f); // green
    gl.glVertex3f(0.0f, 0.0f, 0.0f);
    gl.glVertex3f(0.0f, maxVal, 0.0f);
    gl.glEnd();

    //z-axis in blue
    gl.glBegin(GL_LINE_STRIP);
    gl.glColor3f(0.0f, 0.0f, 1.0f); // blue
    gl.glVertex3f(0.0f, 0.0f, 0.0f);
    gl.glVertex3f(0.0f, 0.0f, maxVal);
    gl.glEnd();
  }

  /**
   * Called back before the OpenGL context is destroyed. Release resource such
   * as buffers.
   */
  override def dispose(drawable: GLAutoDrawable) {
  }

  // ----- Implement methods declared in KeyListener -----

  override def keyPressed(e: KeyEvent) {
    e.getKeyCode match {
      case VK_SPACE =>
        cube.doOneMove()
        
        if (cube.proper)
          System.out.println("PROPER!")
      case VK_PAGE_UP => // increase rotational speed in z
        rotateSpeedZ += rotateSpeedIncrement
      case VK_PAGE_DOWN => //decrease rotational speed in z
        rotateSpeedZ -= rotateSpeedIncrement
      case VK_UP => // decrease rotational speed in x
        rotateSpeedX -= rotateSpeedIncrement;
      case VK_DOWN => // increase rotational speed in x
        rotateSpeedX += rotateSpeedIncrement;
      case VK_LEFT => // decrease rotational speed in y
        rotateSpeedY -= rotateSpeedIncrement;
      case VK_RIGHT => // increase rotational speed in y
        rotateSpeedY += rotateSpeedIncrement;
      case VK_MINUS => // zoom-out
        z -= zIncrement;
      case VK_PLUS => // zoom-in
        z += zIncrement;
      case VK_P => //letter P: print the Latin Square
        System.out.println(cube)
      case VK_A => //letter A: toggle axis letters
        cube.getDrawingOptions().setShowAxisLetters(!cube.getDrawingOptions().isShowAxisLetters())
      case VK_L => // toggle light on/off
        lightOn = !lightOn;
      case VK_F => // switch to the next filter (NEAREST, LINEAR, MIPMAP)
      // currTextureFilter = (currTextureFilter + 1) % textures.length;
      case VK_X => //change color
        this.gradientOnAxis = 'x'
      case VK_Y => 
        this.gradientOnAxis = 'y'
      case VK_Z =>
        this.gradientOnAxis = 'z'
      case VK_G =>
        this.gradientOnAxis = 'g'
      case other =>
      //does nothing

    }
  }

  override def keyReleased(e: KeyEvent) {
    rotateSpeedX = 0
    rotateSpeedY = 0
    rotateSpeedZ = 0
  }

  override def keyTyped(e: KeyEvent) {
  }
}
