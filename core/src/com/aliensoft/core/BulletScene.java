package com.aliensoft.core;

public class BulletScene extends SceneRender {

	public void update() {
		for (GameObject obj : objects.values()) {
			obj.update();
		}
	}

}
