const Common = function() {};

Common.extend = function(target, settings, params) {
    params = params || {};
    for (let i in settings) {
        target[i] = params[i] || settings[i];
    }
    return target;
};

let _canvas;
Common.getCanvas = function() {
    if (!_canvas) {
        _canvas = document.getElementById("canvas");
        _canvas.width = window.innerWidth;
        _canvas.height = window.innerHeight;
        //自动跟随窗口变化
        window.addEventListener("resize", function () {
            _canvas.width = window.innerWidth;
            _canvas.height = window.innerHeight;
        });
    }
    return _canvas;
};
Common.width = function () {
    return Common.getCanvas().width;
};

Common.height = function () {
    return Common.getCanvas().height;
};

let _context;
Common.getContext = function() {
    if (_context == null) {
        const canvas = this.getCanvas();
        _context = canvas.getContext('2d');
    }
    return _context;
};

Common.images = function() {
    let img = document.createElement('img');
    img.src = 'tank/image/tank01.png';
    img.widthPics = 4;
    img.heightPics = 1;
    img.displayWidth = img.width / img.widthPics;
    img.displayHeight = img.height / img.heightPics;
    return img;
};