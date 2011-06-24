/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gameCore;

import com.ardor3d.framework.Updater;
import com.ardor3d.util.ReadOnlyTimer;
import gameCore.network.NetworkManager;
import gameCore.game.physics.PhysicsManager;
import gameCore.players.PlayerManager;
import gameCore.game.scene.GameScene;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author Fra
 */
public class GameCore extends Observable implements Updater, Observer{

    private PlayerManager playerManager;
    private NetworkManager networkManager;

    private GameScene scene;



    public void init() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void update(ReadOnlyTimer timer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void update(Observable o, Object arg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
