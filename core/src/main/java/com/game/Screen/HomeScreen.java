package com.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.Main;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.Pixmap;

public class HomeScreen implements Screen {
    private Main game;
    private Texture bg;
    private OrthographicCamera cam;
    private Viewport vp;
    private Stage stage;
    private BitmapFont font;

    public HomeScreen(final Main game) {
        this.game = game;
        bg = new Texture("angryBird-2.jpg");
        cam = new OrthographicCamera();
        vp = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT, cam);
        stage = new Stage(vp, game.batch);
        font = new BitmapFont();
        font.getData().setScale(2.0f);

        // Create a TextButtonStyle
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;

        // Create button backgrounds using rounded rectangles
        buttonStyle.up = createRoundedDrawable(Color.BLACK);
        buttonStyle.down = createRoundedDrawable(Color.DARK_GRAY);
        buttonStyle.over = createRoundedDrawable(Color.LIGHT_GRAY);

        // Create buttons with white text
        TextButton playButton = new TextButton("PLAY", buttonStyle);
        TextButton settingsButton = new TextButton("SETTINGS", buttonStyle);
        TextButton quitButton = new TextButton("QUIT", buttonStyle);

        // Set text color to white
        playButton.getLabel().setColor(Color.WHITE);
        settingsButton.getLabel().setColor(Color.WHITE);
        quitButton.getLabel().setColor(Color.WHITE);

        // Adding ClickListeners for buttons
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                game.setScreen(new PlayScreen(game));
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                game.setScreen(new SettingsScreen(game));
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Aligning table with buttons
        Table table = new Table();
        table.bottom();
        table.setFillParent(true);
        // Adjust padding to make buttons closer together
        table.add(playButton).pad(5); // Reduced padding
        table.row();
        table.add(settingsButton).pad(5); // Reduced padding
        table.row();
        table.add(quitButton).pad(5); // Reduced padding

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    private TextureRegionDrawable createRoundedDrawable(Color color) {
        int width = 160; // Reduced width of the button
        int height = 40; // Reduced height of the button
        int radius = 10; // Radius for rounded corners

        // Create a Pixmap for the button background
        Pixmap roundedPixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        roundedPixmap.setColor(color);
        roundedPixmap.fill();

        // Draw rounded corners
        roundedPixmap.setColor(color);
        roundedPixmap.fillCircle(radius, radius, radius); // Top-left
        roundedPixmap.fillCircle(width - radius, radius, radius); // Top-right
        roundedPixmap.fillCircle(radius, height - radius, radius); // Bottom-left
        roundedPixmap.fillCircle(width - radius, height - radius, radius); // Bottom-right

        // Draw the edges
        roundedPixmap.fillRectangle(radius, 0, width - 2 * radius , height); // Middle section
        roundedPixmap.fillRectangle(0, radius, width, height - 2 * radius); // Middle section

        Texture texture = new Texture(roundedPixmap);
        roundedPixmap.dispose(); // Dispose of the pixmap to free memory

        return new TextureRegionDrawable(texture);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1); //setting bg color to black
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); //clearing color buffer

        //implementing spriteBatch
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
        stage.dispose();
        font.dispose();
    }
}
