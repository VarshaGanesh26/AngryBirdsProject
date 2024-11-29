package com.game.Sprites;

public class SmallPig extends BasePig {

    public SmallPig(float x, float y) {
        super("Pig.png", x, y); // Call the superclass constructor with the texture path
    }

    @Override
    public void takeDamage() {
        // Implement the behavior when KingPin takes damage
        System.out.println("KingPin takes damage!");
    }
}
