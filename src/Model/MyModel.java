package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import Server.Configurations;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MyModel extends Observable implements IModel {

    //<editor-fold desc="Fields">
    private Server mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
    private Server solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
    private ExecutorService threadPool = Executors.newCachedThreadPool();

    //<editor-fold desc="Maze">
    private int[][] mazeIntArr;
    private Maze currentMaze;
    private Solution solution;
    private String algoType;
    private String threadNumber;
    private String mazeGenerator = null;
    //</editor-fold>
    //</editor-fold>
    @Override
    public ArrayList<AState> getSolution() {
        return solution.getSolutionPath();
    }

    public MyModel() {
    }

    //<editor-fold desc="Servers">
    public void startServers() {
        mazeGeneratingServer.start();
        solveSearchProblemServer.start();
    }

    private void stopServers() {
        //Closing Servers before ending
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();

    }
    //</editor-fold>

    //<editor-fold desc="Character Fields">
    private int characterPositionRow = 0;
    private int characterPositionColumn = 0;
    //</editor-fold>

    //<editor-fold desc="Getters">
    @Override
    public int[][] getMaze() {
        return mazeIntArr;
    }

    @Override
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    @Override
    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }
    @Override
    public int getFinalPositionRow() {
        return currentMaze.getGoalPosition().getRowIndex();
    }

    @Override
    public int getFinalPositionColumn() {
        return currentMaze.getGoalPosition().getColumnIndex();
    }

    @Override
    public boolean mazeExist() {
        if(mazeIntArr ==  null){
            return false;
        }
        return true;
    }
    @Override
    public String getMazeGenerator() {
        setProp();
        return mazeGenerator;
    }

    @Override
    public String getAlgorithm() {
        return algoType;
    }

    @Override
    public String getThreadNumber() {
        return threadNumber;
    }
    //</editor-fold>

    //<editor-fold desc="Model Functionality">
    @Override
    public void generateMaze(int width, int height) {
        //Generate maze
        threadPool.execute(() -> {
            try {
                CommunicateWithServer_MazeGenerating(width, height);
                CommunicateWithServer_SolveSearchProblem();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setChanged(); //Raise a flag that I have changed
            notifyObservers(mazeIntArr); //Wave the flag so the observers will notice
        });
    }

    @Override
    public void moveCharacter(KeyCode movement) {
        switch (movement) {
            //Up
            case NUMPAD8:
                if(characterPositionRow-1 >= 0 && mazeIntArr[characterPositionRow-1][characterPositionColumn] != 1)
                    characterPositionRow--;
                System.out.println("["+characterPositionRow+","+characterPositionColumn+"]");
                break;
            //Down
            case NUMPAD2:
                //characterPositionRow+1 <= mazeIntArr.length &&
                if( characterPositionRow+1 < mazeIntArr.length && mazeIntArr[characterPositionRow+1][characterPositionColumn] != 1) {
                    characterPositionRow++;
                    System.out.println(mazeIntArr[characterPositionRow][characterPositionColumn]);
                }
                System.out.println("["+characterPositionRow+","+characterPositionColumn+"]");
                break;
            //Right
            case NUMPAD6:
                //characterPositionRow+1 <= mazeIntArr[0].length &&
                if( characterPositionColumn+1 < mazeIntArr[0].length && mazeIntArr[characterPositionRow][characterPositionColumn+1] != 1) {
                    characterPositionColumn++;
                    System.out.println("[" + characterPositionRow + "," + characterPositionColumn + "]");
                }
                break;
            //Left
            case NUMPAD4:
                //characterPositionRow-1 >= 0 &&
                if(characterPositionColumn-1 >= 0 &&mazeIntArr[characterPositionRow][characterPositionColumn-1] != 1) {
                    characterPositionColumn--;
                    System.out.println("[" + characterPositionRow + "," + characterPositionColumn + "]");
                }
                break;
            case NUMPAD7:
                //Up & Left
                if( (characterPositionColumn-1 >=0 && characterPositionRow-1 >=0 &&
                        mazeIntArr[characterPositionRow][characterPositionColumn-1] != 1 && mazeIntArr[characterPositionRow-1][characterPositionColumn-1] != 1)
                        ||
                        (characterPositionColumn-1 >=0 && characterPositionRow-1 >=0 &&
                                mazeIntArr[characterPositionRow-1][characterPositionColumn] != 1 && mazeIntArr[characterPositionRow-1][characterPositionColumn-1] != 1))
                {
                    characterPositionColumn--;
                    characterPositionRow--;
                    System.out.println("[" + characterPositionRow + "," + characterPositionColumn + "]");
                }
                break;
            case NUMPAD3:
                //characterPositionRow-1 >= 0 &&
                if(characterPositionColumn+1 < mazeIntArr[0].length && characterPositionRow+1 < mazeIntArr.length &&
                        mazeIntArr[characterPositionRow][characterPositionColumn+1] != 1 && mazeIntArr[characterPositionRow+1][characterPositionColumn+1] != 1
                        ||(characterPositionColumn+1 < mazeIntArr[0].length && characterPositionRow+1 < mazeIntArr.length &&
                        mazeIntArr[characterPositionRow+1][characterPositionColumn] != 1 && mazeIntArr[characterPositionRow+1][characterPositionColumn+1] != 1)) {
                    characterPositionColumn++;
                    characterPositionRow++;
                    System.out.println("[" + characterPositionRow + "," + characterPositionColumn + "]");
                }
                break;
            case NUMPAD9:
                //characterPositionRow-1 >= 0 &&
                if(characterPositionColumn+1 < mazeIntArr[0].length && characterPositionRow-1 >=0  &&
                        mazeIntArr[characterPositionRow][characterPositionColumn+1] != 1 && mazeIntArr[characterPositionRow-1][characterPositionColumn+1] != 1
                        ||(characterPositionColumn+1 < mazeIntArr[0].length && characterPositionRow-1 >=0 &&
                        mazeIntArr[characterPositionRow-1][characterPositionColumn] != 1 && mazeIntArr[characterPositionRow-1][characterPositionColumn+1] != 1)) {
                    characterPositionColumn++;
                    characterPositionRow--;
                    System.out.println("[" + characterPositionRow + "," + characterPositionColumn + "]");
                }
                break;
            case NUMPAD1:
                //characterPositionRow-1 >= 0 &&
                if(characterPositionColumn-1 >=0  && characterPositionRow+1 < mazeIntArr.length &&
                        mazeIntArr[characterPositionRow][characterPositionColumn-1] != 1 && mazeIntArr[characterPositionRow+1][characterPositionColumn-1] != 1
                        ||(characterPositionColumn-1 >=0  && characterPositionRow+1 < mazeIntArr.length &&
                        mazeIntArr[characterPositionRow+1][characterPositionColumn] != 1 && mazeIntArr[characterPositionRow+1][characterPositionColumn-1] != 1)) {
                    characterPositionColumn--;
                    characterPositionRow++;
                    System.out.println("[" + characterPositionRow + "," + characterPositionColumn + "]");
                }
                break;
            case HOME:
                characterPositionRow = 0;
                characterPositionColumn = 0;
        }
        setChanged();
        notifyObservers(/*Can forward an Object*/);
    }

    private boolean MazeFinished(){
        if(characterPositionRow ==  currentMaze.getGoalPosition().getRowIndex() && characterPositionColumn ==  currentMaze.getGoalPosition().getColumnIndex())
            return  true;
        else return false;
    }
    //</editor-fold>

    //<editor-fold desc="Servers Communicate Functions">

    //<editor-fold desc="CommunicateWithServer_MazeGenerating">

    private void CommunicateWithServer_MazeGenerating(int width, int height) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{width, height};
                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[1000000 /*CHANGE SIZE ACCORDING TO YOU MAZE SIZE*/]; //allocating byte[] for the decompressed maze -
                        is.read(decompressedMaze); //Fill decompressedMaze with bytes
                        Maze maze = new Maze(decompressedMaze);
                        //This MyModel fields
                        currentMaze =maze;
                        mazeIntArr = maze.getMazeArray();
                        //Updating
                        setChanged();
                        notifyObservers(mazeIntArr);
                        System.out.println();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    //</editor-fold>

    //<editor-fold desc="CommunicateWithServer_SolveSearchProblem">

    private void CommunicateWithServer_SolveSearchProblem() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        MyMazeGenerator mg = new MyMazeGenerator();
                        Maze maze = currentMaze;
                        //maze.print();
                        toServer.writeObject(maze); //send maze to server
                        toServer.flush();
                        Solution mazeSolution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        //Print Maze Solution retrieved from the server
                        //System.out.println(String.format("Solution steps: %s", mazeSolution));
                        ArrayList<AState> mazeSolutionSteps = mazeSolution.getSolutionPath();
                        for (int i = 0; i < mazeSolutionSteps.size(); i++) {
                            System.out.println(String.format("%s. %s", i, mazeSolutionSteps.get(i).toString()));
                        }
                        //We need to hold the soultion here
                        solution = mazeSolution;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    //</editor-fold>

    //</editor-fold>

    //<editor-fold desc="Closing">

    @Override
    public void close() {
        try {
            stopServers();
            threadPool.shutdown();
            threadPool.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Setters">
    public void setToStart() {
        this.characterPositionRow = 0;
        this.characterPositionColumn = 0;
        while(currentMaze == null) //Wait till the maze is created
            setChanged();
        notifyObservers(/*Can forward an Object*/);
    }

    private void setCurrentMaze(Maze newOne)
    {
        currentMaze = newOne;
    }
    private void setCharacterPositionRow(Position p)
    {
        characterPositionRow = p.getRowIndex();
    }
    private void setCharacterPositionColumn(Position p)
    {
        characterPositionColumn = p.getColumnIndex();
    }

    private void setProp(){
        Properties prop = new Properties();
        InputStream  input= Configurations.class.getClassLoader().getResourceAsStream("config.properties");

        try{
            prop.load(input);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        mazeGenerator = prop.getProperty("IMazeGenerator");
        threadNumber = prop.getProperty("PoolSize");
        algoType = prop.getProperty("ISearchingAlgorithm");
    }

    //</editor-fold>

    //<editor-fold desc="Save & Load maze">
    public void saveMaze(String nameOfTheMaze,String path){

        try {
            //save maze
            FileOutputStream fileOut = new FileOutputStream(path +".Maze");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(currentMaze);
            fileOut.close();
            objectOut.close();
            //save characterPosition
            Position characterPosition = new Position(getCharacterPositionRow(),getCharacterPositionColumn());
            FileOutputStream fileOutCharacterPosition = new FileOutputStream(path + "CharacterPosition");
            ObjectOutputStream objectOutCharacterPosition = new ObjectOutputStream(fileOutCharacterPosition);
            objectOutCharacterPosition.writeObject(characterPosition);

            objectOutCharacterPosition.close();
            fileOutCharacterPosition.close();

            //save solution
            FileOutputStream fileOutSol = new FileOutputStream(path+ "Solution");
            ObjectOutputStream objectOutSol = new ObjectOutputStream(fileOutSol);
            objectOutSol.writeObject(solution);

            fileOutSol.close();
            objectOutSol.close();

        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadMaze(String path) {
        try{
            String othersPath = path.substring(0,path.length() -5);
            FileInputStream fiMaze = new FileInputStream(new File(path));
            ObjectInputStream oiMaze = new ObjectInputStream(fiMaze);
            FileInputStream fiSol = new FileInputStream(new File(othersPath+ "Solution"));
            ObjectInputStream oiSol = new ObjectInputStream(fiSol);
            FileInputStream fiPosition = new FileInputStream(new File(othersPath)+ "CharacterPosition");
            ObjectInputStream oiPosition = new ObjectInputStream(fiPosition);
            // Read objects
            Maze mazeFromFile = (Maze) oiMaze.readObject();
            Position positionFromFile = (Position) oiPosition.readObject();
            this.solution = (Solution) oiSol.readObject();
            setCurrentMaze(mazeFromFile);
            setCharacterPositionColumn(positionFromFile);
            setCharacterPositionRow(positionFromFile);
            mazeIntArr = currentMaze.getMazeArray();
            setChanged();
            notifyObservers(mazeIntArr);
            oiSol.close();
            fiSol.close();
            oiMaze.close();
            fiMaze.close();
            oiPosition.close();
            fiPosition.close();
        }catch (IOException e) {
            System.out.println("problem");
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
    //</editor-fold>
}
