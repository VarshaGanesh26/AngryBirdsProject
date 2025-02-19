package com.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.game.Screen.HomeScreen;
import com.game.Screen.LevelThree;

public class Main extends Game {
    public static final int V_WIDTH=800;
    public static final int V_HEIGHT =500;
    public static final int PPM = 100;
    public SpriteBatch batch;
    public Music bgm;

    @Override
    public void create() {
        batch = new SpriteBatch();
        bgm = Gdx.audio.newMusic(Gdx.files.internal("theme.mp3"));
        bgm.setLooping(true);
        setScreen(new HomeScreen(this));
        //Gdx.app.log("Main", "Create method called.");

    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        //disposing all resources
        batch.dispose();
        bgm.dispose();
    }
}
