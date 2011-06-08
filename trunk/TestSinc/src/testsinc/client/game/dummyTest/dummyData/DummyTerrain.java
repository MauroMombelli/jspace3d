package testsinc.client.game.dummyTest.dummyData;

import javax.vecmath.Vector3f;

import testsinc.client.game.GameClientEntity;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.MaterialState.ColorMaterial;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.shape.Box;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class DummyTerrain extends GameClientEntity {

	public DummyTerrain() {
		this(new RigidBody(0, new DefaultMotionState(), new BoxShape(
				new Vector3f(100, 0.1f, 100))), new Box("Terrain", new Vector3(
				0, -20, 0), 100, 0.1, 100));
	}

	private DummyTerrain(RigidBody physicalEntity, Spatial graphicalEntity) {
		super(physicalEntity, graphicalEntity);
		getGraphicalEntity().setModelBound(new BoundingBox());
		getGraphicalEntity().setRandomColors();

		// Add a material to the box, to show both vertex color and
		// lighting/shading.
		final MaterialState ms = new MaterialState();
		ms.setColorMaterial(ColorMaterial.Diffuse);
		getGraphicalEntity().setRenderState(ms);
	}

	@Override
	public Mesh getGraphicalEntity() {
		return (Mesh) super.getGraphicalEntity();
	}

	public void setGraphicalEntity(Mesh graphicalEntity) {
		// TODO Auto-generated method stub
		super.setGraphicalEntity(graphicalEntity);
	}

	@Override
	public RigidBody getPhysicalEntity() {
		// TODO Auto-generated method stub
		return super.getPhysicalEntity();
	}

	@Override
	public void setPhysicalEntity(RigidBody physicalEntity) {
		// TODO Auto-generated method stub
		super.setPhysicalEntity(physicalEntity);
	}

	public void setPosition(double x, double y, double z) {
		getGraphicalEntity().setTranslation(x, y, z);
		Transform xform = new Transform();
		xform.setIdentity();
		xform.origin.set((float) x, (float) y, (float) z);
		getPhysicalEntity().setCenterOfMassTransform(xform);
	}

}
