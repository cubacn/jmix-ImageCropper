function cn_jmix_imagecropper_toolkit_ui_ImgCropServerComponent() {
    var connector = this;
    var element = connector.getElement();
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
    var sizeLabel = document.createElement('div');
    sizeLabel.setAttribute('class', 'cr-preview-label');

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
    /**
     * Initialize custom element attribute.
     * @param state
     * @param ew
     * @param eh
     * @param pw
     */
    var setElementAttr = function (state, ew, eh, pw) {
        // Add preview elements after setting the cropping component
        // and set preview properties.
        ew = element.offsetWidth;
        eh = element.offsetHeight;
        pw = ew * 0.46;
        element.style.width = ew * 0.5 + 'px';
        preview.style.height = pw + 'px';
        preview.style.width = pw + 'px';
        preview.style.maxHeight = pw + 'px';
        preview.style.maxWidth = pw + 'px';
        preview.appendChild(img);
        element.parentElement.appendChild(preview);
        element.parentElement.appendChild(sizeLabel);
        sizeLabel.style.top = pw + 'px';
        sizeLabel.style.left = preview.offsetLeft + 'px';
        img.style.left = (pw - state.viewPort.width)/2 + 'px';
        img.style.top = (pw - state.viewPort.height)/2 + 'px';
    }

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
            croppie = new Croppie(element, opts);
            setElementAttr(state);

            element.addEventListener('update', function (ev) {
                updateF();
            });
            croppie.bind({
                url: url
            })
        } else {
            // Reset element attribute.
            croppie.element.style.width = '50%';
            img.style.left = (pw - state.viewPort.width)/2 + 'px';
            img.style.top = (pw - state.viewPort.height)/2 + 'px';
            updateF();
        }

    }
}
