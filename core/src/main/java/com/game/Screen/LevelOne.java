package com.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.math.Rectangle;
import com.game.Main;
import com.game.Sprites.*;

import java.util.ArrayList;

public class LevelOne implements Screen {
    private Main game;
    private Texture bg;
    private OrthographicCamera cam;
    private Viewport vp;
    private Stage stage;
    private SpriteBatch sb;
    final LevelOne curr_level = this;

    private Wood_vrt wood;
    private Glass glass;
    private Pig pig;
    private RedBird activeBird;
    private RedBird bird2;
    private Slingshot slingshot;
    private Win win;
    private Lose lose;
    private BitmapFont font;

    // Physics variables
    private Vector2 slingshotAnchor;
    private Vector2 dragStart;
    private boolean isDragging = false;
    private boolean birdLaunched = false;
    private float launchForce = 2.0f;
    private Vector2 velocity;
    private float gravity = -9.8f * 15; // Scaled for pixels
    private ShapeRenderer trajectoryRenderer;
    private static final int TRAJECTORY_POINTS = 30;
    private int remainingBirds = 2; // Add this
    private ArrayList<RedBird> birdQueue;

    // Collision detection
    private Rectangle pigBounds;
    private Rectangle glassBounds;
    private Rectangle woodBounds;

    public LevelOne(Main game) {
        this.game = game;
        this.sb = game.batch;

        bg = new Texture("background.jpg");
        cam = new OrthographicCamera();
        vp = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT, cam);
        stage = new Stage(vp, game.batch);
        font = new BitmapFont();
        font.getData().setScale(2.0f);

        // Initialize physics components
        trajectoryRenderer = new ShapeRenderer();
        slingshotAnchor = new Vector2(185, 165); // Adjusted slingshot pocket position
        velocity = new Vector2();

