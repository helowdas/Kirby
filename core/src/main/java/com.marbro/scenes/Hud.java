package com.marbro.scenes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.marbro.screens.level1.Level1;

public class Hud {
    //Camara y escenario
    public Stage stage;
    private Viewport viewport;

    //Atributos del mundo
    private Integer worldTimer;
    private float timeCount;
    private int score;

    //Screen
    private Level1 screen;

    Label countDownLabel;
    Label scoreLabel;
    Label timeLabel;
    Label levelLabel;
    Label worldLabel;
    Label kirbyLabel;

    // Hud.java

    private int playerLives;
    Label livesLabel;

    public Hud(SpriteBatch sb, Level1 screen) {
        // Inicialización de variables
        worldTimer = 3;
        timeCount = 0;
        score = 0;
        playerLives = 5; // Ejemplo de salud

        viewport = new FitViewport(800, 600, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        // Creación de etiquetas
        countDownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.BLACK));
        scoreLabel = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.BLACK));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.BLACK));
        levelLabel = new Label("LEVEL", new Label.LabelStyle(new BitmapFont(), Color.BLACK));
        worldLabel = new Label("WORLD", new Label.LabelStyle(new BitmapFont(), Color.BLACK));
        kirbyLabel = new Label("JUGADOR", new Label.LabelStyle(new BitmapFont(), Color.BLACK));
        livesLabel = new Label(String.format("Salud: %01d", playerLives), new Label.LabelStyle(new BitmapFont(), Color.BLACK));

        // Añadir etiquetas a la tabla
        table.add(kirbyLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row();
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countDownLabel).expandX();
        table.row();
        table.add(livesLabel).expandX().colspan(3).bottom().padBottom(10);

        stage.addActor(table);

        this.screen = screen;
    }

    // Agrega estos métodos a la clase Hud

    public void update(float dt) {
        timeCount += dt;
        if(timeCount >= 1) {
            worldTimer--;
            countDownLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }
    }

    public void addScore(int value) {
        score += value;
        scoreLabel.setText(String.format("%06d", score));
    }

    public void loseLife() {
        playerLives--;
        livesLabel.setText(String.format("Lives: %01d", playerLives));
    }

    public int getTime(){
        return worldTimer;
    }


}
