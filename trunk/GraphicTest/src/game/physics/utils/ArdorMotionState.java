package game.physics.utils;

import javax.vecmath.Quat4f;

import com.ardor3d.math.Quaternion;
import com.ardor3d.scenegraph.Spatial;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

public class ArdorMotionState extends MotionState {

	public final Transform graphicsWorldTrans = new Transform();
	public final Transform centerOfMassOffset = new Transform();
	public final Transform startWorldTrans = new Transform();

	private Quaternion tempQuaternion = new Quaternion();
	private Quat4f dummy = new Quat4f();
	private Spatial spatial;

	public ArdorMotionState() {
		graphicsWorldTrans.setIdentity();
		centerOfMassOffset.setIdentity();
		startWorldTrans.setIdentity();
	}

	public ArdorMotionState(Transform startTrans) {
		this.graphicsWorldTrans.set(startTrans);
		centerOfMassOffset.setIdentity();
		this.startWorldTrans.set(startTrans);
	}

	public ArdorMotionState(Transform startTrans, Transform centerOfMassOffset) {
		this.graphicsWorldTrans.set(startTrans);
		this.centerOfMassOffset.set(centerOfMassOffset);
		this.startWorldTrans.set(startTrans);
	}

	public void setSpatial(Spatial spatial) {
		this.spatial = spatial;
	}

	@Override
	public Transform getWorldTransform(Transform worldTrans) {
		worldTrans.set(graphicsWorldTrans);
		return worldTrans;
	}

	@Override
	public void setWorldTransform(Transform worldTrans) {
		this.spatial.setTranslation(worldTrans.origin.x, worldTrans.origin.y,
				worldTrans.origin.z);

		tempQuaternion.set(worldTrans.getRotation(dummy).x,
				worldTrans.getRotation(dummy).y,
				worldTrans.getRotation(dummy).z,
				worldTrans.getRotation(dummy).w);
		this.spatial.setRotation(tempQuaternion);
	}

}