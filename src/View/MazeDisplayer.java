package View;

import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class MazeDisplayer extends Canvas {

    //<editor-fold desc="Params">
    private int[][] maze;
    private int characterPositionRow = 0;
    private int characterPositionColumn = 0;
    private int finalPostionRow;
    private int finalPostionColumn;
    private boolean showSolution = false;
    private ArrayList<AState> solution;
    private double cellHeight;
    private double cellWidth;
    private double canvasHeight ;
    private double canvasWidth ;
    //</editor-fold>


    //<editor-fold desc="Setters of maze and positions">
    void setMaze(int[][] maze) {
        this.maze = maze;
        redraw();
    }

    void setCharacterPosition(int row, int column) {
        characterPositionRow = row;
        characterPositionColumn = column;
        redraw();
    }

    void setFinalPosition(int row, int column) {
        finalPostionRow = row;
        finalPostionColumn = column;
        redraw();
    }
    //</editor-fold>

    //<editor-fold desc="Redraw maze and solution">
    /**
     * re-draw the Maze
     */
    void redraw() {
        if (maze != null) {
            cellHeight = canvasHeight / maze.length;
            cellWidth = canvasWidth / maze[0].length;

            try {
                javafx.scene.image.Image wallImage = new javafx.scene.image.Image(new FileInputStream(ImageFileNameWall.get()));
                javafx.scene.image.Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));
                javafx.scene.image.Image goalImage = new Image(new FileInputStream(ImageFileNameGoal.get()));
                javafx.scene.image.Image solutionImage = new Image(new FileInputStream(ImageFileNameSolution.get()));

                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());
                //Draw Maze
                for (int i = 0; i < maze.length; i++) {
                    for (int j = 0; j < maze[i].length; j++) {
                        if (maze[i][j] == 1) {
                            gc.drawImage(wallImage, j * cellHeight, i * cellWidth, cellHeight, cellWidth);
                        }
                    }
                }
                if (showSolution) {
                    for (int i = 0; i < solution.size(); i++) {
                        MazeState mazeState = (MazeState) solution.get(i);
                        Position solutionPosition = mazeState.getStatePosition();
                        int solRow = solutionPosition.getRowIndex();
                        int solCol = solutionPosition.getColumnIndex();
                        gc.drawImage(solutionImage, solCol * cellHeight, solRow * cellWidth, cellHeight, cellWidth);

                    }
                }
                //Draw Character
                gc.drawImage(goalImage, finalPostionColumn * cellHeight, finalPostionRow * cellWidth, cellHeight, cellWidth);
                gc.drawImage(characterImage, characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    void ReSizeDraw(Pane pane) {
        setWidth(pane.getWidth());
        setHeight(pane.getHeight());
        canvasHeight =  getWidth();
        canvasWidth =getHeight();
        redraw();
    }

    /**
     * showing the solution of the current maze
     * @param solution - of the maze
     */
    void ShowSolution(ArrayList<AState> solution) {
        this.solution = solution;
        if (!showSolution)
            showSolution = true;
        else showSolution = false;

    }
    //</editor-fold>

    //<editor-fold desc="region Properties, Setters & Getters">
    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();
    private StringProperty ImageFileNameGoal = new SimpleStringProperty();
    private StringProperty ImageFileNameSolution = new SimpleStringProperty();

    //<editor-fold desc="Getters">
    public String getImageFileNameWall() {
        return ImageFileNameWall.get();
    }

    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }

    public String getImageFileNameGoal() {
        return ImageFileNameGoal.get();
    }

    public String getImageFileNameSolution() {
        return ImageFileNameWall.get();
    }

    //</editor-fold>
    //<editor-fold desc="Setters">
    public void setImageFileNameWall(String imageFileNameWall) {

        this.ImageFileNameWall.set(imageFileNameWall);
    }

    public void setImageFileNameCharacter(String imageFileNameCharacter) {
        this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }

    public void setImageFileNameGoal(String imageFileNameGoal) {
        this.ImageFileNameGoal.set(imageFileNameGoal);
    }

    public void setImageFileNameSolution(String imageFileNameSolution) {
        this.ImageFileNameSolution.set(imageFileNameSolution);
    }
    //</editor-fold>
    //</editor-fold>

}

