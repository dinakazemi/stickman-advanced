package stickman.view;

import javafx.scene.layout.Pane;
import stickman.model.GameEngine;

public interface ForegroundDrawer{

  void draw( Pane pane);

  void update(GameEngine model);


}
