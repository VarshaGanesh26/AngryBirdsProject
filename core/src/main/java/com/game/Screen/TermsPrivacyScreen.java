package com.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.Main;

public class TermsPrivacyScreen implements Screen {
    private Main game;
    private Texture bg;
    private OrthographicCamera cam;
    private Viewport vp;
    private Stage stage;
    private BitmapFont font;
    private Sound clickSound;

    public TermsPrivacyScreen(final Main game) {
        this.game = game;
        bg = new Texture("background_x.jpg");
        cam = new OrthographicCamera();
        vp = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT, cam);
        stage = new Stage(vp, game.batch);
        font = new BitmapFont();
        font.getData().setScale(0.8f);

        clickSound = Gdx.audio.newSound(Gdx.files.internal("click.mp3"));

        Label.LabelStyle ls = new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.BLACK);

        //creating label with text to be displayed
        Label tncLabel = new Label("Angry Birds may collect personal and non-personal information from users, but they do not knowingly collect personal information from children under 13." +
            "Subject to these TOS, Rovio hereby grants you a non-exclusive, non-transferable, non-sublicensable, limited right and license to use the Services for your personal, non-commercial use. The rights granted to you are subject to your compliance with these TOS.\n" +
            "\n" +
            "Except as set forth above, you do not receive any other license. Rovio retains all right, title and interest in and to the Services, including, but not limited to, all copyrights, trademarks, rights, in each case whether registered or not and all applications thereof." +
            " Unless expressly authorized by applicable law, the Services may not be copied, reproduced, or distributed in any manner or medium, in whole or in part, without Rovio’s prior written consent. " +
            "Rovio reserves all rights not expressly granted to you herein.", ls);
        tncLabel.setWrap(true); //text wrapping enabled
        tncLabel.setWidth(Main.V_WIDTH - 40);
        GlyphLayout layout = new GlyphLayout(font, tncLabel.getText()); //layout centre
        tncLabel.setPosition((Main.V_WIDTH - layout.width) / 2, (float) Main.V_HEIGHT / 2 - layout.height / 2);

        tncLabel.setPosition(20, (float) Main.V_HEIGHT / 2);

        stage.addActor(tncLabel);

        //creating home label
        Label.LabelStyle homeLabelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label homeLabel = new Label("HOME", homeLabelStyle);

        homeLabel.setPosition(Main.V_WIDTH - homeLabel.getWidth() - 20, 20);

        homeLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                game.setScreen(new HomeScreen(game)); //clicking will display home screen
            }
        });

        stage.addActor(homeLabel);
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
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        bg.dispose();
        stage.dispose();
        font.dispose();
        clickSound.play();
    }
}
