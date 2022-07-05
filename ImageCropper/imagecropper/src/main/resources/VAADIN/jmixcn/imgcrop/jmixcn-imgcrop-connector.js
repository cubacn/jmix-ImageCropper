function cn_jmix_imagecropper_toolkit_ui_ImgCropServerComponent() {
    var connector = this;
    var element = connector.getElement();
    element.style.height="100%";
    element.style.width="100%";
    element.style.display='inline-flex';
    element.style["flex-direction"]="row";
    element.style["justify-content"]="space-between";

    var rpcProxy = connector.getRpcProxy();
    var croppie = null;
    var quality = 1;
    var imageBase64 = null;

    // Create preview div element.
    var preview = document.createElement('div');
    preview.setAttribute('class', 'cr-preview')
    var img = document.createElement('img');
    img.setAttribute('class', 'cr-preview-img');
    img.setAttribute('src', '');

    var previewImageWrapper=document.createElement('div');
    previewImageWrapper.setAttribute("class","cr-preview-img-wrapper")
    previewImageWrapper.appendChild(img);
    preview.appendChild(previewImageWrapper);

    var sizeLabel = document.createElement('div');
    sizeLabel.setAttribute('class', 'cr-preview-label');
    preview.appendChild(sizeLabel);


    var originalImageWrapper=document.createElement('div');
    originalImageWrapper.setAttribute("class","cr-original-image-wrapper");
    var originalImage=document.createElement('div');
    originalImage.setAttribute("class","cr-original-image");
    originalImageWrapper.append(originalImage)

    element.appendChild(originalImageWrapper);
    element.appendChild(preview);

    connector.doDestroy = function () {
        if (croppie != null) {
            destroy();
        }
    };


    var me = this;
    /**
     * Crop result callback.
     * @param pw Preview element height
     */
    var updateF = function (pw) {
        croppie.result({
            type: 'base64',
            quality: quality,
            format: "jpeg"
        }).then(function (base64) {
            img.src = base64;
            imageBase64 = base64;
            croppie.result({
                type: 'blob',
                quality: quality,
                format: "jpeg"
            }).then(function (blob) {
                size=blob.size;
                sizeLabel.innerText = formatFilesize(size);
            });
        })
        element.style.height="100%";
        element.style.width="100%";
    };

    var formatFilesize=function (filesize) {
        if(!filesize){
            return "0 Bytes";
        }
        var unitArr = ["Bytes", "KB", "MB"];
        var index = 0;
        var srcsize = parseFloat(filesize);
        index = Math.floor(Math.log(srcsize) / Math.log(1024));
        var size = srcsize / Math.pow(1024, index);
        size = size.toFixed(2);
        return size + unitArr[index];
    };

    connector.registerRpc({
        gerImageCropResult: function () {
            rpcProxy.resultUpdate(imageBase64)
        }
    });

    connector.onStateChange = function () {
        var me = this;
        var opts = {};
        var ew, eh, pw;
        var state = connector.getState();
        quality = state.quality;
        quality = parseFloat(quality.toFixed(1));
        if (croppie == null) {
            opts = {
                customClass: state.customClass,
                enableExif: state.enableExif,
                enableOrientation: state.enableOrientation,
                enableResize: state.enableResize,
                enableZoom: state.enableZoom,
                mouseWheelZoom: state.mouseWheelZoom,
                showZoomer: state.showZoomer,
                viewport: state.viewPort
            };
            var url = "data:image/jpeg;base64," + state.imageBase64;
            croppie = new Croppie(originalImage, opts);
            originalImage.addEventListener('update', function (ev) {
                updateF();
            });
            croppie.bind({
                url: url
            })
        } else {
            updateF();
        }

    }
}
