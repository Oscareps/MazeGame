package ViewModel;

import Model.IModel;
import algorithms.search.AState;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;


public class ViewModel extends Observable implements Observer {

    private IModel model;

    /**
     * Builder
     * @param model - getting from main
     */
    public ViewModel(IModel model) {
        this.model = model;
    }

    //<editor-fold desc="Take care Observable">
    @Override
    public void update(Observable o, Object arg) {
        if (o == model) {
            //Notify my observer (View) that I have changed
            setChanged();
            notifyObservers();
        }
    }
    //</editor-fold>

    //<editor-fold desc="ViewModel Functionality">
    public void generateMaze(int width, int height) {
        model.generateMaze(width, height);
    }

    public void moveCharacter(KeyCode movement) {
        model.moveCharacter(movement);
    }
    //</editor-fold>

    //<editor-fold desc="Getters">
    public int[][] getMaze() {
        return model.getMaze();
    }

    public int getCharacterPositionRow() {
        return model.getCharacterPositionRow();
    }

    public int getCharacterPositionColumn() {
        return model.getCharacterPositionColumn();
    }

    public int getFinalPositionRow() {

        return model.getFinalPositionRow();
    }

    public int getFinalPositionColumn() {
        return model.getFinalPositionColumn();
    }

    public ArrayList<AState> getSolution() {
        return model.getSolution();
    }

    public String getMazeGenerator(){

        return model.getMazeGenerator();
    }
    public String getAlgorithm(){
        return model.getAlgorithm();
    }
    public String getThreadNumber(){
        return model.getThreadNumber();
    }
    //</editor-fold>

    //<editor-fold desc="Setters">
    public void setToStart() {
        model.setToStart();
    }
    //</editor-fold>

    //<editor-fold desc="Save & load Maze">
    public void saveMaze(String name, String path) {

        model.saveMaze(name, path);
    }


    public void loadMaze(String path) {

        model.loadMaze(path);
    }
    //</editor-fold>

    //<editor-fold desc="Close & Exit from the Maze">
    public boolean closeGame() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // ... user chose OK
            // Close the program properly
            model.close();
            return true;
        }
        return false;
    }
    public boolean mazeExist(){
        if(model.getMaze()!=null){
            return true;
        }
        return false;
    }
    //</editor-fold>
}