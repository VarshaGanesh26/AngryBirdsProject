package com.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.Main;
import com.game.Sprites.*;

import java.util.ArrayList;

public class LevelTwo implements Screen {
    public static final float PPM = 100f;
    private static final float GROUND_HEIGHT = 110;
    private Main game;
    private Texture bg;
    private OrthographicCamera cam;
    private Viewport vp;
    private Stage stage;
    private SpriteBatch sb;
    private Sound clickSound;
    final LevelTwo curr_level = this;

    // Game objects
    private Wood_ver wood1, wood2, wood3, wood4, wood5, wood6;
    private Stone stone1, stone2, stone3;
    private MediumPig pig1, pig2;
    private BlackBird blackBird;
    private YellowBird yellowBird;
    private RedBird redBird;
    private Bird activeBird;
    private Slingshot slingshot;
    private BitmapFont font;
    private boolean isExploding = false;
    private float explosionTimer = 0;
    private final float EXPLOSION_DURATION = 0.5f;
    private Texture explosionTexture;
    private boolean pig1Dead = false;
    private boolean pig2Dead = false;

    // Box2D components
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private Body birdBody;
    private Body[] woodBodies;
    private Body[] stoneBodies;
    private Body[] pigBodies;
    private ArrayList<Bird> birdQueue;
    private ShapeRenderer trajectoryRenderer;
    private Array<Body> bodiesToMakeDynamic;

    // Physics variables
    private Vector2 slingshotAnchor;
    private boolean isDragging = false;
    private boolean birdLaunched = false;
    private float launchForce = 0.8f;
    private float gravity = -9.8f * 10;

    public LevelTwo(Main game) {
        this.game = game;
        this.sb = game.batch;

        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();
        bodiesToMakeDynamic = new Array<>();

        setupContactListener();

        bg = new Texture("background.jpg");
        explosionTexture = new Texture("explosion.png");
        clickSound = Gdx.audio.newSound(Gdx.files.internal("click.mp3"));

        cam = new OrthographicCamera();
        vp = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT, cam);
        stage = new Stage(vp, game.batch);
        font = new BitmapFont();
        font.getData().setScale(2.0f);

        trajectoryRenderer = new ShapeRenderer();
        slingshotAnchor = new Vector2(185, 210);

