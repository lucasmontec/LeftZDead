package com.aliensoft.core;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;

public class GameObject implements Disposable {

	/* Bullet */
	public btRigidBody								body;
	private btRigidBody.btRigidBodyConstructionInfo	constructionInfo;

	/* 3d api */
	private ModelInstance							model;

	/* game api */
	private String				objectID;
	private static int								UID;

	public GameObject() {
		makeID();
	}

	public GameObject(Model mdl, btCollisionShape shape, float mass) {
		model = new ModelInstance(mdl);
		Vector3 localInertia = new Vector3(0, 0, 0);
		if (mass > 0f)
			shape.calculateLocalInertia(mass, localInertia);
		constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
		body = new btRigidBody(constructionInfo);
		body.setUserValue(UID);
		makeID();
	}

	public void translate(Vector3 pos) {
		model.transform.trn(pos);
		body.setWorldTransform(model.transform);
	}

	public void setAngles(float pitch, float yaw, float roll) {
		model.transform.setFromEulerAngles(pitch, yaw, roll);
		body.setWorldTransform(model.transform);
	}

	/**
	 * Updates the model transform from the bullet rigidbody transform
	 */
	public void update() {
		body.getWorldTransform(model.transform);
	}

	public ModelInstance getModel() {
		return model;
	}

	@Override
	public void dispose() {
		constructionInfo.dispose();
	}

	public String getObjectID() {
		return objectID;
	}

	private void makeID() {
		objectID = "OBJ_" + (UID++);
	}
}
