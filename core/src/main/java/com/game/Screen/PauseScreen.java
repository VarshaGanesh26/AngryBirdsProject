package com.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.Main;

public class PauseScreen implements Screen {
    private Main game;
    private Texture bg;
    private OrthographicCamera cam;
    private Viewport vp;
    private Stage stage;
    private BitmapFont font;
    private Screen previousScreen; // Generic Screen reference instead of specific level
    private Sound clickSound;

    public PauseScreen(final Main game, Screen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;
        bg = new Texture("background.jpg");
        cam = new OrthographicCamera();
        vp = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT, cam);
        stage = new Stage(vp, game.batch);
        font = new BitmapFont();
        font.getData().setScale(2.0f);

        clickSound = Gdx.audio.newSound(Gdx.files.internal("click.mp3"));

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label.LabelStyle ls = new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.WHITE);

        Label resumeLabel = new Label("RESUME", ls);
        Label quitGameLabel = new Label("QUIT GAME", ls);
        Label quitAndSaveLabel = new Label("QUIT AND SAVE", ls);

        resumeLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                game.setScreen(previousScreen);
            }
        });

        quitGameLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                game.setScreen(new HomeScreen(game));
            }
        });

        quitAndSaveLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();

                GameState state = null;
                if (previousScreen instanceof LevelOne) {
                    state = ((LevelOne) previousScreen).captureGameState();
                } else if (previousScreen instanceof LevelTwo) {
                    state = ((LevelTwo) previousScreen).captureGameState();
                }

                if (state != null) {
                    GameState.saveGame(state, "savegame.dat");
                }

                game.setScreen(new HomeScreen(game));
            }
        });



        table.add(resumeLabel).padBottom(20).row();
        table.add(quitGameLabel).padBottom(20).row();
        table.add(quitAndSaveLabel);

        stage.addActor(table);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin(); //initializing batch
        game.batch.draw(bg, 0, 0, Main.V_WIDTH, Main.V_HEIGHT); //drawing texture
        game.batch.end();

        //rendering stage
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        vp.update(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        //dispose resources
        bg.dispose();
        stage.dispose();
        font.dispose();
        clickSound.dispose();
    }
}
