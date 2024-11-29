package com.game.Screen;

import com.game.Sprites.BlackBird;
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

public class LevelThree implements Screen {
    private static final float PPM = 100f;
    private static final float GROUND_HEIGHT = 110;
    private Main game;
    private Texture bg;
    private OrthographicCamera cam;
    private Viewport vp;
    private Stage stage;
    private SpriteBatch sb;
    private Sound clickSound;
    final LevelThree curr_level = this;

    // Game objects
    private Wood_hor wood1, wood2, wood3, wood4;
    private Wood_ver woodv1, woodv2, woodv3, woodv4, woodv5, woodv6;
    private Wood_vrt wood;
    private Glass_ver glass1, glass2;
    private Stone stone;
    private SmallPig pig;
    private KingPin kpig;
    private MediumPig mpig;
    private BlackBird blackBird1, blackBird2;
    private YellowBird yellowBird1, yellowBird2;
    private RedBird redBird1, redBird2;
    private Bird activeBird;
    private Slingshot slingshot;
    private Win win;
    private Lose lose;
    private BitmapFont font;
    private boolean isExploding = false;
    private float explosionTimer = 0;
    private final float EXPLOSION_DURATION = 0.5f;
    private Texture explosionTexture;
    private boolean pigDead = false;
    private boolean kpigDead = false;
    private boolean mpigDead = false;
    private Array<Body> bodiesToMakeDynamic;


    // Box2D components
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private Body birdBody;
    private Body[] woodHorBodies;
    private Body[] woodVerBodies;
    private Body woodVrtBody;
    private Body[] glassBodies;
    private Body stoneBody;
    private Body pigBody;
    private Body kpigBody;
    private Body mpigBody;
    private ArrayList<Bird> birdQueue;
    private ShapeRenderer trajectoryRenderer;


    // Physics variables
    private Vector2 slingshotAnchor;
    private boolean isDragging = false;
    private boolean birdLaunched = false;
    private float launchForce = 0.8f;
    private float gravity = -9.8f * 10;

