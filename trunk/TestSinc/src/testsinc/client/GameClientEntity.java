package testsinc.client;

import com.ardor3d.scenegraph.Spatial;
import com.bulletphysics.dynamics.RigidBody;

import testsinc.shared.GameEntity;

/**
 * Extends {@link GameEntity}.
 * <br>
 * <br>
 * Will provide specific functions to the client physical simulation and graphical update pipeline.
 * <br>
 * <br>
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
		// TODO Auto-generated constructor stub
	}
	
	

}
