package com.example.checkers.utils;

/**
 * Class for program-wide settings
 */
public class AppConfig {
    private static final int EASY_DIFFICULTY = 100;
    private static final int MEDIUM_DIFFICULTY = 500;
    private static final int HARD_DIFFICULTY = 1000;
    private static final int VERY_HARD_DIFFICULTY=  5000;
    public static final String EASY = "Easy";
    public static final String MEDIUM = "Medium";
    public static final String HARD = "Hard";
    public static final String VERY_HARD = "Very hard";
    public static final String MINIMAX = "Minimax";
    public static final String MCTS = "MCTS";
    public static final String HUMAN = "Human";
    public static boolean isPlayerOneHuman;
    public static boolean isPlayerTwoHuman;
    public static int timeSlice = 1000;
    public static int mcts_budget = 1000;
    public static int minimax_depth = 5;
    public static String playerOneModel = MINIMAX;
    public static String playerTwoModel = MINIMAX;

    public static void setPlayerModel(boolean isPlayerOne, String s){
        if(isPlayerOne){
            playerOneModel = s;
            isPlayerOneHuman = s.equals(HUMAN) ? true : false;
        }
        else{
            playerTwoModel = s;
            isPlayerTwoHuman = s.equals(HUMAN) ? true : false;
        }
    }
    public static void setDifficulty(String s){
        switch (s) {
            case EASY:
                minimax_depth = 3;
                mcts_budget = 500;
                break;
            case MEDIUM:
                minimax_depth = 5;
                mcts_budget = 2000;
                break;
            case HARD:
                minimax_depth = 7;
                mcts_budget = 4000;
                break;
            case VERY_HARD:
                minimax_depth = 8;
                mcts_budget = 8000;
                break;
        }
    }

}
