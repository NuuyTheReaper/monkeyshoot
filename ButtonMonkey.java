import greenfoot.*;

public class ButtonMonkey extends Actor {
    private String type;

    public ButtonMonkey(String type) {
        this.type = type;
        setImage(type.toLowerCase() + ".png"); // Hindari error huruf besar/kecil
    }

    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            BloonsWorld world = (BloonsWorld) getWorld();
            world.setPlacingMonkeyType(type);
        }
    }
}
