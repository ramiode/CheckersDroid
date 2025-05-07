package com.example.checkers.utils;

/**
 * Class for program-wide settings
 */
public class AppConfig {
    private static final int EASY_DIFFICULTY = 100;
    private static final int MEDIUM_DIFFICULTY = 500;
    private static final int HARD_DIFFICULTY = 1000;
    private static final int VERY_HARD_DIFFICULTY=  10000;
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
    public static void setTimeSlice(String s){
        switch (s) {
            case EASY:
                timeSlice = EASY_DIFFICULTY;
                break;
            case MEDIUM:
                timeSlice = MEDIUM_DIFFICULTY;
                break;
            case HARD:
                timeSlice = HARD_DIFFICULTY;
                break;
            case VERY_HARD:
                timeSlice = VERY_HARD_DIFFICULTY;
                break;
            default:
                timeSlice = 2000; // optional default case
        }
    }

}
