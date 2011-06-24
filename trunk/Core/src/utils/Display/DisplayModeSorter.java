/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.Display;

import java.awt.DisplayMode;
import java.util.Comparator;

/**
 *
 * @author Fra
 */
public class DisplayModeSorter implements Comparator<DisplayMode> {

    public int compare(final DisplayMode a, final DisplayMode b) {
        // Width
        if (a.getWidth() != b.getWidth()) {
            return (a.getWidth() > b.getWidth()) ? 1 : -1;
        }
        // Height
        if (a.getHeight() != b.getHeight()) {
            return (a.getHeight() > b.getHeight()) ? 1 : -1;
        }
        // Bit depth
        if (a.getBitDepth() != b.getBitDepth()) {
            return (a.getBitDepth() > b.getBitDepth()) ? 1 : -1;
        }
        // Refresh rate
        if (a.getRefreshRate() != b.getRefreshRate()) {
            return (a.getRefreshRate() > b.getRefreshRate()) ? 1 : -1;
        }
        // All fields are equal
        return 0;
    }
}
