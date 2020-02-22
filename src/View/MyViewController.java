package View;

import ViewModel.ViewModel;
import algorithms.search.AState;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MyViewController implements Observer, IView {

    //<editor-fold desc="Fields">
    //Controls
    public MazeDisplayer mazeDisplayer;
    public Button btn_generateMazeEasy;
    public Button btn_generateMazeMedium;
    public Button btn_generateMazeHard;
    public Button btn_solveMaze;
    public Button btn_playStop;
    public javafx.scene.layout.Pane pane;
    private Media media;
    private MediaPlayer mediaPlayer;
    private boolean playBoolean = false;
    private boolean showingSol = false;
    private ArrayList<AState> solution;
    //Properties - For Binding
    private StringProperty characterPositionRow = new SimpleStringProperty("1");
    private StringProperty characterPositionColumn = new SimpleStringProperty("1");
    private boolean winningSong = false;
    @FXML
    private ViewModel viewModel;
    private Scene mainScene;
    private Stage mainStage;
    //</editor-fold>

    //<editor-fold desc="Set Up">
    public void initialize(ViewModel viewModel, Stage mainStage, Scene mainScene) {
        this.viewModel = viewModel;
        this.mainScene = mainScene;
        this.mainStage = mainStage;
        setResizeEvent();
    }
    //</editor-fold>

    //<editor-fold desc="Update Maze">
    @Override
    public void update(Observable o, Object arg) {
        //int finalPositionRow = viewModel.getFinalPositionRow();
        //int finalPositionCol = viewModel.getFinalPositionColumn();
        if (o == viewModel) {
            displayMaze(viewModel.getMaze());
            btn_generateMazeEasy.setDisable(false);
            btn_generateMazeMedium.setDisable(false);
            btn_generateMazeHard.setDisable(false);
        }
        if (viewModel.getCharacterPositionRow() == viewModel.getFinalPositionRow() && viewModel.getCharacterPositionColumn() == viewModel.getFinalPositionColumn()
                && !winningSong) {
            WinningMusic();
            Win();
            winningSong = true;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Maze Display">
    @Override
    public void displayMaze(int[][] maze) {
        mazeDisplayer.setMaze(maze);
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        int finalPositionRow = viewModel.getFinalPositionRow();
        int finalPositionCol = viewModel.getFinalPositionColumn();
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
        btn_solveMaze.setDisable(false);
        System.out.println("" + finalPositionRow + " " + finalPositionCol);
        mazeDisplayer.setFinalPosition(finalPositionRow, finalPositionCol);
    }
    //</editor-fold>


    //<editor-fold desc="Maze Generate & Solve">
    public void generateEasyMaze() {
        winningSong = false;
        btn_generateMazeEasy.setDisable(true);
        btn_solveMaze.setDisable(true);
        viewModel.generateMaze(17, 17);
        viewModel.setToStart();
        unAbleSol();
    }

    public void generateMediumMaze() {
        winningSong = false;
        //int height = Integer.valueOf(39);
        //int width = Integer.valueOf(39);
        btn_generateMazeEasy.setDisable(true);
        btn_solveMaze.setDisable(true);
        viewModel.generateMaze(39, 39);
        viewModel.setToStart();
        unAbleSol();
    }

    public void generateHardMaze() {
        winningSong = false;
        //int height = Integer.valueOf(61);
        //int width = Integer.valueOf(61);
        btn_generateMazeEasy.setDisable(true);
        btn_solveMaze.setDisable(true);
        viewModel.generateMaze(61, 61);
        viewModel.setToStart();
        unAbleSol();
    }

    private void unAbleSol() {
        if (showingSol) {
            mazeDisplayer.ShowSolution(solution);
            mazeDisplayer.redraw();
            showingSol = false;
        }
    }

    public void solveMaze(ActionEvent actionEvent) {
        solution = viewModel.getSolution();
        if (!showingSol) {
            showAlert("Showing solution, press again to disable ");
            showingSol = true;
            mazeDisplayer.ShowSolution(solution);
            mazeDisplayer.redraw();
        } else if (showingSol) {
            mazeDisplayer.ShowSolution(solution);
            mazeDisplayer.redraw();
            showingSol = false;
        }
    }
    //</editor-fold>

    //<editor-fold desc="View Functionality">
    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.showAndWait();
    }

    /*
    private void finshAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }
    */

    public void KeyPressed(KeyEvent keyEvent) {
        if (viewModel.mazeExist()) {
            viewModel.moveCharacter(keyEvent.getCode());
            keyEvent.consume();
        }
    }

    private void setResizeEvent() {
        this.pane.widthProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Width: " + newValue);
            mazeDisplayer.ReSizeDraw(pane);
        });

        this.pane.heightProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Height: " + newValue);
            mazeDisplayer.ReSizeDraw(pane);
        });
    }

    public void onScroll(ScrollEvent scrollEvent) {
        if (viewModel.mazeExist()) {
            if (scrollEvent.isControlDown()) {
                double zoomSize = 1.053;
                if (scrollEvent.getDeltaY() < 0) {
                    zoomSize = 2.0 - zoomSize;
                }
                mazeDisplayer.setScaleX(mazeDisplayer.getScaleX() * zoomSize);
                mazeDisplayer.setScaleY(mazeDisplayer.getScaleY() * zoomSize);
            }
        }
    }

    private void Win() {
        try {
            Stage stage = new Stage();
            stage.setTitle("You're The Best");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("WinPage.fxml").openStream());
            Scene scene = new Scene(root, 295, 220);
            scene.getStylesheets().add(getClass().getResource("WinPage.css").toExternalForm());
            stage.setResizable(false);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Check this one
    public void mouseClicked(MouseEvent mouseEvent) {
        this.mazeDisplayer.requestFocus();
    }
    //endregion
    //</editor-fold>

    //<editor-fold desc="Menu Items">

    /**
     * Help menu
     *
     * @param actionEvent;
     */
    public void openHelp(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Help");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Help.fxml").openStream());
            Scene scene = new Scene(root, 850, 450);
            scene.getStylesheets().add(getClass().getResource("Help.css").toExternalForm());
            stage.setResizable(false);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * About menu
     *
     * @param actionEvent;
     */
    public void About(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("About");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
            Scene scene = new Scene(root, 595, 320);
            scene.getStylesheets().add(getClass().getResource("About.css").toExternalForm());
            stage.setResizable(false);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //<editor-fold desc="Save & Load">

    /**
     * save the current maze
     * @param actionEvent;
     */
    public void saveMazeWindow(ActionEvent actionEvent) {
        if(!viewModel.mazeExist()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cannot save");
            alert.setHeaderText("There is no maze to save");
            alert.setContentText("To be able to save please generate a maze");
            alert.showAndWait();
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        //Show save file dialog
        File file = fileChooser.showSaveDialog(mainStage);
        if (file != null) {
            viewModel.saveMaze(file.getName(), file.getPath());
        }
    }

    /**
     * load the maze and the solution from saved one
     * @param actionEvent;
     */
    public void loadMaze(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Mazes", "*.Maze*"));
        File file = fileChooser.showOpenDialog(mainStage);
        if (fileChooser != null && file != null) {
            if (!((file.getPath().contains(".Maze")) || file.getPath().contains("CharacterPosition") || file.getPath().contains(("Solution")))) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Warning");
                alert.setHeaderText("This file is invalid");
                alert.setContentText("Cannot open this file," +
                        "\nPlease choose the correct file");
                alert.showAndWait();
                loadMaze(actionEvent);
            } else {
                viewModel.loadMaze(file.getPath());
            }
        }
    }
    //</editor-fold>

    /**
     * closing game from menu
     */
    public void closeGame() {
        if(viewModel.closeGame())
        {
            mainStage.close();
        }
    }

    /**
     * Showing the properties if the maze
     */
    public void showProp(){
        VBox propVBox= new VBox(35);
        Label generateLbl =new Label("Maze Generator Type:   "+ viewModel.getMazeGenerator());
        Label algoLbl =new Label("Search Algorithm Type:   "+ viewModel.getAlgorithm());
        Label threadsLbl =new Label("Number of Threads:   "+ viewModel.getThreadNumber());
        propVBox.getChildren().addAll(generateLbl,algoLbl,threadsLbl);
        propVBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(propVBox, 350, 200);
        Stage propStage = new Stage();
        propStage.setTitle("Properties");
        propStage.setScene(scene );
        propStage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
        propStage.showAndWait();
    }

    /**
     * open a new Maze
     * @param actionEvent;
     */
    public void newMaze(ActionEvent actionEvent) {
        Stage stage = new Stage();
        stage.setTitle("New Game");
        Button button = new Button("This One!");
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.getItems().add("Easy");
        choiceBox.getItems().add("Medium");
        choiceBox.getItems().add("Hard");
        choiceBox.setValue("Easy");
        button.setOnAction(e -> {
            String choice = choiceBox.getValue();
            if(choice.equals("Easy"))
            {
                generateEasyMaze();
            }
            else if (choice.equals("Medium"))
            {
                generateMediumMaze();
            }
            else if(choice.equals("Hard"))
            {
                generateHardMaze();
            }
            stage.close();
        });
        VBox layout1 = new VBox(10);
        Label label = new Label("How Strong Are You?");
        layout1.getChildren().add(label);
        layout1.setPadding(new Insets(20,20,20,20));
        layout1.getChildren().addAll(choiceBox);
        layout1.getChildren().addAll(button);
        layout1.setAlignment(Pos.CENTER);
        Scene newScane = new Scene(layout1,300,250);
        stage.setScene(newScane);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
        stage.showAndWait();
    }
    //</editor-fold>

    //<editor-fold desc="Music">
    public void PlayMusic() {
        String path = "resources\\DBZGame.mp3";
        //Instantiating Media class
        media = new Media(new File(path).toURI().toString());
        //Instantiating MediaPlayer class
        mediaPlayer = new MediaPlayer(media);
        //by setting this property to true, the audio will be played
        if (!playBoolean) {
            playBoolean = true;
            mediaPlayer.play();
        }
    }

    public void stopPlayMusic() {
        if (playBoolean) {
            playBoolean = false;
            mediaPlayer.pause();
        } else {
            playBoolean = true;
            mediaPlayer.play();
        }
    }

    private void WinningMusic() {
        String path = "resources\\DBWinner.mp3";
        if (playBoolean) {
            playBoolean = false;
            mediaPlayer.pause();
        }
        media = new Media(new File(path).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        //to play the song again
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {

                media = new Media((new File("resources\\DBZGame.mp3").toURI().toString()));
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.play();
                playBoolean = true;
            }
        });
    }
    //</editor-fold>
}
