package game;

import java.util.concurrent.atomic.AtomicBoolean;

import com.ardor3d.framework.Updater;
import com.ardor3d.util.ReadOnlyTimer;
import game.physics.PhysicsEngine;

/**
 * <p>
 * Extend this class to implement a generic game loop with the update method. A
 * single thread should access the loop. For now this abstract implementation
 * manages only the physical world. We should check whether to leave all of it
 * to the implementation instead.
 * </p>
 * <p>
 * Important: set gameInitialized to true after calling the init method in your
 * game implementation, otherwise the start method will generate an exception.
 * </p>
 * 
 * 
 * @author Goffredo Goffrei
 * 
 */

public abstract class AbstractGame implements Updater {

	protected final PhysicsEngine physicalEngine = new PhysicsEngine();
	private AtomicBoolean running = new AtomicBoolean(false);
	protected AtomicBoolean gameInitialized = new AtomicBoolean(false);

	/**
	 * Override this method to define your game initializations.
	 * 
	 */
	@Override
	public void init() {

	}

	/**
	 * Override this method to define your game loop. *
	 */
	@Override
	public void update(ReadOnlyTimer arg0) {
		/*
		 * Update game only if not paused (like when in menu)
		 */

		if (running.get()) {
			physicalEngine.update();
		}
	}

	/**
	 * Call this method to start the loop. <br>
	 * TODO implement exception
	 */
	public void start() {
		if (gameInitialized.get()) {
			running.set(true);
		} else {
			System.out.println("Game not initialized!");
		}
	}

	/**
	 * Causes the game to pause after it completes the current loop.
	 */
	public void pause() {
		running.set(false);
	}

}
