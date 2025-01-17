package com.marbro.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.marbro.MainGame;

import java.util.ArrayList;

import static com.marbro.MainGame.usuario;

public class MenuScreen implements Screen {

    private MainGame game;
    private Stage stage;
    private ArrayList<TextButton> buttons;
    private ArrayList<String> buttonNames;
    private OrthographicCamera camera;
    private Texture fondo;
    private SpriteBatch sb;
    private Label label;


    public MenuScreen(MainGame game) {
        this.game = game;
        initializeCameraAndStage();
        initializeSkinAndButtonNames();
        createAndAddButtonsToStage();
        fondo = new Texture(Gdx.files.internal("fondos/fondo_menu.jpg"));
        sb = new SpriteBatch();
    }

    private void initializeCameraAndStage() {
        camera = new OrthographicCamera();
        stage = new Stage(new ScreenViewport(camera));
    }

    private void initializeSkinAndButtonNames() {
        buttons = new ArrayList<>();
        buttonNames = new ArrayList<>();
        buttonNames.add("Jugar");
        buttonNames.add("Ayuda");
        buttonNames.add("Acerca de");
        buttonNames.add("Salir");
    }

    private void createAndAddButtonsToStage() {
        float buttonXPosition = Gdx.graphics.getWidth() / 2f;
        float buttonYPosition = Gdx.graphics.getHeight() / 2f;

        for (String name : buttonNames) {
            TextButton button = createButton(name, 0);
            button.setPosition(buttonXPosition, buttonYPosition);
            buttonYPosition -= 100; // Ajuste para la posición vertical de los botones
            button.setSize(200, 50); // Ajuste el tamaño de los botones
            buttons.add(button);
            stage.addActor(button);
        }

        TextButton button = createButton("Ranking", 0);
        button.setPosition(50, 50);
        button.setSize(100, 50);
        stage.addActor(button);

        // Crear y agregar una etiqueta para mostrar el nombre del jugador
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font");
        label = new Label("Jugador: " + usuario, labelStyle);
        label.setPosition(50, Gdx.graphics.getHeight() - 50); // Posicionar la etiqueta

        stage.addActor(label);
    }

    private TextButton createButton(String name, float yPosition) {
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        TextButton button = new TextButton(name, skin);
        button.setPosition(Gdx.graphics.getWidth() / 2f - button.getWidth() / 2f, yPosition);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                switch (name) {
                    case "Jugar":
                        game.setScreen(MainGame.getLevel1());
                        break;
                    case "Ayuda":
                        game.setScreen(MainGame.getHelpScreen());
                        break;
                    case "Acerca de":
                        game.setScreen(MainGame.getAboutScreen());
                        break;
                    case "Ranking":
                        game.setScreen(MainGame.getRankingScreen());
                        break;
                    case "Salir":
                        Gdx.app.exit();
                        break;
                }
            }

        });
        return button;


    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sb.begin();
        sb.draw(fondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sb.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        sb.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void updatePlayerNameLabel() {
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        labelStyle.font = skin.getFont("default-font");

        if (label != null) {
            label.remove();
        }

        label = new Label("Jugador: " + usuario, labelStyle);
        label.setPosition(50, Gdx.graphics.getHeight() - 50); // Posicionar la etiqueta
        stage.addActor(label);
    }

}
