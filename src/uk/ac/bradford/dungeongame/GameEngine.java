/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.bradford.dungeongame;

import java.awt.Point;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import uk.ac.bradford.dungeongame.Entity.EntityType;
import uk.ac.bradford.dungeongame.Entity.numberOfPlayer;

/**
 * The GameEngine class is responsible for managing information about the game,
 * creating levels, the player and monsters, as well as updating information
 * when a key is pressed while the game is running.
 *
 * @author prtrundl Modified by Konrad Pomian. For details, see the attached
 * report.
 */
public class GameEngine {

    /**
     * An enumeration type to represent different types of tiles that make up a
     * dungeon level. Each type has a corresponding image file that is used to
     * draw the right tile to the screen for each tile in a level. Floors are
     * open for monsters and the player to move into, walls should be
     * impassable, stairs allow the player to progress to the next level of the
     * dungeon, and chests can yield a reward when moved over.
     */
    public enum TileType {
        WALL, FLOOR, STAIRS, WOODEN_CHEST, SAPPHIRE_CHEST, RUBY_CHEST
    }

    /**
     * An enumeration type to list treasures that are possible to get from a
     * chest.
     */
    public enum Treasure {
        HEALTH, TEMPORARY_DMG_BOOST, TEMPORARY_IMMORTALITY
    }

    /**
     * The width of the dungeon level, measured in tiles. Changing this may
     * cause the display to draw incorrectly, and as a minimum the size of the
     * GUI would need to be adjusted.
     */
    public static final int DUNGEON_WIDTH = 25;

    /**
     * The height of the dungeon level, measured in tiles. Changing this may
     * cause the display to draw incorrectly, and as a minimum the size of the
     * GUI would need to be adjusted.
     */
    public static final int DUNGEON_HEIGHT = 18;

    /**
     * The maximum number of monsters that can be generated on a single level of
     * the dungeon. This attribute can be used to fix the size of an array (or
     * similar) that will store monsters.
     */
    public static final int MAX_MONSTERS = 40;

    /**
     * The chance of a wall being generated instead of a floor when generating
     * the level. 1.0 is 100% chance, 0.0 is 0% chance.
     */
    public static final double WALL_CHANCE = 0.05;

    /**
     * The chance of a wooden chest being spawned on a floor when generating the
     * level.
     */
    public static final double WOODEN_CHEST_CHANCE = 0.02;

    /**
     * The chance of a sapphire chest being spawned on a floor when generating
     * the level.
     */
    public static final double SAPPHIRE_CHEST_CHANCE = 0.015;

    /**
     * The chance of a ruby chest being spawned on a floor when generating the
     * level.
     */
    public static final double RUBY_CHEST_CHANCE = 0.01;

    /**
     * A random number generator that can be used to include randomised choices
     * in the creation of levels, in choosing places to spawn the player and
     * monsters, and to randomise movement and damage. This currently uses a
     * seed value of 123 to generate random numbers - this helps you find bugs
     * by giving you the same numbers each time you run the program. Remove the
     * seed value if you want different results each game.
     */
    private Random rng = new Random();

    /**
     * The current level number for the dungeon. As the player moves down stairs
     * the level number should be increased and can be used to increase the
     * difficulty e.g. by creating additional monsters with more health. Also
     * the deeper the player is, the greater chance for chest is.
     */
    private int depth = 1;  //current dunegeon level

    /**
     * The GUI associated with a GameEngine object. THis link allows the engine
     * to pass level (tiles) and entity information to the GUI to be drawn.
     */
    private GameGUI gui;

    /**
     * The 2 dimensional array of tiles the represent the current dungeon level.
     * The size of this array should use the DUNGEON_HEIGHT and DUNGEON_WIDTH
     * attributes when it is created.
     */
    private TileType[][] tiles;

    /**
     * An ArrayList of Point objects used to create and track possible locations
     * to spawn players, monsters, a chest and the stairs.
     */
    private ArrayList<Point> spawns;

    /**
     * An Entity object that is the current player. This object stores the state
     * information for the player, including health, the current position and
     * more.
     */
    protected Entity player;

    /**
     * An Entity object that represent the second player. This Entity will be
     * spawned when F1 key is pressed. Using this option, it is possible to play
     * with second person.
     */
    protected Entity secondPlayer;

    /**
     * An array of Entity objects that represents the monsters in the current
     * level of the dungeon. Elements in this array should be of the type
     * Entity, meaning that a monster is alive and needs to be drawn or moved,
     * or should be null which means nothing is drawn or processed for movement.
     * Null values in this array are skipped during drawing and movement
     * processing. Monsters (Entity objects) that die due to player attacks can
     * be replaced with the value null in this array which removes them from the
     * game.
     */
    private Entity[] monsters;

