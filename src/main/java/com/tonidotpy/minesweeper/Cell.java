package com.tonidotpy.minesweeper;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Cell extends ImageView {
    public enum Type {
        ZERO,
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVENT,
        EIGHT,
        MINE,
        EXPLODED
    }
    public enum State {
        EMPTY,
        FLAG,
        QUESTION_MARK,
        SHOWN
    }


    private Type type;
    private State state;


    public boolean isMine() { return type == Type.MINE || type == Type.EXPLODED; }
    public boolean isFlag() { return state == State.FLAG; }
    public boolean isQuestionMark() { return state == State.QUESTION_MARK; }
    public boolean isShown() { return state == State.SHOWN; }

    public Type getType() { return type; }
    public State getState() { return state; }
    public int getTotNearMines() { return isMine() ? 0 : type.ordinal(); }

    public void setType(Type type) { this.type = type; }
    public void setType(int index) {
        if (index >= 0 && index < Type.values().length)
            setType(Type.values()[index]);
    }
    public void setState(State state) { this.state = state; }

    public boolean CanBeShown() { return !isShown() && !isFlag() && !isQuestionMark(); }


    public Cell() {
        super(ImageManager.getEmptyImage());
        this.type  = Type.ZERO;
        this.state = State.EMPTY;

        this.setSmooth(false);
    }


    public void Show() {
        this.state = State.SHOWN;
        Image image;
        switch (type) {
            case MINE     -> image = ImageManager.getMineImage();
            case EXPLODED -> image = ImageManager.getExplodedImage();
            default       -> image = ImageManager.getNumImage(type.ordinal());
        }
        this.setImage(image);
    }

    // Toggle state between empty, flag and question mark (shown is excluded)
    public void ToggleState() {
        this.state = State.values()[(this.state.ordinal() + 1) % 3];
        Image image;
        switch (state) {
            case FLAG          -> image = ImageManager.getFlagImage();
            case QUESTION_MARK -> image = ImageManager.getQuestionMarkImage();
            default            -> image = ImageManager.getEmptyImage();
        }
        this.setImage(image);
    }


    public void Reset() {
        this.type  = Type.ZERO;
        this.state = State.EMPTY;
        this.setImage(ImageManager.getEmptyImage());
    }
}
