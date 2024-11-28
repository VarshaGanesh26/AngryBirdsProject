package com.game.Sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BlackBird extends Bird {
    public BlackBird(float x, float y) {
        super(x, y, "BlackBird.png");
    }

    @Override
    public void draw(SpriteBatch sb) {
        sb.draw(texture, getX(), getY(), getWidth(), getHeight());
    }
}
