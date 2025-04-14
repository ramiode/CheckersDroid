package com.example.checkers.game;

public class HumanPlayer extends Player{
    private volatile MoveAction selectedAction;
    /**
     * Constructor for human player.
     *
     * @param isRed Set as true if player should be red
     */
    public HumanPlayer(boolean isRed, String name) {
        super(isRed, name);
    }

    @Override
    public MoveAction getNextMove(){
        return selectedAction;
    }

    @Override
    public boolean isHuman() {
        return true;
    }
    @Override
    public void setNextMove(MoveAction nextMove){
        selectedAction = nextMove;
    }
}
