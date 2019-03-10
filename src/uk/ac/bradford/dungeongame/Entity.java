package uk.ac.bradford.dungeongame;

import java.awt.Point;

/**
 * The Entity class stores basic state information for both the Player and
 * Monster entity types in the game. The type of entity is stored as an
 * EntityType, an enumeration type local to this class.
 *
 * @author prtrundl
 * Modified by Konrad Pomian. For details, see the attached report.
 */
public class Entity {

    /**
     * EntityType is an enumeration type with two possible values, representing
     * either a Monster or the Player in the game.
     */
    public enum EntityType {
        PLAYER, MONSTER
    }

    /**
     * An enumeration type to recognize which player is the first and which is
     * the second.
     */
    public enum numberOfPlayer {
        FIRST_PLAYER, SECOND_PLAYER
    }

    /**
     * number stores information about if the player is the first or the second
     */
    private numberOfPlayer number;

    /**
     * maxHealth stores the maximum possible health for this Entity
     */
    private int maxHealth;

    /**
     * health stores the current health for this Entity
     */
    private int health;

    /**
     * shield stores additional health as a shield for this Entity
     */
    private int shield;

    /**
     * Default maximum damage that can be dealt by this Entity
     */
    private int defaultMaxDamage;

    /**
     * Current maximum damage that can be dealt by this Entity
     */
    private int currentMaxDamage;

    /**
     * Specify how many turns of immortality the player has
     */
    private int immortality;

    /**
     * xPos is the current x position in the game for this Entity
     */
    private int xPos;

    /**
     * yPos is the current y position in the game for this Entity
     */
    private int yPos;

    /**
     * The previous position of this Entity
     */
    private Point previousPosition;

    /**
     * The difference between x coordinates of a player and a monster
     */
    private static int xDifference;

    /**
     * The difference between y coordinates of a player and a monster
     */
    private static int yDifference;

    /**
     * type is used to distinguish between the player and monsters in the game
     */
    private EntityType type;

    /**
     * This constructor is used to create an Entity object to use in the game
     * for the player
     *
     * @param num number of the player
     * @param maxHealth the maximum health of this Entity, also used to set its
     * starting health value
     * @param maxDamage the maximum damage that can be dealt by this Entity
     * @param x the X position of this Entity in the game
     * @param y the Y position of this Entity in the game
     * @param type They type of Entity, either EntityType.PLAYER or
     * EntityType.MONSTER
     */
    public Entity(numberOfPlayer num, int maxHealth, int maxDamage, int x, int y, EntityType type) {
        number = num;
        this.maxHealth = maxHealth;
        health = maxHealth;
        defaultMaxDamage = maxDamage;
        currentMaxDamage = maxDamage;
        xPos = x;
        yPos = y;
        this.type = type;
    }

    /**
     * This constructor is used to create an Entity object to use in the game
     * for the monster
     *
     * @param maxHealth the maximum health of this Entity, also used to set its
     * starting health value
     * @param maxDamage the maximum damage that can be dealt by this Entity
     * @param x the X position of this Entity in the game
     * @param y the Y position of this Entity in the game
     * @param type They type of Entity, either EntityType.PLAYER or
     */
    public Entity(int maxHealth, int maxDamage, int x, int y, EntityType type) {
        this.maxHealth = maxHealth;
        health = maxHealth;
        defaultMaxDamage = maxDamage;
        currentMaxDamage = maxDamage;
        xPos = x;
        yPos = y;
        this.type = type;
    }

    /**
     * This method returns the current X position for this Entity in the game
     *
     * @return The X co-ordinate of this Entity in the game
     */
    public int getX() {
        return xPos;
    }

    /**
     * This method returns the current Y position for this Entity in the game
     *
     * @return The Y co-ordinate of this Entity in the game
     */
    public int getY() {
        return yPos;
    }

    /**
     * This method returns the current X and Y position stored in a Point
     * variable for this entity in the game.
     *
     * @return The coordinates of this Entity in the game.
     */
    public Point getCoordinates() {
        Point position = new Point(xPos, yPos);
        return position;
    }

    /**
     * This method sets the previous position of the Entity in the game.
     */
    public void setPreviousPosition() {
        previousPosition = new Point(xPos, yPos);
    }

    /**
     * This method returns the previous position for this Entity in the game.
     *
     * @return The previous position for this Entity.
     */
    public Point getPreviousPosition() {
        return previousPosition;
    }

