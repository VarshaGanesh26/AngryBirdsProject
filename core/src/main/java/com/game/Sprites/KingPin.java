package com.game.Sprites;

public class KingPin extends BasePig {

    public KingPin(float x, float y) {
        super("kingpig.png", x, y);
    }

    @Override
    public void takeDamage() {
        System.out.println("KingPin takes damage!");
    }
}
