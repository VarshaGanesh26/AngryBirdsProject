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

public class LevelThree implements Screen{
    private Main game;
    private Texture bg;
    private OrthographicCamera cam;
    private Viewport vp;
    private Stage stage;
    private SpriteBatch sb;
    final LevelThree curr_level= this;

    private Wood_hor wood1;
    private Wood_hor wood2;
    private Wood_hor wood3;
    private Wood_hor wood4;
    private Wood_ver woodv1;
    private Wood_ver woodv2;
    private Wood_ver woodv3;
    private Wood_ver woodv4;
    private Wood_ver woodv5;
    private Wood_ver woodv6;
    private Wood_vrt wood;
    private Glass_ver glass1;
    private Glass_ver glass2;
    private Stone stone;
    private SmallPig pig;
    private KingPin kpig;
    private Pig mpig;
    private BlackBird bird1;
    private YellowBird bird2;
    private RedBird bird3;
    private RedBird bird4;
    private RedBird bird5;
    private YellowBird bird6;
    private Slingshot slingshot;
    private Win win;
    private Lose lose;
    private BitmapFont font;

    public LevelThree(Main game) {
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
        wood1 = new Wood_hor(514, 211);
        wood1.setSize(90, 18);
        wood2 = new Wood_hor(528, 315);
        wood2.setSize(65, 17);
        wood3 = new Wood_hor(538, 182);
        wood3.setSize(38, 12);
        wood4= new Wood_hor(616,200);
        wood4.setSize(93,18);
        woodv1= new Wood_ver(513,105);
        woodv1.setSize(22, 110);
        woodv2= new Wood_ver(580,105);
        woodv2.setSize(22, 110);
        woodv3= new Wood_ver(525,222);
        woodv3.setSize(22, 100);
        woodv4= new Wood_ver(575,222);
        woodv4.setSize(22, 100);
        woodv5= new Wood_ver(617,105);
        woodv5.setSize(22,103);
        woodv6= new Wood_ver(690, 105);
        woodv6.setSize(22,103);
        wood= new Wood_vrt(531, 350);
        wood.setSize(70,70);
        glass1 = new Glass_ver(536, 105);
        glass1.setSize(15,80);
        glass2 = new Glass_ver(563, 105);
        glass2.setSize(15,80);
        stone= new Stone(542,328);
        stone.setSize(35, 25);
        pig = new SmallPig(547, 226);
        pig.setSize(25,25);
        kpig= new KingPin(544, 363);
        kpig.setSize(45,45);
        mpig= new Pig(640, 107);
        mpig.setSize(50,50);
        bird1= new BlackBird(50, 110);
        bird1.setSize(40, 40);
        bird2= new YellowBird(90, 105);
        bird2.setSize(38, 40);
        bird3= new RedBird(130, 110);
        bird3.setSize(38, 38);
        bird4= new RedBird(130, 60);
        bird4.setSize(38,38);
        bird5= new RedBird(90, 60);
        bird5.setSize(38,38);
        bird6= new YellowBird(50,60);
        bird6.setSize(38,40);
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
                game.setScreen(new WinScreen(game));
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
        bird4.draw(sb);
        bird5.draw(sb);
        bird6.draw(sb);
        slingshot.draw(sb);
        wood1.draw(sb);
        wood2.draw(sb);
        wood3.draw(sb);
        wood4.draw(sb);
        woodv1.draw(sb);
        woodv2.draw(sb);
        woodv3.draw(sb);
        woodv4.draw(sb);
        woodv5.draw(sb);
        woodv6.draw(sb);
        wood.draw(sb);
        glass1.draw(sb);
        glass2.draw(sb);
        stone.draw(sb);
        pig.draw(sb);
        kpig.draw(sb);
        mpig.draw(sb);
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
        wood1.dispose();
        wood2.dispose();
        wood3.dispose();
        wood4.dispose();
        woodv1.dispose();
        woodv2.dispose();
        woodv3.dispose();
        woodv4.dispose();
        woodv5.dispose();
        woodv6.dispose();
        wood.dispose();
        glass1.dispose();
        glass2.dispose();
        stone.dispose();
        pig.dispose();
        kpig.dispose();
        mpig.dispose();
        bird1.dispose();
        bird2.dispose();
        bird3.dispose();
        bird4.dispose();
        bird5.dispose();
        bird6.dispose();
        slingshot.dispose();
        win.dispose();
        lose.dispose();
        sb.dispose();
        stage.dispose();
        font.dispose();
    }
}
