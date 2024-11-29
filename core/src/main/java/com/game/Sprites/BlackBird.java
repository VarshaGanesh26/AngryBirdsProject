package com.game.Sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef;

public class BlackBird extends Bird {
    private static final float INITIAL_SIZE = 40;
    private static final float MAX_SIZE = 70;

    private boolean isLaunched = false;
    private boolean sizeUpdated = false;

    public BlackBird(float x, float y) {
        super(x, y, "BlackBird.png");
        setSize(INITIAL_SIZE, INITIAL_SIZE);
    }

    public void update(float dt) {
        if (getBody() != null && getBody().getType() == BodyDef.BodyType.DynamicBody) {
            if (!isLaunched) {
                isLaunched = true;
            }
        }

        if (isLaunched && !sizeUpdated) {
            setSize(MAX_SIZE, MAX_SIZE);
            sizeUpdated = true;
        }
    }

    @Override
    public void draw(SpriteBatch sb) {
        sb.draw(texture, getX(), getY(), getWidth(), getHeight());
    }

    public void reset() {
        isLaunched = false;
        sizeUpdated = false;
        setSize(INITIAL_SIZE, INITIAL_SIZE);
    }
}
