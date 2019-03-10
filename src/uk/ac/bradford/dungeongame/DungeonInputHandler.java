package uk.ac.bradford.dungeongame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import uk.ac.bradford.dungeongame.Entity.numberOfPlayer;

/**
 * This class handles keyboard events (key presses) captured by a GameGUI object
 * that are passed to an instance of this class. The class is responsible for
 * calling methods in the GameEngine class that will update tiles, players and
 * monsters for the various key presses that are handled.
 *
 * @author prtrundl
 * Modified by Konrad Pomian. For details, see the attached report.
 */
public class DungeonInputHandler implements KeyListener {

    GameEngine engine;      //GameEngine that this class calls methods from

    /**
     * Constructor that forms a connection between a DungeonInputHandler object
     * and a GameEngine object. The GameEngine object registered here is the one
     * that will have methods called to change player and monster positions etc.
     *
     * @param eng The GameEngine object that this DungeonInputHandler is linked
     * to
     */
    public DungeonInputHandler(GameEngine eng) {
        engine = eng;
    }

    /**
     * Unused method
     *
     * @param e
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Method to handle key presses captured by the GameGUI. The method
     * currently calls the game engine to do a game turn for any key press, but
     * if the up, down, left or right arrow keys are pressed it also calls
     * methods in the engine to update the game by moving the player (and
     * monsters if implemented). Also if W, S, A or D has been pressed, it would
     * call the method in the engine to update the second player's position in
     * the game.
     *
     * @param e A KeyEvent object generated when a keyboard key is pressed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        //Checking if the second player is not alive and F1 button has been
        //pressed and if so, spawns the second player
        if (engine.secondPlayer == null) {
            if (e.getKeyCode() == KeyEvent.VK_F1) {
                engine.spawnSecondPlayer();
                engine.updateGUI();
                return;
            }
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                engine.movePlayerLeft(numberOfPlayer.FIRST_PLAYER);
                break;  //handle left arrow key
            case KeyEvent.VK_RIGHT:
                engine.movePlayerRight(numberOfPlayer.FIRST_PLAYER);
                break;//handle right arrow
            case KeyEvent.VK_UP:
                engine.movePlayerUp(numberOfPlayer.FIRST_PLAYER);
                break;      //handle up arrow
            case KeyEvent.VK_DOWN:
                engine.movePlayerDown(numberOfPlayer.FIRST_PLAYER);
                break;  //handle down arrow
            case KeyEvent.VK_A:
                engine.movePlayerLeft(numberOfPlayer.SECOND_PLAYER);
                break;  //handle A key
            case KeyEvent.VK_D:
                engine.movePlayerRight(numberOfPlayer.SECOND_PLAYER);
                break;//handle D key
            case KeyEvent.VK_W:
                engine.movePlayerUp(numberOfPlayer.SECOND_PLAYER);
                break;      //handle W key
            case KeyEvent.VK_S:
                engine.movePlayerDown(numberOfPlayer.SECOND_PLAYER);
                break;  //handle S key
            }
        engine.doTurn();    //any key press will result in this method being called

    }

    /**
     * Unused method
     *
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e) {
    }

}
