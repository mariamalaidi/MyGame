package com.world.game;
import com.world.game.state.GameState;
import com.world.game.state.GameStateManger;
import com.world.game.util.KeyHandler;
import com.world.game.util.MouseHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GamePanel extends JPanel implements Runnable{
    public static int width;
    public static int height;
    private Thread thread;
    private BufferedImage image;
    private Graphics2D graphics2D;
    private boolean running = false;
    private MouseHandler mouseHandler;
    private KeyHandler keyHandler;
    private GameStateManger gameStateManger;

    public GamePanel(int width, int height){
        GamePanel.width = width;
        GamePanel.height = height;
        setPreferredSize(new Dimension(width, height));
        setFocusable(true);
        requestFocus();
    }

    public void addNotify(){
        super.addNotify();
        if(thread == null ){
            thread = new Thread(this, "Game Thread");
            thread.start();
        }
    }

    public void init(){
        running = true;
        image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        graphics2D = (Graphics2D) image.getGraphics();
        mouseHandler = new MouseHandler();
        keyHandler = new KeyHandler();
        gameStateManger = new GameStateManger();

    }

    @Override
    public void run() {
        init();
        final double GAME_HERTZ = 60.0;
        final double TIME_BEFORE_UPDATE = 1000000000/ GAME_HERTZ;
        final int MOST_UPDATE_BEFORE_RENDER  = 5;
        double lastUpdateTime = System.nanoTime();
        double lastRendered;
        final double TARGET_FPS = 60;
        final double TOTAL_TIME_BEFORE_RENDER = 1000000000 / TARGET_FPS;
        int frameCount = 0;
        int lastSecondTime =  (int) (lastUpdateTime/1000000000 );
        int oldFrameCount = 0 ;

        while(running) {
            double now = System.nanoTime();
            int updateCount = 0;
            while (((now - lastUpdateTime) > TIME_BEFORE_UPDATE) && (updateCount < MOST_UPDATE_BEFORE_RENDER)) {
                update();
                input(mouseHandler, keyHandler);
                lastUpdateTime += TIME_BEFORE_UPDATE;
                updateCount++;
            }
            if ((now - lastUpdateTime) > TIME_BEFORE_UPDATE) {
                lastUpdateTime = now - TIME_BEFORE_UPDATE;
            }
            input(mouseHandler, keyHandler);
            render();
            lastRendered = now;
            frameCount++;
            draw();

            int thisSecond = (int) (lastUpdateTime / 1000000000);
            if (thisSecond > lastSecondTime){
                if (frameCount != oldFrameCount) {
                    System.out.println("New Second " + thisSecond + " Frames " + frameCount);
                    oldFrameCount = frameCount;
                }
                frameCount = 0;
                lastSecondTime = thisSecond;
            }

            while(now - lastRendered < TOTAL_TIME_BEFORE_RENDER & now - lastUpdateTime < TIME_BEFORE_UPDATE){
                Thread.yield();
                try{
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    System.out.println("Thread yielding");
                }
                now = System.nanoTime();
            }
        }

    }

    public void input(MouseHandler mouseHandler, KeyHandler keyHandler){
        gameStateManger.input(mouseHandler, keyHandler);
    }

    public void update(){
        gameStateManger.update();
    }

    public void render(){
        if(graphics2D != null){
            graphics2D.setColor(new Color(66, 134,244));
            graphics2D.fillRect(0,0,width,height);
            gameStateManger.render(graphics2D);
        }
    }

    public void draw(){
        Graphics graphics = (Graphics) this.getGraphics();
        graphics.drawImage(image, 0, 0, width,height,null);
        graphics.dispose();
    }
}
