package com.aliensoft.core;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class SceneRender implements Disposable {

	/** Renderers for game objects */
	protected final HashMap<Class<?>, IObjectRenderer>	renderers	= new HashMap<>();

	/** Game physics objects */
	protected final HashMap<String, GameObject>			objects		= new HashMap<>();

	/** Models for decoration */
	protected final Array<ModelInstance>				models		= new Array<>();

	protected ModelBatch								batch;

	public void create() {
		batch = new ModelBatch(Gdx.files.internal("shaders/lighting.vertex.glsl"),
				Gdx.files.internal("shaders/lighting.fragment.glsl"));
	}

	/**
	 * Renders the scene and all objects in it using the renderers.
	 * 
	 * @param mb
	 *            The batch to use for rendering
	 * @param env
	 *            The environment to render each model
	 * @param cam
	 *            The camera to render the scene to
	 * @param delta
	 *            The delta time since last call
	 * @return the number of objects not rendered (# of obj that don't have a renderer)
	 */
	public int render(Environment env, Camera cam, float delta) {
		batch.begin(cam);
		int objectsIgnoredInRender = 0;
		for (GameObject obj : objects.values()) {
			IObjectRenderer renderer = renderers.get(obj.getClass());

			//Render if not null
			if (renderer != null) {
				renderer.render(obj, env, batch);
			} else
				objectsIgnoredInRender++;
		}

		// Render decoration objects
		batch.render(models);

		batch.end();

		return objectsIgnoredInRender;
	}

	public void dispose() {
		for (GameObject obj : objects.values()) {
			obj.dispose();
		}
		objects.clear();
		renderers.clear();

		batch.dispose();
	}

	/**
	 * Register a renderer to render a {@link GameObject} class. <br>
	 * This will override an older renderer if the class is the same
	 * as a already registered renderer
	 * 
	 * @param renderer
	 *            The renderer to register
	 */
	public void registerRenderer(IObjectRenderer renderer) {
		renderers.put(renderer.getRenderClass(), renderer);
	}

	/**
	 * Register a object to the scene
	 * 
	 * @param object
	 *            The object to be registered
	 * @return False if a object with the same ID is already registered. True if it was registered.
	 */
	public boolean registerObject(GameObject object) {
		if (objects.containsKey(object.getObjectID()))
			return false;

		objects.put(object.getObjectID(), object);
		return true;
	}

	/**
	 * Register a decoration model for rendering in this scene.
	 * Doesn't have collision.
	 * 
	 * @param model
	 */
	public void registerDecorationObject(ModelInstance model) {
		models.add(model);
	}

}
