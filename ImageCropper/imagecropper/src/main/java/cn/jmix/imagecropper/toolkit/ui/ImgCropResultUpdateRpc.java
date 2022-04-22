package cn.jmix.imagecropper.toolkit.ui;

import com.vaadin.shared.communication.ServerRpc;

public interface ImgCropResultUpdateRpc extends ServerRpc {

    void resultUpdate(String base64);

}
