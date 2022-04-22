package cn.jmix.imagecropper.screen;

import cn.jmix.imagecropper.toolkit.ui.ImgCropServerComponent;
import io.jmix.ui.screen.ScreenOptions;

import java.io.InputStream;

/**
 * Represent  some options for cropping an image
 * Created by Ray.Lv on 2019/10/25.
 */
public class ImageCropWindowOptions implements ScreenOptions {

    private int cropQuality = 10;
    private byte[] result;
    private ImgCropServerComponent.ViewPort viewPort;

    public ImageCropWindowOptions(InputStream inputStream) {
        this(inputStream, 10, new ImgCropServerComponent.ViewPort());
    }

    public ImageCropWindowOptions(InputStream inputStream, int cropQuality) {
        this(inputStream, cropQuality, new ImgCropServerComponent.ViewPort());
    }

    /**
     * Contructor
     * @param fileStream      the inputStream of image file  to be cropped
     * @param cropQuality the quality for  output image,1-10
     * @param viewPort Viewport
     */
    public ImageCropWindowOptions(InputStream fileStream, int cropQuality, ImgCropServerComponent.ViewPort viewPort) {
        this.imageFileStream = fileStream;
        this.cropQuality = cropQuality;
        this.viewPort = viewPort;
    }

    private InputStream imageFileStream;

    public InputStream getImageFileStream() {
        return imageFileStream;
    }

    public int getCropQuality() {
        return cropQuality;
    }

    public void setCropQuality(int cropQuality) {
        this.cropQuality = cropQuality;
    }

    public void setResult(byte[] result) {
        this.result = result;
    }

    public byte[] getResult() {
        return result;
    }

    /**
     * dialog width
     */
    public String windowWidth = "800px";
    /**
     * dialog height
     */
    public String windowHeight = "600px";

    public ImgCropServerComponent.ViewPort getViewPort() {
        return viewPort;
    }


}
