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
    private Sound clickSound;

    public HomeScreen(final Main game) {
        this.game = game;
        bg = new Texture("angryBird-2.jpg");
        cam = new OrthographicCamera();
        vp = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT, cam);
        stage = new Stage(vp, game.batch);
        font = new BitmapFont();
        font.getData().setScale(2.0f);

        clickSound = Gdx.audio.newSound(Gdx.files.internal("click.mp3"));

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;

        buttonStyle.up = createRoundedDrawable(Color.BLACK);
        buttonStyle.down = createRoundedDrawable(Color.DARK_GRAY);
        buttonStyle.over = createRoundedDrawable(Color.LIGHT_GRAY);

        TextButton playButton = new TextButton("PLAY", buttonStyle);
        TextButton settingsButton = new TextButton("SETTINGS", buttonStyle);
        TextButton quitButton = new TextButton("QUIT", buttonStyle);

        playButton.getLabel().setColor(Color.WHITE);
        settingsButton.getLabel().setColor(Color.WHITE);
        quitButton.getLabel().setColor(Color.WHITE);

        // Adding ClickListeners for buttons
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                clickSound.play();
                game.setScreen(new PlayScreen(game));
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                clickSound.play();
                game.setScreen(new SettingsScreen(game));
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                clickSound.play();
                Gdx.app.exit();
            }
        });

        Table table = new Table();
        table.bottom();
        table.setFillParent(true);
        table.add(playButton).pad(5);
        table.row();
        table.add(settingsButton).pad(5);
        table.row();
        table.add(quitButton).pad(5); // Reduced padding

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    private TextureRegionDrawable createRoundedDrawable(Color color) {
        int width = 160;
        int height = 40;
        int radius = 10;

        Pixmap roundedPixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        roundedPixmap.setColor(color);
        roundedPixmap.fill();

        roundedPixmap.setColor(color);

        Texture texture = new Texture(roundedPixmap);
        roundedPixmap.dispose();

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
        clickSound.dispose();
    }
}