    /**
     * Constructor that creates a GameEngine object and connects it with a
     * GameGUI object.
     *
     * @param gui The GameGUI object that this engine will pass information to
     * in order to draw levels and entities to the screen.
     */
    public GameEngine(GameGUI gui) {
        this.gui = gui;
        startGame();
    }

    /**
     * Generates a new dungeon level. The method builds a 2D array of TileType
     * values that will be used to draw tiles to the screen and to add a variety
     * of elements into each level. Tiles can be floors or walls. The method
     * should contain the implementation of an algorithm to create an
     * interesting and varied level each time it is called.
     *
     * @return A 2D array of TileTypes representing the tiles in the current
     * level of the dungeon. The size of this array should use the width and
     * height of the dungeon.
     */
    private TileType[][] generateLevel() {
        TileType[][] level = new TileType[DUNGEON_WIDTH][DUNGEON_HEIGHT];

        //Filling the level with FLOOR and WALL tiles.
        for (int i = 0; i < DUNGEON_WIDTH; i++) {
            for (int j = 0; j < DUNGEON_HEIGHT; j++) {
                double chance = rng.nextDouble();
                if (chance <= WALL_CHANCE) {
                    level[i][j] = TileType.WALL;
                } else {
                    level[i][j] = TileType.FLOOR;
                }
            }
        }

        //Filling the borders of the level with WALLS tiles.
        for (int i = 0; i < DUNGEON_WIDTH; i++) {
            if (i == 0 || i == DUNGEON_WIDTH - 1) {
                for (int j = 0; j < DUNGEON_HEIGHT; j++) {
                    level[i][j] = TileType.WALL;
                }
            } else {
                level[i][0] = TileType.WALL;
                level[i][DUNGEON_HEIGHT - 1] = TileType.WALL;
            }
        }

        tiles = level;
        return level;
    }

    /**
     * Gets a suitable spawn point and spawns the stairs on that point.
     */
    private void spawnStairs() {
        Point stairs = spawns.remove(rng.nextInt(spawns.size()));

        tiles[stairs.x][stairs.y] = TileType.STAIRS;
    }

    /**
     * Spawns chest on suitable spawn point. Type of the chest depends on the
     * double value, which will be generated by the rng.
     */
    private void spawnChest() {
        Point chest;

        for (int i = 0; i < depth * 8; i++) {
            //Picking random spawn point from the spawns and randomizing chance
            //to know, which chest should be spawned.
            chest = spawns.get(rng.nextInt(spawns.size()));
            double chance = rng.nextDouble();
            if (chance <= RUBY_CHEST_CHANCE) {
                spawns.remove(chest);
                tiles[chest.x][chest.y] = TileType.RUBY_CHEST;
                break;
                //Here the chance has to be in that division, to be equal to the
                //SAPPHIRE_CHEST_CHANCE.
            } else if (chance <= SAPPHIRE_CHEST_CHANCE + RUBY_CHEST_CHANCE
                    && chance > RUBY_CHEST_CHANCE) {
                spawns.remove(chest);
                tiles[chest.x][chest.y] = TileType.SAPPHIRE_CHEST;
                break;
                //Here the chance has to be in that division, to be equal to the
                //WOODEN_CHEST_CHANCE.
            } else if (chance <= WOODEN_CHEST_CHANCE + SAPPHIRE_CHEST_CHANCE
                    && chance > SAPPHIRE_CHEST_CHANCE) {
                spawns.remove(chest);
                tiles[chest.x][chest.y] = TileType.WOODEN_CHEST;
                break;
            }
        }
    }

