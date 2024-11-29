package com.game.Sprites;

public class SmallPig extends BasePig {

    public SmallPig(float x, float y) {
        super("Pig.png", x, y);
    }

    @Override
    public void takeDamage() {
        System.out.println("KingPin takes damage!");
    }
}
