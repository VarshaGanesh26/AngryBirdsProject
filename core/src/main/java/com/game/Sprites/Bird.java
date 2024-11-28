package com.game.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

public abstract class Bird extends Sprite {
    protected Texture texture;
    protected Body body;

    public Bird(float x, float y, String texturePath) {
        texture = new Texture(texturePath);
        setTexture(texture);
        setPosition(x, y);
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public abstract void draw(SpriteBatch sb);

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}

