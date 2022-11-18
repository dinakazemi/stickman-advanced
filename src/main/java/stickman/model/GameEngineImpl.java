package stickman.model;

import java.awt.desktop.SystemEventListener;
import java.io.IOException;
import java.util.List;



import org.json.JSONException;
import stickman.model.config.ConfigParser;
import stickman.model.config.JsonConfigParser;
import stickman.model.entities.Entity;
import stickman.model.entities.MovableEntity;
import stickman.model.entities.character.Hero;
import stickman.model.entities.character.Subject;
import stickman.model.levels.Level;
import stickman.model.levels.LevelBuilder;
import stickman.view.Observer;
import stickman.view.SoundPlayer;

import static java.time.Instant.ofEpochMilli;

/** An implementation of the Game Engine interface, allows control of the player */
public class GameEngineImpl implements GameEngine, Observer {
  private Level currentLevel;
  private ConfigParser config;
  private String headsUpDisplayMessage = "";
  private boolean needsRefresh = false;
  private boolean gameFinished = false;
  private SoundPlayer soundPlayer = new SoundPlayer();
  private Subject subject;
  private boolean gameRestored;
  private long startTime;
  private long endTime;
  private long restoredMoment = 0;
  private long savedSnapshot = 0;
  private long totalScore = 0;
  private long levelScore;
  private long savedTotalScore;

  public GameEngineImpl(ConfigParser config) {
    this.config = config;
  }

  @Override
  public Level getCurrentLevel() {
    return this.currentLevel;
  }

  @Override
  public void startLevel() {

    long restoredTotalScore = 0;

    if (currentLevel != null) {
      // If we're restarting delete all the entities, so they're no longer rendered
      restoredTotalScore = totalScore;
      currentLevel.getEntities().forEach(Entity::delete);
      needsRefresh = true;
    }

    currentLevel = LevelBuilder.fromConfig(config).build();
    setSubject(currentLevel.getHero());
    currentLevel.getHero().addObserver(this);
    levelScore = currentLevel.getTargetTime();

    startTime = System.currentTimeMillis();
    totalScore = currentLevel.getTargetTime();
    totalScore = (restoredTotalScore);
    //System.out.println(currentLevel.getTargetTime());
  }

  /**
   * Loads the next level when hero completes a level.
   * @param levelNumber a counter indicating the which level (ie which configuration file) to load.
   */
  @Override
  public void loadNextLevel(int levelNumber){
    //level transition after the character finishes a level

    String configPath = "./level_" + levelNumber + ".json";
    currentLevel.getEntities().forEach(Entity::delete);
    needsRefresh = true;

    try{
      config = new JsonConfigParser(configPath);
    } catch (IOException e) {
      System.err.println("IO error when attempting to read configuration");
      e.printStackTrace();
      System.exit(1);
    } catch (JSONException e) {
      System.err.println(
              "Configuration is not well formed, please refer to example.json for an "
                      + "example of a well formed configuration");
      e.printStackTrace();
      System.exit(2);
    }
  }

  @Override
  public boolean jump() {
    if (currentLevel.getHero().jump()) {
      soundPlayer.playJumpSound();
      return true;
    }
    return false;
  }

  @Override
  public boolean moveLeft() {
    return currentLevel.getHero().moveLeft();
  }

  @Override
  public boolean moveRight() {
    return currentLevel.getHero().moveRight();
  }

  @Override
  public boolean stopMoving() {
    return currentLevel.getHero().stopMoving();
  }

  @Override
  public long getTimeSinceStart() {
    if (gameFinished) {
      return endTime - startTime;
    }

    if (gameRestored){
      gameRestored = false;
      startTime = restoredMoment - savedSnapshot;
      return savedSnapshot + System.currentTimeMillis() - restoredMoment;
    }

    return System.currentTimeMillis()-startTime;
  }

  @Override
  public long getHeroHealth() {
    if (currentLevel.getHero() != null) {
      return currentLevel.getHero().getHealth();
    }
    return 0;
  }

