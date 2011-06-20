package Main;



import gameCore.GameCore;
import java.io.File;

import org.lwjgl.LWJGLUtil;
import settings.GUI.components.SettingsDialog;

public class DummyClient {

	static {
		//File lwjglNativeFolder = new File(System.getProperty("user.dir"),
		//		"TestSinc" + File.separator + "lib" + File.separator
		//				+ "lwjgl-2.7.1" + File.separator + "native");
            File lwjglNativeFolder = new File(System.getProperty("user.dir"),
				".." + File.separator +"lib" + File.separator
						+ "lwjgl-2.7.1" + File.separator + "native");
		File thisSoNativeFolder = new File(lwjglNativeFolder,
				LWJGLUtil.getPlatformName());
		// LWJGLUtil.getPlatform()

		System.out.println(System.getProperties());

		System.out.println("Loading native library from: "
				+ thisSoNativeFolder.getAbsolutePath());
		System.setProperty("org.lwjgl.librarypath",
				thisSoNativeFolder.getAbsolutePath());

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DummyClient main = new DummyClient();

	}
        
	public DummyClient() {
		GameCore gameCore = new GameCore();
                SettingsDialog dialog = new SettingsDialog(new javax.swing.JFrame(), true);
                dialog.setLocationRelativeTo(null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.pack();
                dialog.setVisible(true);
	}
}
