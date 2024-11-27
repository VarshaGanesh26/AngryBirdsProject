package com.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
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

public class LevelTwo implements Screen{
    private Main game;
    private Texture bg;
    private OrthographicCamera cam;
    private Viewport vp;
    private Stage stage;
    private SpriteBatch sb;
    private Sound clickSound;
    final LevelTwo curr_level= this;

    private Wood_ver wood1;
    private Wood_ver wood2;
    private Wood_ver wood3;
    private Wood_ver wood4;
    private Wood_ver wood5;
    private Wood_ver wood6;
    private Pig pig1;
    private Pig pig2;
    private BlackBird bird1;
    private YellowBird bird2;
    private RedBird bird3;
    private Slingshot slingshot;
    private Stone stone1;
    private Stone stone2;
    private Stone stone3;
    private Win win;
    private Lose lose;
    private BitmapFont font;

    public LevelTwo(Main game) {
        this.game=game;
        this.sb = game.batch;

        bg = new Texture("background.jpg");
        cam = new OrthographicCamera();
        vp = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT, cam);
        stage= new Stage(vp, game.batch);
        font= new BitmapFont();

        clickSound = Gdx.audio.newSound(Gdx.files.internal("click.mp3"));

        Table table= new Table();
        table.top().left();
        table.setFillParent(true);
        Label.LabelStyle ls = new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.WHITE);

        //creating pause label and its ClickListener
        Label pauseLabel = new Label("Pause", ls);
        pauseLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                game.setScreen(new PauseScreen(game, curr_level));
            }
        });

        table.add(pauseLabel).padTop(10).padLeft(10);
        stage.addActor(table);

        //instantiating each object and positioning them on the screen
        wood1 = new Wood_ver(500, 105);
        wood1.setSize(25, 90);
        wood2 = new Wood_ver(550, 105);
        wood2.setSize(25, 90);
        wood3 = new Wood_ver(500, 205);
        wood3.setSize(25, 90);
        wood4 = new Wood_ver(550, 205);
        wood4.setSize(25, 90);
        wood5 = new Wood_ver(600, 105);
        wood5.setSize(25, 90);
        wood6 = new Wood_ver(650, 105);
        wood6.setSize(25, 90);
        pig1 = new Pig(518, 307);
        pig1.setSize(50,50);
        pig2 = new Pig(613, 207);
        pig2.setSize(50,50);
        bird1= new BlackBird(50, 110);
        bird1.setSize(40, 40);
        bird2= new YellowBird(90, 105);
        bird2.setSize(38, 40);
        bird3= new RedBird(130, 110);
        bird3.setSize(38, 38);
        slingshot= new Slingshot(160, 110);
        slingshot.setSize(70, 110);
        stone1= new Stone(495, 192);
        stone1.setSize(80,18);
        stone2= new Stone(495, 293);
        stone2.setSize(80,18);
        stone3= new Stone(600, 192);
        stone3.setSize(75,18);
        win= new Win(220, 330);
        win.setSize(80, 30);
        lose= new Lose(335, 330);
        lose.setSize(80, 30);


        //creating ClickListeners for labels
        win.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LevelThree(game));
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
        bird3.draw(sb);
        slingshot.draw(sb);
        wood1.draw(sb);
        wood2.draw(sb);
        wood3.draw(sb);
        wood4.draw(sb);
        wood5.draw(sb);
        wood6.draw(sb);
        pig1.draw(sb);
        pig2.draw(sb);
        win.draw(sb);
        lose.draw(sb);
        stone1.draw(sb);
        stone2.draw(sb);
        stone3.draw(sb);

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
        wood1.dispose();
        wood2.dispose();
        wood3.dispose();
        wood4.dispose();
        wood5.dispose();
        wood6.dispose();
        bird1.dispose();
        bird2.dispose();
        bird3.dispose();
        pig1.dispose();
        pig2.dispose();
        stone1.dispose();
        stone2.dispose();
        stone3.dispose();
        slingshot.dispose();
        win.dispose();
        lose.dispose();
        sb.dispose();
        stage.dispose();
        font.dispose();
        clickSound.dispose();
    }
}
