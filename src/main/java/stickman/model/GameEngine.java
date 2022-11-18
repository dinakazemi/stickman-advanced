package stickman.model;

import stickman.model.entities.character.Subject;
import stickman.model.levels.Level;
import stickman.view.Observer;

//game engine has been turned into an abstract class instead of interface to be able to implement Subject
public interface GameEngine {


  Level getCurrentLevel();

  void startLevel();

  // Hero inputs - boolean for success (possibly for sound feedback)
  boolean jump();

  boolean moveLeft();

  boolean moveRight();

  boolean stopMoving();

  long getTimeSinceStart();

  long getHeroHealth();

  void loadNextLevel(int levelNumber);

  void tick();

  /**
   * When a level is restarted we need to clean out the old entity views, this is how we signal to
   * the
   *
   * @return true if the game engine needs the entities refreshed
   */
  boolean needsRefresh();

  /** After a refresh is completed, the clean method must be called to reset the flag */
  void clean();

  String getHeadsUpDisplayMessage();



  Memento save ();

  void restore (Memento memento);

  long getLevelScore();

  long getTotalScore();
}
