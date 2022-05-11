package com.tonidotpy.minesweeper;

import javafx.scene.image.Image;

public final class ImageManager {
    private static final Image[] numImages;
    private static final Image[] digitImages;
    private static final Image emptyImage;
    private static final Image emptyPressedImage;
    private static final Image mineImage;
    private static final Image explodedImage;
    private static final Image flagImage;
    private static final Image questionMarkImage;

    private static final Image smileImage;
    private static final Image smilePressedImage;
    private static final Image deadImage;
    private static final Image swagImage;


    public static Image getNumImage(int num) { return (num >= 0 && num < numImages.length) ? numImages[num] : null; }
    public static Image getDigitImage(int num) { return (num >= 0 && num < digitImages.length - 1) ? digitImages[num] : null; }
    public static Image getDigitOffImage() { return digitImages[digitImages.length - 2]; }
    public static Image getDigitMinusImage() { return digitImages[digitImages.length - 1]; }
    public static Image getEmptyImage() { return emptyImage; }
    public static Image getEmptyPressedImage() { return emptyPressedImage; }
    public static Image getMineImage()  { return mineImage;  }
    public static Image getExplodedImage()  { return explodedImage;  }
    public static Image getFlagImage()  { return flagImage;  }
    public static Image getQuestionMarkImage()  { return questionMarkImage;  }

    public static Image getSmileImage() { return smileImage; }
    public static Image getSmilePressedImage() { return smilePressedImage; }
    public static Image getDeadImage()  { return deadImage; }
    public static Image getSwagImage()  { return swagImage; }


    static {
        String imagePath = "file:src/main/resources/com/tonidotpy/minesweeper/images/";

        double cellSize = 16.0, controlsSize = 24.0;
        emptyImage        = new Image(imagePath + "empty.png",        cellSize, cellSize, true, false);
        emptyPressedImage = new Image(imagePath + "empty_pressed.png",cellSize, cellSize, true, false);
        mineImage         = new Image(imagePath + "mine.png",         cellSize, cellSize, true, false);
        explodedImage     = new Image(imagePath + "exploded.png",     cellSize, cellSize, true, false);
        flagImage         = new Image(imagePath + "flag.png",         cellSize, cellSize, true, false);
        questionMarkImage = new Image(imagePath + "questionmark.png", cellSize, cellSize, true, false);

        smileImage        = new Image(imagePath + "smile.png",         controlsSize, controlsSize, true, false);
        smilePressedImage = new Image(imagePath + "smile_pressed.png", controlsSize, controlsSize, true, false);
        deadImage         = new Image(imagePath + "dead.png",          controlsSize, controlsSize, true, false);
        swagImage         = new Image(imagePath + "swag.png",          controlsSize, controlsSize, true, false);

        numImages  = new Image[9];
        digitImages  = new Image[12];
        for (int i = 0; i < 9; i++) {
            numImages[i] = new Image(imagePath + i + ".png", cellSize, cellSize, true, false);
            digitImages[i] = new Image(imagePath + "digit-" + i + ".png", 13.0, 23.0, true, false);
        }
        digitImages[9]  = new Image(imagePath + "digit-9.png", 13.0, 23.0, true, false);
        digitImages[10] = new Image(imagePath + "digit-off.png", 13.0, 23.0, true, false);
        digitImages[11] = new Image(imagePath + "digit-minus.png", 13.0, 23.0, true, false);
    }
}
