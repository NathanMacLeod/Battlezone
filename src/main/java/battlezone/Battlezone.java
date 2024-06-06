/*
 * File added by Nathan MacLeod 2019
 */
package battlezone;
import java.awt.GraphicsEnvironment;
import Geometry.*;
import gameObject.*;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import render.RenderManager;
import java.awt.event.*;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.File;
import java.util.StringTokenizer;
/**
 *
 * @author macle
 */
public class Battlezone extends JFrame implements Runnable {
    private Thread run;
    private boolean running;
    private boolean w, s, i, k;
    private double fov;
    private ArrayList<Object3D> objects = new ArrayList<Object3D>();
    private ArrayList<Updatable> updatable = new ArrayList<Updatable>();
    private ArrayList<Updatable> toAddUpdatable = new ArrayList<Updatable>();
    private ArrayList<Updatable> toRemoveUpdatable = new ArrayList<Updatable>();
    private ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
    private ArrayList<Tank> tanks = new ArrayList<Tank>();
    private PlayerTank player;
    private Enemy enemy;
    private HUD hud;
    private double enemyRespawnTime = 2.5;
    private double enemyRespawnCounter;
    private double playerRespawnTime = 3;
    private double playerRespawnCounter;
    private double mapRadius = 300;
    private double obstacleObstructionRadius = 30;
    private double tankObstructionRadius = 10;
    private int numberOfObstacles = 15;
    private int score = 0;
    private int lives = 3;
    private BackgroundImage backgroundImage;
    private int state; //0 main menu, 1 in game, 2 guide page 1, 3 guide page 2, 4 guide page 3, 5 high scores, 6 game over/ add high score screen 
    private MenuPage[] menuPages;
    private double menuPanAngle;
    private int requestedStateChange = -1;
    private double menuPanSpeed = 0.1;
    private double menuViewHeight = -16;
    private double maxMissileProbability = 0.33;
    private double missileProbabilityScaleFactor = 0.35;
    
