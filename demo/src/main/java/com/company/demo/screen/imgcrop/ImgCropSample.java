package com.company.demo.screen.imgcrop;

import cn.jmix.imagecropper.toolkit.ui.ImgCropServerComponent;
import cn.jmix.imagecropper.screen.ImageCropWindow;
import cn.jmix.imagecropper.screen.ImageCropWindowOptions;

import io.jmix.ui.Notifications;
import io.jmix.ui.component.*;
import io.jmix.ui.screen.*;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;


/**
 * Created by Ray.Lv on 2019/10/21.
 */

@UiController("jmixcn_ImgCropSample")
@UiDescriptor("ImgCropSample.xml")
public class ImgCropSample extends Screen {

    @Inject
    private Image image;
    @Inject
    private FileUploadField uploadField;

    @Inject
    private Notifications notifications;
    @Inject
    private Button cropBtn;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        cropBtn.setEnabled(false);
    }

    @Subscribe("cropBtn")
    public void onCropBtnClick(Button.ClickEvent event) {
        InputStream file =  uploadField.getFileContent();
        if(file==null){
            return;
        }
        // Create an viewport configuration object
        ImgCropServerComponent.ViewPort viewPort =
                new ImgCropServerComponent.ViewPort(200, 100,
                ImgCropServerComponent.ViewPortType.square);
        // Create an option object
        ImageCropWindowOptions options = new ImageCropWindowOptions(file, 10, viewPort);
        // Open a winow for cropping an image
        ImageCropWindow.showAsDialog(this, options, (cropWindowAfterScreenCloseEvent)->{
            // process the cropping result
            if(cropWindowAfterScreenCloseEvent.getCloseAction().equals(WINDOW_DISCARD_AND_CLOSE_ACTION)){
               //cropping window is closed by  "Cancel" button
            }else if(cropWindowAfterScreenCloseEvent.getCloseAction().equals(WINDOW_COMMIT_AND_CLOSE_ACTION)){
                // cropping window is closed  by "ok" button,then we can get the cropping result in bytes.
                byte[] result = options.getResult();
                if (result != null) {
                    //show the cropping result to an image component
                    image.setSource(StreamResource.class)
                            .setStreamSupplier(()-> new ByteArrayInputStream(result)).setBufferSize(1024);
                }
            }
        });
    }

//    @Subscribe("uploadField")
//    public void onUploadFileUploadSucceed(FileUploadField.FileUploadSucceedEvent event) {
//        InputStream fileContent = ((FileUploadField) event.getSource()).getFileContent();
//        if (event.getContentLength()>0) {
//            notifications.create()
//                    .withCaption("Tip")
//                    .withDescription("File has been uploaded to temporary storage，click [Crop] button to start crop image" )
//                    .show();
//            image.setSource(StreamResource.class).setStreamSupplier(() -> fileContent);
//            cropBtn.setEnabled(true);
//        }
//    }

    @Subscribe("uploadField")
    public void onUploadFieldFileUploadError(UploadField.FileUploadErrorEvent event) {
        notifications.create()
                .withCaption("Tip")
                .withDescription("Uploading failed"+event.getCause().getMessage() )
                .show();
    }


    @Subscribe("uploadField")
    public void onUploadFieldFileUploadSucceed(SingleFileUploadField.FileUploadSucceedEvent event) {
        InputStream fileContent = ((FileUploadField) event.getSource()).getFileContent();
        if (StringUtils.isNotEmpty(event.getFileName())) {
            notifications.create()
                    .withCaption("Tip")
                    .withDescription("File has been uploaded to temporary storage，click [Crop] button to start crop image" )
                    .show();
            image.setSource(StreamResource.class).setStreamSupplier(() -> fileContent);
            cropBtn.setEnabled(true);
        }
    }


}
