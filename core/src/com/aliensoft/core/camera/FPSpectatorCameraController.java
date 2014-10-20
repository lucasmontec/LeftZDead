package com.aliensoft.core.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;

/**
 * Takes a {@link Camera} instance and controls it via w,a,s,d and mouse panning.
 * 
 * @author badlogic
 */
public class FPSpectatorCameraController extends InputAdapter {
	private final Camera	camera;
	private final IntIntMap	keys				= new IntIntMap();
	private final int		STRAFE_LEFT			= Keys.A;
	private final int		STRAFE_RIGHT		= Keys.D;
	private final int		FORWARD				= Keys.W;
	private final int		BACKWARD			= Keys.S;
	private float			velocity			= 5;
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
	public FPSpectatorCameraController(Camera camera, float verticalLimit) {
		this.camera = camera;
		this.verticalLimit = (verticalLimit > 0.999f ? 0.999f : verticalLimit);
		setResetMouse(true);
	}

	/**
	 * Defaults the vertical limit to 0.98f
	 * 
	 * @param camera
	 */
	public FPSpectatorCameraController(Camera camera) {
		this(camera, 0.98f);
	}

	@Override
	public boolean keyDown(int keycode) {
		keys.put(keycode, keycode);
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		keys.remove(keycode, 0);
		return true;
	}

	/**
	 * Sets the velocity in units per second for moving forward, backward and strafing left/right.
	 * 
	 * @param velocity
	 *            the velocity in units per second
	 */
	public void setVelocity(float velocity) {
		this.velocity = velocity;
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
		update(Gdx.graphics.getDeltaTime());
	}

	public void update(float deltaTime) {
		if (shouldResetMouse)
			updateMouse();

		if (keys.containsKey(FORWARD)) {
			tmp.set(camera.direction).nor().scl(deltaTime * velocity);
			camera.position.add(tmp);
		}
		if (keys.containsKey(BACKWARD)) {
			tmp.set(camera.direction).nor().scl(-deltaTime * velocity);
			camera.position.add(tmp);
		}
		if (keys.containsKey(STRAFE_LEFT)) {
			tmp.set(camera.direction).crs(camera.up).nor().scl(-deltaTime * velocity);
			camera.position.add(tmp);
		}
		if (keys.containsKey(STRAFE_RIGHT)) {
			tmp.set(camera.direction).crs(camera.up).nor().scl(deltaTime * velocity);
			camera.position.add(tmp);
		}
		/*
		 * if (keys.containsKey(UP)) {
		 * tmp.set(camera.up).nor().scl(deltaTime * velocity);
		 * camera.position.add(tmp);
		 * }
		 * if (keys.containsKey(DOWN)) {
		 * tmp.set(camera.up).nor().scl(-deltaTime * velocity);
		 * camera.position.add(tmp);
		 * }
		 */
		camera.update(true);
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
