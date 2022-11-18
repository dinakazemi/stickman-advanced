package stickman.view;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import stickman.model.GameEngine;
import stickman.model.entities.character.Subject;

public class HeadsUpDisplayDrawer implements ForegroundDrawer {
  private static final double X_PADDING = 50;
  private static final double Y_PADDING = 5;
  private Pane pane;
  //private GameEngine model;
  private Label timeElapsed = new Label();
  private Label health = new Label();
  private Label message = new Label();
  private Label levelScore = new Label();
  private Label totalScore = new Label();
  private Subject subject;

  @Override
  public void draw(Pane pane) {
    //this.model = model;
    this.pane = pane;
    //update();
    timeElapsed.setFont(new Font("Monospaced Regular", 20));
    health.setFont(new Font("Monospaced Regular", 20));
    message.setFont(new Font("Monospaced Regular", 20));
    levelScore.setFont(new Font("Monospaced Regular", 20));
    totalScore.setFont(new Font("Monospaced Regular", 20));

    this.pane.getChildren().addAll(timeElapsed, health, message, levelScore, totalScore);
  }

  @Override
  public void update(GameEngine model) {
    // Set the positions of the labels
    timeElapsed.setLayoutX(X_PADDING / 2);
    timeElapsed.setLayoutY(Y_PADDING);

    health.setLayoutX(timeElapsed.getLayoutX() + X_PADDING + timeElapsed.getWidth());
    health.setLayoutY(Y_PADDING);

    levelScore.setLayoutX(health.getLayoutX() + X_PADDING + health.getWidth());
    levelScore.setLayoutY(Y_PADDING);

    totalScore.setLayoutX(levelScore.getLayoutX() + X_PADDING + levelScore.getWidth());
    totalScore.setLayoutY(Y_PADDING);
    //System.out.println(totalScore.getLayoutX());

    message.setLayoutX(pane.getWidth() / 6);
    message.setLayoutY(pane.getHeight() / 4);

    // Set the text
    timeElapsed.setText(String.format("TIME%n %03d", model.getTimeSinceStart()/ 1000));
    health.setText(String.format("HEALTH%n   %03d", model.getHeroHealth()));
    //System.out.println(model.getHeroHealth());
    levelScore.setText(String.format("CURRENT SCORE%n   %03d", model.getLevelScore()));
    totalScore.setText(String.format("TOTAL SCORE%n   %03d", model.getTotalScore()));
    message.setText(model.getHeadsUpDisplayMessage());
  }



}
