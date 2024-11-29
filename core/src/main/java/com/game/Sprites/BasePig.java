package com.game.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class BasePig extends Sprite {
    protected Texture texture;

    public BasePig(String texturePath, float x, float y) {
        texture = new Texture(texturePath); // Load the texture from the provided path
        setTexture(texture);
        setPosition(x, y);
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, getX(), getY(), getWidth(), getHeight()); // Draw the pig at its position
    }

    public void dispose() {
        texture.dispose(); // Dispose of the texture when no longer needed
    }

    public abstract void takeDamage(); // Abstract method to be implemented by subclasses
}