        Table table = new Table();
        table.top().left();
        table.setFillParent(true);
        Label.LabelStyle ls = new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.WHITE);

        Label pauseLabel = new Label("Pause", ls);
        pauseLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PauseScreen(game, curr_level));
            }
        });

        table.add(pauseLabel).padTop(10).padLeft(10);
        stage.addActor(table);

        // Initialize game objects
        wood = new Wood_vrt(530, 110);
        wood.setSize(60, 90);
        glass = new Glass(490, 200);
        glass.setSize(140, 20);
        pig = new Pig(535, 220);
        pig.setSize(50, 50);
        birdQueue = new ArrayList<RedBird>();
        activeBird = new RedBird(185, 165); // First bird at slingshot
        activeBird.setSize(38, 38);
        slingshot = new Slingshot(150, 110);
        slingshot.setSize(70, 110);
        win = new Win(220, 330);
        win.setSize(80, 30);
        lose = new Lose(335, 330);
        lose.setSize(80, 30);
        RedBird waitingBird = new RedBird(90, 110);
        waitingBird.setSize(38, 38);
        birdQueue.add(waitingBird);

        // Initialize collision bounds
        pigBounds = new Rectangle(pig.getX(), pig.getY(), pig.getWidth(), pig.getHeight());
        glassBounds = new Rectangle(glass.getX(), glass.getY(), glass.getWidth(), glass.getHeight());
        woodBounds = new Rectangle(wood.getX(), wood.getY(), wood.getWidth(), wood.getHeight());


    }



    private boolean isNearBird(Vector2 touchPos) {
        return touchPos.dst(activeBird.getX() + activeBird.getWidth()/2,
            activeBird.getY() + activeBird.getHeight()/2) < 50;
    }

    private void launchBird() {
        Vector2 birdCenter = new Vector2(activeBird.getX() + activeBird.getWidth()/2,
            activeBird.getY() + activeBird.getHeight()/2);

        // Match the velocity from trajectory
        velocity = new Vector2(5f, 2f);  // Same values as in drawTrajectory
        velocity.scl(100);  // Scale to match trajectory speed
        birdLaunched = true;
    }

    private void updateBirdPhysics(float delta) {
        if (birdLaunched && !isDragging) {
            // Update velocity considering gravity
            velocity.y += gravity * delta;

            // Update position
            activeBird.setPosition(
                activeBird.getX() + velocity.x * delta,
                activeBird.getY() + velocity.y * delta
            );

            // Check collisions
            Rectangle birdBounds = new Rectangle(
                activeBird.getX(), activeBird.getY(),
                activeBird.getWidth(), activeBird.getHeight()
            );

            if (birdBounds.overlaps(pigBounds)) {
                handlePigCollision();
            }

            // Reset if bird goes off screen
            if (activeBird.getY() < 0 || activeBird.getX() > Main.V_WIDTH ||
                activeBird.getX() < -activeBird.getWidth()) {
                resetBird();
            }
        }
    }

    private void handlePigCollision() {
        // Handle pig hit - could trigger win condition
        game.setScreen(new LevelTwo(game));
    }

    private void resetBird() {
        remainingBirds--;

        if (remainingBirds > 0 && !birdQueue.isEmpty()) {
            // Get next bird from queue
            RedBird nextBird = birdQueue.remove(0);
            activeBird = nextBird;
            activeBird.setPosition(185, 165); // Position at slingshot
        } else {
            // No more birds - game over!
            game.setScreen(new LoseScreen(game));
        }

        birdLaunched = false;
        velocity.setZero();
    }

    private void drawTrajectory() {
        if (isDragging) {
            Vector2 birdCenter = new Vector2(
                activeBird.getX() + activeBird.getWidth()/2,
                activeBird.getY() + activeBird.getHeight()/2
            );

            Vector2 pigCenter = new Vector2(
                pig.getX() + pig.getWidth()/2,
                pig.getY() + pig.getHeight()/2
            );

            trajectoryRenderer.begin(ShapeRenderer.ShapeType.Filled);
            trajectoryRenderer.setColor(1, 1, 1, 0.5f);

            // Calculate direction to pig
            Vector2 direction = new Vector2(pigCenter).sub(birdCenter).nor();
            float distance = birdCenter.dst(pigCenter);
            Vector2 vel = direction.scl(distance * 0.8f); // Scale velocity based on distance

            float t = 0;
            float dt = 0.2f;

            for (int i = 0; i < 15; i++) {
                t += dt;
                float x = birdCenter.x + vel.x * t;
                // Reduced gravity effect and made trajectory more horizontal
                float y = birdCenter.y + vel.y * t - 0.1f * Math.abs(gravity) * t * t;

                trajectoryRenderer.circle(x, y, 3);
            }

            trajectoryRenderer.end();
        }
    }

    @Override
    public void show() {
        // Remove this line since we'll handle input differently
        // Gdx.input.setInputProcessor(stage);

        // Create a multiplexer to handle both stage and game input
        com.badlogic.gdx.InputMultiplexer multiplexer = new com.badlogic.gdx.InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new com.badlogic.gdx.InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector2 worldPos = vp.unproject(new Vector2(screenX, screenY));
                if (!birdLaunched && isNearBird(worldPos)) {
                    isDragging = true;
                    dragStart = worldPos;
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (isDragging) {
                    Vector2 worldPos = vp.unproject(new Vector2(screenX, screenY));
                    Vector2 dragDir = new Vector2(slingshotAnchor).sub(worldPos);
                    float maxDrag = 100f;
                    if (dragDir.len() > maxDrag) {
                        dragDir.nor().scl(maxDrag);
                        worldPos = new Vector2(slingshotAnchor).sub(dragDir);
                    }
                    activeBird.setPosition(worldPos.x - activeBird.getWidth()/2,
                        worldPos.y - activeBird.getHeight()/2);
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (isDragging) {
                    isDragging = false;
                    launchBird();
                    return true;
                }
                return false;
            }
        });
        Gdx.input.setInputProcessor(multiplexer);
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        updateBirdPhysics(delta);

        cam.update();
        sb.setProjectionMatrix(cam.combined);
        trajectoryRenderer.setProjectionMatrix(cam.combined);

        sb.begin();
        sb.draw(bg, 0, 0, Main.V_WIDTH, Main.V_HEIGHT);

        // Draw game objects
        slingshot.draw(sb);
        wood.draw(sb);
        glass.draw(sb);
        pig.draw(sb);

        // Draw active bird
        activeBird.draw(sb);

        // Draw waiting birds from queue
        for (RedBird bird : birdQueue) {
            bird.draw(sb);
        }

        win.draw(sb);
        lose.draw(sb);

        sb.end();

        drawTrajectory();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1/30f));
        stage.draw();
    }
    // ... [Previous resize, pause, resume, hide methods remain unchanged]
    @Override
    public void resize(int width, int height) {
        vp.update(width, height);
        cam.position.set(cam.viewportWidth / 2, cam.viewportHeight / 2, 0);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        bg.dispose();
        wood.dispose();
        glass.dispose();
        pig.dispose();
        activeBird.dispose();
        for (RedBird bird : birdQueue) {
            bird.dispose();
        }
        bird2.dispose();
        slingshot.dispose();
        win.dispose();
        lose.dispose();
        sb.dispose();
        stage.dispose();
        font.dispose();
        trajectoryRenderer.dispose();
    }
}
