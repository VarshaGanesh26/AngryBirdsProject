package com.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
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

public class PlayScreen implements Screen {
    private Main game;
    private Texture bg;
    private OrthographicCamera cam;
    private Viewport vp;
    private Stage stage;
    private BitmapFont font;
    private Sound clickSound;

    public PlayScreen(final Main game) {
        this.game = game;
        bg = new Texture("bg.jpg");
        cam = new OrthographicCamera();
        vp = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT, cam);
        stage = new Stage(vp, game.batch);
        font = new BitmapFont();
        font.getData().setScale(2.0f);
        clickSound = Gdx.audio.newSound(Gdx.files.internal("click.mp3"));

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label.LabelStyle ls = new Label.LabelStyle(font, Color.WHITE);

        Label newGameLabel = new Label("NEW GAME", ls);
        Label savedGameLabel = new Label("SAVED GAME", ls);
        Label homeLabel= new Label("HOME", ls);

        newGameLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                game.setScreen(new LevelScreen(game));
            }
        });

        savedGameLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                GameState state = GameState.loadGame("savegame.dat");
                if (state != null) {
                    Screen levelScreen = new LevelOne(game);
                    if (state.currentLevel == 1) {
                        levelScreen = new LevelOne(game);
                        ((LevelOne)levelScreen).loadGameState(state);
                    } else if (state.currentLevel == 2) {
                        levelScreen = new LevelTwo(game);
                        ((LevelTwo)levelScreen).loadGameState(state);
                    }
                    game.setScreen(levelScreen);
                } else {
                    game.setScreen(new HomeScreen(game));
                }
            }
        });

        homeLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                clickSound.play();
                game.setScreen(new HomeScreen(game));
            }
        });

        table.add(newGameLabel).padBottom(20).row();
        table.add(savedGameLabel);

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

        game.batch.begin();
        game.batch.draw(bg, 0, 0, Main.V_WIDTH, Main.V_HEIGHT);
        game.batch.end();

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
        bg.dispose();
        stage.dispose();
        font.dispose();
        clickSound.dispose();
    }
}
