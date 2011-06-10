package Main;



import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.lwjgl.LWJGLUtil;
import simpleExample.gui.SimpleGUI;

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

	protected SimpleGUI gui;

	public DummyClient() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					gui = new SimpleGUI();
					gui.getFrame().setVisible(true);
				}
			});
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		gui.start();
	}
}
