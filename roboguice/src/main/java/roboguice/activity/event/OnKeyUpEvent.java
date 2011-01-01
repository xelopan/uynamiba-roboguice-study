package roboguice.activity.event;

import android.view.KeyEvent;

/**
 * Class representing the event raised by RoboActivity.onKeyUp()
 *
 * @author John Ericksen
 */
public class OnKeyUpEvent {
        private int keyCode;
        private KeyEvent event;

        public OnKeyUpEvent(int keyCode, KeyEvent event) {
            this.keyCode = keyCode;
            this.event = event;
        }

        public int getKeyCode() {
            return keyCode;
        }

        public KeyEvent getEvent() {
            return event;
        }
    }