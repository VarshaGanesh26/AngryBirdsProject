package com.game.Sprites;

public class KingPin extends BasePig {

    public KingPin(float x, float y) {
        super("kingpig.png", x, y); // Call the superclass constructor with the texture path
    }

    @Override
    public void takeDamage() {
        // Implement the behavior when KingPin takes damage
        System.out.println("KingPin takes damage!");
    }
}
