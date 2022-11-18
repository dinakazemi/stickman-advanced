package stickman.model;

import stickman.model.entities.Entity;
import stickman.model.entities.MovableEntity;
import stickman.model.entities.StaticEntity;
import stickman.model.entities.character.Hero;
import stickman.model.levels.EmptyLevel;
import stickman.model.levels.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Using Memento design pattern and Prototype. Recreates the current state of the level when the game is saved
 * and stores it.
 */
public class Memento {
    private Level level;
    //private Level savedLevel;

    public Memento (Level level){
        //rebuilding the level
        //this.savedLevel = level;
        this.level = new EmptyLevel(level.getConfigParser());

        List<MovableEntity> dynamic = new ArrayList<>();
        for (MovableEntity e: level.getDynamicEntities()){
            dynamic.add(e);
        }
        dynamic.remove(level.getHero());

        for (Entity e:dynamic){
            this.level.addDynamicEntity((MovableEntity) e.clone());
        }
        for (Entity e:level.getStaticEntities()){
            this.level.addStaticEntity((StaticEntity)e.clone());
        }
        //System.out.println(h == null);
        this.level.setLevelId(level.getLevelId());
//        this.level.getDynamicEntities().remove(level.getHero());
//        this.level.getEntities().remove(level.getHero());
//        this.level.getStaticEntities().remove(level.getHero());
        this.level.addHero(level.getHero().clone());
//        for (Entity e:this.level.getEntities()){
//            if (e instanceof Hero){
//                System.out.println("yes");
//            }
//        }
    }

    /**
     * Returns the recreated level
     * @return The level recreated in the constructor.
     */
    public Level restore(Level currentLevel) {
        //this.level.getHero().getHeroAnimator().setMovableEntity(this.level.getHero());
        //this.level.getHero().stopMoving();
        return level;
    }


}
