/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

import java.io.File;
import org.lwjgl.LWJGLUtil;

/**
 *
 * @author Fra
 */
public class NativeLoader {

    public static void loadLwjglNatives(){
        //File lwjglNativeFolder = new File(System.getProperty("user.dir"),
        //		"TestSinc" + File.separator + "lib" + File.separator
        //				+ "lwjgl-2.7.1" + File.separator + "native");
        File lwjglNativeFolder = new File(System.getProperty("user.dir"),
                ".." + File.separator + "Shared libraries" + File.separator
                + "Native" + File.separator + "lwjgl");
        File thisSoNativeFolder = new File(lwjglNativeFolder,
                LWJGLUtil.getPlatformName());
        // LWJGLUtil.getPlatform()

        System.out.println(System.getProperties());

        System.out.println("Loading native library from: "
                + thisSoNativeFolder.getAbsolutePath());
        System.setProperty("org.lwjgl.librarypath",
                thisSoNativeFolder.getAbsolutePath());
    }
}