    /**
     * Checks if the player stands on the tile with a chest and if so, it
     * replaces the chest with the floor and gives the player a random treasure.
     */
    private void getTreasure(Entity p) {
        //Randomizing int value to know, which boost the player should get
        int treasure = rng.nextInt(3);

        if (p != null) {
            switch (tiles[p.getX()][p.getY()]) {
                //Checking if the chest is wooden.
                case WOODEN_CHEST:
                    tiles[p.getX()][p.getY()] = TileType.FLOOR;
                    switch (treasure) {
                        case 0:
                            p.changeHealth(-rng.nextInt(p.getMaxHealth() / 4));
                            System.out.println("YOU GOT HEALTH");
                            break;
                        case 1:
                            p.changeCurrentMaxDamage(rng.nextInt(20));
                            System.out.println("YOU GOT ADDITIONAL DMG");
                            break;
                        case 2:
                            p.changeImmortality(3);
                            System.out.println("YOU GOT TEMPORARY IMMORTALITY");
                    }
                    break;
                //Checking if the chest is sapphire.
                case SAPPHIRE_CHEST:
                    tiles[p.getX()][p.getY()] = TileType.FLOOR;
                    switch (treasure) {
                        case 0:
                            p.changeHealth(-rng.nextInt(p.getMaxHealth() / 2 - p.getMaxHealth() / 4) - p.getMaxHealth() / 4);
                            System.out.println("YOU GOT HEALTH");
                            break;
                        case 1:
                            p.changeCurrentMaxDamage((rng.nextInt(1) + 1) * 20);
                            System.out.println("YOU GOT ADDITIONAL DMG");
                            break;
                        case 2:
                            p.changeImmortality(6);
                            System.out.println("YOU GOT TEMPORARY IMMORTALITY");
                    }
                    break;
                //Checking if the chest is ruby.
                case RUBY_CHEST:
                    tiles[p.getX()][p.getY()] = TileType.FLOOR;
                    switch (treasure) {
                        case 0:
                            p.changeHealth(-rng.nextInt(p.getMaxHealth() - p.getMaxHealth() / 2) - p.getMaxHealth() / 2);
                            System.out.println("YOU GOT HEALTH");
                            break;
                        case 1:
                            p.changeCurrentMaxDamage((rng.nextInt(1) + 1) * 30);
                            System.out.println("YOU GOT ADDITIONAL DMG");
                            break;
                        case 2:
                            p.changeImmortality(9);
                            System.out.println("YOU GOT TEMPORARY IMMORTALITY");
                    }
            }
        }
    }

    /**
     * Generates spawn points for players, monsters, stairs and chests. The
     * method processes the tiles array and finds tiles that are suitable for
     * spawning, i.e. tiles that are not walls or stairs. Suitable tiles should
     * be added to the ArrayList that will contain Point objects - Points are a
     * simple kind of object that contain an X and a Y co-ordinate stored using
     * the int primitive type and are part of the Java language (search for the
     * Point API documentation and examples of their use).
     *
     * The algorithm also checks if a floor tile is surrounded by four walls and
     * if so, that tile is removed from the ArrayList.
     *
     * @return An ArrayList containing Point objects representing suitable X and
     * Y co-ordinates in the current level that the player or monsters can be
     * spawned in
     */
    private ArrayList<Point> getSpawns() {
        ArrayList<Point> s = new ArrayList<Point>();

        //Adding suitable spawn points.
        Point suitableSpawn;
        for (int i = 0; i < DUNGEON_WIDTH; i++) {
            for (int j = 0; j < DUNGEON_HEIGHT; j++) {
                if (tiles[i][j] == TileType.FLOOR) {
                    //Checking if the tile is not surrounded by walls.
                    if (tiles[i + 1][j] != TileType.WALL && tiles[i - 1][j] != TileType.WALL
                            && tiles[i][j + 1] != TileType.WALL && tiles[i][j - 1] != TileType.WALL) {
                        suitableSpawn = new Point(i, j);
                        s.add(suitableSpawn);
                    }
                }
            }
        }
        spawns = s;
        return s;
    }

    /**
     * Spawns monsters in suitable locations in the current level. The method
     * uses the spawns ArrayList to pick suitable positions to add monsters,
     * removing these positions from the spawns ArrayList as they are used
     * (using the remove() method) to avoid multiple monsters spawning in the
     * same location. The method creates monsters by instantiating the Entity
     * class, setting health, damage that could be dealt by the monster, and the
     * X and Y position for the monster using the X and Y values in the Point
     * object removed from the spawns ArrayList.
     *
     * @return A array of Entity objects representing the monsters for the
     * current level of the dungeon
     */
    private Entity[] spawnMonsters() {
        Point monsterSpawn;
        Entity[] monsters;

        //Checking if depth reached maximum amout of monsters.
        if (depth > MAX_MONSTERS) {
            monsters = new Entity[MAX_MONSTERS];
        } else {
            monsters = new Entity[depth];
        }

        //Setting health of the monsters.
        int health = (int) (50 * Math.pow(1.03, depth - 1));
        int damage = (int) (20 * Math.pow(1.03, depth - 1));

        //Creating monsters.
        for (int i = 0; i < depth; i++) {
            monsterSpawn = spawns.remove(rng.nextInt(spawns.size()));
            monsters[i] = new Entity(health, damage, monsterSpawn.x, monsterSpawn.y, EntityType.MONSTER);
        }

        this.monsters = monsters;
        return monsters;
    }

