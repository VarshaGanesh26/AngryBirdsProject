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

public class SettingsScreen implements Screen {
    private Main game;
    private Texture background;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;
    private BitmapFont font;

    public SettingsScreen(final Main game) {
        this.game = game;
        background = new Texture("setting.jpg");
        camera = new OrthographicCamera();
        viewport = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
        font = new BitmapFont();

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.WHITE);

        Label termsLabel = new Label("TERMS AND PRIVACY", labelStyle);
        Label volumeLabel = new Label("VOLUME", labelStyle);
        Label backLabel = new Label("GO BACK", labelStyle);

        // Add click listeners
        termsLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: Implement terms and privacy screen
                System.out.println("Terms and Privacy clicked");
            }
        });

        volumeLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: Implement volume controls
                System.out.println("Volume clicked");
            }
        });

        backLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Return to HomeScreen when GO BACK is clicked
                game.setScreen(new HomeScreen(game));
            }
        });

        table.add(termsLabel).padBottom(20).row();
        table.add(volumeLabel).padBottom(20).row();
        table.add(backLabel);

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
