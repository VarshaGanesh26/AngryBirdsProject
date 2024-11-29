package com.game.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class BasePig extends Sprite {
    protected Texture texture;

    public BasePig(String texturePath, float x, float y) {
        texture = new Texture(texturePath);
        setTexture(texture);
        setPosition(x, y);
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }

    public void dispose() {
        texture.dispose();
    }

    public abstract void takeDamage();
}
