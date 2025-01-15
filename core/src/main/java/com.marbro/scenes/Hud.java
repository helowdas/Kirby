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
import com.marbro.entities.player.Kirby;
import com.marbro.ranking.Ranking;
import com.marbro.screens.level1.Level1;

import static com.marbro.MainGame.puntuacion_archivos;

public class Hud {
    //Camara y escenario
    public Stage stage;
    private Viewport viewport;

    //Atributos del mundo
    public static Integer worldTimer;
    private float timeCount;
    private static int score;

    //Screen
    private Level1 screen;

    Label countDownLabel;
    Label timeLabel;
    Label levelLabel;
    Label worldLabel;
    Label kirbyLabel;

    public static int playerLives;
    // Hud.java

    public static Label livesLabel;
    public static Label scoreLabel;

    private Kirby kirby;

    public Hud(SpriteBatch sb, Level1 screen) {
        // Inicialización de variables
        worldTimer = 300;
        timeCount = 0;
        score = puntuacion_archivos;
        kirby = screen.getKirby();

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

    public static void setPlayerHealt(int healt) {
        playerLives = healt;
        livesLabel.setText(String.format("Lives: %01d", playerLives));

    }

    public static void resetTimer(){
        worldTimer = 300;
    }

    public static void actScore(int puntos) {
        Hud.score = Ranking.getInstance().obtenerPuntuacion();
        scoreLabel.setText(String.format("Puntos: %01d", score + puntos));
    }


}
