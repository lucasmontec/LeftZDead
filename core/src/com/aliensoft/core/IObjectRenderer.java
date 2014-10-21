package com.aliensoft.core;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public interface IObjectRenderer {

	public Class<?> getRenderClass();

	public void render(GameObject obj, Environment env, ModelBatch batch);

}
