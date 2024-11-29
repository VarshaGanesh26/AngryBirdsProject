package com.game.Sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class YellowBird extends Bird {
    public YellowBird(float x, float y) {
        super(x, y, "YellowBird.png");
    }

    @Override
    public void draw(SpriteBatch sb) {
        sb.draw(texture, getX(), getY(), getWidth(), getHeight());
    }
}
