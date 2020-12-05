package com.jon.game;

import com.jon.game.graphics.Screen;
import com.jon.game.input.Keyboard;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Game extends Canvas implements Runnable {
    private static final long serialVersionUID = 1L;

    public static int width = 300;
    public static int height = width / 16 * 9;
    public static int scale = 3;
    public static String title = "Java-Game";

    private Thread thread;
    private JFrame frame;
    private Keyboard key;
    private boolean running = false;

    private Screen screen;

    private BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    // Convert image object into an array of integers, so we can create an image
    private int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

    // Constructor
    public Game() {
        Dimension size = new Dimension(width * scale, height * scale);
        setPreferredSize(size);

        frame = new JFrame();
        screen = new Screen(width, height); // Create a new screen object
        key = new Keyboard();

        addKeyListener(key);
    }

    public synchronized void start() {
        running = true;
        thread = new Thread(this, "Display");
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        final double ns = 1000000000.0 / 60.0;
        double delta = 0;

        int frames = 0;
        int updates = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            while (delta >= 1) {
                update();
                updates++;
                delta--;
            }

            render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println(updates + "ups, " + frames + "fps" );
                frame.setTitle(title + " | " + updates + " ups" + " | " + frames + " fps");
                updates = 0;
                frames = 0;
            }
        }
        stop();
    }
    int x = 0;
    int y = 0;

    public void update() {
        key.update();
        if (key.up) y--;
        if (key.down) y++;
        if (key.left) x--;
        if (key.right) x++;
    }

    public void render () {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3); // Triple buffer = speed improvement
            return;
        }

        screen.clear();
        screen.render(x, y);

        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = screen.pixels[i];
        }

        Graphics g = bs.getDrawGraphics(); // Links buffer and graphics -- creating graph. context for drawing buffer

        // Set back buffer to black
        g.setColor(Color.BLACK);
        g.fillRect(0,0, getWidth(), getHeight());

        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);

        g.dispose(); // Remove last frame graphic
        bs.show(); //Show next graphic

    }

    public static void main(String[] args) {
        Game game = new Game();
        game.frame.setResizable(false); // Make sure window is NOT resizable
        game.frame.setTitle(Game.title);
        game.frame.add(game); // Adds component into the frame
        game.frame.pack(); // Set size of frame according to component
        game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Closes program when exit button is clicked
        game.frame.setLocationRelativeTo(null); // Set window to center of screen
        game.frame.setVisible(true); // Makes sure we can see our window.

        game.start();
    }
}
