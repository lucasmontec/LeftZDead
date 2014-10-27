package com.aliensoft.test;

import com.aliensoft.core.BulletScene;
import com.aliensoft.core.GameObject;
import com.aliensoft.core.GameObjectRenderer;
import com.aliensoft.core.camera.FPSpectatorCameraController;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;

public class LZDGameBulletTest extends ApplicationAdapter {
	public Environment					lights;
	public PerspectiveCamera			cam;
	public Model						floorModel;
	public Model						ballModel;
	private FPSpectatorCameraController	camController;

	private BulletScene					scene;

	btCollisionConfiguration			collisionConfig;
	btDispatcher						dispatcher;

	GameObject							floor;

	btDiscreteDynamicsWorld				world;

	final static short					GROUND_FLAG	= 1 << 8;
	final static short					OBJECT_FLAG	= 1 << 9;
	final static short					ALL_FLAG	= -1;

	@Override
	public void create () {
		Bullet.init();

		lights = new Environment();
		lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, 1f));
		lights.add(new DirectionalLight().set(0.7f, 0.7f, 0.7f, -1f, -0.8f, -0.2f));
		// lights.add(new PointLight().set(Color.BLUE, 0f, 10f, 4f, 50f));

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(10f, 10f, 10f);
		cam.near = 0.1f;
		cam.far = 300f;
		cam.update();

		ModelBuilder modelBuilder = new ModelBuilder();
		floorModel = modelBuilder.createBox(
				80f,
				1f,
				80f,
				new Material(ColorAttribute.createDiffuse(Color.WHITE)),
				Usage.Position | Usage.Normal);
		ballModel = modelBuilder.createSphere(
				1f,
				1f,
				1f,
				6,
				6,
				new Material(ColorAttribute.createDiffuse(Color.BLACK), ColorAttribute
						.createSpecular(Color.BLUE)),
						Usage.Position | Usage.Normal);

		camController = new FPSpectatorCameraController(cam);
		camController.setVelocity(20f);
		Gdx.input.setInputProcessor(new InputMultiplexer(new BallSpawnerProcessor(), camController));

		scene = new BulletScene();
		scene.create();
		scene.registerRenderer(new GameObjectRenderer());

		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);

		world = new btDiscreteDynamicsWorld(dispatcher, new btDbvtBroadphase(),
				new btSequentialImpulseConstraintSolver(), collisionConfig);
		world.setGravity(new Vector3(0, -10f, 0));

		floor = new GameObject(floorModel, new btBoxShape(new Vector3(40f, 0.5f, 40f)), 0f);
		scene.registerObject(floor);
		world.addRigidBody(floor.body, GROUND_FLAG, ALL_FLAG);
	}

	public void spawnBall() {
		GameObject nBall = new GameObject(ballModel, new btSphereShape(0.5f), 5f);
		nBall.translate(new Vector3(0, 10f, 0));
		scene.registerObject(nBall);
		world.addRigidBody(nBall.body, OBJECT_FLAG, ALL_FLAG);
	}

	public void shootBall() {
		GameObject nBall = new GameObject(ballModel, new btSphereShape(0.5f), 50f);
		nBall.translate(new Vector3(cam.position));
		scene.registerObject(nBall);
		world.addRigidBody(nBall.body, OBJECT_FLAG, ALL_FLAG);
		nBall.body.applyCentralImpulse(new Vector3(cam.direction).scl(1500f));
	}

	@Override
	public void dispose() {
		scene.dispose();
		floorModel.dispose();
		floor.dispose();
		collisionConfig.dispose();
		dispatcher.dispose();
		world.dispose();
		scene.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		final float delta = Math.min(1f / 30f, Gdx.graphics.getDeltaTime());
		world.stepSimulation(delta, 5, 1f / 60f);

		scene.update();

		camController.update();
		scene.render(lights, cam, Gdx.graphics.getDeltaTime());
	}

	private class BallSpawnerProcessor extends InputAdapter {
		@Override
		public boolean touchDown(int x, int y, int pointer, int button) {
			if (button == Buttons.LEFT)
				spawnBall();

			if (button == Buttons.RIGHT)
				shootBall();
			return true;
		}
	}
}
