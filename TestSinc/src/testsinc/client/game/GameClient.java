package testsinc.client.game;

import testsinc.shared.AbstractGame;

import com.ardor3d.scenegraph.Node;

/**
 * Actual game loop for the client. Call init() method to create a game. Call
 * the start method to start the game.
 * 
 * @author Goffredo Goffrei
 * 
 */

public class GameClient extends AbstractGame {
	private Node rootNode;

	public GameClient() {
		setRootNode(new Node());
	}

	@Override
	public void init() {
		super.init();
		// TODO initialize graphics (load scene)
		// TODO initialize physics
		// TODO initialize input triggers (map keys to actions)
		gameInitialized.set(true);
	}

	/**
	 * @param rootNode
	 *            the rootNode to set
	 */
	public void setRootNode(Node rootNode) {
		this.rootNode = rootNode;
	}

	/**
	 * @return the rootNode
	 */
	public Node getRootNode() {
		return rootNode;
	}

}
