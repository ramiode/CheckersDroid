package com.example.checkers.game.models;

import com.example.checkers.game.Player;
import com.example.checkers.game.Stone;

import java.util.LinkedList;
import java.util.List;

public class JumpAction extends Action{
    private LinkedList<Integer> path;
    private LinkedList<Stone> captured;
    public JumpAction(Player player, Stone stone, LinkedList<Integer> path, LinkedList<Stone> captured) {
        super(player, stone);
        this.path = path;
        this.captured = captured;
    }

    public JumpAction(Player player, Stone stone){
        super(player, stone);
        path = new LinkedList<>();
        captured = new LinkedList<>();
    }

    public void addJumpPosition(int position){
        if(path != null){
            path.add(position);
        }
    }

    public void addCapturedStone(Stone stone){
        if(captured != null){
            captured.add(stone);
        }
    }

    public List<Integer> getPositions(){
        return path;
    }
    public List<Stone> getCapturedStones(){
        return captured;
    }
}
