package stickman.model.entities.character;

import stickman.view.Observer;

/**
 * Using observer design pattern. Subject (Game Engine) is watched by the Observer (ForegroundDrawer) to
 * display the correct message on the screen.
 */
public interface Subject {
    void postUpdate();
    void notifyObservers();
    void addObserver(Observer o);
    Hero getState ();
}
