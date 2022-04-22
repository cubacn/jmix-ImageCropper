package cn.jmix.imagecropper.toolkit.ui;


import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.shared.ui.JavaScriptComponentState;
import com.vaadin.ui.AbstractJavaScriptComponent;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;


@JavaScript({"vaadin://jmixcn/imgcrop/croppie.js", "vaadin://jmixcn/imgcrop/jmixcn-imgcrop-connector.js"})
@StyleSheet({"vaadin://jmixcn/imgcrop/croppie.css", "vaadin://jmixcn/imgcrop/jmixcn-imgcrop-connector.css"})
public class ImgCropServerComponent extends AbstractJavaScriptComponent {

    /**
     * ViewPort Type
     */
    public enum ViewPortType {
        /**
         * square
         */
        square,
        /**
         * circle
         */
        circle
    }

    public static class ViewPort {
        public ViewPort() {
        }

        /**
         * Constructor
         * @param width the width of viewport
         * @param height the height of viewport
         * @param viewPortType
         */
        public ViewPort(int width, int height, ViewPortType viewPortType) {
            this.width = width;
            this.height = height;
            this.viewPortType = viewPortType;
        }

        public int width = 100;
        public int height = 100;
        public ViewPortType viewPortType = ViewPortType.square;
    }

    String base64ImageFile(InputStream inputStream) {
        try {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Encoding File to base64 string  failed",e);
        }
    }

    public ImgCropServerComponent(InputStream fileContent, ViewPort viewPort
    ) {
        this(fileContent, "", false, false, false, true, true, true, viewPort,1);
    }

    public ImgCropServerComponent(InputStream inputStream, ViewPort viewPort, int quality
    ) {
        this(inputStream, "", false, false, false, true, true, true, viewPort,quality);
    }


    /**
     * setter for quality
     * @param quality 0-1
     */
    public void setQuality(float quality) {
        this.getState(true).quality = quality;
    }

    public ImgCropServerComponent(
            InputStream fileContent
    ) {
        this(fileContent, "", false, false, false, true, true, true, new ViewPort(),1);
    }


    /**
     * Constructor for ImgCropServerComponent
     *
     * @param fileContent
     * @param customClass
     * @param enableExif
     * @param enableOrientation
     * @param enableResize
     * @param enableZoom
     * @param mouseWheelZoom
     * @param showZoomer
     * @param viewPort
     */
    public ImgCropServerComponent(
            InputStream fileContent,
            String customClass,
            boolean enableExif,
            boolean enableOrientation,
            boolean enableResize,
            boolean enableZoom,
            boolean mouseWheelZoom,
            boolean showZoomer,
            ViewPort viewPort,
            int quality
    ) {
        this();
        String str64 = base64ImageFile(fileContent);
        ImgCropState state = getState();
        state.customClass = customClass;
        state.imageBase64 = str64;
        state.enableExif = enableExif;
        state.enableOrientation = enableOrientation;
        state.enableResize = enableResize;
        state.enableZoom = enableZoom;
        state.mouseWheelZoom = mouseWheelZoom;
        state.showZoomer = showZoomer;
        state.viewPort = viewPort;

    }

    public interface ImageUpdateListener {
        void imageUpdate(String imageBase64);
    }

    @Override
    protected ImgCropState getState() {
        return (ImgCropState) super.getState();
    }

    @Override
    public ImgCropState getState(boolean markAsDirty) {
        return (ImgCropState) super.getState(markAsDirty);
    }

    public ImageUpdateListener getImageUpdateListener() {
        return imageUpdateListener;
    }

    public String getImageBase64() {
        return getState().imageBase64;
    }

    public void setImageUpdateListener(ImageUpdateListener listener) {
        this.imageUpdateListener = listener;
    }

    private ImageUpdateListener imageUpdateListener;

    public String getCurrentImageBase64(){
        if(currentImageBase64==null){
           return  currentImageBase64;
        }
        if(currentImageBase64.contains(",")){
            return currentImageBase64.split(",")[1];
        }
        return currentImageBase64;
    }

    String currentImageBase64;

    public ImgCropServerComponent() {
        addFunction("imageUpdate", arguments -> {
            String imageBase64 = arguments.getString(0);
            currentImageBase64=imageBase64;
            if (imageUpdateListener != null) {
                imageUpdateListener.imageUpdate(imageBase64);
            }
        });
    }

    public void registerImgCropResultUpdateRpc(ImgCropResultUpdateRpc imgCropResultUpdateRpc) {
        if (imgCropResultUpdateRpc != null) {
            registerRpc(imgCropResultUpdateRpc);
        }
    }

    public void gerImageCropResult() {
        getRpcProxy(ImgCropClientRpc.class).gerImageCropResult();
    }

    public static class ImgCropState extends JavaScriptComponentState {
        public String imageBase64;
        public String customClass;
        public boolean enableExif;
        public boolean enableOrientation;
        public boolean enableResize;
        public boolean enableZoom;
        public boolean mouseWheelZoom;
        public boolean showZoomer;
        public ViewPort viewPort;
        public float quality = 1;
    }
}