    /**
     * Spawns a player entity in the game. The method uses the spawns ArrayList
     * to select a suitable location to spawn the player and removes the Point
     * from the spawns ArrayList. The method instantiates the Entity class and
     * assigns values for the number of the player, its health, damage, position
     * and type of Entity.
     *
     * @return An Entity object representing the player in the game.
     */
    private Entity spawnPlayer() {
        Point playerSpawn = spawns.remove(rng.nextInt(spawns.size()));

        //Creating the player.   
        Entity strongWarrior = new Entity(numberOfPlayer.FIRST_PLAYER, 400, 50, playerSpawn.x, playerSpawn.y, EntityType.PLAYER);
        player = strongWarrior;
        return strongWarrior;
    }

    /**
     * Spawns the second player if the adequate key has been pressed. The method
     * uses the spawns ArrayList to select a suitable location to spawn a player
     * and removes the Point from the spawns ArrayList. The method instantiates
     * the Entity class and assigns value for the number of the player, its
     * health, damage, position and type of Entity.
     *
     * @return An Entity object representing the second player in the game.
     */
    protected Entity spawnSecondPlayer() {
        Point secondPlayerSpawn = spawns.remove(rng.nextInt(spawns.size()));

        //Creating the second player
        Entity sneakyRouge = new Entity(numberOfPlayer.SECOND_PLAYER, 250, 80, secondPlayerSpawn.x, secondPlayerSpawn.y, EntityType.PLAYER);
        secondPlayer = sneakyRouge;
        return sneakyRouge;
    }

    /**
     * Handles the movement of the player when attempting to move left in the
     * game. This method is called by the DungeonInputHandler class when the
     * user has pressed the left arrow key on the keyboard. The method checks
     * whether the tile to the left of the player is empty for movement and if
     * it is updates the player object's X and Y locations with the new
     * position. If the tile to the left of the player is not empty the method
     * will not update the player position, but may make other changes to the
     * game, such as damaging a monster in the tile to the left. The method does
     * the same operations to the second player too.
     *
     * @param num the number of player, which has to be checked.
     */
    public void movePlayerLeft(numberOfPlayer num) {
        if (num == numberOfPlayer.FIRST_PLAYER) {
            player.setPreviousPosition();
            if (tiles[player.getX() - 1][player.getY()] != TileType.WALL) {
                player.setPosition(player.getX() - 1, player.getY());
            }

            playerOnPlayer(player, secondPlayer, player.getPreviousPosition());
            playerCombat(player.getPreviousPosition(), player);
            getTreasure(player);
            player.statsUpdater();

        } else {
            if (secondPlayer != null) {
                secondPlayer.setPreviousPosition();
                if (tiles[secondPlayer.getX() - 1][secondPlayer.getY()] != TileType.WALL) {
                    secondPlayer.setPosition(secondPlayer.getX() - 1, secondPlayer.getY());
                }

                playerOnPlayer(secondPlayer, player, secondPlayer.getPreviousPosition());
                playerCombat(secondPlayer.getPreviousPosition(), secondPlayer);
                getTreasure(secondPlayer);
                secondPlayer.statsUpdater();
            }
        }
    }

    /**
     * Handles the movement of the player when attempting to move right in the
     * game. This method is called by the DungeonInputHandler class when the
     * user has pressed the right arrow key on the keyboard. The method checks
     * whether the tile to the right of the player is empty for movement and if
     * it is updates the player object's X and Y locations with the new
     * position. If the tile to the right of the player is not empty the method
     * will not update the player position, but may make other changes to the
     * game, such as damaging a monster in the tile to the right.The method does
     * the same operations to the second player too.
     *
     * @param num the number of player, which has to be checked.
     */
    public void movePlayerRight(numberOfPlayer num) {
        if (num == numberOfPlayer.FIRST_PLAYER) {
            if (player != null) {
                player.setPreviousPosition();
                if (tiles[player.getX() + 1][player.getY()] != TileType.WALL) {
                    player.setPosition(player.getX() + 1, player.getY());
                }
            }

            playerOnPlayer(player, secondPlayer, player.getPreviousPosition());
            playerCombat(player.getPreviousPosition(), player);
            getTreasure(player);
            player.statsUpdater();

        } else {
            if (secondPlayer != null) {
                secondPlayer.setPreviousPosition();
                if (tiles[secondPlayer.getX() + 1][secondPlayer.getY()] != TileType.WALL) {
                    secondPlayer.setPosition(secondPlayer.getX() + 1, secondPlayer.getY());
                }

                playerOnPlayer(secondPlayer, player, secondPlayer.getPreviousPosition());
                playerCombat(secondPlayer.getPreviousPosition(), secondPlayer);
                getTreasure(secondPlayer);
                secondPlayer.statsUpdater();

            }
        }
    }

