package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
    private Texture background;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;
    private BitmapFont font;
    private Screen previousScreen; // To store the screen to return to when resuming

    public PauseScreen(final Main game, final Screen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;
        background = new Texture("pauseScreen.jpeg");
        camera = new OrthographicCamera();
        viewport = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
        font = new BitmapFont();

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.WHITE);

        // Create labels
        Label resumeLabel = new Label("RESUME", labelStyle);
        Label quitGameLabel = new Label("QUIT GAME", labelStyle);
        Label quitAndSaveLabel = new Label("QUIT AND SAVE", labelStyle);

        // Add click listeners
        resumeLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Return to the previous screen
                game.setScreen(previousScreen);
            }
        });

        quitGameLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Exit the game
                Gdx.app.exit();
            }
        });

        quitAndSaveLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: Implement save game functionality
                // For now, just return to home screen
                game.setScreen(new HomeScreen(game));
            }
        });

        // Add labels to table with spacing
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

        game.batch.begin();
        game.batch.draw(background, 0, 0, Main.V_WIDTH, Main.V_HEIGHT);
        game.batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        background.dispose();
        stage.dispose();
        font.dispose();
    }
}
