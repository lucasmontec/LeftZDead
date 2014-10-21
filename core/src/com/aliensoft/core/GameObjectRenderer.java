package com.aliensoft.core;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public class GameObjectRenderer implements IObjectRenderer {

	@Override
	public Class<?> getRenderClass() {
		return GameObject.class;
	}

	@Override
	public void render(GameObject obj, Environment env, ModelBatch batch) {
		batch.render(obj.getModel(), env);
	}

}
