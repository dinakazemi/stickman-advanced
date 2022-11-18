package stickman.model.levels;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import stickman.model.Memento;
import stickman.model.config.ConfigParser;
import stickman.model.config.LevelConfig;
import stickman.model.config.Position;
import stickman.model.entities.Entity;
import stickman.model.entities.MovableEntity;
import stickman.model.entities.character.Hero;
import stickman.model.entities.character.movementStrategies.MovementStrategy;
import stickman.view.Observer;
import stickman.model.entities.platforms.LogPlatform;

/** An empty level, used as the basis for all other levels */
public class EmptyLevel implements Level{

  private double floorHeight;
  private double width;
  private double height;
  private Hero hero = null;
  private List<Entity> staticEntities = new ArrayList<Entity>();
  private List<MovableEntity> dynamicEntities = new ArrayList<MovableEntity>();
  private long targetTime;
  private Observer observer;
  private ConfigParser configParser; //added this for save method
  private int levelId;

  public EmptyLevel(ConfigParser configParser) {
    LevelConfig level = configParser.getLevel();
    this.floorHeight = level.getFloorHeight();
    this.height = level.getHeight();
    this.width = level.getWidth();
    this.targetTime = level.getTargetTime();
    this.configParser = configParser;
    this.levelId = level.getLevelId();
  }

//  //added for save method
//  public EmptyLevel (Level emptyLevel){
//    this.floorHeight = emptyLevel.getFloorHeight();
//    this.height = emptyLevel.getHeight();
//    this.width = emptyLevel.getWidth();
//    this.targetTime = emptyLevel.getTargetTime();
//    this.configParser = emptyLevel.getConfigParser();
//  }

  @Override
  public void setHero(Hero hero){
    this.hero = hero;

  }

  @Override
  public void setLevelId (int id){
    levelId = id;
  }

  @Override
  public int getLevelId (){ return levelId; }

  public Hero getHero() { return hero; }

  @Override
  public long getTargetTime() {
    return targetTime;
  }

  @Override
  public List<Entity> getEntities() {
    // There are no static entities yet, but later there might be!
    return Stream.concat(staticEntities.stream(), dynamicEntities.stream())
        .collect(Collectors.toList());
  }

  @Override
  public List<Entity> getStaticEntities() {
    return staticEntities;
  }

  @Override
  public List<MovableEntity> getDynamicEntities() {
    return dynamicEntities;
  }

  @Override
  public double getHeight() {
    return height;
  }

  @Override
  public double getWidth() {
    return width;
  }

  @Override
  public void tick() {

    // Ensure nothing falls into the ground with an ad-hoc floor entity
    Position<Double> floorPosition = new Position<Double>(0.0, getFloorHeight());
    LogPlatform floor = new LogPlatform(floorPosition);
    for (MovableEntity a : dynamicEntities) {
      if (a.getYPos() + a.getHeight() > getFloorHeight()) {
        a.feedbackOnTop(floor);
      }
    }
  }

  @Override
  public double getFloorHeight() {
    return floorHeight;
  }

  @Override
  public void addStaticEntity(Entity entity) {
    this.staticEntities.add(entity);
  }

  @Override
  public void addDynamicEntity(MovableEntity entity) {
    this.dynamicEntities.add(entity);
  }

  @Override
  public void addHero(Hero hero) {
    if (this.hero != null) {
      throw new Error("Hero already set for the level");
    }
    this.hero = hero;
    this.dynamicEntities.add(hero);
  }

  //save method for Memento
  @Override
  public Memento save (){
    return new Memento(this);
  }

  //added for save to work
  @Override
  public void setConfigParser (ConfigParser configParser){
    this.configParser = configParser;
  }

  @Override
  public ConfigParser getConfigParser (){ return configParser;}

}
