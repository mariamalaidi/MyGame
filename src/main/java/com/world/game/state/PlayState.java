package com.world.game.state;

import com.world.game.util.KeyHandler;
import com.world.game.util.MouseHandler;

import java.awt.*;

public class PlayState  extends GameState{
    public PlayState(GameStateManger gameStateManger){
        super(gameStateManger);
    }

    @Override
    public void update() {

    }

    @Override
    public void input(MouseHandler mouseHandler, KeyHandler keyHandler) {

    }

    @Override
    public void render(Graphics2D graphics2D) {
        graphics2D.setColor(Color.red);
        graphics2D.fillRect(100, 100,64,64);
    }
}
