/**
 * Creation date: 02/12/2017
 * 
 */
package commons.utils;

public class DrawingOptions {

	//default options
	private float z = -80.0f;
	private int FPS = 40; // animator's target frames per second
	
	//window
	private String windowTitle = "ScalaLSGP"; // window's
	private int frameWidth = 1000;
	private int frameHeight = 1000;
	private int frameXPosition = 150;
	private int frameYPosition = 0;
	
	//booleans
	private boolean fullScreen = false;
	private boolean showAxisLetters = false;
	private boolean showAxis = true;
	private boolean showGrid = false;
	private boolean showBoxes = true;
	
	/*	private final int CANVAS_WIDTH = 300; // width of the drawable
	private final int CANVAS_HEIGHT = 200; // height of the drawable*/

	public DrawingOptions() {
		//nothing
	}
	public String getWindowTitle() {
		return windowTitle;
	}

	public float getZ() {
		return z;
	}
	public int getFPS() {
		return FPS;
	}
	public int getFrameWidth() {
		return frameWidth;
	}
	public int getFrameHeight() {
		return frameHeight;
	}
	public int getFrameXPosition() {
		return frameXPosition;
	}
	public int getFrameYPosition() {
		return frameYPosition;
	}
	public boolean isFullScreen() {
		return fullScreen;
	}
	public boolean isShowAxisLetters() {
		return showAxisLetters;
	}
	public boolean isShowAxis() {
		return showAxis;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public void setFPS(int fPS) {
		FPS = fPS;
	}

	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
	}

	public void setFrameWidth(int frameWidth) {
		this.frameWidth = frameWidth;
	}

	public void setFrameHeight(int frameHeight) {
		this.frameHeight = frameHeight;
	}

	public void setFrameXPosition(int frameXPosition) {
		this.frameXPosition = frameXPosition;
	}

	public void setFrameYPosition(int frameYPosition) {
		this.frameYPosition = frameYPosition;
	}

	public void setFullScreen(boolean fullScreen) {
		this.fullScreen = fullScreen;
	}

	public void setShowAxisLetters(boolean showAxisLetters) {
		this.showAxisLetters = showAxisLetters;
	}

	public void setShowAxis(boolean showAxis) {
		this.showAxis = showAxis;
	}

	public boolean isShowGrid() {
		return showGrid;
	}

	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}

	public boolean isShowBoxes() {
		return showBoxes;
	}

	public void setShowBoxes(boolean showBoxes) {
		this.showBoxes = showBoxes;
	}

}
