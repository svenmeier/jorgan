package jorgan.swing;

import java.awt.*;
import java.awt.event.*;

/**
 * Disable the screen saver.
 */
public class ScreenSaverDisabler implements Runnable {

    /**
     * The keyCode to use.
     */
    private int keyCode;
    
    /**
     * The time to wait.
     */
    private long wait;
    
    /**
     * The thread used to trigger the robot.
     */
    private Thread thread; 

    public ScreenSaverDisabler() {
        this(KeyEvent.VK_SHIFT, 1000);
    }

    public ScreenSaverDisabler(int keyCode, long wait) {
        this.keyCode = keyCode;
        this.wait    = wait;
    }

    public void disable(boolean disable) {
        if (disable) {
            if (thread == null) {
                thread = new Thread(this);
                thread.setDaemon(true);
                thread.start();
            }
        } else {
            thread = null;
        }
    }
    
    public void run() {
        try {
            Robot r = new Robot();

            while (thread == Thread.currentThread()) {
                r.keyPress  (keyCode);
                r.keyRelease(keyCode);
                
                Thread.sleep(wait);
            }
        } catch (InterruptedException shouldNotBeInterrupted) {
        } catch (AWTException         robotNotAllowed) {
        }
    }    
}