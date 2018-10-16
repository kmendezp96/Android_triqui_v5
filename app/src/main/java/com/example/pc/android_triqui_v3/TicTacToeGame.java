package com.example.pc.android_triqui_v3;

/* TicTacToeConsole.java
 * By Frank McCown (Harding University)
 *
 * This is a tic-tac-toe game that runs in the console window.  The human
 * is X and the computer is O.
 */

import java.util.Random;

public class TicTacToeGame {

    private char mBoard[] = {'1','2','3','4','5','6','7','8','9'};
    public static final int BOARD_SIZE = 9;
    public static final char HUMAN_PLAYER = 'X';
    public static final char COMPUTER_PLAYER = 'O';
    public static final char OPEN_SPOT = ' ';
    public enum DifficultyLevel{Easy,Harder,Expert};
    private DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;
    private Random mRand;

    public TicTacToeGame() {
        // Seed the random number generator
        mRand = new Random();
    }

    public DifficultyLevel getDifficultyLevel() {
        return mDifficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel){
        mDifficultyLevel =  difficultyLevel;
    }

    public int getRandomMove(){
        // Generate random move
        int move;
        do
        {
            move = mRand.nextInt(BOARD_SIZE);
        } while (mBoard[move] == HUMAN_PLAYER || mBoard[move] == COMPUTER_PLAYER);
        return move;
    }

    public int getWinningMove(){
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                char curr = mBoard[i];
                mBoard[i] = COMPUTER_PLAYER;
                if (checkForWinner() == 3) {
                    return i;
                }
                else
                    mBoard[i] = curr;
            }
        }
        return -1;
    }

    public int getBlockingMove() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                char curr = mBoard[i];   // Save the current number
                mBoard[i] = HUMAN_PLAYER;
                if (checkForWinner() == 2) {
                    mBoard[i] = COMPUTER_PLAYER;
                    return i;
                }
                else
                    mBoard[i] = curr;
            }
        }
        return -1;
    }

    /**
     * Clear the board of all X and 0 by setting all spots to OPEN_SPOT
     */
    public void clearBoard(){
        for(int i=0; i<BOARD_SIZE; i++){
            mBoard[i]=OPEN_SPOT;
        }
    }

    /**
     * Set the given player at the given location on the game board
     * the location must be available, or the board will not be changed
     *
     * @param player - the HUMAN_PLAYER or COMPUTER_PLAYER
     * @param location - the location (0-8) to place the move
     */
    public boolean setMove(char player, int location){
        if(location <=8 && location >= 0 && mBoard[location]==OPEN_SPOT){
            mBoard[location]=player;
            return true;
        }
        return false;
    }

    /**
     * Return the best move for the computer to make. Must call setMove()
     * to actually make the computer move to that location.
     * @return the best move for the computer to make (0-8)
     */
    public int getComputerMove(){
        int move = -1;

        if( mDifficultyLevel == DifficultyLevel.Easy){
            move = getRandomMove();
        } else if ( mDifficultyLevel == DifficultyLevel.Harder){
            move = getWinningMove();
            if(move ==-1 ) {
                move = getRandomMove();
            }
        } else if (mDifficultyLevel == DifficultyLevel.Expert){
            move = getWinningMove();
            if(move == -1) {
                move = getBlockingMove();
            }
            if (move == -1){
                move = getRandomMove();
            }
        }


        mBoard[move] = COMPUTER_PLAYER;
        return move;
    }

    /**
     * Check for a winner and return status value indicating who has won
     * @return 0 if no winner or tie yet, 1 if it's a tie, 2 if X won, 3 if 0 won
     */
    public int checkForWinner(){
        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3)	{
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+1] == HUMAN_PLAYER &&
                    mBoard[i+2]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+1]== COMPUTER_PLAYER &&
                    mBoard[i+2] == COMPUTER_PLAYER)
                return 3;
        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+3] == HUMAN_PLAYER &&
                    mBoard[i+6]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+3] == COMPUTER_PLAYER &&
                    mBoard[i+6]== COMPUTER_PLAYER)
                return 3;
        }

        // Check for diagonal wins
        if ((mBoard[0] == HUMAN_PLAYER &&
                mBoard[4] == HUMAN_PLAYER &&
                mBoard[8] == HUMAN_PLAYER) ||
                (mBoard[2] == HUMAN_PLAYER &&
                        mBoard[4] == HUMAN_PLAYER &&
                        mBoard[6] == HUMAN_PLAYER))
            return 2;
        if ((mBoard[0] == COMPUTER_PLAYER &&
                mBoard[4] == COMPUTER_PLAYER &&
                mBoard[8] == COMPUTER_PLAYER) ||
                (mBoard[2] == COMPUTER_PLAYER &&
                        mBoard[4] == COMPUTER_PLAYER &&
                        mBoard[6] == COMPUTER_PLAYER))
            return 3;

        // Check for tie
        for (int i = 0; i < BOARD_SIZE; i++) {
            // If we find a number, then no one has won yet
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER)
                return 0;
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1;
    }

    public char getBoardOccupant(int i){
        if(mBoard[i] == HUMAN_PLAYER)
            return HUMAN_PLAYER;
        else if(mBoard[i] == COMPUTER_PLAYER)
            return COMPUTER_PLAYER;
        else return 0;
    }
    public char[] getBoardState(){
        return this.mBoard;
    }

    public void setBoardState(char[] newMBoard){
        this.mBoard = newMBoard.clone();
    }

}