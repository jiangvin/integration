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

let _messages = [];
Common.addMessage = function(context, color) {
    let message = {};
    message.lifetime = 300; //显示时间300帧，5秒
    message.context = context;
    message.color = color;
    _messages.unshift(message); //塞在头部
};
Common.messages = function() {
    return _messages;
};
Common.clearMessages = function() {
    _messages = [];
};

//按钮
Common.buttonBind = function(callback) {
    //先删除之前的事件
    Common.buttonUnbind();
    $('#button').bind('click',callback);
};
Common.buttonUnbind = function() {
    $('#button').unbind('click');
};
Common.buttonEnable = function(enable) {
    document.getElementById('button').style.visibility = enable ? 'visible' : 'hidden';
};

//输入框
let _bindMessageControl;
let _inputEnable = true;
Common.getInputText = function() {
    return $('#input').val();
};
Common.inputEnable = function(enable) {
    _inputEnable = enable;
    document.getElementById('input').style.visibility = _inputEnable ? 'visible' : 'hidden';
};
Common.inputResize = function() {
    const input = $('#input');
    input.attr("placeholder","请输入信息");
    input.val("");
    input.removeClass("input-name");
    input.addClass("input-message");
};
Common.inputBindMessageControl = function() {
    if (_bindMessageControl) {
        return;
    }

    _bindMessageControl = true;
    window.addEventListener("keydown",function (e) {
        if (e.key !== "Enter") {
            return;
        }

        const input = $('#input');
        if (_inputEnable) {
            //关闭输入框
            //关闭输入框前先处理文字信息
            const text = input.val();
            if (text !== "") {
                Common.addMessage(text,"#FFF");
                input.val("");
            }
            _inputEnable = !_inputEnable;
            Common.inputEnable(_inputEnable);
        } else {
            //打开输入框
            _inputEnable = !_inputEnable;
            Common.inputEnable(_inputEnable);
            input.focus();
        }

    });
};

//测试类
Common.images = function() {
    let img = document.createElement('img');
    img.src = 'tank/image/tank01.png';
    img.widthPics = 4;
    img.heightPics = 1;
    img.displayWidth = img.width / img.widthPics;
    img.displayHeight = img.height / img.heightPics;
    return img;
};