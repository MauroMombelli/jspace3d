package testsinc.client.game;

import javax.vecmath.Quat4f;

import testsinc.physic.utils.ArdorMotionState;
import testsinc.shared.GameEntity;

import com.ardor3d.math.Quaternion;
import com.ardor3d.scenegraph.Spatial;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

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

	protected Quaternion quat;
	protected Transform xForm;

	protected GameClientEntity() {
		quat = new Quaternion();
		xForm = new Transform();
	}

	/**
	 * @param physicalEntity
	 * @param graphicalEntity
	 */
	public GameClientEntity(RigidBody physicalEntity, Spatial graphicalEntity) {
		super(physicalEntity, graphicalEntity);
		quat = new Quaternion();
		xForm = new Transform();
	}

	public void syncGraphicsWithPhysics() {
		xForm = ((ArdorMotionState) getPhysicalEntity().getMotionState()).graphicsWorldTrans;
		getGraphicalEntity().setTranslation(xForm.origin.x, xForm.origin.y,
				xForm.origin.z);
		quat.set(xForm.getRotation(new Quat4f()).x,
				xForm.getRotation(new Quat4f()).y,
				xForm.getRotation(new Quat4f()).z,
				xForm.getRotation(new Quat4f()).w);
		getGraphicalEntity().setRotation(quat);
	}
}
