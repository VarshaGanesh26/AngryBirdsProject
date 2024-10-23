package Screens;

import Scenes.Hud;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.Main;

public class HomeScreen implements Screen {
    private Main game;
    private Texture texture;
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Stage stage;
    private Hud hud;

    public HomeScreen(final Main game) {
        this.game = game;
        texture = new Texture("angryBird-2.jpeg");
        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT, gamecam);
        stage = new Stage(gamePort, game.batch);
        hud = new Hud(game.batch);

        Table table = new Table();
        table.bottom();
        table.setFillParent(true);

        Label playLabel = new Label("PLAY", hud.playLabel.getStyle());
        Label settingsLabel = new Label("SETTINGS", hud.settingsLabel.getStyle());
        Label quitLabel = new Label("QUIT", hud.quitLabel.getStyle());

        playLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LevelScreen(game));
            }
        });

        settingsLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Implement settings screen transition here
            }
        });

        quitLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        table.add(playLabel).expandX().padBottom(10);
        table.row();
        table.add(settingsLabel).expandX().padBottom(10);
        table.row();
        table.add(quitLabel).expandX().padBottom(10);

        stage.addActor(table);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(texture, 0, 0, Main.V_WIDTH, Main.V_HEIGHT);
        game.batch.end();

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        texture.dispose();
        stage.dispose();
    }
}
