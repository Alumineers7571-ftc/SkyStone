package org.firstinspires.ftc.teamcode.lib.util;

public class Stone{

    private int position = 0;
    private boolean isSkystone = false;

    private boolean isCollected = false;
    private boolean isDisplaced = false;

    private final int STONE_WIDTH = 10.16;//cm
    private final int STONE_LENGTH = 20.32;//cm

    public Stone(int position){

        this.position = position;
        this.isSkystone = false;

    }

    public Stone(int position, boolean isSkystone){

        this.position = position;
        this.isSkystone = isSkystone;

    }



    public int getPosition(){
        return position;
    }

    public boolean isSkystone(){
        return isSkystone;
    }

    public void setIsSkystone(boolean isSkystone){
        this.isSkystone = isSkystone;
    }

    public int getWidth(){
        return STONE_WIDTH;
    }

    public int getHeight(){
        return STONE_HEIGHT;
    }

}