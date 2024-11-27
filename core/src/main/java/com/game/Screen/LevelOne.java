package com.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.Main;
import com.game.Sprites.*;

import java.util.ArrayList;

public class LevelOne implements Screen {
    private static final float PPM = 100f;
    private static final float GROUND_HEIGHT = 110;
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
    private Slingshot slingshot;
    private Win win;
    private Lose lose;
    private BitmapFont font;
    private boolean isExploding = false;
    private float explosionTimer = 0;
    private final float EXPLOSION_DURATION = 0.5f;
    private Texture explosionTexture;

    // Box2D components
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private Body birdBody;
    private Body pigBody;
    private Body woodBody;
    private Body glassBody;
    private Body groundBody;

    // Physics variables
    private Vector2 slingshotAnchor;
    private Vector2 dragStart;
    private boolean isDragging = false;
    private boolean birdLaunched = false;
    private float launchForce = 0.8f;
    private float gravity = -9.8f * 10;
    private ShapeRenderer trajectoryRenderer;
    private int remainingBirds = 2;
    private ArrayList<RedBird> birdQueue;
    private ArrayList<RedBird> allBirds; // New field to track all birds
    private RedBird selectedBird; // New field to track currently selected bird
    private Vector2 originalPosition;


    public LevelOne(Main game) {
        this.game = game;
        this.sb = game.batch;
        allBirds = new ArrayList<RedBird>();

        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();

        bg = new Texture("background.jpg");
        explosionTexture = new Texture("explosion.png");
        cam = new OrthographicCamera();
        vp = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT, cam);
        stage = new Stage(vp, game.batch);
        font = new BitmapFont();
        font.getData().setScale(2.0f);

        trajectoryRenderer = new ShapeRenderer();
        slingshotAnchor = new Vector2(185, 165);

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
        wood = new Wood_vrt(530, GROUND_HEIGHT);
        wood.setSize(60, 90);
        glass = new Glass(490, GROUND_HEIGHT + 90);
        glass.setSize(140, 20);
        pig = new Pig(535, GROUND_HEIGHT + 90 + 20);
        pig.setSize(50, 50);

        activeBird = new RedBird(185, 165);
        activeBird.setSize(38, 38);
        allBirds.add(activeBird);

        birdQueue = new ArrayList<RedBird>();
        RedBird waitingBird = new RedBird(90, 110);
        waitingBird.setSize(38, 38);
        birdQueue.add(waitingBird);
        allBirds.add(waitingBird);



        slingshot = new Slingshot(150, 110);
        slingshot.setSize(70, 110);
        win = new Win(220, 330);
        win.setSize(80, 30);
        lose = new Lose(335, 330);
        lose.setSize(80, 30);