    public LevelThree(Main game) {
        this.game = game;
        this.sb = game.batch;
        pigDead = false;
        kpigDead = false;
        mpigDead = false;
        isExploding = false;

        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();
        bodiesToMakeDynamic = new Array<>();

        bg = new Texture("background.jpg");
        explosionTexture = new Texture("explosion.png");
        clickSound = Gdx.audio.newSound(Gdx.files.internal("click.mp3"));
        bodiesToMakeDynamic = new Array<>();

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
        // Initialize wood structures
        wood1 = new Wood_hor(514, 211);
        wood1.setSize(90, 18);
        wood2 = new Wood_hor(528, 315);
        wood2.setSize(65, 17);
        wood3 = new Wood_hor(538, 182);
        wood3.setSize(38, 12);
        wood4 = new Wood_hor(616, 200);
        wood4.setSize(93, 18);

        woodv1 = new Wood_ver(513, 105);
        woodv1.setSize(22, 110);
        woodv2 = new Wood_ver(580, 105);
        woodv2.setSize(22, 110);
        woodv3 = new Wood_ver(525, 222);
        woodv3.setSize(22, 100);
        woodv4 = new Wood_ver(575, 222);
        woodv4.setSize(22, 100);
        woodv5 = new Wood_ver(617, 105);
        woodv5.setSize(22, 103);
        woodv6 = new Wood_ver(690, 105);
        woodv6.setSize(22, 103);

        wood = new Wood_vrt(531, 350);
        wood.setSize(70, 70);

        glass1 = new Glass_ver(536, 105);
        glass1.setSize(15, 80);
        glass2 = new Glass_ver(563, 105);
        glass2.setSize(15, 80);

        stone = new Stone(542, 328);
        stone.setSize(35, 25);

        // Initialize pigs
        pig = new SmallPig(547, 226);
        pig.setSize(25, 25);
        kpig = new KingPin(544, 363);
        kpig.setSize(45, 45);
        mpig = new MediumPig(640, 107);
        mpig.setSize(50, 50);

        // Initialize birds
        blackBird1 = new BlackBird(185, 210);  // First bird (active)
        blackBird1.setSize(40, 40);
        blackBird2 = new BlackBird(50, 110);   // In queue
        blackBird2.setSize(40, 40);

        yellowBird1 = new YellowBird(90, 105); // In queue
        yellowBird1.setSize(38, 40);
        yellowBird2 = new YellowBird(50, 60);  // In queue
        yellowBird2.setSize(38, 40);

        redBird1 = new RedBird(130, 110);      // In queue
        redBird1.setSize(38, 38);
        redBird2 = new RedBird(90, 60);        // In queue
        redBird2.setSize(38, 38);

        activeBird = blackBird1;

        slingshot = new Slingshot(150, 110);
        slingshot.setSize(70, 110);
        win = new Win(220, 330);
        win.setSize(80, 30);
        lose = new Lose(335, 330);
        lose.setSize(80, 30);

        win.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new WinScreen(game , curr_level , 3));
            }
        });

        lose.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LoseScreen(game , curr_level , 3));
            }
        });
    }

    private void initializeBirdQueue() {
        birdQueue = new ArrayList<>();
        birdQueue.add(blackBird2);
        birdQueue.add(yellowBird1);
        birdQueue.add(yellowBird2);
        birdQueue.add(redBird1);
        birdQueue.add(redBird2);
    }

    private void createPhysicsBodies() {
        // Create ground
        BodyDef groundDef = new BodyDef();
        groundDef.position.set(Main.V_WIDTH / 2 / PPM, (GROUND_HEIGHT - 5) / PPM);
        Body groundBody = world.createBody(groundDef);
        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(Main.V_WIDTH / 2 / PPM, 10 / PPM);
        groundBody.createFixture(groundShape, 0.0f).setUserData("ground");
        groundShape.dispose();

        createBirdBody();

        woodHorBodies = new Body[4];
        Wood_hor[] woodsHor = {wood1, wood2, wood3, wood4};
        for (int i = 0; i < woodsHor.length; i++) {
            woodHorBodies[i] = createWoodBody(woodsHor[i], true);
        }

        woodVerBodies = new Body[6];
        Wood_ver[] woodsVer = {woodv1, woodv2, woodv3, woodv4, woodv5, woodv6};
        for (int i = 0; i < woodsVer.length; i++) {
            woodVerBodies[i] = createWoodBody(woodsVer[i], false);
        }

        woodVrtBody = createWoodVrtBody(wood);

        glassBodies = new Body[2];
        Glass_ver[] glasses = {glass1, glass2};
        for (int i = 0; i < glasses.length; i++) {
            glassBodies[i] = createGlassBody(glasses[i]);
        }

        stoneBody = createStoneBody(stone);
        pigBody = createPigBody(pig, "pig");
        kpigBody = createPigBody(kpig, "kpig");
        mpigBody = createPigBody(mpig, "mpig");
    }

    private Body createBirdBody() {
        BodyDef birdDef = new BodyDef();
        birdDef.type = BodyDef.BodyType.KinematicBody;
        birdDef.position.set(activeBird.getX() / PPM, activeBird.getY() / PPM);

        birdBody = world.createBody(birdDef);
        CircleShape birdShape = new CircleShape();
        birdShape.setRadius(activeBird.getWidth() / 2 / PPM);

        FixtureDef birdFixture = new FixtureDef();
        birdFixture.shape = birdShape;
        birdFixture.density = 0.8f;
        birdFixture.friction = 0.2f;
        birdFixture.restitution = 0.4f;

        birdBody.createFixture(birdFixture).setUserData("bird");
        birdShape.dispose();

        return birdBody;
    }

    private Body createWoodBody(Object wood, boolean isHorizontal) {
        BodyDef woodDef = new BodyDef();
        woodDef.type = BodyDef.BodyType.KinematicBody;

        float x, y, width, height;
        if (isHorizontal) {
            Wood_hor woodH = (Wood_hor) wood;
            x = woodH.getX();
            y = woodH.getY();
            width = woodH.getWidth();
            height = woodH.getHeight();
        } else {
            Wood_ver woodV = (Wood_ver) wood;
            x = woodV.getX();
            y = woodV.getY();
            width = woodV.getWidth();
            height = woodV.getHeight();
        }

        woodDef.position.set((x + width/2) / PPM, (y + height/2) / PPM);

        Body woodBody = world.createBody(woodDef);
        PolygonShape woodShape = new PolygonShape();
        woodShape.setAsBox(width/2 / PPM, height/2 / PPM);

        FixtureDef woodFixture = new FixtureDef();
        woodFixture.shape = woodShape;
        woodFixture.density = 3.0f;
        woodFixture.friction = 0.8f;
        woodFixture.restitution = 0.1f;

        woodBody.createFixture(woodFixture).setUserData("wood");
        woodShape.dispose();
        return woodBody;
    }

    private Body createWoodVrtBody(Wood_vrt woodVrt) {
        BodyDef woodDef = new BodyDef();
        woodDef.type = BodyDef.BodyType.KinematicBody;
        woodDef.position.set((woodVrt.getX() + woodVrt.getWidth()/2) / PPM,
            (woodVrt.getY() + woodVrt.getHeight()/2) / PPM);

        Body woodBody = world.createBody(woodDef);
        PolygonShape woodShape = new PolygonShape();
        woodShape.setAsBox(woodVrt.getWidth()/2 / PPM, woodVrt.getHeight()/2 / PPM);

        FixtureDef woodFixture = new FixtureDef();
        woodFixture.shape = woodShape;
        woodFixture.density = 3.0f;
        woodFixture.friction = 0.8f;
        woodFixture.restitution = 0.1f;

        woodBody.createFixture(woodFixture).setUserData("wood");
        woodShape.dispose();
        return woodBody;
    }
    private Body createGlassBody(Glass_ver glass) {
        BodyDef glassDef = new BodyDef();
        glassDef.type = BodyDef.BodyType.KinematicBody;
        glassDef.position.set((glass.getX() + glass.getWidth()/2) / PPM,
            (glass.getY() + glass.getHeight()/2) / PPM);

        Body glassBody = world.createBody(glassDef);
        PolygonShape glassShape = new PolygonShape();
        glassShape.setAsBox(glass.getWidth()/2 / PPM, glass.getHeight()/2 / PPM);

        FixtureDef glassFixture = new FixtureDef();
        glassFixture.shape = glassShape;
        glassFixture.density = 2.0f;
        glassFixture.friction = 0.3f;
        glassFixture.restitution = 0.2f;

        glassBody.createFixture(glassFixture).setUserData("glass");
        glassShape.dispose();
        return glassBody;
    }

    private Body createStoneBody(Stone stone) {
        BodyDef stoneDef = new BodyDef();
        stoneDef.type = BodyDef.BodyType.KinematicBody;
        stoneDef.position.set((stone.getX() + stone.getWidth()/2) / PPM,
            (stone.getY() + stone.getHeight()/2) / PPM);

        Body stoneBody = world.createBody(stoneDef);
        PolygonShape stoneShape = new PolygonShape();
        stoneShape.setAsBox(stone.getWidth()/2 / PPM, stone.getHeight()/2 / PPM);

        FixtureDef stoneFixture = new FixtureDef();
        stoneFixture.shape = stoneShape;
        stoneFixture.density = 5.0f;
        stoneFixture.friction = 0.4f;
        stoneFixture.restitution = 0.1f;

        stoneBody.createFixture(stoneFixture).setUserData("stone");
        stoneShape.dispose();
        return stoneBody;
    }

    private Body createPigBody(Object pig, String identifier) {
        BodyDef pigDef = new BodyDef();
        // Change initial type to Kinematic instead of Dynamic
        pigDef.type = BodyDef.BodyType.KinematicBody;

        float x, y, width, height;
        if (pig instanceof SmallPig) {
            SmallPig smallPig = (SmallPig) pig;
            x = smallPig.getX();
            y = smallPig.getY();
            width = smallPig.getWidth();
            height = smallPig.getHeight();
        } else if (pig instanceof KingPin) {
            KingPin kingPig = (KingPin) pig;
            x = kingPig.getX();
            y = kingPig.getY();
            width = kingPig.getWidth();
            height = kingPig.getHeight();
        } else {
            MediumPig mediumPig = (MediumPig) pig;
            x = mediumPig.getX();
            y = mediumPig.getY();
            width = mediumPig.getWidth();
            height = mediumPig.getHeight();
        }

        pigDef.position.set((x + width/2) / PPM, (y + height/2) / PPM);

        Body pigBody = world.createBody(pigDef);
        CircleShape pigShape = new CircleShape();
        pigShape.setRadius(width/2 / PPM);

        FixtureDef pigFixture = new FixtureDef();
        pigFixture.shape = pigShape;
        pigFixture.density = 1.0f;
        pigFixture.friction = 0.2f;
        pigFixture.restitution = 0.2f;

        pigBody.createFixture(pigFixture).setUserData(identifier);
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

                if ("bird".equals(userDataA)) {
                    if ("wood".equals(userDataB) || "stone".equals(userDataB) || "glass".equals(userDataB)) {
                        bodiesToMakeDynamic.add(fixtureB.getBody());
                    }
                } else if ("bird".equals(userDataB)) {
                    if ("wood".equals(userDataA) || "stone".equals(userDataA) || "glass".equals(userDataA)) {
                        bodiesToMakeDynamic.add(fixtureA.getBody());
                    }
                }

                // Check pig deaths
                checkPigDeath(userDataA, userDataB);

                // Check for level completion
                if (pigDead && kpigDead && mpigDead) {
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

    private void handleBirdCollision(Body structure, String structureType) {
        if ("wood".equals(structureType) || "glass".equals(structureType) || "stone".equals(structureType)) {
            bodiesToMakeDynamic.add(structure);
            makeConnectedBodiesDynamic(structure);
        }
    }


    private void checkPigDeath(String userDataA, String userDataB) {
        // Check collisions for each pig type
        if (checkPigCollision(userDataA, userDataB, "pig") ||
            checkGroundCollision(userDataA, userDataB, "pig")) {
            pigDead = true;
        }
        if (checkPigCollision(userDataA, userDataB, "kpig") ||
            checkGroundCollision(userDataA, userDataB, "kpig")) {
            kpigDead = true;
        }
        if (checkPigCollision(userDataA, userDataB, "mpig") ||
            checkGroundCollision(userDataA, userDataB, "mpig")) {
            mpigDead = true;
        }
    }

    private boolean checkPigCollision(String userDataA, String userDataB, String pigType) {
        return ("bird".equals(userDataA) && pigType.equals(userDataB)) ||
            (pigType.equals(userDataA) && "bird".equals(userDataB));
    }

    private boolean checkGroundCollision(String userDataA, String userDataB, String pigType) {
        return ("ground".equals(userDataA) && pigType.equals(userDataB)) ||
            (pigType.equals(userDataA) && "ground".equals(userDataB));
    }

    private void makeConnectedBodiesDynamic(Body startBody) {
        for (Body body : getAllBodies()) {
            if (body.getType() == BodyDef.BodyType.KinematicBody) {
                String userData = (String) body.getFixtureList().first().getUserData();
                // Check if the body is above or connected to the start body
                if (isBodyAboveOrTouching(startBody, body)) {
                    bodiesToMakeDynamic.add(body);
                    // Recursively check bodies above this one
                    makeConnectedBodiesDynamic(body);
                }
            }
        }
    }

    private Array<Body> getAllBodies() {
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);
        return bodies;
    }


    private boolean isBodyAboveOrTouching(Body baseBody, Body testBody) {
        // Get positions
        float baseTop = baseBody.getPosition().y + getBodyHeight(baseBody);
        float testBottom = testBody.getPosition().y - getBodyHeight(testBody);
        float testLeft = testBody.getPosition().x - getBodyWidth(testBody);
        float testRight = testBody.getPosition().x + getBodyWidth(testBody);
        float baseLeft = baseBody.getPosition().x - getBodyWidth(baseBody);
        float baseRight = baseBody.getPosition().x + getBodyWidth(baseBody);

        // Check if testBody is above baseBody and their x-coordinates overlap
        return testBottom <= baseTop + 0.1f &&
            testRight >= baseLeft &&
            testLeft <= baseRight;
    }

    private float getBodyHeight(Body body) {
        if (body.getFixtureList().size > 0) {
            Shape shape = body.getFixtureList().first().getShape();
            if (shape instanceof CircleShape) {
                return shape.getRadius() * 2;
            } else if (shape instanceof PolygonShape) {
                PolygonShape poly = (PolygonShape) shape;
                float[] vertices = new float[poly.getVertexCount() * 2];
                float minY = Float.MAX_VALUE;
                float maxY = Float.MIN_VALUE;

                for (int i = 0; i < poly.getVertexCount(); i++) {
                    Vector2 vertex = new Vector2();
                    poly.getVertex(i, vertex);
                    minY = Math.min(minY, vertex.y);
                    maxY = Math.max(maxY, vertex.y);
                }
                return maxY - minY;
            }
        }
        return 0;
    }

    private float getBodyWidth(Body body) {
        if (body.getFixtureList().size > 0) {
            Shape shape = body.getFixtureList().first().getShape();
            if (shape instanceof CircleShape) {
                return shape.getRadius() * 2;
            } else if (shape instanceof PolygonShape) {
                PolygonShape poly = (PolygonShape) shape;
                float[] vertices = new float[poly.getVertexCount() * 2];
                float minX = Float.MAX_VALUE;
                float maxX = Float.MIN_VALUE;

                for (int i = 0; i < poly.getVertexCount(); i++) {
                    Vector2 vertex = new Vector2();
                    poly.getVertex(i, vertex);
                    minX = Math.min(minX, vertex.x);
                    maxX = Math.max(maxX, vertex.x);
                }
                return maxX - minX;
            }
        }
        return 0;
    }




    private void updatePhysicsSprites() {
        // Update bird position
        if (birdLaunched && birdBody != null) {
            activeBird.setPosition(
                birdBody.getPosition().x * PPM - activeBird.getWidth() / 2,
                birdBody.getPosition().y * PPM - activeBird.getHeight() / 2
            );
        }

        // Update horizontal wood positions
        Wood_hor[] woodsHor = {wood1, wood2, wood3, wood4};
        for (int i = 0; i < woodsHor.length; i++) {
            Body body = woodHorBodies[i];
            woodsHor[i].setPosition(
                body.getPosition().x * PPM - woodsHor[i].getWidth()/2,
                body.getPosition().y * PPM - woodsHor[i].getHeight()/2
            );
            woodsHor[i].setRotation((float) Math.toDegrees(body.getAngle()));
        }

        // Update vertical wood positions
        Wood_ver[] woodsVer = {woodv1, woodv2, woodv3, woodv4, woodv5, woodv6};
        for (int i = 0; i < woodsVer.length; i++) {
            Body body = woodVerBodies[i];
            woodsVer[i].setPosition(
                body.getPosition().x * PPM - woodsVer[i].getWidth()/2,
                body.getPosition().y * PPM - woodsVer[i].getHeight()/2
            );
            woodsVer[i].setRotation((float) Math.toDegrees(body.getAngle()));
        }

        // Update wood_vrt position
        wood.setPosition(
            woodVrtBody.getPosition().x * PPM - wood.getWidth()/2,
            woodVrtBody.getPosition().y * PPM - wood.getHeight()/2
        );
        wood.setRotation((float) Math.toDegrees(woodVrtBody.getAngle()));

        // Update glass positions
        Glass_ver[] glasses = {glass1, glass2};
        for (int i = 0; i < glasses.length; i++) {
            Body body = glassBodies[i];
            glasses[i].setPosition(
                body.getPosition().x * PPM - glasses[i].getWidth()/2,
                body.getPosition().y * PPM - glasses[i].getHeight()/2
            );
            glasses[i].setRotation((float) Math.toDegrees(body.getAngle()));
        }

        // Update stone position
        stone.setPosition(
            stoneBody.getPosition().x * PPM - stone.getWidth()/2,
            stoneBody.getPosition().y * PPM - stone.getHeight()/2
        );
        stone.setRotation((float) Math.toDegrees(stoneBody.getAngle()));

        // Update pig positions if not dead
        if (!pigDead) {
            pig.setPosition(
                pigBody.getPosition().x * PPM - pig.getWidth()/2,
                pigBody.getPosition().y * PPM - pig.getHeight()/2
            );
        }
        if (!kpigDead) {
            kpig.setPosition(
                kpigBody.getPosition().x * PPM - kpig.getWidth()/2,
                kpigBody.getPosition().y * PPM - kpig.getHeight()/2
            );
        }
        if (!mpigDead) {
            mpig.setPosition(
                mpigBody.getPosition().x * PPM - mpig.getWidth()/2,
                mpigBody.getPosition().y * PPM - mpig.getHeight()/2
            );
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
            game.setScreen(new LoseScreen(game , curr_level , 3));
        }

        birdLaunched = false;
        isDragging = false;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        world.step(1/60f, 6, 2);
        updatePhysicsSprites();

        if (isBirdOnGround() && !isExploding) {
            resetBird();
        }

        cam.update();
        sb.setProjectionMatrix(cam.combined);
        trajectoryRenderer.setProjectionMatrix(cam.combined);

        sb.begin();
        sb.draw(bg, 0, 0, Main.V_WIDTH, Main.V_HEIGHT);

        // Draw all structures
        wood1.draw(sb);
        wood2.draw(sb);
        wood3.draw(sb);
        wood4.draw(sb);
        woodv1.draw(sb);
        woodv2.draw(sb);
        woodv3.draw(sb);
        woodv4.draw(sb);
        woodv5.draw(sb);
        woodv6.draw(sb);
        wood.draw(sb);
        glass1.draw(sb);
        glass2.draw(sb);
        stone.draw(sb);

        // Draw slingshot
        slingshot.draw(sb);

        // Draw pigs with explosion effects if dead
        if (!pigDead) {
            pig.draw(sb);
        } else {
            sb.draw(explosionTexture,
                pig.getX() - 20, pig.getY() - 20,
                pig.getWidth() + 40, pig.getHeight() + 40);
        }

        if (!kpigDead) {
            kpig.draw(sb);
        } else {
            sb.draw(explosionTexture,
                kpig.getX() - 20, kpig.getY() - 20,
                kpig.getWidth() + 40, kpig.getHeight() + 40);
        }

        if (!mpigDead) {
            mpig.draw(sb);
        } else {
            sb.draw(explosionTexture,
                mpig.getX() - 20, mpig.getY() - 20,
                mpig.getWidth() + 40, mpig.getHeight() + 40);
        }

        // Check for level completion
        if (isExploding) {
            explosionTimer += delta;
            if (explosionTimer >= EXPLOSION_DURATION) {
                game.setScreen(new WinScreen(game , curr_level , 3));
            }
        }

        // Draw active bird and queue
        activeBird.draw(sb);
        float queueX = 50;
        float queueY = 110;
        for (Bird bird : birdQueue) {
            bird.setPosition(queueX, queueY);
            bird.draw(sb);
            queueX += 40;
            if (queueX > 130) {
                queueX = 50;
                queueY -= 50;
            }
        }

        for (Body body : bodiesToMakeDynamic) {
            if (body != null && body.getType() != BodyDef.BodyType.DynamicBody) {
                body.setType(BodyDef.BodyType.DynamicBody);
            }
        }

        win.draw(sb);
        lose.draw(sb);

        sb.end();
        bodiesToMakeDynamic.clear();

        updatePhysicsSprites();

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
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        bg.dispose();
        wood1.dispose();
        wood2.dispose();
        wood3.dispose();
        wood4.dispose();
        woodv1.dispose();
        woodv2.dispose();
        woodv3.dispose();
        woodv4.dispose();
        woodv5.dispose();
        woodv6.dispose();
        wood.dispose();
        glass1.dispose();
        glass2.dispose();
        stone.dispose();
        pig.dispose();
        kpig.dispose();
        mpig.dispose();
        blackBird1.dispose();
        blackBird2.dispose();
        yellowBird1.dispose();
        yellowBird2.dispose();
        redBird1.dispose();
        redBird2.dispose();
        slingshot.dispose();
        win.dispose();
        lose.dispose();
        stage.dispose();
        font.dispose();
        clickSound.dispose();
        explosionTexture.dispose();
        world.dispose();
        debugRenderer.dispose();
        trajectoryRenderer.dispose();
    }
}
