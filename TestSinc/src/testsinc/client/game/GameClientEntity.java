package testsinc.client.game;

import javax.vecmath.Quat4f;

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

	/**
	 * @param physicalEntity
	 * @param graphicalEntity
	 */
	public GameClientEntity(RigidBody physicalEntity, Spatial graphicalEntity) {
		super(physicalEntity, graphicalEntity);

	}

	public void syncGraphicsWithPhysics() {
		Transform out = new Transform();
		getPhysicalEntity().getWorldTransform(out);
		getGraphicalEntity().setTranslation(out.origin.x, out.origin.y,
				out.origin.z);
		Quat4f quatTemp = new Quat4f();
		out.getRotation(quatTemp);
		Quaternion quat = new Quaternion();
		quat.setW(quatTemp.w);
		quat.setX(quatTemp.x);
		quat.setY(quatTemp.y);
		quat.setZ(quatTemp.z);

		getGraphicalEntity().setRotation(quat);
	}
}