    /**
     * Handles the movement of the player when attempting to move up in the
     * game. This method is called by the DungeonInputHandler class when the
     * user has pressed the up arrow key on the keyboard. The method checks
     * whether the tile above the player is empty for movement and if it is
     * updates the player object's X and Y locations with the new position. If
     * the tile above the player is not empty the method will not update the
     * player position, but may make other changes to the game, such as damaging
     * a monster in the tile above the player.The method does the same
     * operations to the second player too.
     *
     * @param num the number of player, which has to be checked.
     */
    public void movePlayerUp(numberOfPlayer num) {
        if (num == numberOfPlayer.FIRST_PLAYER) {
            if (player != null) {
                player.setPreviousPosition();
                if (tiles[player.getX()][player.getY() - 1] != TileType.WALL) {
                    player.setPosition(player.getX(), player.getY() - 1);
                }
            }

            playerOnPlayer(player, secondPlayer, player.getPreviousPosition());
            playerCombat(player.getPreviousPosition(), player);
            getTreasure(player);
            player.statsUpdater();

        } else {
            if (secondPlayer != null) {
                secondPlayer.setPreviousPosition();
                if (tiles[secondPlayer.getX()][secondPlayer.getY() - 1] != TileType.WALL) {
                    secondPlayer.setPosition(secondPlayer.getX(), secondPlayer.getY() - 1);
                }

                playerOnPlayer(secondPlayer, player, secondPlayer.getPreviousPosition());
                playerCombat(secondPlayer.getPreviousPosition(), secondPlayer);
                getTreasure(secondPlayer);
                secondPlayer.statsUpdater();
            }
        }
    }

    /**
     * Handles the movement of the player when attempting to move right in the
     * game. This method is called by the DungeonInputHandler class when the
     * user has pressed the down arrow key on the keyboard. The method checks
     * whether the tile below the player is empty for movement and if it is
     * updates the player object's X and Y locations with the new position. If
     * the tile below the player is not empty the method will not update the
     * player position, but may make other changes to the game, such as damaging
     * a monster in the tile below the player.The method does the same
     * operations to the second player too.
     *
     * @param num the number of player, which has to be checked.
     */
    public void movePlayerDown(numberOfPlayer num) {
        if (num == numberOfPlayer.FIRST_PLAYER) {
            if (player != null) {
                player.setPreviousPosition();
                if (tiles[player.getX()][player.getY() + 1] != TileType.WALL) {
                    player.setPosition(player.getX(), player.getY() + 1);
                }
            }

            playerOnPlayer(player, secondPlayer, player.getPreviousPosition());
            playerCombat(player.getPreviousPosition(), player);
            getTreasure(player);
            player.statsUpdater();

        } else {
            if (secondPlayer != null) {
                secondPlayer.setPreviousPosition();
                if (tiles[secondPlayer.getX()][secondPlayer.getY() + 1] != TileType.WALL) {
                    secondPlayer.setPosition(secondPlayer.getX(), secondPlayer.getY() + 1);
                }

                playerOnPlayer(secondPlayer, player, secondPlayer.getPreviousPosition());
                playerCombat(secondPlayer.getPreviousPosition(), secondPlayer);
                getTreasure(secondPlayer);
                secondPlayer.statsUpdater();
            }
        }
    }

    /**
     * Checks if both are on the same tile and if so, shifts one of them to its
     * previous position.
     *
     * @param p the player, which has done its move and stands on the second
     * player
     * @param p2 the second player
     * @param previousPosition the previous position of the player, which has
     * done its move.
     */
    private void playerOnPlayer(Entity p, Entity p2, Point previousPosition) {
        if (p != null && p2 != null) {
            if (p.getCoordinates().equals(p2.getCoordinates())) {
                p.setPosition(previousPosition.x, previousPosition.y);
            }
        }
    }

    /**
     * Reduces a monster's health in response to the player attempting to move
     * into the same square as the monster (attacking the monster).
     *
     * @param m The Entity which is the monster that the player is attacking
     * @param p The player, which is attacking the monster.
     */
    private void hitMonster(Entity m, Entity p) {
        if (p != null) {
            m.changeHealth(rng.nextInt((p.getCurrentMaxDamage())));
        }
    }

