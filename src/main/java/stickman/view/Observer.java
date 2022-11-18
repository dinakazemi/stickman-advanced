package stickman.view;

import stickman.model.entities.character.Subject;
//import stickman.model.levels.Level;

public interface Observer {
    void update();
    void setSubject (Subject subject);
}
