package jacomatt.opengl

import java.awt.event.KeyEvent.VK_DOWN;
import java.awt.event.KeyEvent.VK_F;
import java.awt.event.KeyEvent.VK_L;
import java.awt.event.KeyEvent.VK_LEFT;
import java.awt.event.KeyEvent.VK_PAGE_DOWN;
import java.awt.event.KeyEvent.VK_PAGE_UP;
import java.awt.event.KeyEvent.VK_RIGHT;
import java.awt.event.KeyEvent.VK_SPACE;
import java.awt.event.KeyEvent.VK_UP;
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
import jacomatt.model.IncidenceCube

class DrawIncidenceCube(var cube: IncidenceCube) extends GLEventListener with KeyListener {
  // inicializo el TextRenderer (solo una vez en toda la ejecucion, sino se cuelga!)
  var textRenderer: TextRenderer = new TextRenderer(new Font("SansSerif", Font.PLAIN, 12));
  var gl: GL2 = null
  var glu: GLU = null

  var quadric: GLUquadric = null

  var rotateAngleX: Float = 0.0F // rotational angle for x-axis in
  // degree
  var rotateAngleY: Float = 0.0F // rotational angle for y-axis in
  // degree
  var z = -100.0F // z-location
  var rotateSpeedX = 0.0F // rotational speed for x-axis
  var rotateSpeedY = 0.0F // rotational speed for y-axis

  var zIncrement = 0.5F // for zoom in/out
  var rotateSpeedXIncrement = 0.5F // adjusting x rotational
  // speed
  var rotateSpeedYIncrement = 0.5F // adjusting y rotational
  // speed
  protected var n: Int = cube.order //dimension

  var right: Float = 8.0F //value that is overwritten later

  var textScaleFactor = 1.0F

  var lightOn: Boolean = false

  val maxVal: Float = 100.0F

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
    gl.glRotatef(rotateAngleX, 1.0f, 0.0f, 0.0f); // rotate about the x-axis
    gl.glRotatef(rotateAngleY, 0.0f, 1.0f, 0.0f); // rotate about the y-axis

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
    for (x <- 0 to (n - 1))
      for (y <- 0 to (n - 1)) {
        var z = cube.plusOneZCoordOf(x, y)
        if (z != (-1)) { //if the point exists (it must)
          this.pointPlus1(gl, x, y, z);
        }

        z = cube.minusOneCoordOf(x, y);
        if (z != (-1)) {
          this.pointMinus1(gl, x, y, z);

          z = cube.secondPlusOneZCoordOf(x, y);
          if (z != (-1))
            this.pointPlus1(gl, x, y, z);

        } //if there is a -1

      } //for y

    //

    //		for (x <- 1 to 20) {
    //		  this.pointPlus1(gl, x, x, x)
    //		  this.pointPlus1(gl, x, x, 0.0F)
    //		  this.pointPlus1(gl, x, 0.0F, x)
    //		  this.pointPlus1(gl, 0.0F, x, x)
    //		}

    this.drawAxis(gl)
    this.axisLetters(gl)
    // Update the rotational position after each refresh, based on
    // rotational speed.
    rotateAngleX += rotateSpeedX;
    rotateAngleY += rotateSpeedY;
  }

  def drawSquareLimits(gl: GL2) {

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

  def selectCube(gl: GL2, x: Float, y: Float, z: Float) {
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

  private def drawCube1By1Solid(gl: GL2, x: Float, y: Float, z: Float, red: Float, green: Float, blue: Float) {

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
  }

  def pointMinus1(gl: GL2, x: Float, y: Float, z: Float) {
    this.drawCube1By1Solid(gl, x, y, z, 1, 0, 0);
  }

  def pointPlus1(gl: GL2, x: Float, y: Float, z: Float) {
    //gradient colors as functions of coordinates
    val greenGrad: Float = (-y / n) + 1;
    if (greenGrad < 0 || greenGrad > 1)
      System.out.println("Bad grad: " + greenGrad);
    val blueGrad: Float = (-z / n) + 1;
    val redGrad: Float = (-x / n) + 1;

    this.drawCube1By1Solid(gl, x, y, z, redGrad, greenGrad, blueGrad);
    this.drawCube1By1(gl, x, y, z);
  }

  def axisLetters(gl: GL2) {
    // labels sobre los ejes
    // String letras = "abcdefghijklmnopqrstuvwxy01234567890";

    // label del eje x
    var digit: String = ""
    var x: Int = 0

    for (x <- 0 to (n - 1)) {
      var car: Int = x % 10;
      digit = Integer.toString(car);

      this.drawText(gl, digit, x, n, n);
      // System.out.print(Character.toString((char)indice));
    }
    // label del eje y
    gl.glRotatef(90.0f, 0.0f, 0.0f, 1.0f); // rotacion sobre el eje z
    for (x <- 0 to (n - 1)) {
      var car: Int = x % 10;
      digit = Integer.toString(car);

      this.drawText(gl, digit, x, 0.0f, n);
    }
    gl.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f); // vuelvo el eje z como estaba
    gl.glRotatef(90.0f, 0.0f, 1.0f, 0.0f); // roto 90 el eje y
    // label del eje z
    var caracter: Int = 96;
    for (x <- -n to -1) {
      caracter = (caracter + 1); // cambio el caracter actual modulo n
      drawText(gl, caracter.toChar + "", x, n, 0.0F);
    }
    gl.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f); // vuelvo el eje y como estaba

  }

  def drawText(gl: GL2, text: String, x: Float, y: Float, z: Float) {
    textRenderer.begin3DRendering();
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDisable(GL.GL_CULL_FACE);
    textRenderer.draw3D(text, x, y, z, textScaleFactor);
    textRenderer.end3DRendering();
  }

  def drawCube1By1(gl: GL2, x: Float, y: Float, z: Float) {
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
    //axis in blue
    gl.glBegin(GL_LINE_STRIP);
    gl.glColor3f(0.0f, 0.0f, 1.0f); // blue
    gl.glVertex3f(0.0f, 0.0f, 0.0f);
    gl.glVertex3f(maxVal, 0.0f, 0.0f);
    gl.glEnd();

    gl.glBegin(GL_LINE_STRIP);
    gl.glVertex3f(0.0f, 0.0f, 0.0f);
    gl.glVertex3f(0.0f, maxVal, 0.0f);
    gl.glEnd();

    gl.glBegin(GL_LINE_STRIP);
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
        
      case VK_L => // toggle light on/off
        lightOn = !lightOn;
      case VK_F => // switch to the next filter (NEAREST, LINEAR, MIPMAP)
      // currTextureFilter = (currTextureFilter + 1) % textures.length;
      case VK_PAGE_UP => // zoom-out
        z -= zIncrement;
      case VK_PAGE_DOWN => // zoom-in
        z += zIncrement;
      case VK_UP => // decrease rotational speed in x
        rotateSpeedX -= rotateSpeedXIncrement;
      case VK_DOWN => // increase rotational speed in x
        rotateSpeedX += rotateSpeedXIncrement;
      case VK_LEFT => // decrease rotational speed in y
        rotateSpeedY -= rotateSpeedYIncrement;
      case VK_RIGHT => // increase rotational speed in y
        rotateSpeedY += rotateSpeedYIncrement;
      case 65 => //letter A: toggle show axis letters
        System.out.println(cube)
      case 66 => //letter B: Does something
        System.out.println("66");
      case x =>
      //does nothing

    }
  }

  override def keyReleased(e: KeyEvent) {
  }

  override def keyTyped(e: KeyEvent) {
  }
}
