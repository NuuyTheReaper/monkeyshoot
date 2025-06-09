import greenfoot.*;
import java.util.Random;

/**
 * A subclass of World which handles all of the main logic of the game
 */
public class BloonsWorld extends World
{
    private int level = 1;
    private int totalBloons;      // jumlah balon untuk level saat ini
    private int bloonsSpawned = 0;
    private boolean levelDone = false;
    private int levelTransitionDelay = 0;
    private boolean showNextLevelMessage = false;

    private static final int WORLD_SIZE = 500;

    private Random rand; // Random object for bloon color
    private Path path;   // Path for the bloons to follow
    private int delay;   // Counter to delay bloon spawn

    private int money;   // How much money the player has
    private int lives;   // How many lives the player has

    private String placingMonkeyType = null; // Tipe monkey yang sedang dipasang (null = tidak pasang)

    // Constructor
    public BloonsWorld() {
        super(WORLD_SIZE, WORLD_SIZE, 1); // 500x500 world at 1x scale
        Greenfoot.setSpeed(50); // Set world speed to reasonable default

        // Buat path balon
        path = new Path(new Point[]{
            new Point(450, 0),
            new Point(450, 150),
            new Point(370, 150),
            new Point(370, 75),
            new Point(60 , 75),
            new Point(60 , 260),
            new Point(130, 260),
            new Point(130, 175),
            new Point(185, 175),
            new Point(185, 260),
            new Point(245, 260),
            new Point(245, 175),
            new Point(305, 175),
            new Point(305, 350),
            new Point(375, 350),
            new Point(375, 235),
            new Point(450, 235),
            new Point(450, 425),
            new Point(230, 425),
            new Point(230, 350),
            new Point(0  , 350)
        });

        delay = 0;
        rand = new Random();

        money = 100;
        lives = 200;

        // Tambah tombol monkey di pojok bawah
        addObject(new ButtonMonkey("SuperMonkey"), 400, 475);
        addObject(new ButtonMonkey("SniperMonkey"), 450, 480);

        // Mulai level pertama
        startLevel(1);
    }

    public void act() {
        // Tampilkan info lives, money, dan level
        showText("Lives: " + lives, 80, 425);
        showText("Money: $" + money, 80, 475);
        showText("Level: " + level, 80, 450);
        int remainingBloons = totalBloons - bloonsSpawned + getObjects(Bloon.class).size();
        showText("Bloons Left: " + remainingBloons, 80, 400);


        // Jika sedang mode pasang monkey dan mouse diklik di dunia
        if (placingMonkeyType != null && Greenfoot.mouseClicked(null)) {
            MouseInfo mi = Greenfoot.getMouseInfo();
            if (mi != null) {
                int x = mi.getX();
                int y = mi.getY();

                // Jangan pasang di area tombol (misal di kanan bawah 400 ke atas)
                if (x < 400) {
                    addMonkey(placingMonkeyType, x, y);
                    placingMonkeyType = null;
                    showText("", getWidth()/2, 20);
                } else {
                    showText("Cannot place here!", getWidth()/2, 20);
                }
            }
        }

        // Cek game over
        if (lives < 0) {
            Greenfoot.stop();
            if (Greenfoot.ask("Game Over. Play Again?").equalsIgnoreCase("yes")) {
                Greenfoot.setWorld(new BloonsWorld());
                Greenfoot.start();
            }
        }

        // Spawn balon sesuai level dan delay
        if (!levelDone) {
            if (delay > 120 && bloonsSpawned < totalBloons) {
                Bloon b = null;
                if (level == 1) {
                    int r = rand.nextInt(3);
                    b = (r == 0) ? new RedBloon(path) : (r == 1) ? new BlueBloon(path) : new GreenBloon(path);
                } else if (level == 2) {
                    int r = rand.nextInt(4);
                    if (r == 0) b = new RedBloon(path);
                    else if (r == 1) b = new BlueBloon(path);
                    else if (r == 2) b = new GreenBloon(path);
                    else b = new RedBloon(path);
                }

                addObject(b, 0, 0);
                bloonsSpawned++;
                delay = 0;
            } else {
                delay++;
            }

            if (bloonsSpawned == totalBloons && getObjects(Bloon.class).isEmpty()) {
                if (!showNextLevelMessage) {
                    showText("Level " + level + " Complete!", getWidth() / 2, getHeight() / 2);
                    showNextLevelMessage = true;
                    levelTransitionDelay = 0;
                } else {
                    levelTransitionDelay++;
                    if (levelTransitionDelay > 100) {
                        levelDone = true;
                        showText("", getWidth() / 2, getHeight() / 2); // hapus pesan
                        nextLevel();
                    }
                }
}

        }
    }

    private void nextLevel() {
    if (level == 1) {
        showNextLevelMessage = false;
        startLevel(2);
    } else {
        showText("YOU WIN THE GAME!", getWidth() / 2, getHeight() / 2);
        Greenfoot.delay(100); // Tahan sebentar sebelum muncul prompt
        if (Greenfoot.ask("You Win! Play Again?").equalsIgnoreCase("yes")) {
            Greenfoot.setWorld(new BloonsWorld());
        } else {
            Greenfoot.stop();
        }
    }
}


    public void addMonkey(String type, int x, int y) {
        Monkey m = null;
        if ("SuperMonkey".equalsIgnoreCase(type)) {
            m = new SuperMonkey();
        } else if ("SniperMonkey".equalsIgnoreCase(type)) {
            m = new SniperMonkey();
        }
        if (m != null) {
            addObject(m, x, y);
        }
    }

    public void setPlacingMonkeyType(String type) {
        this.placingMonkeyType = type;
        showText("Placing: " + type + " - Click on world", getWidth()/2, 20);
    }

    public void decreaseLives(int deaths) {
        lives -= deaths;
    }

    public void addMoney() {
        money++;
    }

    public void removeMoney(int cash) {
        money -= cash;
    }

    public int getMoney() {
        return money;
    }

    private void startLevel(int lvl) {
        bloonsSpawned = 0;
        delay = 0;
        levelDone = false;
        level = lvl;

        if (lvl == 1) {
            totalBloons = 100;
            setBackground("./images/map_level1.png");
        } else if (lvl == 2) {
            totalBloons = 300;
            setBackground("./images/map_level2.png");
        }
    }
}
