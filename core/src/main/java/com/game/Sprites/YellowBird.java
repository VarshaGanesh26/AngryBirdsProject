package com.game.Sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.game.Screen.LevelTwo;

public class YellowBird extends Bird {
    public YellowBird(float x, float y) {
        super(x, y, "YellowBird.png");
    }

    public void multiply(World world) {
        float offset = 0.5f;

        for (int i = 0; i < 2; i++) {
            YellowBird newBird = new YellowBird(getX(), getY());
            newBird.setSize(getWidth(), getHeight());

            BodyDef birdDef = new BodyDef();
            birdDef.type = BodyDef.BodyType.DynamicBody;
            birdDef.position.set((newBird.getX() + offset * (i * 2 - 1)) / LevelTwo.PPM,
                newBird.getY() / LevelTwo.PPM);

            Body birdBody = world.createBody(birdDef);
            CircleShape birdShape = new CircleShape();
            birdShape.setRadius(newBird.getWidth() / 2 / LevelTwo.PPM);

            FixtureDef birdFixture = new FixtureDef();
            birdFixture.shape = birdShape;
            birdFixture.density = 0.8f;
            birdFixture.friction = 0.2f;
            birdFixture.restitution = 0.4f;

            birdBody.createFixture(birdFixture).setUserData("bird");
            birdShape.dispose();

            birdBody.setLinearVelocity(birdBody.getLinearVelocity().x,
                birdBody.getLinearVelocity().y);
        }
    }
    @Override
    public void draw(SpriteBatch sb) {
        sb.draw(texture, getX(), getY(), getWidth(), getHeight());
    }
}