    /**
     * Checks if the player is on the same tile as the monster and if so,
     * changes the monster's position to the previous one and starts combat.
     *
     * @param previousPosition the previous position of this entity
     * @param m the entity (monster) that is going to start a combat
     * @param p the player, which has been attacked.
     */
    private void monsterCombat(Point previousPosition, Entity m, Entity p) {
        if (p != null) {
            if (p.getCoordinates().equals(m.getCoordinates())) {
                m.setPosition(previousPosition.x, previousPosition.y);
                hitPlayer(m, p);
                hitMonster(m, p);
            }
        }
    }

    /**
     * Moves all monsters on the current level. The method processes all
     * non-null elements in the monsters array and calls the moveMonster method
     * for each one. Checks also if two monsters or a monster and a player are
     * on the same tile and if so, forces one of monsters to change its position
     * to the previous one.
     */
    private void moveMonsters() {
        for (int i = 0; i < monsters.length; i++) {
            if (monsters[i] != null) {
                moveMonster(monsters[i]);
                for (int j = 0; j < monsters.length; j++) {
                    //Checks if there are at least two monsters, if they are not
                    //the same Entity object and if the second one is not null.
                    if (j != i && depth > 1 && monsters[j] != null) {
                        if (player != null) {
                            //Checks if the first monster is not on the same tile
                            //as the second monster or a player.
                            if (monsters[i].getCoordinates().equals(monsters[j].getCoordinates())
                                    || monsters[i].getCoordinates().equals(player.getCoordinates())) {
                                monsters[i].setPosition(monsters[i].getPreviousPosition().x,
                                        monsters[i].getPreviousPosition().y);
                            }
                        }
                        //Checks the same as in the previous if statement, but for
                        //the second player.
                        if (secondPlayer != null) {
                            if (monsters[i].getCoordinates().equals(monsters[j].getCoordinates())
                                    || monsters[i].getCoordinates().equals(secondPlayer.getCoordinates())) {
                                monsters[i].setPosition(monsters[i].getPreviousPosition().x,
                                        monsters[i].getPreviousPosition().y);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks the shorter way from a monster to a player.
     *
     * @param p the player, which is going to be chased by the monster
     * @param m the monster, which is going to chase the player
     */
    private void chasePlayer(Entity p, Entity m) {
        if (p != null) {

            //Checking if more efficient is to go left or right insted of up or
            //down.
            if (abs(m.getXDifference(p)) >= abs(m.getYDifference(p))) {
                //Checking if more efficient is to go left or right
                if (m.getXDifference(p) > 0) {
                    m.setPosition(m.getX() + 1, m.getY());
                } else if (m.getXDifference(p) < 0) {
                    m.setPosition(m.getX() - 1, m.getY());
                }

                //Checking if more efficient is to go up or down insted of left or
                //right.
            } else if (abs(m.getXDifference(p)) < abs(m.getYDifference(p))) {
                //Checking if more efficient is to go up or down.
                if (m.getYDifference(p) > 0) {
                    m.setPosition(m.getX(), m.getY() + 1);
                } else if (m.getYDifference(p) < 0) {
                    m.setPosition(m.getX(), m.getY() - 1);
                }
            }
        }
    }

    /**
     * Checks if any wall is on the way and if so, forces a monster to stay in
     * its current position and return boolean value.
     *
     * @param m The Entity (monster) that is going to be checked.
     * @return The boolean value. It is true when the monster stands on a wall,
     * otherwise it is false.
     */
    private boolean avoid(Entity m) {
        if (tiles[m.getX()][m.getY()] == TileType.WALL) {
            m.setPosition(m.getPreviousPosition().x, m.getPreviousPosition().y);
            return true;
        }
        return false;
    }

    /**
     * Looks for another way for a monster if the avoidWall method returned true
     * value. At very beginning checks where the wall is and then figures out
     * which way would be faster. It is possible, that after the monster made
     * its move, there would be also a wall, so the algorithm checks it and if
     * so, the monster is moved to its previous position.
     *
     * @param m - The monster, which is trying to find another way.
     * @param p The player, which the monster is chasing.
     */
    private void findAnotherPath(Entity m, Entity p) {
        boolean monsterMoved = false;

        if (p != null) {
            //Checks if the wall is on the left or on the right
            if (tiles[m.getX() + 1][m.getY()] == TileType.WALL || tiles[m.getX() - 1][m.getY()] == TileType.WALL) {
                //Figures out which way is the best
                if (m.getYDifference(p) >= 0) {
                    m.setPosition(m.getX(), m.getY() + 1);
                    monsterMoved = true;
                    //Checks if the tile on which the monster stands is a wall.
                    if (tiles[m.getX()][m.getY()] == TileType.WALL) {
                        m.setPosition(m.getPreviousPosition().x, m.getPreviousPosition().y);
                        monsterMoved = false;
                    }
                } else if (m.getYDifference(p) <= 0) {
                    m.setPosition(m.getX(), m.getY() - 1);
                    monsterMoved = true;
                    //Checks ones more if the tile on which the monster stands is a wall.
                    if (tiles[m.getX()][m.getY()] == TileType.WALL) {
                        m.setPosition(m.getPreviousPosition().x, m.getPreviousPosition().y);
                        monsterMoved = false;
                    }
                }
            }

            //Checks if the monster has moved.
            if (monsterMoved) {
                return;
            }

            //Checks if the wall is on the top or on the bottom.
            if (tiles[m.getX()][m.getY() - 1] == TileType.WALL || tiles[m.getX()][m.getY() + 1] == TileType.WALL) {
                //Figures out which way is the best
                if (m.getXDifference(p) >= 0) {
                    m.setPosition(m.getX() + 1, m.getY());
                    //Checks if the monster stands on a wall or on the player.
                    if (tiles[m.getX()][m.getY()] == TileType.WALL) {
                        m.setPosition(m.getPreviousPosition().x, m.getPreviousPosition().y);
                    }
                } else if (m.getXDifference(p) <= 0) {
                    m.setPosition(m.getX() - 1, m.getY());
                    //Checks ones more if the monster stands on a wall or on the player.
                    if (tiles[m.getX()][m.getY()] == TileType.WALL) {
                        m.setPosition(m.getPreviousPosition().x, m.getPreviousPosition().y);
                    }
                }
            }
        }
    }

    /**
     * Moves a specific monster in the game. The method calls multiple methods
     * related to its movement such as checking the fastest way to a player and
     * avoiding walls. If the second player has been spawned and is still alive,
     * finds the most efficient path to the nearest player.
     *
     * @param m The Entity (monster) that needs to be moved
     */
    private void moveMonster(Entity m) {
        //Setting the previous position of the monster.
        m.setPreviousPosition();

        boolean ifWall;
        if (secondPlayer == null) {
            if (player != null) {
                chasePlayer(player, m);
                monsterCombat(m.getPreviousPosition(), m, player);
                ifWall = avoid(m);
                if (ifWall == true) {
                    findAnotherPath(m, player);
                }
            }
        } else if (secondPlayer != null) {
            if (player != null) {
                //Checking if the path to the first player is shorter than the
                //path to the second.
                if (abs(m.getXDifference(player)) + abs(m.getYDifference(player)) <= abs(m.getXDifference(secondPlayer)) + abs(m.getYDifference(secondPlayer))) {
                    chasePlayer(player, m);
                    monsterCombat(m.getPreviousPosition(), m, player);
                    ifWall = avoid(m);
                    if (ifWall == true) {
                        findAnotherPath(m, player);
                    }
                    //Checking if the path to the second player is shorter than the
                    //path to the first.
                } else if (abs(m.getXDifference(player)) + abs(m.getYDifference(player)) >= abs(m.getXDifference(secondPlayer)) + abs(m.getYDifference(secondPlayer))) {
                    chasePlayer(secondPlayer, m);
                    monsterCombat(m.getPreviousPosition(), m, secondPlayer);
                    ifWall = avoid(m);
                    if (ifWall == true) {
                        findAnotherPath(m, secondPlayer);
                    }
                }
            } else if (player == null) {
                chasePlayer(secondPlayer, m);
                monsterCombat(m.getPreviousPosition(), m, secondPlayer);
                ifWall = avoid(m);
                if (ifWall == true) {
                    findAnotherPath(m, secondPlayer);
                }
            }

        }
    }

    /**
     * Reduces the health of a player when hit by a monster - a monster next to
     * the player can attack it instead of moving and should call this method to
     * reduce the player's health.
     *
     * @param m The monster, which is attacking the player.
     * @param p The player, which has been attacked by the monster.
     */
    private void hitPlayer(Entity m, Entity p) {
        if (p != null) {
            //Checking if the player is not immortal
            if (p.getImmortality() == 0) {
                //Checking if the player has a shield
                if (p.getShield() > 0) {
                    p.removeShield(rng.nextInt(m.getCurrentMaxDamage()));
                } else {
                    p.changeHealth(rng.nextInt(m.getCurrentMaxDamage()));
                }

            }

        }
    }

    /**
     * Checks if any monster is on the same tile as a player and if so, changes
     * the player's position to the previous one and starts combat.
     *
     * @param previousPosition The player's previous position.
     * @param p The player, which is attacking.
     */
    private void playerCombat(Point previousPosition, Entity p) {
        for (int i = 0; i < monsters.length; i++) {
            if (monsters[i] != null && p != null) {
                if (p.getCoordinates().equals(monsters[i].getCoordinates())) {
                    p.setPosition(previousPosition.x, previousPosition.y);
                    hitMonster(monsters[i], p);
                    hitPlayer(monsters[i], p);
                }
            }

        }
    }

    /**
     * Processes the monsters array to find any Entity in the array with 0 or
     * less health. Any Entity in the array with 0 or less health should be set
     * to null; when drawing or moving monsters the null elements in the
     * monsters array are skipped.
     */
    private void cleanDeadMonsters() {
        for (int i = 0; i < monsters.length; i++) {
            if (monsters[i] != null) {
                if (monsters[i].getHealth() < 1) {
                    monsters[i] = null;
                }
            }
        }
    }

    /**
     * Called in response to the player moving into a Stair tile in the game.
     * The method increases the dungeon depth, generates a new level by calling
     * the generateLevel method, fills the spawns ArrayList with suitable spawn
     * locations and spawns stairs, chest and monsters. Finally it places the
     * player(s) in the new level by calling the placePlayer() method. Note that
     * a new player object should not be created here unless the health of the
     * player should be reset.
     */
    private void descendLevel() {
        depth++;
        generateLevel();
        getSpawns();
        spawnStairs();
        spawnChest();
        placePlayer(player);
        placePlayer(secondPlayer);

        for (int i = 0; i < monsters.length; i++) {
            monsters[i] = null;
        }
        spawnMonsters();
    }

    /**
     * Places the player(s) in a dungeon level by choosing a spawn location from
     * the spawns ArrayList, removing the spawn position as it is used. The
     * method sets the players position in the level by calling its setPosition
     * method with the x and y values of the Point taken from the spawns
     * ArrayList.
     */
    private void placePlayer(Entity p) {
        if (p != null) {
            Point playerSpawnPoint = spawns.remove(rng.nextInt(spawns.size()));
            p.setPosition(playerSpawnPoint.x, playerSpawnPoint.y);
        }
    }

    /**
     * Performs a single turn of the game when the user presses a key on the
     * keyboard. The method cleans dead monsters, moves any monsters still alive
     * and then checks if both players are dead, exiting the game or resetting
     * it after an appropriate output to the user is given. It checks if one of
     * the players moved into a stairs tile and calls the descendLevel method if
     * it does. Finally it requests the GUI to redraw the game level by passing
     * it the tiles, player and monsters for the current level.
     */
    public void doTurn() {
        cleanDeadMonsters();
        moveMonsters();
        if (player != null) {
            if (tiles[player.getX()][player.getY()] == TileType.STAIRS) {
                descendLevel();
            }
            if (player.getHealth() < 1) {
                player = null;
            }
        }
        if (secondPlayer != null) {
            if (tiles[secondPlayer.getX()][secondPlayer.getY()] == TileType.STAIRS) {
                descendLevel();
            }
            if (secondPlayer.getHealth() < 1) {
                secondPlayer = null;
            }
        }
        if (player == null && secondPlayer == null) {
            Scanner restartGame = new Scanner(System.in);
            String decision;

            do {
                System.out.println("\nDO YOU WANT TO RESTART THE GAME?\tY = Yes | N = No");
                decision = restartGame.nextLine();

                if (decision.equals("Y")) {
                    startGame();
                } else if (decision.equals("N")) {
                    System.exit(0);
                } else {
                    System.out.println("YOU HAD ONE JOB...\n");
                }

            } while (!(decision.equals("Y") || decision.equals("N")));
        }

        gui.updateDisplay(tiles, player, secondPlayer, monsters);
    }

    /**
     * Starts a game. This method sets the depth to one, generates a level,
     * finds spawn positions in the level, spawns stairs, chest, monsters and
     * the player and then requests the GUI to update the level on the screen
     * using the information on tiles, players and monsters.
     */
    public void startGame() {
        depth = 1;
        tiles = generateLevel();
        spawns = getSpawns();
        spawnStairs();
        spawnChest();
        monsters = spawnMonsters();
        player = spawnPlayer();
        gui.updateDisplay(tiles, player, secondPlayer, monsters);
    }

    /**
     * Requests the GUI to update the level on the screen using the information
     * on tiles, players and monsters.
     */
    public void updateGUI() {
        gui.updateDisplay(tiles, player, secondPlayer, monsters);
    }
}
