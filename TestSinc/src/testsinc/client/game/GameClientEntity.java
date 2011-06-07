package testsinc.client.game;

import testsinc.shared.GameEntity;

import com.ardor3d.scenegraph.Spatial;
import com.bulletphysics.dynamics.RigidBody;

/**
 * Extends {@link GameEntity}. <br>
 * <p>
 * Will provide specific functions to the client's entities, in particular to
 * the physical simulation and graphical update pipeline.
 * </p>
 * <br>
 * 
 * @author Goffredo Goffrei
 * 
 */

public class GameClientEntity extends GameEntity {

	/**
	 * @param physicalEntity
	 * @param graphicalEntity
	 */
	public GameClientEntity(RigidBody physicalEntity, Spatial graphicalEntity) {
		super(physicalEntity, graphicalEntity);

	}

}
