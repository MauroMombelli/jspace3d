/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package settings;

import java.awt.EventQueue;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import settings.GUI.components.SettingsDialog;

/**
 *
 * @author Fra
 */
public class GameSettingsManager {

    private GameSettings gameSettings;
    private File configFile;
    private Thread EDT;

    public GameSettingsManager(String pathToFile) {
        configFile = new File(pathToFile);
        gameSettings = new GameSettings();
    }

    /**
     * Returns true if the GUI is shown, false if not.
     * @return
     */
    public void startUp() {
        if (configFile.exists()) {
            gameSettings.loadFrom(configFile);
            if (gameSettings.isForceGUI()) {
                showGUI();
                saveSettingsToFile();
            }
        } else {
            Logger.getLogger(GameSettingsManager.class.getName()).log(Level.INFO, "No settings file found, creating a default one.");
            showGUI();
            saveSettingsToFile();
        }
    }

    public void saveSettingsToFile() {
        gameSettings.saveTo(configFile);
    }

    private void showGUI() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                public void run() {
                    SettingsDialog dialog = new SettingsDialog(gameSettings);
                    dialog.pack();
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);
                    //this is severly a bad hack
                    setEDT(Thread.currentThread());
                }
            });
        } catch (InterruptedException ex) {
            Logger.getLogger(GameSettingsManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(GameSettingsManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            if(EDT!=null)
                EDT.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(GameSettingsManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setEDT(Thread edt) {
        if (SwingUtilities.isEventDispatchThread()) {
            EDT = edt;
        }
    }
}
