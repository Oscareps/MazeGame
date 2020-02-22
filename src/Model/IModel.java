package Model;

import algorithms.search.AState;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;

public interface IModel { //Maze
    void generateMaze(int width, int height);
    int[][] getMaze();
    ArrayList<AState> getSolution();

    //Character
    void moveCharacter(KeyCode movement);
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
    int getFinalPositionRow();
    int getFinalPositionColumn();
    void setToStart();
    void saveMaze(String nameOfTheMaze, String path);
    void loadMaze(String path);
    String getMazeGenerator();
    String getAlgorithm();
    String getThreadNumber();
    boolean mazeExist();

        //
    void close();
}