  private void updateEntities() {
    // Collect the entities from the current level
    List<Entity> staticEntities = currentLevel.getStaticEntities();
    List<MovableEntity> dynamicEntities = currentLevel.getDynamicEntities();

    // Remove any dead entities
    staticEntities.removeIf(Entity::isDeleted);
    dynamicEntities.removeIf(Entity::isDeleted);

    // Move everything that can move
    for (MovableEntity a : dynamicEntities) {
      a.moveTick();
    }

    // Check for collisions
    for (MovableEntity a : dynamicEntities) {
      for (Entity b : currentLevel.getEntities()) {
        if (a != b && a.overlappingSameLayer(b)) {
          b.handleCollision(a);
          // Only do one collision at a time
          break;
        }
      }
    }
  }

  private void updateState() {

    Hero hero = currentLevel.getHero();
    subject.postUpdate();
    // Check if we need to change state based on the hero
    if (hero.isFinished()) {
      //hero.setTotalScore(hero.getTotalScore() + hero.getCurrentScore());
      //levelCounter ++;
      //System.out.println(currentLevel.getHero().getTotalScore()/1000);
//      currentLevel.getEntities().forEach(Entity::delete);
//      needsRefresh = true;
      if (currentLevel.getLevelId() == 2){
        headsUpDisplayMessage = "WINNER!\nLEVEL SCORE: "+ levelScore + "\nTOTAL SCORE: " + totalScore;
        startTime = 0;
        endTime = 0;
        gameFinished = true;
      }
      else{
        headsUpDisplayMessage = "LEVEL COMPLETED IN " + getTimeSinceStart()/1000 + " SECONDS.\nLEVEL "+ currentLevel.getLevelId() + 1 +" LOADED!!\nLEVEL SCORE: "+ levelScore + "\n" + "TOTAL SCORE: " + totalScore;
        endTime = System.currentTimeMillis();
        this.loadNextLevel(currentLevel.getLevelId() + 1);
        this.startLevel();
      }

    } else if (hero.isDeleted()) {
      headsUpDisplayMessage = "YOU LOST: TRY AGAIN!";
      this.startLevel();
    } else if (headsUpDisplayMessage != null && hero.getXVelocity() != 0) {
      headsUpDisplayMessage = null;
    }

  }

  @Override
  public void tick() {
    // Don't update anything once we've completed the game
    if (gameFinished) {
      //once a level is finished, update the hero's total score with hero's score in this level.
      //currentLevel.getHero().setTotalScore(currentLevel.getHero().getTotalScore() + currentLevel.getHero().getCurrentScore());
      return;
    }

    updateEntities();
    updateState();

    // Make the level tick if it has anything to do
    this.currentLevel.tick();
  }

  @Override
  public boolean needsRefresh() {
    return needsRefresh;
  }

  @Override
  public void clean() {
    needsRefresh = false;
  }

  public String getHeadsUpDisplayMessage() {
    return headsUpDisplayMessage;
  }


  /**
   * Called when the game is saved
   * @return Returns the saved version of the game
   */
  @Override
  public Memento save (){
    savedSnapshot = System.currentTimeMillis() - startTime;
    savedTotalScore = totalScore;
    return currentLevel.save();
  }

  /**
   * Called when the saved game is restored.
   * @param memento Receives the saved version of the game
   */
  @Override
  public void restore (Memento memento){
    currentLevel.getEntities().forEach(Entity::delete);
    needsRefresh = true;
    currentLevel = memento.restore(currentLevel);
    setSubject(currentLevel.getHero());
    currentLevel.getHero().addObserver(this);
    totalScore = savedTotalScore;
    this.config = currentLevel.getConfigParser();
    gameRestored = true;
    restoredMoment = System.currentTimeMillis();
  }

  @Override
  public void update(){
    levelScore = subject.getState().getEnemiesKilled () * 100 + currentLevel.getTargetTime() - getTimeSinceStart()/1000;
    if (levelScore <= 0){
      levelScore = 0;
    }
    if (currentLevel.getHero().isFinished()){
      totalScore += levelScore;
    }
    //System.out.println(levelScore);
  }

  @Override
  public void setSubject (Subject subject){
    this.subject = subject;
  }

  @Override
  public long getLevelScore (){
    return levelScore;
  }

  @Override
  public long getTotalScore (){
    return totalScore;
  }

}

