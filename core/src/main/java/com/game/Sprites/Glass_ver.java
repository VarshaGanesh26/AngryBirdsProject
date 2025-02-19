package com.game.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Glass_ver extends Sprite {
    private Texture texture;

    public Glass_ver(float x, float y) {
        texture = new Texture("glass_ver.png");
        setTexture(texture);
        setPosition(x,y);
    }

    public void draw(SpriteBatch spritebatch){
        spritebatch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }

    public void dispose() {
        texture.dispose();
    }


}