    public Battlezone() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
//        setUndecorated(true);
        this.setResizable(false);
        this.resize(1600, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        gd.setFullScreenWindow(this);
        setVisible(true);
        Battlezone b = this;
        
        createMenus();
        state = -1;
        changeState(0);
        
        
        fov = (getWidth()/2.0) * Math.sqrt(3);
        this.addKeyListener(new KeyAdapter() {
            
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    running = false;
                    System.exit(0);
                }
                
                if(state == 1) {
                    switch(e.getKeyCode()) {
                        case KeyEvent.VK_W:
                            w = true;
                            break;
                        case KeyEvent.VK_S:
                            s = true;
                            break;
                        case KeyEvent.VK_K:
                            k = true;
                            break;
                        case KeyEvent.VK_I:
                            i = true;
                            break;
                        case KeyEvent.VK_SPACE:
                            player.tryToFire(b);
                            break;
                    }
                }
                else {
                    if(state == 5)
                        ((WriteHighScorePage)menuPages[5]).enterHighScore(e.getKeyCode(), b);
                    requestedStateChange = menuPages[state].getStateChange(e.getKeyCode());
                }
            }
            
            public void keyReleased(KeyEvent e) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_W:
                        w = false;
                        break;
                    case KeyEvent.VK_S:
                        s = false;
                        break;
                    case KeyEvent.VK_K:
                        k = false;
                        break;
                    case KeyEvent.VK_I:
                        i = false;
                        break;
                }
            }
        });
    }
    
    public void saveScore() {
        /*
            gets the current high scores from a text file, finds where the current score belongs and rewrites the file with the new score
        */
        try {
            String scores = getHighScores();
            String[] scoreList = getStringArrayScores(scores);
            int replaceLocation = getScorePlacement(scores, score);
            for(int j = scoreList.length - 1; j > replaceLocation; j--) {

                scoreList[j] = scoreList[j - 1];
        }
        int[] nameChars = ((WriteHighScorePage)menuPages[5]).getNameChars();
        String name = "" + (char) nameChars[0] + (char) nameChars[1] + (char) nameChars[2];
        scoreList[replaceLocation] = name + score;
        PrintWriter out = new PrintWriter(new File(Class.class.getResource("scores.txt").getFile()));
        for(int k = 0; k < scoreList.length; k++) {
            out.println(scoreList[k]);
        }
        out.close();
        }
        catch(Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    public String getHighScores() {
        /*
        retrives the high scores from the text document
        */
        try {
            Scanner wanner = new Scanner(Battlezone.class.getClassLoader().getResourceAsStream("scores.txt"));
            String scores = "";
            while(wanner.hasNext()) {
                scores += wanner.next() + " ";
            }
            return scores;
        }
        catch(Exception e) {
            e.printStackTrace(System.err);
            return "";
        }
    }
    
    public String[] getStringArrayScores(String scores) {
        /*
        sorts the scores into a string array
        */
        StringTokenizer tokens = new StringTokenizer(scores);
        String[] fancySchmancy = new String[tokens.countTokens()];
        int i = 0;
        while(tokens.hasMoreTokens()) {
            fancySchmancy[i] = tokens.nextToken();
            i++;
        }
        return fancySchmancy;
    }
    
    private int getScorePlacement(String scores, int score) {
        //gets the scores of the high scores , and sees where score fits into it
        //If its within top 5 it return where, if not it returns -1
        StringTokenizer tokens = new StringTokenizer(scores);
        int i = -1;
        int j = 0;
        while(tokens.hasMoreTokens()) {
            int scoreAt = Integer.parseInt(tokens.nextToken().substring(3));
            if(score > scoreAt) {
               i = j;
               break;
            }
            j++;
        }
        return i;
    }
    
    private void createMenus() {
        int[] dim = new int[] {getWidth(), getHeight()};
        menuPages = new MenuPage[] {new MainMenu(dim), null, new OverviewPage(dim), new ControlsPage(dim), new TipsPage(dim), new WriteHighScorePage(dim),
        new ViewHighScorePage(dim)};
    }
    
    private void initializeMenu() {
        resetNonPlayerEnteties();
        menuPanAngle = 0;
    }
    
    public Enemy getEnemy() {
        return enemy;
    }
    
    public double getMapRadius() {
        return mapRadius;
    }
    
    public void start() {
        running = true;
        run = new Thread(this);
        run.start();
    }
    
    public void run() {
        long currentTime = System.nanoTime();
        long previousTime = currentTime;
        
        while(running) {
            currentTime = System.nanoTime();
            double timePassed = (currentTime - previousTime)/ Math.pow(10, 9);
            previousTime = currentTime;
            
            if(requestedStateChange != -1) {
                changeState(requestedStateChange);
                requestedStateChange = -1;
            }
            
            runState(timePassed);
            render();
        }
    }
    
    private void runState(double timePassed) {
        if(state == 1)
            runGame(timePassed);
        else 
            runMenu(timePassed);
    }    
        
    private void runGame(double timePassed) {
        gameUpdate(timePassed);
        gameLogic(timePassed);
    }
    
    private void runMenu(double timePassed) {
        if(state == 5)
            ((WriteHighScorePage)menuPages[5]).update(timePassed);
        menuUpdate(timePassed);
    }
    
    private void changeState(int newState) {
        if(newState == 1)
            initializeGame();
        else if(state == 1 || state == -1)
            initializeMenu();
        state = newState;
    }
    
    private void removeOutOfBoundsShells() {
        for(int i = 0; i < updatable.size(); i++) {
            if(!(updatable.get(i) instanceof TankShell))
                continue;
            TankShell shell = (TankShell) updatable.get(i);
            if(Math.pow(shell.getX() - player.getX(), 2) + Math.pow(shell.getZ() - player.getZ(), 2) > Math.pow(mapRadius, 2))
                removeUpdatable(shell);
        }
    }
    
    private void replaceOutOfBoundsEnemy() {
        
        double playerX;
        double playerZ;
        if(player != null) {
            playerX = player.getX();
            playerZ = player.getZ();
        }
        else {
            playerX = 0;
            playerZ = 0;
        }
        
        if(enemy == null || (enemy instanceof Missile && !((Missile)enemy).getInitiallyLanded()) || Math.pow(enemy.getX() - playerX, 2) + Math.pow(enemy.getZ() - playerZ, 2) <= Math.pow(mapRadius, 2))
           return;
        
        removeUpdatable((Updatable) enemy);
        if(state == 1)
            addEnemy();
        else
            addTank();
        
    }
    
    private void replaceOutOfBoundsObstacles() {
        for(int i = 0; i < obstacles.size(); i++) {
            Obstacle o = obstacles.get(i);
            if(Math.pow(o.getX() - player.getX(), 2) + Math.pow(o.getZ() - player.getZ(), 2) <= Math.pow(mapRadius, 2))
                continue;
            
            do {
                double angle = player.getYRot() + (Math.random() * Math.PI);
                if(player.getTrack1() == -1 || player.getTrack2() == -1)
                    angle += Math.PI;
                double randomX = player.getX() + (Math.cos(angle) * mapRadius);
                double randomZ = player.getZ() + (Math.sin(angle) * mapRadius);
                boolean fail = false;
                for(Obstacle ob : obstacles) {
                    if(Math.pow(randomX - ob.getX(), 2) + Math.pow(randomZ - ob.getZ(), 2) < Math.pow(obstacleObstructionRadius * 2, 2)) {
                        fail = true;
                        break;
                    }
                }
                if(fail) {
                    continue;
                }
                removeUpdatable(obstacles.remove(i));
                addUpdatable(new Obstacle(new double[] {randomX, 0, randomZ, 0, Math.random() * 2 * Math.PI, 0}, 10, player.getBulletHeight()));
                break;
            } while(true);
        }
    }
    
    private void initializeGame() {
        lives = 3;
        score = 0;
        resetScene();
    }
    
    private void clearEntities() {
        obstacles.clear();
        updatable.clear();
        objects.clear();
        tanks.clear();
        toAddUpdatable.clear();
        toRemoveUpdatable.clear();
    }
    
    private void resetScene() {
        clearEntities();
        enemyRespawnCounter = 0;
        playerRespawnCounter = 0;
        
        player = new PlayerTank(new double[] {0, 0, 0, 0, 0, 0}, 10, ModelManager.getModelBulletHeight(10));
        addUpdatable(player);
        hud = new HUD(4, new int[] {getWidth()/2, getHeight()/6}, getHeight() * 80.0/900, new int[] {getWidth()/2, getHeight()/2}, getHeight() * 100.0/900);
        addUpdatable(hud);
        addObstaclesRandomLocation(numberOfObstacles);
        addEnemy();
        backgroundImage = new BackgroundImage(new int[] {getWidth(), getHeight()});
        updatable.add(backgroundImage);
    }
    
    private void resetNonPlayerEnteties() {
        clearEntities();
        addObstaclesRandomLocation(numberOfObstacles);
        addTank();
        backgroundImage = new BackgroundImage(new int[] {getWidth(), getHeight()});
        updatable.add(backgroundImage);
    }
    
    public int getScore() {
        return score;
    }
    
    public int getLives() {
        return lives;
    }
    
    private void gameLogic(double timePassed) {
        if(playerRespawnCounter > 0) {
            playerRespawnCounter-= timePassed;
            if(playerRespawnCounter <= 0) {
                lives--;
                if(lives <= 0) {
                    if(getScorePlacement(getHighScores(), score) != -1)
                        changeState(5);
                    else
                        changeState(0);
                }
                else
                    resetScene();
            }
        }
        else if(player.getDead()) {
            playerRespawnCounter = playerRespawnTime;
        }
        
        if(enemyRespawnCounter > 0) {
            enemyRespawnCounter-= timePassed;
            if(enemyRespawnCounter <= 0)
                addEnemy();
        }
        else if(enemy.getDead()) {
            score += 1000;
            if(enemy instanceof Missile)
                score += 500;
            enemyRespawnCounter = enemyRespawnTime;
        }
        
    }
    
    private void addObstaclesRandomLocation(int numObstacles) {
        for(int i = 0; i < numObstacles; i++) {
            double[] pos = getRandomValidLocation(obstacleObstructionRadius, 1);
            double bulletHeight = -1;
            if(player != null)
                bulletHeight = player.getBulletHeight();
            Obstacle o = new Obstacle(new double[] {pos[0], 0, pos[1], 0, Math.random() * 2 * Math.PI, 0}, 10, bulletHeight);
            addUpdatable((Updatable) o);
        }
    }
    
    private double[] getRandomValidLocation(double objectObstruction, double mapRatio) {
        double mapRadius = this.mapRadius * mapRatio;
        while(true) {
            double playerX;
            double playerZ;
            if(player != null) {
                playerX = player.getX();
                playerZ = player.getZ();
            }
            else {
                playerX = 0;
                playerZ = 0;
            }
            
            double randomX = playerX + (Math.random() * 2 * mapRadius) - mapRadius;
            double randomZ = playerZ + (Math.random() * 2 * mapRadius) - mapRadius;
            
            if(Math.pow(randomX - playerX, 2) + Math.pow(randomZ - playerZ, 2) > Math.pow(mapRadius, 2)) 
                continue;
            
            
            if(Math.pow(randomX - playerX, 2) + Math.pow(randomZ - playerZ, 2) < Math.pow(tankObstructionRadius + objectObstruction, 2)) 
                continue;
            
            
            boolean fail = false;
            for(Obstacle o : obstacles) {
                if(Math.pow(randomX - o.getX(), 2) + Math.pow(randomZ - o.getZ(), 2) < Math.pow(obstacleObstructionRadius + objectObstruction, 2)) {
                    fail = true;
                    break;
                }
            }
            if(fail) {
                continue;
            }
            
            return new double[] {randomX, randomZ};
        }
    }
    
    private void addTank() {
        double[] pos = getRandomValidLocation(tankObstructionRadius, 3.0/4);
        if(player == null) {
            enemy = new EnemyTank(new double[] {pos[0], 0, pos[1], 0, Math.PI * 2 * Math.random(), 0}, 10, -1);
            addUpdatable((Updatable) enemy);
            return;
        }
        
        enemy = new EnemyTank(new double[] {pos[0], 0, pos[1], 0, 0, 0}, 10, player.getBulletHeight());
        double[] angles = player.getAngularDistsToTank((Tank) enemy);
        double angleFromPlayer = angles[0];
        if(angles[1] < angles[0])
            angleFromPlayer = angles[1];
        
        if(angleFromPlayer >= Math.PI/6) {

            double enemyAngle = (0.5 * angleFromPlayer) + (Math.random() * 2 * angleFromPlayer);
            if((int) (2 * Math.random()) == 0) {
                enemyAngle *= -1;
            }

            double angleToTank = -(Math.PI/2) + ((Tank) enemy).getAngleToTank(player);

            enemy.setYRot(angleToTank += enemyAngle);
        }
        else
            enemy.setYRot(Math.random() * 2 * Math.PI);
        
        addUpdatable((Updatable) enemy);
    }
    
    private void addMissile() {
        double playerAngle;
        if(player == null)
            return;
        else
            playerAngle = player.getYRot() + Math.PI/2;
        
        enemy = new Missile(new double[] {player.getX() + (Math.cos(playerAngle) * mapRadius), -80, player.getZ() + (Math.sin(playerAngle) * mapRadius), 0, playerAngle + Math.PI/2, 0}, 5);
        addUpdatable((Updatable) enemy);
    }
    
    private void addEnemy() {
        if(player == null)
            return;
        
        double missileProbability = maxMissileProbability * (score/1000.0) * missileProbabilityScaleFactor;
        if(missileProbability > maxMissileProbability)
            missileProbability = maxMissileProbability;
        
        if(Math.random() <= missileProbability)
            addMissile();
        else
            addTank();
        
    }
    
    private void addUpdatables() {
        while(toAddUpdatable.size() != 0) {
            Updatable u = toAddUpdatable.get(0);
            updatable.add(u);
            if(u instanceof Object3D)  
                objects.add((Object3D) u);
            if(u instanceof Obstacle)  
                obstacles.add((Obstacle) u);
            if(u instanceof Tank)
                tanks.add((Tank) u);
            toAddUpdatable.remove(u);
        }
    }
    
    private void removeUpdatables() {
        while(toRemoveUpdatable.size() != 0) {
            Updatable u = toRemoveUpdatable.get(0);
            updatable.remove(u);
            if(u instanceof Object3D)  
                objects.remove((Object3D) u);
            if(u instanceof Obstacle)  
                obstacles.remove((Obstacle) u);
            if(u instanceof Tank)
                tanks.remove((Tank) u);
            toRemoveUpdatable.remove(u);
        }
    }
    
    public void addUpdatable(Updatable u) {
        toAddUpdatable.add(u);
    }
    
    public void removeUpdatable(Updatable u) {
        toRemoveUpdatable.add(u);
    }
    
    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }
    
    public ArrayList<Tank> getTanks() {
        return tanks;
    }
    
    public PlayerTank getPlayer() {
        return player;
    }
    
    public void gameUpdate(double timePassed) {
        if(w && !s) 
            player.setTrack2(1);
        else if(s && !w) 
            player.setTrack2(-1);
        else
            player.setTrack2(0);
        
        if(i && !k) 
            player.setTrack1(1);
        else if(k && !i) 
            player.setTrack1(-1);
        else
            player.setTrack1(0);
        
        for(Updatable toUpdate : updatable) 
            toUpdate.update(timePassed, this);
        
        addUpdatables();
        removeUpdatables();
        replaceOutOfBoundsObstacles();
        replaceOutOfBoundsEnemy();
        removeOutOfBoundsShells();
    }
    
    public void menuUpdate(double timePassed) {
        menuPanAngle += menuPanSpeed * timePassed;
        
        for(Updatable toUpdate : updatable) 
            toUpdate.update(timePassed, this);
        
        addUpdatables();
        removeUpdatables();
        replaceOutOfBoundsEnemy();
    }
    
    private void gameRender(Graphics g) {
        BufferedImage render = RenderManager.createRender(objects, new double[] {player.getX(),player.getBulletHeight() - 0.125, player.getZ() - 0.001}, new double[]{player.getXRot(), player.getYRot()}, fov, new int[] {getWidth(), getHeight()});
        g.drawImage(render, 0, 0, null);
        backgroundImage.drawBackground(g, player.getYRot());
        hud.draw(g, this, new int[] {getWidth(), getHeight()});
    }
    
    private void menuRender(Graphics g) {
        BufferedImage render = RenderManager.createRender(objects, new double[] {0,menuViewHeight, 0}, new double[]{0, menuPanAngle}, fov, new int[] {getWidth(), getHeight()});
        g.drawImage(render, 0, 0, null);
        backgroundImage.drawBackground(g, menuPanAngle);
        if(state == 5)
            ((WriteHighScorePage)menuPages[5]).draw(g, score);
        if(state == 6)
            ((ViewHighScorePage)menuPages[6]).draw(g, this);
        menuPages[state].draw(g);
        
    }
    
    private void render() {
        
        BufferedImage doubleBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = doubleBuffer.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        
        if(state == 1) 
            gameRender(g);
        else 
            menuRender(g);

        getGraphics().drawImage(doubleBuffer, 0, 0, null);
        try {
        Thread.sleep(5);
        }
        catch (Exception e) {}
    }

    public static void main(String[] args) {
        Battlezone b = new Battlezone();
        b.start();
    }
    
}