    /**
     * This method count the distance between x coordinates of a player and a
     * monster in the game
     *
     * @param player The Entity representing the player
     */
    private void countXDifference(Entity player) {
        xDifference = player.xPos - xPos;
    }

    /**
     * This method returns the current difference between x coordinates of a
     * monster and a player.
     *
     * @param player The Entity representing the player
     * @return The difference between x coordinate of player and x coordinate of
     * monster.
     */
    public int getXDifference(Entity player) {
        countXDifference(player);
        return xDifference;
    }

    /**
     * This method count the distance between y coordinates of a player and a
     * monster in the game
     *
     * @param player the entity representing the player
     */
    private void countYDifference(Entity player) {
        yDifference = player.yPos - yPos;
    }

    /**
     * This method returns the current difference between x coordinates of a
     * player and a monster
     *
     * @param player The entity representing the player
     * @return The difference between y coordinate of player and y coordinate of
     * monster.
     */
    public int getYDifference(Entity player) {
        countYDifference(player);
        return yDifference;
    }

    /**
     * Sets the position of this Entity in the game
     *
     * @param x The new X position for this Entity
     * @param y The new Y position for this Entity
     */
    public void setPosition(int x, int y) {
        xPos = x;
        yPos = y;
    }

    /**
     * Changes the current health value for this Entity, setting the health to
     * maxHealth if the change would cause the health attribute to exceed
     * maxHealth and percent of that additional health is added to the shield.
     *
     * @param change An integer representing the change in health for this
     * Entity. Passing a negative value will increase the health, passing a
     * positive value will decrease the health.
     */
    public void changeHealth(int change) {
        health -= change;
        if (health > maxHealth) {
            addShield();
            health = maxHealth;
        }
    }

    /**
     * Returns the current health value for this Entity
     *
     * @return the value of the health attribute for this Entity
     */
    public int getHealth() {
        return health;
    }

    /**
     * Returns the maxHealth value for this Entity
     *
     * @return the value of the maxHealth attribute for this Entity
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Changes value of the shield variable if the health is greater than the
     * maxHealth.
     */
    public void addShield() {
        shield += (health - maxHealth) / 2;
    }

    /**
     * Returns the value of the shield
     *
     * @return the value of the shield attribute for this Entity
     */
    public int getShield() {
        return shield;
    }

    /**
     * Decreases durability points of the shield
     *
     * @param damage dealt to the player
     */
    public void removeShield(int damage) {
        shield -= damage;
        if (shield < 0) {
            shield = 0;
        }
    }

    /**
     * Returns defaultMaxDamage value for this Entity
     *
     * @return the value of the defaultMaxDamage attribute for this Entity
     */
    public int getDefaultMaxDamage() {
        return defaultMaxDamage;
    }

    /**
     * Returns currentMaxDamage value for this Entity
     *
     * @return the value of the currentMaxDamage attribute for this Entity
     */
    public int getCurrentMaxDamage() {
        return currentMaxDamage;
    }

    /**
     * Changes currentMaxDamage value if the player has got the damage boost.
     *
     * @param boost specify by how many point the player's maximum damage should
     * be increased
     */
    public void changeCurrentMaxDamage(int boost) {
        currentMaxDamage += boost;
    }

    /**
     * Returns a number of turns, during which the player will be immortal
     *
     * @return The number of turns of being immortal
     */
    public int getImmortality() {
        return immortality;
    }

    /**
     * Changes immortality value if the player has got the immortality boost
     *
     * @param boost The number of turns that is added to the immortality
     * attribute of this Entity
     */
    public void changeImmortality(int boost) {
        immortality += boost;
    }

    /**
     * Returns the type of this Entity, either EntityType.PLAYER or
     * EntityType.MONSTER
     *
     * @return the EntityType of this entity
     */
    public EntityType getType() {
        return type;
    }

    /**
     * Returns the number of this player.
     *
     * @return the number of the player
     */
    public numberOfPlayer getPlayer() {
        return number;
    }

    /**
     * Updates statistics of this player.
     */
    public void statsUpdater() {
        if (currentMaxDamage > defaultMaxDamage) {
            currentMaxDamage -= 2;
            if (currentMaxDamage < defaultMaxDamage) {
                currentMaxDamage = defaultMaxDamage;
            }
        }
        if (immortality > 0) {
            immortality--;
        }
    }
}
