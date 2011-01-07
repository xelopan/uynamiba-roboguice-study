package roboguice.activity.event;

import roboguice.event.Returns;

import android.view.KeyEvent;

/**
 * Class representing the event raised by RoboActivity.onKeyDown()
 *
 * @author Adam Tybor
 * @author John Ericksen
 */
@Returns(boolean.class)
public class OnKeyDownEvent {
    
        protected int keyCode;
        protected KeyEvent event;

        public OnKeyDownEvent(int keyCode, KeyEvent event) {
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
