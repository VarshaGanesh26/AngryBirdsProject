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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.Main;

public class LoseScreen implements Screen {
    private static final float BUTTON_WIDTH = 160;
    private static final float BUTTON_HEIGHT = 60;
    private static final Color BUTTON_COLOR = new Color(0.5f, 0, 0, 0.8f);

    private Main game;
    private Texture bgTexture;
    private SpriteBatch batch;
    private Stage stage;
    private ShapeRenderer shapeRenderer;
    private TextButton homeBtn, retryBtn;
    private Viewport viewport;
    private BitmapFont font;
    private final Screen previousScreen;
    private final int currentLevel;

    public LoseScreen(Main game, Screen previousScreen, int currentLevel) {
        this.game = game;
        this.previousScreen = previousScreen;
        this.currentLevel = currentLevel;
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.viewport = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT);
        this.stage = new Stage(viewport, batch);
        this.bgTexture = new Texture("lose.png");

        createButtons();
        Gdx.input.setInputProcessor(stage);
    }

    private void createButtons() {
        font = new BitmapFont();
        font.getData().setScale(1.5f);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.BLACK;
        style.overFontColor = Color.WHITE;

        homeBtn = new TextButton("Home", style);
        retryBtn = new TextButton("Try Again", style);

        // Calculate center positions
        float centerY = Main.V_HEIGHT / 2;
        float totalWidth = (BUTTON_WIDTH * 2) + 40; // Width of two buttons plus spacing
        float startX = (Main.V_WIDTH - totalWidth) / 2;

        // Position buttons
        homeBtn.setBounds(startX, centerY - BUTTON_HEIGHT/2, BUTTON_WIDTH, BUTTON_HEIGHT);
        retryBtn.setBounds(startX + BUTTON_WIDTH + 40, centerY - BUTTON_HEIGHT/2, BUTTON_WIDTH, BUTTON_HEIGHT);

        homeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new HomeScreen(game));
            }
        });

        retryBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                switch(currentLevel) {
                    case 1: game.setScreen(new LevelOne(game)); break;
                    case 2: game.setScreen(new LevelTwo(game)); break;
                    case 3: game.setScreen(new LevelThree(game)); break;
                }
            }
        });

        stage.addActor(homeBtn);
        stage.addActor(retryBtn);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(bgTexture, 0, 0, Main.V_WIDTH, Main.V_HEIGHT);
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.setColor(BUTTON_COLOR);

        // Draw ovals for both buttons
        for (TextButton button : new TextButton[]{homeBtn, retryBtn}) {
            float centerX = button.getX() + BUTTON_WIDTH/2;
            float centerY = button.getY() + BUTTON_HEIGHT/2;

            int segments = 30;
            for (int i = 0; i < segments; i++) {
                float angle1 = (float) (2f * Math.PI * i / segments);
                float angle2 = (float) (2f * Math.PI * (i + 1) / segments);

                float x1 = centerX + BUTTON_WIDTH/2 * (float) Math.cos(angle1);
                float y1 = centerY + BUTTON_HEIGHT/2 * (float) Math.sin(angle1);
                float x2 = centerX + BUTTON_WIDTH/2 * (float) Math.cos(angle2);
                float y2 = centerY + BUTTON_HEIGHT/2 * (float) Math.sin(angle2);

                shapeRenderer.triangle(
                    centerX, centerY,
                    x1, y1,
                    x2, y2
                );
            }
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        bgTexture.dispose();
        batch.dispose();
        stage.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