        setupUI();
        initializeGameObjects();
        createPhysicsBodies();
        setupContactListener();
        initializeBirdQueue();
    }

    private void setupUI() {
        Table table = new Table();
        table.top().left();
        table.setFillParent(true);
        Label.LabelStyle ls = new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.WHITE);

        Label pauseLabel = new Label("Pause", ls);
        pauseLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                game.setScreen(new PauseScreen(game, curr_level));
            }
        });

        table.add(pauseLabel).padTop(10).padLeft(10);
        stage.addActor(table);
    }

    private void initializeGameObjects() {
        // Initialize wooden structures
        wood1 = new Wood_ver(500, 105);
        wood1.setSize(25, 90);
        wood2 = new Wood_ver(550, 105);
        wood2.setSize(25, 90);
        wood3 = new Wood_ver(500, 205);
        wood3.setSize(25, 90);
        wood4 = new Wood_ver(550, 205);
        wood4.setSize(25, 90);
        wood5 = new Wood_ver(600, 105);
        wood5.setSize(25, 90);
        wood6 = new Wood_ver(650, 105);
        wood6.setSize(25, 90);

        // Initialize stone platforms
        stone1 = new Stone(495, 192);
        stone1.setSize(80, 18);
        stone2 = new Stone(495, 293);
        stone2.setSize(80, 18);
        stone3 = new Stone(600, 192);
        stone3.setSize(75, 18);

        // Initialize pigs
        pig1 = new MediumPig(518, 307);
        pig1.setSize(50, 50);
        pig2 = new MediumPig(613, 207);
        pig2.setSize(50, 50);

        // Initialize birds
        blackBird = new BlackBird(185, 210);
        blackBird.setSize(40, 40);
        yellowBird = new YellowBird(90, 105);
        yellowBird.setSize(38, 40);
        redBird = new RedBird(130, 110);
        redBird.setSize(38, 38);

        activeBird = blackBird; // Start with black bird

        // Initialize other objects
        slingshot = new Slingshot(160, 110);
        slingshot.setSize(70, 110);
        //win = new Win(220, 330);
        //win.setSize(80, 30);
        //lose = new Lose(335, 330);
        //lose.setSize(80, 30);
    }

    private void initializeBirdQueue() {
        birdQueue = new ArrayList<>();
        birdQueue.add(yellowBird);
        birdQueue.add(redBird);
    }

    private void createPhysicsBodies() {
        // Create ground
        BodyDef groundDef = new BodyDef();
        groundDef.position.set(Main.V_WIDTH / 2 / PPM, (GROUND_HEIGHT-5) / PPM);
        Body groundBody = world.createBody(groundDef);
        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(Main.V_WIDTH / 2 / PPM, 10 / PPM);
        groundBody.createFixture(groundShape, 0.0f).setUserData("ground");
        groundShape.dispose();

        // Create bird body
        createBirdBody();

        // Create wood bodies
        woodBodies = new Body[6];
        Wood_ver[] woods = {wood1, wood2, wood3, wood4, wood5, wood6};
        for (int i = 0; i < woods.length; i++) {
            woodBodies[i] = createWoodBody(woods[i]);
        }

        // Create stone bodies
        stoneBodies = new Body[3];
        Stone[] stones = {stone1, stone2, stone3};
        for (int i = 0; i < stones.length; i++) {
            stoneBodies[i] = createStoneBody(stones[i]);
        }

        // Create pig bodies
        pigBodies = new Body[2];
        pigBodies[0] = createPigBody(pig1, "pig1");
        pigBodies[1] = createPigBody(pig2, "pig2");
    }

    private Body createBirdBody() {
        BodyDef birdDef = new BodyDef();
        birdDef.type = BodyDef.BodyType.KinematicBody;  // Start as kinematic so bird doesn't fall
        birdDef.position.set(activeBird.getX() / PPM, activeBird.getY() / PPM);

        birdBody = world.createBody(birdDef);
        CircleShape birdShape = new CircleShape();
        birdShape.setRadius(activeBird.getWidth() / 2 / PPM);

        FixtureDef birdFixture = new FixtureDef();
        birdFixture.shape = birdShape;
        birdFixture.density = 0.8f;     // Standard density for all birds
        birdFixture.friction = 0.2f;
        birdFixture.restitution = 0.4f;

        birdBody.createFixture(birdFixture).setUserData("bird");
        birdShape.dispose();

        return birdBody;
    }
    private Body createWoodBody(Wood_ver wood) {
        BodyDef woodDef = new BodyDef();
        woodDef.type = BodyDef.BodyType.KinematicBody;
        woodDef.position.set((wood.getX() + wood.getWidth()/2) / PPM,
            (wood.getY() + wood.getHeight()/2) / PPM);

        Body woodBody = world.createBody(woodDef);
        PolygonShape woodShape = new PolygonShape();
        woodShape.setAsBox(wood.getWidth()/2 / PPM, wood.getHeight()/2 / PPM);

        FixtureDef woodFixture = new FixtureDef();
        woodFixture.shape = woodShape;
        woodFixture.density = 3.0f;         // Increased density
        woodFixture.friction = 0.8f;        // Increased friction
        woodFixture.restitution = 0.1f;     // Reduced bounciness

        woodBody.createFixture(woodFixture).setUserData("wood");
        woodShape.dispose();
        return woodBody;
    }

    private Body createStoneBody(Stone stone) {
        BodyDef stoneDef = new BodyDef();
        stoneDef.type = BodyDef.BodyType.KinematicBody;  // Change to kinematic instead of dynamic
        stoneDef.position.set((stone.getX() + stone.getWidth()/2) / PPM,
            (stone.getY() + stone.getHeight()/2) / PPM);

        Body stoneBody = world.createBody(stoneDef);
        PolygonShape stoneShape = new PolygonShape();
        stoneShape.setAsBox(stone.getWidth()/2 / PPM, stone.getHeight()/2 / PPM);

        FixtureDef stoneFixture = new FixtureDef();
        stoneFixture.shape = stoneShape;
        stoneFixture.density = 1.0f;
        stoneFixture.friction = 0.4f;
        stoneFixture.restitution = 0.1f;

        stoneBody.createFixture(stoneFixture).setUserData("stone");
        stoneShape.dispose();
        return stoneBody;
    }

    private Body createPigBody(MediumPig pig, String identifier) {
        BodyDef pigDef = new BodyDef();
        pigDef.type = BodyDef.BodyType.DynamicBody;
        pigDef.position.set((pig.getX() + pig.getWidth()/2) / PPM,
            (pig.getY() + pig.getHeight()/2) / PPM);

        Body pigBody = world.createBody(pigDef);
        CircleShape pigShape = new CircleShape();
        pigShape.setRadius(pig.getWidth()/2 / PPM);

        FixtureDef pigFixture = new FixtureDef();
        pigFixture.shape = pigShape;
        pigFixture.density = 1.0f;
        pigFixture.friction = 0.2f;
        pigFixture.restitution = 0.2f;

        pigBody.createFixture(pigFixture).setUserData(identifier); // Use unique identifier
        pigShape.dispose();
        return pigBody;
    }

    private void setupContactListener() {
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                String userDataA = (String) fixtureA.getUserData();
                String userDataB = (String) fixtureB.getUserData();

                // Make structures dynamic on collision with bird or other structures
                if ("bird".equals(userDataA)) {
                    if ("wood".equals(userDataB) || "stone".equals(userDataB)) {
                        bodiesToMakeDynamic.add(fixtureB.getBody());
                        makeConnectedBodiesDynamic(fixtureB.getBody());
                    }
                } else if ("bird".equals(userDataB)) {
                    if ("wood".equals(userDataA) || "stone".equals(userDataA)) {
                        bodiesToMakeDynamic.add(fixtureA.getBody());
                        makeConnectedBodiesDynamic(fixtureA.getBody());
                    }
                }

                if (("wood".equals(userDataA) || "stone".equals(userDataA)) &&
                    ("wood".equals(userDataB) || "stone".equals(userDataB))) {
                    if (fixtureA.getBody().getType() == BodyDef.BodyType.DynamicBody) {
                        bodiesToMakeDynamic.add(fixtureB.getBody());
                        makeConnectedBodiesDynamic(fixtureB.getBody());
                    }
                    if (fixtureB.getBody().getType() == BodyDef.BodyType.DynamicBody) {
                        bodiesToMakeDynamic.add(fixtureA.getBody());
                        makeConnectedBodiesDynamic(fixtureA.getBody());
                    }
                }

                // Check for individual pig deaths (bird collision or ground collision)
                if (("bird".equals(userDataA) && "pig1".equals(userDataB)) ||
                    ("pig1".equals(userDataA) && "bird".equals(userDataB)) ||
                    ("ground".equals(userDataA) && "pig1".equals(userDataB)) ||
                    ("pig1".equals(userDataA) && "ground".equals(userDataB))) {
                    pig1Dead = true;
                }

                if (("bird".equals(userDataA) && "pig2".equals(userDataB)) ||
                    ("pig2".equals(userDataA) && "bird".equals(userDataB)) ||
                    ("ground".equals(userDataA) && "pig2".equals(userDataB)) ||
                    ("pig2".equals(userDataA) && "ground".equals(userDataB))) {
                    pig2Dead = true;
                }

                // Check if both pigs are dead to trigger level completion
                if (pig1Dead && pig2Dead) {
                    isExploding = true;
                    explosionTimer = 0;
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

    private void makeConnectedBodiesDynamic(Body startBody) {
        // Get all bodies that are touching or above this body
        for (Body body : getAllBodies()) {
            if (body.getType() == BodyDef.BodyType.KinematicBody) {
                String userData = (String) body.getFixtureList().first().getUserData();
                if ("wood".equals(userData) || "stone".equals(userData)) {
                    // If this body is above or touching the start body
                    if (isBodyAboveOrTouching(startBody, body)) {
                        bodiesToMakeDynamic.add(body);
                    }
                }
            }
        }
    }
    private boolean checkPigContact(Fixture a, Fixture b) {
        return ("bird".equals(a.getUserData()) && "pig".equals(b.getUserData())) ||
            ("pig".equals(a.getUserData()) && "bird".equals(b.getUserData())) ||
            ("ground".equals(a.getUserData()) && "pig".equals(b.getUserData())) ||
            ("ground".equals(b.getUserData()) && "pig".equals(a.getUserData()));
    }
    private boolean isBodyAboveOrTouching(Body baseBody, Body testBody) {
        float baseTop = baseBody.getPosition().y + getBodyHeight(baseBody);
        float testBottom = testBody.getPosition().y - getBodyHeight(testBody);

        // Check if testBody is above baseBody
        return testBottom <= baseTop + 0.1f; // Small tolerance for stability
    }
    private Array<Body> getAllBodies() {
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);
        return bodies;
    }
    private float getBodyHeight(Body body) {
        // Assuming rectangular shapes
        return body.getFixtureList().first().getShape().getRadius() * 2;
    }

    private void updatePhysicsSprites() {
        if (birdLaunched && birdBody != null) {
            activeBird.setPosition(
                birdBody.getPosition().x * PPM - activeBird.getWidth() / 2,
                birdBody.getPosition().y * PPM - activeBird.getHeight() / 2
            );
        }

        // Update wood positions
        Wood_ver[] woods = {wood1, wood2, wood3, wood4, wood5, wood6};
        for (int i = 0; i < woods.length; i++) {
            Body body = woodBodies[i];
            woods[i].setPosition(
                body.getPosition().x * PPM - woods[i].getWidth()/2,
                body.getPosition().y * PPM - woods[i].getHeight()/2
            );
            woods[i].setRotation((float) Math.toDegrees(body.getAngle()));
        }

        // Update stone positions
        Stone[] stones = {stone1, stone2, stone3};
        for (int i = 0; i < stones.length; i++) {
            Body body = stoneBodies[i];
            stones[i].setPosition(
                body.getPosition().x * PPM - stones[i].getWidth()/2,
                body.getPosition().y * PPM - stones[i].getHeight()/2
            );
            stones[i].setRotation((float) Math.toDegrees(body.getAngle()));
        }

        MediumPig[] pigs = {pig1, pig2};
        for (int i = 0; i < pigs.length; i++) {
            Body body = pigBodies[i];
            if (body != null && !isExploding) {
                pigs[i].setPosition(
                    body.getPosition().x * PPM - pigs[i].getWidth()/2,
                    body.getPosition().y * PPM - pigs[i].getHeight()/2
                );
            }
        }
    }

    private void drawTrajectory() {
        if (isDragging) {
            Vector2 birdCenter = new Vector2(
                activeBird.getX() + activeBird.getWidth() / 2,
                activeBird.getY() + activeBird.getHeight() / 2
            );

            Vector2 anchorPos = new Vector2(slingshotAnchor);
            Vector2 launchDir = anchorPos.sub(birdCenter);

            trajectoryRenderer.begin(ShapeRenderer.ShapeType.Filled);
            trajectoryRenderer.setColor(1, 1, 1, 0.5f);

            float dragDistance = launchDir.len();
            float velocityScale = Math.min(dragDistance / 100f, 1f) * launchForce;
            Vector2 vel = launchDir.nor().scl(velocityScale * 300);

            float t = 0;
            float dt = 0.1f;
            Vector2 pos = new Vector2(birdCenter);
            Vector2 gravity = new Vector2(0, this.gravity / 20);

            for (int i = 0; i < 30; i++) {
                t += dt;
                float x = pos.x + vel.x * t;
                float y = pos.y + vel.y * t + 0.5f * gravity.y * t * t;

                trajectoryRenderer.circle(x, y, 2);
            }

            trajectoryRenderer.end();
        }
    }

    private void launchBird() {
        birdBody.setType(BodyDef.BodyType.DynamicBody);


        Vector2 birdCenter = birdBody.getPosition();
        Vector2 anchor = new Vector2(slingshotAnchor.x / PPM, slingshotAnchor.y / PPM);

        Vector2 launchDir = anchor.sub(birdCenter);
        float dragDistance = Math.min(launchDir.len(), 1f);
        float launchPower = dragDistance * 2f;

        Vector2 impulse = launchDir.nor().scl(launchPower);
        birdBody.applyLinearImpulse(impulse, birdBody.getWorldCenter(), true);

        birdLaunched = true;
    }

    private boolean isBirdOnGround() {
        if (birdBody != null) {
            Vector2 position = birdBody.getPosition();
            return position.x * PPM < -50 ||
                position.x * PPM > Main.V_WIDTH + 50 ||
                position.y * PPM < GROUND_HEIGHT + 15;
        }
        return false;
    }

    private void resetBird() {
        if (birdBody != null) {
            world.destroyBody(birdBody);
        }

        if (!birdQueue.isEmpty()) {
            activeBird = birdQueue.remove(0);
            activeBird.setPosition(185, 210);
            createBirdBody();
        } else {
            game.setScreen(new LoseScreen(game, curr_level,2));
        }

        birdLaunched = false;
        isDragging = false;
    }

    @Override
    public void show() {
        com.badlogic.gdx.InputMultiplexer multiplexer = new com.badlogic.gdx.InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new com.badlogic.gdx.InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector2 worldPos = vp.unproject(new Vector2(screenX, screenY));

                if (!birdLaunched && isNearBird(worldPos)) {
                    isDragging = true;
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (isDragging) {
                    Vector2 worldPos = vp.unproject(new Vector2(screenX, screenY));
                    if (worldPos.x >= slingshotAnchor.x) {
                        worldPos.x = slingshotAnchor.x;
                    }
                    Vector2 dragDir = new Vector2(slingshotAnchor).sub(worldPos);
                    float maxDrag = 100f;
                    if (dragDir.len() > maxDrag) {
                        dragDir.nor().scl(maxDrag);
                        worldPos = new Vector2(slingshotAnchor).sub(dragDir);
                    }

                    birdBody.setTransform(
                        (worldPos.x + activeBird.getWidth()/2) / PPM,
                        (worldPos.y + activeBird.getHeight()/2) / PPM,
                        0
                    );
                    birdBody.setLinearVelocity(0, 0);
                    activeBird.setPosition(worldPos.x, worldPos.y);
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

    private boolean isNearBird(Vector2 touchPos) {
        return touchPos.dst(activeBird.getX() + activeBird.getWidth()/2,
            activeBird.getY() + activeBird.getHeight()/2) < 50;
    }

    public GameState captureGameState() {
        GameState state = new GameState();
        state.currentLevel = 2;  // Explicitly set level 2
        state.birdX = activeBird.getX();
        state.birdY = activeBird.getY();
        state.birdLaunched = birdLaunched;
        state.activeBirdType = activeBird instanceof BlackBird ? "black" :
            activeBird instanceof YellowBird ? "yellow" : "red";

        state.pigStates = new ArrayList<>();
        state.pigStates.add(new GameState.PigState(pig1.getX(), pig1.getY(), pig1Dead, "medium", "pig1"));
        state.pigStates.add(new GameState.PigState(pig2.getX(), pig2.getY(), pig2Dead, "medium", "pig2"));

        state.structureStates = new ArrayList<>();
        for (int i = 0; i < woodBodies.length; i++) {
            state.structureStates.add(new GameState.StructureState(woodBodies[i].getPosition().x * PPM,
                woodBodies[i].getPosition().y * PPM, (float)Math.toDegrees(woodBodies[i].getAngle()),
                "wood", "wood" + (i + 1)));
        }

        state.remainingBirds = new ArrayList<>();
        for (Bird bird : birdQueue) {
            String type = bird instanceof BlackBird ? "black" :
                bird instanceof YellowBird ? "yellow" : "red";
            state.remainingBirds.add(new GameState.BirdState(bird.getX(), bird.getY(), type));
        }

        return state;
    }

    public void loadGameState(GameState state) {
        // Reset active bird
        activeBird = createBirdFromType(state.activeBirdType, state.birdX, state.birdY);
        birdLaunched = state.birdLaunched;
        if (birdBody != null) world.destroyBody(birdBody);
        createBirdBody();

        // Reset pigs
        pig1Dead = state.pigStates.get(0).isDead;
        pig2Dead = state.pigStates.get(1).isDead;
        pig1.setPosition(state.pigStates.get(0).x, state.pigStates.get(0).y);
        pig2.setPosition(state.pigStates.get(1).x, state.pigStates.get(1).y);

        // Reset structures
        for (GameState.StructureState structState : state.structureStates) {
            if (structState.type.equals("wood")) {
                int index = Integer.parseInt(structState.identifier.replace("wood", "")) - 1;
                Wood_ver[] woods = {wood1, wood2, wood3, wood4, wood5, wood6};
                woods[index].setPosition(structState.x, structState.y);
                woods[index].setRotation(structState.rotation);
                woodBodies[index].setTransform(structState.x / PPM, structState.y / PPM,
                    (float)Math.toRadians(structState.rotation));
            } else if (structState.type.equals("stone")) {
                int index = Integer.parseInt(structState.identifier.replace("stone", "")) - 1;
                Stone[] stones = {stone1, stone2, stone3};
                stones[index].setPosition(structState.x, structState.y);
                stones[index].setRotation(structState.rotation);
                stoneBodies[index].setTransform(structState.x / PPM, structState.y / PPM,
                    (float)Math.toRadians(structState.rotation));
            }
        }

        // Reset bird queue
        birdQueue.clear();
        for (GameState.BirdState birdState : state.remainingBirds) {
            Bird bird = createBirdFromType(birdState.type, birdState.x, birdState.y);
            birdQueue.add(bird);
        }
    }

    private Bird createBirdFromType(String type, float x, float y) {
        Bird bird;
        switch (type.toLowerCase()) {
            case "black":
                bird = new BlackBird(x, y);
                bird.setSize(40, 40);
                break;
            case "yellow":
                bird = new YellowBird(x, y);
                bird.setSize(38, 40);
                break;
            case "red":
                bird = new RedBird(x, y);
                bird.setSize(38, 38);
                break;
            default:
                bird = new RedBird(x, y);
                bird.setSize(38, 38);
        }
        return bird;
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        world.step(1 / 60f, 6, 2);
        updatePhysicsSprites();
        blackBird.update(delta);

        if (isBirdOnGround() && !isExploding) {
            resetBird();
        }

        cam.update();
        sb.setProjectionMatrix(cam.combined);
        trajectoryRenderer.setProjectionMatrix(cam.combined);

        sb.begin();
        sb.draw(bg, 0, 0, Main.V_WIDTH, Main.V_HEIGHT);

        // Draw all game objects
        slingshot.draw(sb);

        // Draw woods
        wood1.draw(sb);
        wood2.draw(sb);
        wood3.draw(sb);
        wood4.draw(sb);
        wood5.draw(sb);
        wood6.draw(sb);

        // Draw stones
        stone1.draw(sb);
        stone2.draw(sb);
        stone3.draw(sb);

        // Draw pigs
        if (!pig1Dead) {
            pig1.draw(sb);
        }
        if (!pig2Dead) {
            pig2.draw(sb);
        }

        // Draw active bird and queue
        activeBird.draw(sb);
        for (Bird bird : birdQueue) {
            bird.draw(sb);
        }

        // Draw explosions on top of pigs
        if (pig1Dead) {
            sb.draw(explosionTexture,
                pig1.getX() - 20, pig1.getY() - 20,
                pig1.getWidth() + 40, pig1.getHeight() + 40);
        }
        if (pig2Dead) {
            sb.draw(explosionTexture,
                pig2.getX() - 20, pig2.getY() - 20,
                pig2.getWidth() + 40, pig2.getHeight() + 40);
        }

        if (isExploding) {
            explosionTimer += delta;
            if (explosionTimer >= EXPLOSION_DURATION) {
                game.setScreen(new WinScreen(game, curr_level, 2));
            }
        }

        for (Body body : bodiesToMakeDynamic) {
            body.setType(BodyDef.BodyType.DynamicBody);
        }
        bodiesToMakeDynamic.clear();

        updatePhysicsSprites();

        sb.end();

        drawTrajectory();
        debugRenderer.render(world, cam.combined.scl(PPM));

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        vp.update(width, height);
        cam.position.set(cam.viewportWidth / 2, cam.viewportHeight / 2, 0);
    }

    @Override
    public void dispose() {
        bg.dispose();
        wood1.dispose();
        wood2.dispose();
        wood3.dispose();
        wood4.dispose();
        wood5.dispose();
        wood6.dispose();
        stone1.dispose();
        stone2.dispose();
        stone3.dispose();
        pig1.dispose();
        pig2.dispose();
        blackBird.dispose();
        yellowBird.dispose();
        redBird.dispose();
        slingshot.dispose();
        stage.dispose();
        font.dispose();
        clickSound.dispose();
        explosionTexture.dispose();
        world.dispose();
        debugRenderer.dispose();
        trajectoryRenderer.dispose();
        bodiesToMakeDynamic.clear();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}
}
