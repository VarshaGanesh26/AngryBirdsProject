package com.game.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Slingshot extends Actor {
    private Texture texture;

    public Slingshot(float x, float y) {
        texture = new Texture("Slingshot.png");
        setSize(texture.getWidth(), texture.getHeight());
        setPosition(x,y);
    }

    public void draw(SpriteBatch spritebatch){
        spritebatch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public void dispose() {
        texture.dispose();
    }


}