        createBodies();
        setupContactListener();
    }

    private boolean isBirdClicked(Vector2 touchPos, RedBird bird) {
        return touchPos.dst(bird.getX() + bird.getWidth()/2,
            bird.getY() + bird.getHeight()/2) < bird.getWidth()/2;
    }

    private void resetBirdPosition(RedBird bird) {
        if (bird == activeBird) {
            bird.setPosition(185, 165);
        } else {
            bird.setPosition(90, 110);
        }
    }



    private void createBodies() {
        // Ground
        BodyDef groundDef = new BodyDef();
        groundDef.position.set(Main.V_WIDTH / 2 / PPM, GROUND_HEIGHT / PPM);
        groundBody = world.createBody(groundDef);
        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(Main.V_WIDTH / 2 / PPM, 10 / PPM);
        groundBody.createFixture(groundShape, 0.0f).setUserData("ground");
        groundShape.dispose();

        // Bird
        createBirdBody(185, 165);

        // Pig
        BodyDef pigDef = new BodyDef();
        pigDef.type = BodyDef.BodyType.DynamicBody;
        pigDef.position.set((535 + 25) / PPM, (GROUND_HEIGHT + 90 + 20 + 25) / PPM);

        pigBody = world.createBody(pigDef);
        CircleShape pigShape = new CircleShape();
        pigShape.setRadius(25 / PPM);

        FixtureDef pigFixture = new FixtureDef();
        pigFixture.shape = pigShape;
        pigFixture.density = 1.0f;
        pigFixture.friction = 0.2f;
        pigFixture.restitution = 0.2f;

        pigBody.createFixture(pigFixture).setUserData("pig");
        pigShape.dispose();

        // Wood
        BodyDef woodDef = new BodyDef();
        woodDef.type = BodyDef.BodyType.DynamicBody;
        woodDef.position.set((530 + 30) / PPM, (GROUND_HEIGHT + 45) / PPM);

        woodBody = world.createBody(woodDef);
        PolygonShape woodShape = new PolygonShape();
        woodShape.setAsBox(30 / PPM, 45 / PPM);

        FixtureDef woodFixture = new FixtureDef();
        woodFixture.shape = woodShape;
        woodFixture.density = 0.5f;
        woodFixture.friction = 0.4f;
        woodFixture.restitution = 0.2f;

        woodBody.createFixture(woodFixture);
        woodShape.dispose();

        // Glass
        BodyDef glassDef = new BodyDef();
        glassDef.type = BodyDef.BodyType.DynamicBody;
        glassDef.position.set((490 + 70) / PPM, (GROUND_HEIGHT + 90 + 10) / PPM);

        glassBody = world.createBody(glassDef);
        PolygonShape glassShape = new PolygonShape();
        glassShape.setAsBox(70 / PPM, 10 / PPM);

        FixtureDef glassFixture = new FixtureDef();
        glassFixture.shape = glassShape;
        glassFixture.density = 0.3f;
        glassFixture.friction = 0.2f;
        glassFixture.restitution = 0.3f;

        glassBody.createFixture(glassFixture);
        glassShape.dispose();

        // Add world bounds
        BodyDef wallDef = new BodyDef();
        PolygonShape wallShape = new PolygonShape();

        // Left wall
        Body leftWall = world.createBody(wallDef);
        wallShape.setAsBox(1 / PPM, Main.V_HEIGHT / PPM);
        leftWall.createFixture(wallShape, 0.0f);
        leftWall.setTransform(0, Main.V_HEIGHT / 2 / PPM, 0);

        // Right wall
        Body rightWall = world.createBody(wallDef);
        rightWall.createFixture(wallShape, 0.0f);
        rightWall.setTransform(Main.V_WIDTH / PPM, Main.V_HEIGHT / 2 / PPM, 0);

        // Top wall
        Body topWall = world.createBody(wallDef);
        wallShape.setAsBox(Main.V_WIDTH / PPM, 1 / PPM);
        topWall.createFixture(wallShape, 0.0f);
        topWall.setTransform(Main.V_WIDTH / 2 / PPM, Main.V_HEIGHT / PPM, 0);

        wallShape.dispose();
    }

    private void createBirdBody(float x, float y) {
        BodyDef birdDef = new BodyDef();
        birdDef.type = BodyDef.BodyType.DynamicBody;
        birdDef.position.set((x + 19) / PPM, (y + 19) / PPM);

        birdBody = world.createBody(birdDef);
        CircleShape birdShape = new CircleShape();
        birdShape.setRadius(19 / PPM);

        FixtureDef birdFixture = new FixtureDef();
        birdFixture.shape = birdShape;
        birdFixture.density = 0.8f;
        birdFixture.friction = 0.2f;
        birdFixture.restitution = 0.4f;

        birdBody.createFixture(birdFixture);
        birdShape.dispose();
    }

    private void setupContactListener() {
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                // Check for pig hitting ground
                if ((fixtureA.getUserData() != null && fixtureA.getUserData().equals("ground") &&
                    fixtureB.getUserData() != null && fixtureB.getUserData().equals("pig")) ||
                    (fixtureB.getUserData() != null && fixtureB.getUserData().equals("ground") &&
                        fixtureA.getUserData() != null && fixtureA.getUserData().equals("pig"))) {
                    isExploding = true;
                    explosionTimer = 0;
                }

                // Check for bird hitting pig
                if ((fixtureA.getUserData() != null && fixtureA.getUserData().equals("pig")) ||
                    (fixtureB.getUserData() != null && fixtureB.getUserData().equals("pig"))) {
                    if ((fixtureA.getBody() == birdBody || fixtureB.getBody() == birdBody)) {
                        isExploding = true;
                        explosionTimer = 0;
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {}

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {}

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {}
        });
    }

    private boolean isNearBird(Vector2 touchPos) {
        return touchPos.dst(activeBird.getX() + activeBird.getWidth()/2,
            activeBird.getY() + activeBird.getHeight()/2) < 50;
    }

    private void launchBird() {
        Vector2 birdCenter = new Vector2(
            activeBird.getX() + activeBird.getWidth()/2,
            activeBird.getY() + activeBird.getHeight()/2
        );

        Vector2 dragPos = new Vector2(birdCenter);
        Vector2 anchorPos = new Vector2(slingshotAnchor);
        Vector2 launchDir = dragPos.sub(anchorPos);

        float dragDistance = launchDir.len();
        float velocityScale = Math.min(dragDistance / 100f, 1f) * launchForce;
        Vector2 impulse = launchDir.nor().scl(velocityScale * 3f);

        birdBody.setLinearVelocity(0, 0);
        birdBody.applyLinearImpulse(
            impulse,
            birdBody.getWorldCenter(),
            true
        );

        birdLaunched = true;
    }

    private boolean isBirdOnGround() {
        if (birdBody != null) {
            Vector2 position = birdBody.getPosition();
            Vector2 velocity = birdBody.getLinearVelocity();
            return position.y * PPM <= GROUND_HEIGHT + 20 &&
                Math.abs(velocity.x) < 0.1f &&
                Math.abs(velocity.y) < 0.1f;
        }
        return false;
    }

    private void resetBird() {
        if (!birdQueue.isEmpty()) {
            // Destroy current bird body
            if (birdBody != null) {
                world.destroyBody(birdBody);
                birdBody = null;
            }

            // Get next bird from queue
            RedBird nextBird = birdQueue.remove(0);
            activeBird = nextBird;

            // Position new bird at slingshot
            activeBird.setPosition(185, 165);
            activeBird.setSize(38, 38);

            // Create new physics body at slingshot
            createBirdBody(185, 165);

            // Reset states
            birdLaunched = false;
            isDragging = false;
        } else if (remainingBirds <= 0) {
            game.setScreen(new LoseScreen(game));
        }
    }

    private void updatePhysicsSprites() {
        activeBird.setPosition(
            birdBody.getPosition().x * PPM - activeBird.getWidth()/2,
            birdBody.getPosition().y * PPM - activeBird.getHeight()/2
        );

        pig.setPosition(
            pigBody.getPosition().x * PPM - pig.getWidth()/2,
            pigBody.getPosition().y * PPM - pig.getHeight()/2
        );

        wood.setPosition(
            woodBody.getPosition().x * PPM - wood.getWidth()/2,
            woodBody.getPosition().y * PPM - wood.getHeight()/2
        );
        wood.setRotation((float) Math.toDegrees(woodBody.getAngle()));

        glass.setPosition(
            glassBody.getPosition().x * PPM - glass.getWidth()/2,
            glassBody.getPosition().y * PPM - glass.getHeight()/2
        );
        glass.setRotation((float) Math.toDegrees(glassBody.getAngle()));
    }

    private void drawTrajectory() {
        if (isDragging) {
            Vector2 birdCenter = new Vector2(
                activeBird.getX() + activeBird.getWidth()/2,
                activeBird.getY() + activeBird.getHeight()/2
            );

            Vector2 dragPos = new Vector2(birdCenter);
            Vector2 anchorPos = new Vector2(slingshotAnchor);
            Vector2 launchDir = dragPos.sub(anchorPos);

            trajectoryRenderer.begin(ShapeRenderer.ShapeType.Filled);
            trajectoryRenderer.setColor(1, 1, 1, 0.5f);

            float dragDistance = launchDir.len();
            float velocityScale = Math.min(dragDistance / 100f, 1f) * launchForce;
            Vector2 vel = launchDir.nor().scl(velocityScale * 300);

            float t = 0;
            float dt = 0.1f;
            Vector2 pos = new Vector2(birdCenter);
            Vector2 gravity = new Vector2(0, this.gravity / 20);

            for (int i = 0; i < 20; i++) {
                t += dt;
                float x = birdCenter.x + vel.x * t;
                float y = birdCenter.y + vel.y * t + 0.5f * gravity.y * t * t;

                trajectoryRenderer.circle(x, y, 2);
            }

            trajectoryRenderer.end();
        }
    }

    public void show() {
        com.badlogic.gdx.InputMultiplexer multiplexer = new com.badlogic.gdx.InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new com.badlogic.gdx.InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector2 worldPos = vp.unproject(new Vector2(screenX, screenY));

                // Check if any bird is clicked
                for (RedBird bird : allBirds) {
                    if (!birdLaunched && isBirdClicked(worldPos, bird)) {
                        selectedBird = bird;
                        isDragging = true;
                        dragStart = worldPos;
                        originalPosition = new Vector2(bird.getX(), bird.getY());

                        // If clicking a waiting bird, swap it with active bird
                        if (bird != activeBird) {
                            // Store original positions
                            Vector2 waitingBirdPos = new Vector2(bird.getX(), bird.getY());
                            Vector2 activeBirdPos = new Vector2(activeBird.getX(), activeBird.getY());

                            // Swap positions
                            bird.setPosition(activeBirdPos.x, activeBirdPos.y);
                            activeBird.setPosition(waitingBirdPos.x, waitingBirdPos.y);

                            // Swap references
                            int waitingIndex = birdQueue.indexOf(bird);
                            birdQueue.set(waitingIndex, activeBird);
                            RedBird oldActive = activeBird;
                            activeBird = bird;

                            // Update physics body for new active bird
                            if (birdBody != null) {
                                world.destroyBody(birdBody);
                            }
                            createBirdBody(activeBird.getX(), activeBird.getY());
                        }
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (isDragging && selectedBird != null) {
                    Vector2 worldPos = vp.unproject(new Vector2(screenX, screenY));
                    Vector2 dragDir = new Vector2(slingshotAnchor).sub(worldPos);
                    float maxDrag = 100f;
                    if (dragDir.len() > maxDrag) {
                        dragDir.nor().scl(maxDrag);
                        worldPos = new Vector2(slingshotAnchor).sub(dragDir);
                    }

                    if (selectedBird == activeBird) {
                        birdBody.setTransform(
                            (worldPos.x + selectedBird.getWidth()/2) / PPM,
                            (worldPos.y + selectedBird.getHeight()/2) / PPM,
                            0
                        );
                        birdBody.setLinearVelocity(0, 0);
                    }
                    selectedBird.setPosition(worldPos.x, worldPos.y);
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (isDragging && selectedBird != null) {
                    isDragging = false;
                    if (selectedBird == activeBird) {
                        launchBird();
                    } else {
                        // If not the active bird, reset to original position
                        selectedBird.setPosition(originalPosition.x, originalPosition.y);
                    }
                    selectedBird = null;
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

        world.step(1/60f, 6, 2);
        updatePhysicsSprites();

        if (birdLaunched && !isExploding && isBirdOnGround()) {
            resetBird();
        }

        cam.update();
        sb.setProjectionMatrix(cam.combined);
        trajectoryRenderer.setProjectionMatrix(cam.combined);

        sb.begin();
        sb.draw(bg, 0, 0, Main.V_WIDTH, Main.V_HEIGHT);

        slingshot.draw(sb);
        wood.draw(sb);
        glass.draw(sb);

        if (!isExploding) {
            pig.draw(sb);
        } else {
            explosionTimer += delta;
            sb.draw(explosionTexture,
                pig.getX() - 20,
                pig.getY() - 20,
                pig.getWidth() + 40,
                pig.getHeight() + 40);

            if (explosionTimer >= EXPLOSION_DURATION) {
                game.setScreen(new LevelTwo(game));
            }
        }

        activeBird.draw(sb);

        for (RedBird bird : birdQueue) {
            bird.draw(sb);
        }

        win.draw(sb);
        lose.draw(sb);

        sb.end();

        drawTrajectory();
        debugRenderer.render(world, cam.combined.scl(PPM));

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1/30f));
        stage.draw();
    }

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
        slingshot.dispose();
        win.dispose();
        lose.dispose();
        stage.dispose();
        font.dispose();
        trajectoryRenderer.dispose();
        explosionTexture.dispose();
        world.dispose();
        debugRenderer.dispose();
    }
}
