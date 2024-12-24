package com.marbro.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.marbro.MainGame;
import com.marbro.screens.level1.Level1;

import java.util.ArrayList;

public class MenuScreen implements Screen {

    private MainGame game;
    private Stage stage;
    private ArrayList<TextButton> buttons;
    private ArrayList<String> buttonNames;
    private TextButton play;
    private OrthographicCamera camera;
    private Texture fondo;
    private SpriteBatch sb;

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
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));
    }

    // Este metodo sirve para cargar botones
    private void initializeSkinAndButtonNames() {
        //Declarar los arreglos
        buttons = new ArrayList<>();
        buttonNames = new ArrayList<>();

        //Boton de jugar
        buttonNames.add("Jugar");

        //Boton de opciones
        buttonNames.add("Opciones");

        //Boton de salir
        buttonNames.add("Salir");
    }

    //Este metodo sirve para crear y agregar los botones a la pantalla
    private void createAndAddButtonsToStage() {
        float buttonXPosition = Gdx.graphics.getWidth() / 2f;
        float buttonYPosition = Gdx.graphics.getHeight() / 2f;

        for (String name : buttonNames) {
            TextButton button = createButton(name, 0);
            button.setPosition(buttonXPosition, buttonYPosition);
            buttonYPosition -= 100; // Ajuste para la posición vertical de los botones
            button.setSize(200, 50); // Cambia 200 y 50 a los valores de ancho y alto deseados
            buttons.add(button);
            stage.addActor(button);
        }
    }

    private TextButton createButton(String name, float yPosition) {
        Skin skin = new Skin(Gdx.files.internal("uiskin.json")); // Asegúrate de tener un skin para los botones
        TextButton button = new TextButton(name, skin);
        button.setPosition(Gdx.graphics.getWidth() / 2f - button.getWidth() / 2f, yPosition);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (name.equals("Jugar")) {
                    game.setScreen(new Level1(game)); // Cambia a la pantalla del nivel 1
                }
                if (name.equals("Salir")) {
                    Gdx.app.exit();
                }
                // Agrega más condicionales si tienes más botones con diferentes acciones
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
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
