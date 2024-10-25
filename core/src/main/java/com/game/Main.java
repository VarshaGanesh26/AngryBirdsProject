package com.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.game.Screen.HomeScreen;

public class Main extends Game {
    public static final int V_WIDTH=800; //fixing width of screen
    public static final int V_HEIGHT =500; //fixing height of screen
    public SpriteBatch batch;
    public Music bgm;

    @Override
    public void create() {
        batch = new SpriteBatch();
        bgm = Gdx.audio.newMusic(Gdx.files.internal("theme.mp3")); //accessing theme song
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
