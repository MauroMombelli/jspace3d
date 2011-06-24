package Main;

import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.renderer.lwjgl.LwjglContextCapabilities;
import java.awt.GraphicsEnvironment;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;
import settings.GameSettingsManager;
import utils.NativeLoader;

public class DummyClient implements Observer {

    static {
    }
    private final GameSettingsManager gameSettingsManager;

    public DummyClient() {
        gameSettingsManager = new GameSettingsManager("config.txt");
        //blocking Call
        gameSettingsManager.startUp();

        System.out.println("Finished and current thread is: " + Thread.currentThread().getName());
    }

    public static void main(String[] args) {
        NativeLoader.loadLwjglNatives();
        DummyClient tmp = new DummyClient();
    }

    public void update(Observable o, Object arg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
