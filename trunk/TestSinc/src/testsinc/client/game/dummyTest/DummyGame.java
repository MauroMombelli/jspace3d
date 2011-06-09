package testsinc.client.game.dummyTest;

import testsinc.client.game.GameClient;
import testsinc.client.game.dummyTest.dummyData.DummyBox;
import testsinc.client.game.dummyTest.dummyData.DummyTerrain;

import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.lwjgl.LwjglAwtCanvas;
import com.ardor3d.input.ButtonState;
import com.ardor3d.input.InputState;
import com.ardor3d.input.Key;
import com.ardor3d.input.MouseState;
import com.ardor3d.input.logical.AnyKeyCondition;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.KeyHeldCondition;
import com.ardor3d.input.logical.KeyPressedCondition;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.logical.MouseButtonCondition;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TriggerConditions;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.light.PointLight;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.Camera.ProjectionMode;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.util.ReadOnlyTimer;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class DummyGame extends GameClient {

	private LightState _lightState;
	private LogicalLayer logicalLayer;
	private static final int MOVE_SPEED = 30;
	private static final double TURN_SPEED = 0.5;
	private final Matrix3 _incr = new Matrix3();
	private static final double MOUSE_TURN_SPEED = 1;

	@Override
	public void update(ReadOnlyTimer arg0) {
		// TODO Auto-generated method stub
		super.update(arg0);
		final double tpf = arg0.getTimePerFrame();
		logicalLayer.checkTriggers(tpf);
		getRootNode().updateGeometricState(tpf, true);
		/*
		 * for (GameClientEntity entity : entities)
		 * entity.syncGraphicsWithPhysics();
		 */
	}

	public DummyGame(LogicalLayer _logicalLayer, LwjglAwtCanvas theCanvas) {

		logicalLayer = _logicalLayer;
		/**
		 * Create a ZBuffer to display pixels closest to the camera above
		 * farther ones.
		 */
		final ZBufferState buf = new ZBufferState();
		buf.setEnabled(true);
		buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
		getRootNode().setRenderState(buf);

		// ---- LIGHTS
		/** Set up a basic, default light. */
		final PointLight light = new PointLight();
		light.setDiffuse(new ColorRGBA(0.75f, 0.75f, 0.75f, 0.75f));
		light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
		light.setLocation(new Vector3(100, 100, 100));
		light.setEnabled(true);

		/** Attach the light to a lightState and the lightState to rootNode. */
		_lightState = new LightState();
		_lightState.setEnabled(true);
		_lightState.attach(light);
		getRootNode().setRenderState(_lightState);

		getRootNode().getSceneHints().setRenderBucketType(
				RenderBucketType.Opaque);

		registerInputTriggers();
		init();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		super.init();
		gameInitialized.set(false);
		entities.clear();
		getRootNode().detachAllChildren();
		physicalEngine.clear();
		DummyTerrain terrain = new DummyTerrain();
		// terrain.setPosition(0, -20, 0);
		entities.add(terrain);
		getRootNode().attachChild(terrain.getGraphicalEntity());
		physicalEngine.addRigidBody(terrain.getPhysicalEntity());
		for (int i = 0; i < 1; i++) {
			DummyBox temp = new DummyBox();
			temp.setPosition(Math.random() * 20, Math.random() * 20,
					Math.random() * 20);
			entities.add(temp);
			getRootNode().attachChild(temp.getGraphicalEntity());
			physicalEngine.addRigidBody(temp.getPhysicalEntity());
		}
		gameInitialized.set(true);
	}

	private void registerInputTriggers() {
		logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(
				Key.W), new TriggerAction() {
			public void perform(final Canvas source,
					final TwoInputStates inputStates, final double tpf) {
				moveForward(source, tpf);
			}
		}));
		logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(
				Key.S), new TriggerAction() {
			public void perform(final Canvas source,
					final TwoInputStates inputStates, final double tpf) {
				moveBack(source, tpf);
			}
		}));
		logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(
				Key.A), new TriggerAction() {
			public void perform(final Canvas source,
					final TwoInputStates inputStates, final double tpf) {
				moveLeft(source, tpf);
			}
		}));
		logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(
				Key.D), new TriggerAction() {
			public void perform(final Canvas source,
					final TwoInputStates inputStates, final double tpf) {
				moveRight(source, tpf);
			}
		}));
		logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(
				Key.Q), new TriggerAction() {
			public void perform(final Canvas source,
					final TwoInputStates inputStates, final double tpf) {
				moveLeft(source, tpf);
			}
		}));
		logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(
				Key.R), new TriggerAction() {
			public void perform(final Canvas source,
					final TwoInputStates inputStates, final double tpf) {
				resetCamera(source);
				lookAtZero(source);
				init();
			}
		}));

		logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(
				Key.ZERO), new TriggerAction() {
			public void perform(final Canvas source,
					final TwoInputStates inputStates, final double tpf) {
				resetCamera(source);
			}
		}));
		logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(
				Key.NINE), new TriggerAction() {
			public void perform(final Canvas source,
					final TwoInputStates inputStates, final double tpf) {
				lookAtZero(source);
			}
		}));

		final Predicate<TwoInputStates> mouseMovedAndOneButtonPressed = Predicates
				.and(TriggerConditions.mouseMoved(), Predicates.or(
						TriggerConditions.leftButtonDown(),
						TriggerConditions.rightButtonDown()));

		logicalLayer.registerTrigger(new InputTrigger(
				mouseMovedAndOneButtonPressed, new TriggerAction() {
					public void perform(final Canvas source,
							final TwoInputStates inputStates, final double tpf) {
						final MouseState mouseState = inputStates.getCurrent()
								.getMouseState();

						turn(source, mouseState.getDx() * tpf
								* -MOUSE_TURN_SPEED);
						rotateUpDown(source, mouseState.getDy() * tpf
								* -MOUSE_TURN_SPEED);
					}
				}));
		logicalLayer.registerTrigger(new InputTrigger(new MouseButtonCondition(
				ButtonState.DOWN, ButtonState.DOWN, ButtonState.UNDEFINED),
				new TriggerAction() {
					public void perform(final Canvas source,
							final TwoInputStates inputStates, final double tpf) {
						moveForward(source, tpf);
					}
				}));

		logicalLayer.registerTrigger(new InputTrigger(new AnyKeyCondition(),
				new TriggerAction() {
					public void perform(final Canvas source,
							final TwoInputStates inputStates, final double tpf) {
						final InputState current = inputStates.getCurrent();

						System.out.println("Key character pressed: "
								+ current.getKeyboardState().getKeyEvent()
										.getKeyChar());
					}
				}));
	}

	private void lookAtZero(final Canvas source) {
		source.getCanvasRenderer().getCamera()
				.lookAt(Vector3.ZERO, Vector3.UNIT_Y);
	}

	private void resetCamera(final Canvas source) {
		final Vector3 loc = new Vector3(-100.0f, 0.0f, 100.0f);
		final Vector3 left = new Vector3(-1.0f, 0.0f, 0.0f);
		final Vector3 up = new Vector3(0.0f, 1.0f, 0.0f);
		final Vector3 dir = new Vector3(0.0f, 0f, -1.0f);

		source.getCanvasRenderer().getCamera().setFrame(loc, left, up, dir);
	}

	private void rotateUpDown(final Canvas canvas, final double speed) {
		final Camera camera = canvas.getCanvasRenderer().getCamera();

		final Vector3 temp = Vector3.fetchTempInstance();
		_incr.fromAngleNormalAxis(speed, camera.getLeft());

		_incr.applyPost(camera.getLeft(), temp);
		camera.setLeft(temp);

		_incr.applyPost(camera.getDirection(), temp);
		camera.setDirection(temp);

		_incr.applyPost(camera.getUp(), temp);
		camera.setUp(temp);

		Vector3.releaseTempInstance(temp);

		camera.normalize();

	}

	private void turnRight(final Canvas canvas, final double tpf) {
		turn(canvas, -TURN_SPEED * tpf);
	}

	private void turn(final Canvas canvas, final double speed) {
		final Camera camera = canvas.getCanvasRenderer().getCamera();

		final Vector3 temp = Vector3.fetchTempInstance();
		_incr.fromAngleNormalAxis(speed, new Vector3(0, 1, 0));

		_incr.applyPost(camera.getLeft(), temp);
		camera.setLeft(temp);

		_incr.applyPost(camera.getDirection(), temp);
		camera.setDirection(temp);

		_incr.applyPost(camera.getUp(), temp);
		camera.setUp(temp);
		Vector3.releaseTempInstance(temp);

		camera.normalize();
	}

	private void turnLeft(final Canvas canvas, final double tpf) {
		turn(canvas, TURN_SPEED * tpf);
	}

	private void moveForward(final Canvas canvas, final double tpf) {
		final Camera camera = canvas.getCanvasRenderer().getCamera();
		final Vector3 loc = Vector3.fetchTempInstance().set(
				camera.getLocation());
		final Vector3 dir = Vector3.fetchTempInstance();
		if (camera.getProjectionMode() == ProjectionMode.Perspective) {
			dir.set(camera.getDirection());
		} else {
			// move up if in parallel mode
			dir.set(camera.getUp());
		}
		dir.multiplyLocal(MOVE_SPEED * tpf);
		loc.addLocal(dir);
		camera.setLocation(loc);
		Vector3.releaseTempInstance(loc);
		Vector3.releaseTempInstance(dir);
	}

	private void moveLeft(final Canvas canvas, final double tpf) {
		final Camera camera = canvas.getCanvasRenderer().getCamera();
		final Vector3 loc = Vector3.fetchTempInstance().set(
				camera.getLocation());
		final Vector3 dir = Vector3.fetchTempInstance();

		dir.set(camera.getLeft());

		dir.multiplyLocal(MOVE_SPEED * tpf);
		loc.addLocal(dir);
		camera.setLocation(loc);
		Vector3.releaseTempInstance(loc);
		Vector3.releaseTempInstance(dir);
	}

	private void moveRight(final Canvas canvas, final double tpf) {
		final Camera camera = canvas.getCanvasRenderer().getCamera();
		final Vector3 loc = Vector3.fetchTempInstance().set(
				camera.getLocation());
		final Vector3 dir = Vector3.fetchTempInstance();

		dir.set(camera.getLeft());

		dir.multiplyLocal(-MOVE_SPEED * tpf);
		loc.addLocal(dir);
		camera.setLocation(loc);
		Vector3.releaseTempInstance(loc);
		Vector3.releaseTempInstance(dir);
	}

	private void moveBack(final Canvas canvas, final double tpf) {
		final Camera camera = canvas.getCanvasRenderer().getCamera();
		final Vector3 loc = Vector3.fetchTempInstance().set(
				camera.getLocation());
		final Vector3 dir = Vector3.fetchTempInstance();
		if (camera.getProjectionMode() == ProjectionMode.Perspective) {
			dir.set(camera.getDirection());
		} else {
			// move up if in parallel mode
			dir.set(camera.getUp());
		}
		dir.multiplyLocal(-MOVE_SPEED * tpf);
		loc.addLocal(dir);
		camera.setLocation(loc);
		Vector3.releaseTempInstance(loc);
		Vector3.releaseTempInstance(dir);
	}
}
