package stickman.model.levels;

import java.util.List;

import stickman.model.Memento;
import stickman.model.config.ConfigParser;
import stickman.model.entities.Entity;
import stickman.model.entities.MovableEntity;
import stickman.model.entities.character.Hero;

public interface Level {
  List<Entity> getEntities();

  List<Entity> getStaticEntities();

  List<MovableEntity> getDynamicEntities();

  double getHeight();

  double getWidth();

  void tick();

  double getFloorHeight();

  Hero getHero();

  void addStaticEntity(Entity entity);

  void addDynamicEntity(MovableEntity movableEntity);

  void addHero(Hero hero);

  void setHero(Hero hero);

  long getTargetTime ();

  Memento save ();

  void setConfigParser (ConfigParser configParser);

  ConfigParser getConfigParser ();

  void setLevelId (int id);

  int getLevelId ();
}