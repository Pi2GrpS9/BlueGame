package com.example.android.bluetoothchat;

import android.util.Log;

/**
 * Created by kel on 10/11/15.
 */
public class Game {

    /**     This class is describing the game pattern for TIC TAC TOE Game
     *      we are using an int Array to store our value :
     *
     *      -------------------------
     *      |   0   |   1   |   2   |
     *      -------------------------
     *      |   3   |   4   |   5   |
     *      -------------------------
     *      |   6   |   7   |   8   | OKK
     *      -------------------------
     *
     */


    private int currentPlayer = 0;
    private int[] gameArray;

    //the number of the first player have to be given in the constructor parameter
    public Game(int currentPlayer) {
        this.currentPlayer = currentPlayer;
        this.gameArray = new int[] {0,0,0,0,0,0,0,0,0};
    }


    // Add the value to the array
    public boolean markACell(int location) {

        if(gameArray != null && gameArray.length != 0) {
            if(location <= gameArray.length) {
                if(gameArray[location] == 0) {
                    gameArray[location] = currentPlayer;
                    Log.d("Game", currentPlayer + " added mark on cell number " + location);
                    return true;
                } //end test free cell
            }  //end test length using location
        } //end test null & length
        return false;
    }

    // change to the next player (return the player who have to play)
    public int changePlayer(){
        switch(currentPlayer){
            case 1 :
                currentPlayer = 2;
                break;
            case 2 :
                currentPlayer = 1;
                break;
            default:
                currentPlayer = 0;
                break;
        }
        return currentPlayer;
    }

    // check if the game is over
    public boolean checkForWin() {
        // checking lines
        if(gameArray[0] != 0 && gameArray[0] == gameArray[1] && gameArray[1] == gameArray[2]) { return true; }
        else if(gameArray[3] != 0 && gameArray[3] == gameArray[4] && gameArray[4] == gameArray[5]) { return true; }
        else if(gameArray[6] != 0 && gameArray[6] == gameArray[7] && gameArray[7] == gameArray[8]) { return true; }
        // checking columns
        else if(gameArray[0] != 0 && gameArray[0] == gameArray[3] && gameArray[3] == gameArray[6]) { return true; }
        else if(gameArray[1] != 0 && gameArray[1] == gameArray[4] && gameArray[4] == gameArray[7]) { return true; }
        else if(gameArray[2] != 0 && gameArray[2] == gameArray[5] && gameArray[5] == gameArray[8]) { return true; }
        // checking diagonals
        else if(gameArray[0] != 0 && gameArray[0] == gameArray[4] && gameArray[4] == gameArray[8]) { return true; }
        else if(gameArray[2] != 0 && gameArray[2] == gameArray[4] && gameArray[4] == gameArray[6]) { return true; }
        return false;
    }


    public int getCurrentPlayer() {
        return currentPlayer;
    }
}
