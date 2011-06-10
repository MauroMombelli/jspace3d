package simpleExample.testData;

import javax.vecmath.Vector3f;

import game.GameClientEntity;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.MaterialState.ColorMaterial;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.shape.Box;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.Transform;
import game.physics.utils.ArdorMotionState;

public class DummyTerrain extends GameClientEntity {

    public DummyTerrain() {
        super();
        /*
         * Set Graphical entity
         */
        setGraphicalEntity(new Box("Box", new Vector3(0, -20, 0), 50, 0.5,
                50));

        /*
         * Set Physical entity
         */
        CollisionShape colShape = new BoxShape(new Vector3f(50, 0.5f, 50));
        Transform startTransform = new Transform();
        startTransform.setIdentity();
        startTransform.origin.set(0, -20, 0);
        float mass = 0f;
        Vector3f localInertia = new Vector3f(0, 0, 0);
        colShape.calculateLocalInertia(mass, localInertia);
        ArdorMotionState myMotionState = new ArdorMotionState(startTransform);
        myMotionState.setSpatial(graphicalEntity);
        RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass,
                myMotionState, colShape, localInertia);
        RigidBody body = new RigidBody(rbInfo);
        setPhysicalEntity(body);
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
    }

    @Override
    public void syncGraphicsWithPhysics() {
        //super.syncGraphicsWithPhysics();
        System.out.println("position of terrain is: " + getPhysicalEntity().getWorldTransform(xForm).origin.x
                + " "
                + getPhysicalEntity().getWorldTransform(xForm).origin.y
                + " "
                + getPhysicalEntity().getWorldTransform(xForm).origin.y);
    }
}