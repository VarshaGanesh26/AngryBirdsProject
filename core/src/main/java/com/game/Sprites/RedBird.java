package com.game.Sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RedBird extends Bird {
    public RedBird(float x, float y) {
        super(x, y, "RedBird.png");
    }

    @Override
    public void draw(SpriteBatch sb) {
        sb.draw(texture, getX(), getY(), getWidth(), getHeight());
    }
}
