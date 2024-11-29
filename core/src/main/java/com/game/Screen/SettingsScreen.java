package com.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.Main;

public class SettingsScreen implements Screen {
    private Main game;
    private Texture bg;
    private OrthographicCamera cam;
    private Viewport vp;
    private Stage stage;
    private BitmapFont font;
    private boolean flag = false;
    private Label volumeLabel;
    private Sound clickSound;

    public SettingsScreen(final Main game) {
        this.game = game;
        bg = new Texture("setting.jpg");
        cam = new OrthographicCamera();
        vp = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT, cam);
        stage = new Stage(vp, game.batch);
        font = new BitmapFont();
        font.getData().setScale(2.0f);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        clickSound = Gdx.audio.newSound(Gdx.files.internal("click.mp3"));
        Label.LabelStyle ls = new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.WHITE);

        Label termsLabel = new Label("T&C", ls);
        volumeLabel = new Label("VOLUME: off", ls);
        Label backLabel = new Label("EXIT", ls);

        addHoverListener(termsLabel);
        termsLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                game.setScreen(new TermsPrivacyScreen(game));
            }
        });

        addHoverListener(volumeLabel);
        volumeLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                toggleVolume();
            }
        });

        addHoverListener(backLabel);
        backLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                game.setScreen(new HomeScreen(game));
            }
        });

        table.add(termsLabel).padBottom(20).row();
        table.add(volumeLabel).padBottom(20).row();
        table.add(backLabel);

        stage.addActor(table);
    }

    private void toggleVolume() {
        flag = !flag;
        if (flag) {
            volumeLabel.setText("Volume: On");
            game.bgm.setVolume(1.0f);
            if (!game.bgm.isPlaying()) {
                game.bgm.play();
            }
        } else {
            volumeLabel.setText("Volume: Off");
            game.bgm.setVolume(0.0f);
            game.bgm.stop();
        }
    }

    private void addHoverListener(Label label) {
        label.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                label.setColor(com.badlogic.gdx.graphics.Color.RED); // Change color on click
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                label.setColor(com.badlogic.gdx.graphics.Color.GREEN); // Change color on hover
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                label.setColor(com.badlogic.gdx.graphics.Color.WHITE); // Reset color on exit
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        if (game.bgm.isPlaying()) {
            flag = true;
            volumeLabel.setText("Volume: On");
        } else {
            flag = false;
            volumeLabel.setText("Volume: Off");
        }
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
