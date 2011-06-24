/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package settings;

import java.awt.Dimension;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.opengl.Display;

/**
 *
 * @author Fra
 */
public class GameSettings {

    public final boolean DEFAULT_FORCEGUI = true;
    public final Dimension DEFAULT_RESOLUTION = new Dimension(Display.getDesktopDisplayMode().getWidth(),
            Display.getDesktopDisplayMode().getHeight());
    public final int DEFAULT_ANTIALIAS = 0;
    
    private boolean forceGUI;
    private Dimension resolution;
    private int antialias;

    private Properties properties;

    public int getAntialias() {
        return antialias;
    }

    public void setAntialias(int antialias) {        
        properties.setProperty("ANTIALIAS", String.valueOf(antialias));
        this.antialias = antialias;
    }

    public boolean isForceGUI() {
        return forceGUI;
    }

    public void setForceGUI(boolean forceGUI) {
        this.forceGUI = forceGUI;
        properties.setProperty("FORCEGUI", String.valueOf(forceGUI));
    }

    public Dimension getResolution() {
        return resolution;
    }

    public void setResolution(Dimension resolution) {
        properties.setProperty("WIDTH", String.valueOf(resolution.width));
        properties.setProperty("HEIGHT", String.valueOf(resolution.height));
        this.resolution = resolution;
    }

    public GameSettings(){
        properties = new Properties();
        setDefaultSettings();
    }

    private void setDefaultSettings() {
        setAntialias(DEFAULT_ANTIALIAS);
        setForceGUI(DEFAULT_FORCEGUI);
        setResolution(DEFAULT_RESOLUTION);
    }

    void loadFrom(File configFile) {
        properties = new Properties();
        try {
            properties.load(new FileReader(configFile));
        } catch (IOException ex) {
            Logger.getLogger(GameSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
        updateSettingsFromProperties();
    }

    void saveTo(File configFile) {
        try {
            properties.store(new FileWriter(configFile), "JSpace3D settings");
        } catch (IOException ex) {
            Logger.getLogger(GameSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateSettingsFromProperties() {
        antialias = Integer.parseInt(properties.getProperty("ANTIALIAS"));
        forceGUI = Boolean.parseBoolean(properties.getProperty("FORCEGUI"));
        Dimension tmp = new Dimension();
        tmp.setSize(Integer.parseInt(properties.getProperty("WIDTH")),
        Integer.parseInt(properties.getProperty("HEIGHT")));
        resolution = tmp;
    }
}
