package com.game.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Stone extends Sprite {
    private Texture texture;

    public Stone(float x, float y) {
        texture = new Texture("stone.jpeg"); //loading picture from assets
        setTexture(texture);
        setPosition(x,y);
    }

    public void draw(SpriteBatch spritebatch){
        spritebatch.draw(texture, getX(), getY(), getWidth(), getHeight()); //method to set appropriate position
    }

    public void dispose() {
        texture.dispose();
    }


}
