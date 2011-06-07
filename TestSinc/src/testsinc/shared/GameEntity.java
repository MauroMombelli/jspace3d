package testsinc.shared;

import com.ardor3d.scenegraph.Spatial;
import com.bulletphysics.dynamics.RigidBody;

/**
 * @author  Goffredo Goffrei
 */

public abstract class GameEntity {
	
	/**
	 * @uml.property  name="physicalEntity"
	 */
	protected RigidBody physicalEntity;
	/**
	 * @uml.property  name="graphicalEntity"
	 */
	protected Spatial graphicalEntity;
	
	/**
	 * @param physicalEntity  the physicalEntity to set
	 */
	public void setPhysicalEntity(RigidBody physicalEntity) {
		this.physicalEntity = physicalEntity;
	}
	/**
	 * @return  the physicalEntity
	 */
	public RigidBody getPhysicalEntity() {
		return physicalEntity;
	}
	/**
	 * @param graphicalEntity  the graphicalEntity to set
	 */
	public void setGraphicalEntity(Spatial graphicalEntity) {
		this.graphicalEntity = graphicalEntity;
	}
	/**
	 * @return  the graphicalEntity
	 */
	public Spatial getGraphicalEntity() {
		return graphicalEntity;
	}
	/**
	 * @param physicalEntity
	 * @param graphicalEntity
	 */
	protected GameEntity(RigidBody physicalEntity, Spatial graphicalEntity) {
		this.physicalEntity = physicalEntity;
		this.graphicalEntity = graphicalEntity;
	}
	
	
}
