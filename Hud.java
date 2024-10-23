package Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.Main;

public class Hud {
    public Stage stage;
    public Viewport viewport;

    public Label playLabel;
    public Label settingsLabel;
    public Label quitLabel;

    public Hud(SpriteBatch sb) {
        viewport = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.bottom();
        table.setFillParent(true);


        playLabel = new Label("PLAY", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        settingsLabel = new Label("SETTINGS", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        quitLabel = new Label("QUIT", new Label.LabelStyle(new BitmapFont(), Color.WHITE));


        table.row();
        table.add(playLabel).expandX().padTop(10f);
        table.row();
        table.add(settingsLabel).expandX().padTop(10f);
        table.row();
        table.add(quitLabel).expandX().padTop(10f);
        stage.addActor(table);
    }
}

