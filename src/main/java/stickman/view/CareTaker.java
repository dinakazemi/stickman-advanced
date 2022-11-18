package stickman.view;

import stickman.model.Memento;

/**
 * Using Memento desgin pattern.
 */
public class CareTaker {

    private Memento memento;

    public void setMemento (Memento memento){
        this.memento = memento;
    }

    public Memento get(){
        return memento;
    }

}
