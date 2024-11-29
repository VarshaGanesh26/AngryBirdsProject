package com.game.Screen;

import com.badlogic.gdx.math.Vector2;
import java.io.*;
import java.util.ArrayList;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    // Game state data
    public int currentLevel;
    public float birdX;
    public float birdY;
    public boolean birdLaunched;
    public String activeBirdType; // Track current bird type
    public ArrayList<PigState> pigStates;
    public ArrayList<StructureState> structureStates;
    public ArrayList<BirdState> remainingBirds;

    // Inner classes for storing object states
    public static class PigState implements Serializable {
        private static final long serialVersionUID = 1L;
        public float x;
        public float y;
        public boolean isDead;
        public String type;
        public String identifier; // For tracking individual pigs

        public PigState(float x, float y, boolean isDead, String type, String identifier) {
            this.x = x;
            this.y = y;
            this.isDead = isDead;
            this.type = type;
            this.identifier = identifier;
        }
    }

    public static class StructureState implements Serializable {
        private static final long serialVersionUID = 1L;
        public float x;
        public float y;
        public float rotation;
        public String type;
        public String identifier; // For tracking individual structures

        public StructureState(float x, float y, float rotation, String type, String identifier) {
            this.x = x;
            this.y = y;
            this.rotation = rotation;
            this.type = type;
            this.identifier = identifier;
        }
    }

    public static class BirdState implements Serializable {
        private static final long serialVersionUID = 1L;
        public float x;
        public float y;
        public String type;

        public BirdState(float x, float y, String type) {
            this.x = x;
            this.y = y;
            this.type = type;
        }
    }

    public static void saveGame(GameState state, String filename) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(state);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GameState loadGame(String filename) {
        try {
            FileInputStream fileIn = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            GameState state = (GameState) in.readObject();
            in.close();
            fileIn.close();
            return state;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
