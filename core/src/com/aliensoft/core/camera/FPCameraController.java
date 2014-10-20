package com.aliensoft.core.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

/**
 * Takes a {@link Camera} instance and controls it via w,a,s,d and mouse panning.
 * 
 * @author badlogic
 */
public class FPCameraController extends InputAdapter {
	private final Camera	camera;
	private float			degreesPerPixel		= 0.5f;
	private final Vector3	tmp					= new Vector3();
	private boolean			shouldResetMouse	= true;
	private final float		verticalLimit;

	/**
	 * You can set a camera to control and a vertical limit.
	 * The maximum vertical limit allowed is 0.999f.<br>
	 * This limit is the maximum angle the camera can look up or
	 * down.
	 * 
	 * @param camera
	 * @param verticalLimit
	 */
	public FPCameraController(Camera camera, float verticalLimit) {
		this.camera = camera;
		this.verticalLimit = (verticalLimit > 0.999f ? 0.999f : verticalLimit);
		setResetMouse(true);
	}

	/**
	 * Defaults the vertical limit to 0.98f
	 * 
	 * @param camera
	 */
	public FPCameraController(Camera camera) {
		this(camera, 0.98f);
	}

	/**
	 * Sets how many degrees to rotate per pixel the mouse moved.
	 * 
	 * @param degreesPerPixel
	 */
	public void setDegreesPerPixel(float degreesPerPixel) {
		this.degreesPerPixel = degreesPerPixel;
	}

	public void update() {
		if (shouldResetMouse)
			updateMouse();

		camera.update(true);
	}

	/**
	 * Move the camera by the vector pos
	 * 
	 * @param pos
	 */
	public void translate(Vector3 pos) {
		camera.translate(pos);
	}

	/**
	 * returns the camera current position
	 * 
	 * @return The same as written above man!
	 */
	public Vector3 getPosition() {
		return camera.position;
	}

	private void updateMouse() {
		// Get the screen center
		float cx = Gdx.graphics.getWidth() / 2;
		float cy = Gdx.graphics.getHeight() / 2;

		// Calculate delta using distance
		float dx = (cx - Gdx.input.getX()) * degreesPerPixel;
		float dy = (cy - Gdx.input.getY()) * degreesPerPixel;

		// Rotate the camera around the y axis (side by side rotation)
		camera.direction.rotate(camera.up, dx);

		// Tmp is now camera 'left' vector (the perpendicular vector to the up/direction plane)
		tmp.set(camera.direction).crs(camera.up).nor();

		// Lock the camera to the vertical limit
		// allow going up if in lower limit
		if ((camera.direction.y <= -verticalLimit && dy > 0)
				// allow going down if in upper limit
				|| (camera.direction.y >= verticalLimit && dy < 0)
				// allow allowed movement
				|| (camera.direction.y < verticalLimit && camera.direction.y > -verticalLimit))
			// Rotate using the left axis
			camera.direction.rotate(tmp, dy);

		// Reset the cursor to the center of the screen
		Gdx.input.setCursorPosition((int) cx, (int) cy);
	}

	/**
	 * if the camera is active and reseting mouse pos
	 * 
	 * @return the shouldResetMouse
	 */
	public boolean isShouldResetMouse() {
		return shouldResetMouse;
	}

	/**
	 * enable/disable camera update by mouse
	 * 
	 * @param shouldResetMouse
	 *            the shouldResetMouse to set
	 */
	public void setResetMouse(boolean shouldResetMouse) {
		this.shouldResetMouse = shouldResetMouse;
		Gdx.input.setCursorCatched(shouldResetMouse);
	}
}
