/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Test.server.physic;

import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Clock;
import javax.vecmath.Vector3f;

/**
 *
 * @author mauro
 */
public class EnginePhysic {

    private DynamicsWorld dynamicsWorld;
    private CollisionConfiguration collisionConfiguration;
    private CollisionDispatcher dispatcher;
    private DbvtBroadphase broadphase;
    private SequentialImpulseConstraintSolver solver;
    private Clock clock = new Clock();

    public EnginePhysic() {
        initPhysics();
    }

    private void initPhysics() {

        // collision configuration contains default setup for memory, collision setup
        collisionConfiguration = new DefaultCollisionConfiguration();

        // use the default collision dispatcher. For parallel processing you can use a diffent dispatcher (see Extras/BulletMultiThreaded)
        dispatcher = new CollisionDispatcher(collisionConfiguration);

        broadphase = new DbvtBroadphase();

        // the default constraint solver. For parallel processing you can use a different solver (see Extras/BulletMultiThreaded)
        SequentialImpulseConstraintSolver sol = new SequentialImpulseConstraintSolver();
        solver = sol;

        // TODO: needed for SimpleDynamicsWorld
        //sol.setSolverMode(sol.getSolverMode() & ~SolverMode.SOLVER_CACHE_FRIENDLY.getMask());

        dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);

        dynamicsWorld.setGravity(new Vector3f(0f, -10f, 0f));
    }

    public void update() {
        // simple dynamics world doesn't handle fixed-time-stepping
        float ms = getDeltaTimeMicroseconds();

        // step the simulation
        if (dynamicsWorld != null) {
            dynamicsWorld.stepSimulation(ms / 1000000f);
            // optional but useful: debug drawing
            dynamicsWorld.debugDrawWorld();
        }
    }

    private float getDeltaTimeMicroseconds() {
        float dt = clock.getTimeMicroseconds();
        clock.reset();
        return dt;
    }
}
