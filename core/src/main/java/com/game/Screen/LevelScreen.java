package com.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.Main;
import com.game.Screen.LevelOne;
import com.game.Screen.LevelThree;
import com.game.Screen.LevelTwo;

public class LevelScreen implements Screen {
    private static final float BUTTON_RADIUS = 40;

    private Main game;
    private Texture mapTexture;
    private SpriteBatch batch;
    private Stage stage;
    private ShapeRenderer shapeRenderer;
    private Table buttonTable;
    private Viewport viewport;
    private TextButton level1, level2, level3;
    private BitmapFont font;

    public LevelScreen(Main game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.viewport = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT);
        this.stage = new Stage(viewport, batch);
        this.mapTexture = new Texture("background-2.jpg");

        createButtons();
        Gdx.input.setInputProcessor(stage);
    }

    private void createButtons() {
        font = new BitmapFont();
        font.getData().setScale(2);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.YELLOW;  // Color when hovered

        level1 = new TextButton("1", style);
        level2 = new TextButton("2", style);
        level3 = new TextButton("3", style);

        // Position buttons in triangle formation
        level1.setPosition(Main.V_WIDTH/2 - 20, Main.V_HEIGHT/2 + 100);  // Higher
        level2.setPosition(Main.V_WIDTH/2 - BUTTON_RADIUS - 120, Main.V_HEIGHT/2 - 80);  // Further left
        level3.setPosition(Main.V_WIDTH/2 + BUTTON_RADIUS + 80, Main.V_HEIGHT/2 - 80);   // Further right

        level1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LevelOne(game));
            }
        });

        level2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LevelTwo(game));
            }
        });

        level3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LevelThree(game));
            }
        });

        stage.addActor(level1);
        stage.addActor(level2);
        stage.addActor(level3);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw map background
        batch.begin();
        batch.draw(mapTexture, 0, 0, Main.V_WIDTH, Main.V_HEIGHT);
        batch.end();

        // Draw circles behind buttons
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        // Draw circle for each button
        shapeRenderer.setColor(0, 0, 0, 0.5f);  // Semi-transparent black
        shapeRenderer.circle(level1.getX() + 20, level1.getY() + 20, BUTTON_RADIUS);
        shapeRenderer.circle(level2.getX() + 20, level2.getY() + 20, BUTTON_RADIUS);
        shapeRenderer.circle(level3.getX() + 20, level3.getY() + 20, BUTTON_RADIUS);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Draw stage (buttons and text)
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        mapTexture.dispose();
        batch.dispose();
        stage.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }

    // Other required Screen methods
    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
