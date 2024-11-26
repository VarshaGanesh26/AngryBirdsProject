package com.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.Main;
import com.game.Sprites.*;

public class LevelOne implements Screen{
    private Main game;
    private Texture bg;
    private OrthographicCamera cam;
    private Viewport vp;
    private Stage stage;
    private SpriteBatch sb;
    final LevelOne curr_level= this;

    private Wood_vrt wood;
    private Glass glass;
    private Pig pig;
    private RedBird bird1;
    private RedBird bird2;
    private Slingshot slingshot;
    private Win win;
    private Lose lose;
    private BitmapFont font;

    public LevelOne(Main game) {
        this.game=game;
        this.sb = game.batch;

        bg = new Texture("background.jpg");
        cam = new OrthographicCamera();
        vp = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT, cam);
        stage= new Stage(vp, game.batch);
        font= new BitmapFont();
        font.getData().setScale(2.0f);

        Table table= new Table();
        table.top().left();
        table.setFillParent(true);
        Label.LabelStyle ls = new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.WHITE);

        //creating pause label and its ClickListener
        Label pauseLabel = new Label("Pause", ls);
        pauseLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PauseScreen(game, curr_level));
            }
        });

        table.add(pauseLabel).padTop(10).padLeft(10);
        stage.addActor(table);

        //instantiating each object and positioning them on the screen
        wood = new Wood_vrt(530, 110);
        wood.setSize(60, 90);
        glass = new Glass(490, 200);
        glass.setSize(140,20);
        pig = new Pig(535, 220);
        pig.setSize(50,50);
        bird1= new RedBird(50, 110);
        bird1.setSize(38, 38);
        bird2= new RedBird(90, 110);
        bird2.setSize(38, 38);
        slingshot= new Slingshot(150, 110);
        slingshot.setSize(70, 110);
        win= new Win(220, 330);
        win.setSize(80, 30);
        lose= new Lose(335, 330);
        lose.setSize(80, 30);

        //creating ClickListeners for labels
        win.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LevelTwo(game));
            }
        });

        lose.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LoseScreen(game));
            }
        });

        stage.addActor(win);
        stage.addActor(lose);

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1); //setting bg color to white
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT); //clear color buffer

        cam.update(); //update camera to be in sync with current view
        sb.setProjectionMatrix(cam.combined); //ensure correct position of objects

        sb.begin();
        sb.draw(bg, 0, 0, Main.V_WIDTH, Main.V_HEIGHT);

        //drawing all elements
        bird1.draw(sb);
        bird2.draw(sb);
        slingshot.draw(sb);
        wood.draw(sb);
        glass.draw(sb);
        pig.draw(sb);
        win.draw(sb);
        lose.draw(sb);

        sb.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
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
        //dispose all resources
        bg.dispose();
        wood.dispose();
        glass.dispose();
        pig.dispose();
        bird1.dispose();
        bird2.dispose();
        slingshot.dispose();
        win.dispose();
        lose.dispose();
        sb.dispose();
        stage.dispose();
        font.dispose();
    }
}
