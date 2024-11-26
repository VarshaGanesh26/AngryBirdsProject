package com.game.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Wood_hor extends Sprite {
    private Texture texture;

    public Wood_hor(float x, float y) {
        texture = new Texture("wood_hor.png"); //loading picture from assets
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
