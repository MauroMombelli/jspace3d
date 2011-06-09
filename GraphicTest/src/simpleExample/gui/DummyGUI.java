package simpleExample.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;

import simpleExample.DummyGame;

import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.framework.Scene;
import com.ardor3d.framework.lwjgl.LwjglAwtCanvas;
import com.ardor3d.framework.lwjgl.LwjglCanvasRenderer;
import com.ardor3d.input.ControllerWrapper;
import com.ardor3d.input.Key;
import com.ardor3d.input.MouseCursor;
import com.ardor3d.input.PhysicalLayer;
import com.ardor3d.input.awt.AwtFocusWrapper;
import com.ardor3d.input.awt.AwtKeyboardWrapper;
import com.ardor3d.input.awt.AwtMouseManager;
import com.ardor3d.input.awt.AwtMouseWrapper;
import com.ardor3d.input.logical.DummyControllerWrapper;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.KeyPressedCondition;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.ContextManager;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.util.ContextGarbageCollector;
import com.ardor3d.util.GameTaskQueue;
import com.ardor3d.util.GameTaskQueueManager;
import com.ardor3d.util.Timer;

public class DummyGUI implements Scene {

    private LwjglAwtCanvas theCanvas;
    final LogicalLayer logicalLayer = new LogicalLayer();
    private JFrame frame;
    final AtomicBoolean exit = new AtomicBoolean(false);
    final Timer timer = new Timer();
    final FrameHandler frameWork = new FrameHandler(timer);
    final DummyGame game = new DummyGame(logicalLayer, theCanvas);

    public DummyGUI() {

        final Scene scene1 = this;

        frameWork.addUpdater(game);

        frame = new JFrame("Dummy Game!!");

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(final WindowEvent e) {
                exit.set(true);
            }
        });

        frame.addComponentListener(new ComponentListener() {

            @Override
            public void componentShown(ComponentEvent e) {
                lookAtZero(theCanvas);
                resetCamera(theCanvas);

            }

            @Override
            public void componentResized(ComponentEvent e) {
                if (theCanvas != null) {
                    if (theCanvas.getCanvasRenderer() != null) {
                        if (theCanvas.getCanvasRenderer().getCamera() != null) {
                            theCanvas.getCanvasRenderer().getCamera().resize(frame.getContentPane().getWidth(),
                                    frame.getContentPane().getHeight());
                            theCanvas.getCanvasRenderer().getCamera().setFrustumPerspective(
                                    45.0f,
                                    (float) frame.getContentPane().getWidth()
                                    / (float) frame.getContentPane().getHeight(), 1, 1000);
                        }
                    }
                }
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                // TODO Auto-generated method stub
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                // TODO Auto-generated method stub
            }
        });

        try {
            addCanvas(frame, scene1, logicalLayer, frameWork);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        frame.pack();
        game.init();

    }

    public void start() {
        game.start();
        while (!exit.get()) {
            frameWork.updateFrame();
            Thread.yield();
        }
        frame.dispose();
    }

    private void resetCamera(final Canvas source) {
        final Vector3 loc = new Vector3(-100.0f, 0.0f, 100.0f);
        final Vector3 left = new Vector3(-1.0f, 0.0f, 0.0f);
        final Vector3 up = new Vector3(0.0f, 1.0f, 0.0f);
        final Vector3 dir = new Vector3(0.0f, 0f, -1.0f);
    if(source.getCanvasRenderer().getCamera()!=null)
        source.getCanvasRenderer().getCamera().setFrame(loc, left, up, dir);
    }

    private void addCanvas(final JFrame frame, final Scene scene,
            final LogicalLayer logicalLayer, final FrameHandler frameWork)
            throws Exception {
        final LwjglCanvasRenderer canvasRenderer = new LwjglCanvasRenderer(
                scene);

        final DisplaySettings settings = new DisplaySettings(1280, 1024, 24, 0,
                0, 16, 0, 0, false, false);
        frame.getContentPane().setLayout(new BorderLayout());
        theCanvas = new LwjglAwtCanvas(settings, canvasRenderer);

        frame.getContentPane().add(theCanvas);

        theCanvas.setSize(new Dimension(1280, 1024));
        theCanvas.setVisible(true);

        final AwtKeyboardWrapper keyboardWrapper = new AwtKeyboardWrapper(
                theCanvas);
        final AwtFocusWrapper focusWrapper = new AwtFocusWrapper(theCanvas);
        final AwtMouseManager mouseManager = new AwtMouseManager(theCanvas);
        final AwtMouseWrapper mouseWrapper = new AwtMouseWrapper(theCanvas,
                mouseManager);
        final ControllerWrapper controllerWrapper = new DummyControllerWrapper();

        final PhysicalLayer pl = new PhysicalLayer(keyboardWrapper,
                mouseWrapper, controllerWrapper, focusWrapper);

        logicalLayer.registerInput(theCanvas, pl);

        logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(
                Key.H), new TriggerAction() {

            public void perform(final Canvas source,
                    final TwoInputStates inputStates, final double tpf) {
                if (source != theCanvas) {
                    return;
                }
            }
        }));
        logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(
                Key.J), new TriggerAction() {

            public void perform(final Canvas source,
                    final TwoInputStates inputStates, final double tpf) {
                if (source != theCanvas) {
                    return;
                }

                mouseManager.setCursor(MouseCursor.SYSTEM_DEFAULT);
            }
        }));

        frameWork.addCanvas(theCanvas);

    }

    private void lookAtZero(final Canvas source) {
        if(source.getCanvasRenderer().getCamera()!=null)
            source.getCanvasRenderer().getCamera().lookAt(Vector3.ZERO, Vector3.UNIT_Y);
    }

    @Override
    public PickResults doPick(Ray3 arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean renderUnto(Renderer arg0) {
        // Execute renderQueue item
        GameTaskQueueManager.getManager(ContextManager.getCurrentContext()).getQueue(GameTaskQueue.RENDER).execute(arg0);
        ContextGarbageCollector.doRuntimeCleanup(arg0);

        arg0.draw(game.getRootNode());
        return true;
    }

    public JFrame getFrame() {
        // TODO Auto-generated method stub
        return frame;
    }
}
