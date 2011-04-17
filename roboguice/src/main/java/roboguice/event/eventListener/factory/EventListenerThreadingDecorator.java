package roboguice.event.eventListener.factory;

import roboguice.event.EventListener;
import roboguice.event.EventThread;
import roboguice.event.eventListener.AsynchronousEventListenerDecorator;
import roboguice.event.eventListener.UIThreadEventListenerDecorator;

import android.os.Handler;

import com.google.inject.Provider;

import javax.inject.Inject;

/**
 * @author John Ericksen
 */
public class EventListenerThreadingDecorator {

    protected Provider<Handler> handlerProvider;

    @Inject
    public EventListenerThreadingDecorator( Provider<Handler> handlerProvider) {
        this.handlerProvider = handlerProvider;
    }

    public <T> EventListener<T> decorate(EventThread threadType, EventListener<T> eventListener){
        switch (threadType){
            case UI:
                return new UIThreadEventListenerDecorator<T>(eventListener, handlerProvider.get() );
            case BACKGROUND:
                return new AsynchronousEventListenerDecorator<T>(eventListener);
            default:
                return eventListener;
        }
    }
}
