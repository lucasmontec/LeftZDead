package com.aliensoft.core;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

public class GameObject {

	/* Bullet */
	private btCollisionShape	collisionShape;
	private btCollisionObject	collisionObject;

	/* 3d api */
	private ModelInstance		model;

	/* game api */
	private String				objectID;

	public GameObject() {}

	public GameObject(ModelInstance mdl, btCollisionShape shape) {
		model = mdl;
		collisionShape = shape;
	}

	/* Calls after constructing inside constructor */
	{
		collisionObject = new btCollisionObject();
		create();
	}

	/**
	 * Makes the instance valid applying the bullet shape and model transform
	 */
	private void create() {
		if(collisionObject != null) {
			// Set shape
			if (collisionShape != null)
				collisionObject.setCollisionShape(collisionShape);
			// Set transform
			if (model != null)
				collisionObject.setWorldTransform(model.transform);
		}
	}

	public String getObjectID() {
		return objectID;
	}

	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}
}
