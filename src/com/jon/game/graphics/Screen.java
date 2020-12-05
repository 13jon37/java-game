package com.jon.game.graphics;

import com.jon.game.level.tile.Tile;

import java.util.Random;

public class Screen {

    private int width, height; // W and H of our screen
    public int[] pixels; // Contains all of our pixel data
    public final int MAP_SIZE = 16;
    public final int MAP_SIZE_MASK = MAP_SIZE - 1;

    public int[] tiles = new int[MAP_SIZE * MAP_SIZE];

    private Random random = new Random();

    // Constructor
    public Screen(int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new int[width * height];

        for (int i = 0; i < MAP_SIZE * MAP_SIZE; i++) {
            tiles[i] = random.nextInt(0xffffff);
        }
    }

    public void clear() {
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = 0; // Set every pixel to black
        }
    }

    /*
    * Each iteration will render one pixel to the screen
    * The x for loop is nested so we can actually render all the pixels
    * (for every y pixel fill in all the x pixels)
    * */
    public void render(int xOffset, int yOffset) {
        for (int y =0; y < height; y++) {
            int yp = y + yOffset;
            if (yp < 0 || yp >= height) continue;
            for (int x = 0; x < width; x++) {
                int xp = x + xOffset;
                if (xp < 0 || xp >= width) continue;
                // [x + y *width] is the index we want to access
                pixels[xp  + yp * width] = Sprite.grass.pixels[(x & 15) + (y & 15) * Sprite.grass.SIZE];
            }
        }
    }

    public void renderTile(int xp, int yp, Tile tile) {
        for (int y = 0; y < tile.sprite.SIZE; y++) {
            // Set absolute y
            int ya = y + yp;
            for (int x = 0; x < tile.sprite.SIZE; x ++) {
                // Set absolute x
                int xa = x + xp;
                // Only render tiles we can see
                if (xa  < 0 || xa >= width || ya < 0 || ya > width) break;
                pixels[xa + ya * width] = tile.sprite.pixels[x + y * tile.sprite.SIZE];
            }
        }
    }

}
