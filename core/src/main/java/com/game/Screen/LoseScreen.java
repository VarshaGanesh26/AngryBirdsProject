package com.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.game.Main;

public class LoseScreen implements Screen {
    private Main game;
    private Texture bg;
    private OrthographicCamera cam;
    private Viewport vp;
    private SpriteBatch sb;
    private Stage stage;
    private BitmapFont font;

    public LoseScreen(Main game) {
        this.game = game;
        this.sb = game.batch;

        //instantiating objects
        bg = new Texture("lose.png");
        cam = new OrthographicCamera();
        vp = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT, cam);
        stage = new Stage(vp, game.batch);
        font = new BitmapFont();

        //home label
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);
        Label homeLabel = new Label("HOME", labelStyle);
        float labelWidth = homeLabel.getWidth();
        float labelHeight = homeLabel.getHeight();
        homeLabel.setPosition(Main.V_WIDTH - labelWidth - 10, labelHeight + 10);

        homeLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new HomeScreen(game)); // Go to HomeScreen when clicked
            }
        });

        stage.addActor(homeLabel);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        //updating camera to be in sync with current view
        cam.update();
        sb.setProjectionMatrix(cam.combined);

        sb.begin();
        sb.draw(bg, 0, 0, Main.V_WIDTH, Main.V_HEIGHT);
        sb.end();

        //adding to stage
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        //adjusting viewport to fit screen size
        vp.update(width, height);
        cam.position.set(cam.viewportWidth / 2, cam.viewportHeight / 2, 0);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        //disposing all resources
        bg.dispose();
        stage.dispose();
        font.dispose();
    }
}
