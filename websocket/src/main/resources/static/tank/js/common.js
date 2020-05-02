const Common = function() {};

//全局初始化函数，在创建game后启动
Common.init = function() {

    // //在手机上禁用滑动
    // window.addEventListener('touchmove', function(e) {
    //     e.stopPropagation();
    // }, false);
    //
    //
    // document.addEventListener("plusready",function (e) {
    //     console.log("plusready");
    // });
    //
    // window.addEventListener('touchstart', function(e) {
    //     console.log("touchstart:" + e.touches[0].clientX + " " + e.touches[0].clientY);
    // });
    //
    // window.addEventListener('touchmove', function(e) {
    //     console.log("touchmove:" + e.touches[0].clientX + " " + e.touches[0].clientY);
    // });
    //
    // window.addEventListener('touchend', function(e) {
    //     console.log("touchend");
    // });

};

Common.extend = function(target, settings, params) {
    params = params || {};
    for (let i in settings) {
        target[i] = params[i] || settings[i];
    }
    return target;
};

let _game;
Common.getGame = function() {
    if (!_game) {
        _game = new Game("canvas");
        Common.init();
    }
    return _game;
};

let _canvas;
Common.getCanvas = function() {
    if (!_canvas) {
        _canvas = document.getElementById("canvas");
        Common.windowChange();

        //自动跟随窗口变化
        window.addEventListener("resize", function () {
            Common.windowChange();
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
Common.windowChange = function() {
    const width = window.innerWidth;
    const height = window.innerHeight;

    let style = "";
    if(width >= height) { // 横屏
        style += "width:" + width + "px;";  // 注意旋转后的宽高切换
        style += "height:" + height + "px;";
        style += "-webkit-transform: rotate(0); transform: rotate(0);";
        style += "-webkit-transform-origin: 0 0;";
        style += "transform-origin: 0 0;";
        _canvas.width = width;
        _canvas.height = height;
    }
    else { // 竖屏
        style += "width:" + height + "px;";
        style += "height:" + width + "px;";
        style += "-webkit-transform: rotate(90deg); transform: rotate(90deg);";
        // 注意旋转中点的处理
        style += "-webkit-transform-origin: " + width / 2 + "px " + width / 2 + "px;";
        style += "transform-origin: " + width / 2 + "px " + width / 2 + "px;";
        _canvas.width = height;
        _canvas.height = width;
    }
    let wrapper =  document.getElementById("wrapper");
    wrapper.style.cssText = style;
};

//操控相关
let _touch = null;
Common.setTouch = function(touch) {
    if (_touch !== null) {
        return;
    }
    _touch = touch;
    if (_touch) {

    } else {
        Common.bindKeyboard();
    }

};
Common.getTouch = function() {
    return _touch;
};
Common.bindKeyboard = function() {
    window.addEventListener("keydown",function(e) {
        let event = null;
        switch (e.key) {
            case "Up":
            case "ArrowUp":
                event = "Up";
                break;
            case "Down":
            case "ArrowDown":
                event = "Down";
                break;
            case "Left":
            case "ArrowLeft":
                event = "Left";
                break;
            case "Right":
            case "ArrowRight":
                event = "Right";
                break;
            default:
                break;
        }
        if (event != null) {
            _game.controlEvent(event);
        }
    });
    window.addEventListener('keyup',function(e) {
        let event = null;
        switch (e.key) {
            case "ArrowUp":
            case "ArrowDown":
            case "ArrowLeft":
            case "ArrowRight":
            case "Up":
            case "Down":
            case "Left":
            case "Right":
                event = "Stop";
                break;
            default:
                break;
        }
        if (event != null) {
            _game.controlEvent(event);
        }
    });
};

let _context;
Common.getContext = function() {
    if (!_context) {
        const canvas = this.getCanvas();
        _context = canvas.getContext('2d');
    }
    return _context;
};

//按钮
Common.buttonBind = function(callback) {
    //先删除之前的事件
    Common.buttonUnbind();
    $('#button1').bind('click',callback);
    $('#button2').bind('click',callback);
};
Common.buttonUnbind = function() {
    $('#button1').unbind('click');
    $('#button2').unbind('click');
};
Common.buttonEnable = function(enable) {
    document.getElementById('button1').style.visibility = enable ? 'visible' : 'hidden';
    document.getElementById('button2').style.visibility = enable ? 'visible' : 'hidden';
};

//输入框
let _bindMessageControl;
let _inputEnable = true;
Common.inputText = function() {
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
                Common.sendStompMessage(text);
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

//网络通信
let _stompClient;
Common.stompConnect = function(name) {
    const socket = new SockJS('/websocket-simple?name=' + name);
    _stompClient = Stomp.over(socket);
    _stompClient.connect({}, function(frame) {
        _game.addMessage("网络连接中: " + frame,"#ffffff");

        // 客户端订阅消息, 公共消息和私有消息
        _stompClient.subscribe('/topic/send', function (response) {
            _game.receiveStompMessage(JSON.parse(response.body));
        });
        _stompClient.subscribe('/user/queue/send', function (response) {
            _game.receiveStompMessage(JSON.parse(response.body));
        });
    });
};
Common.sendStompMessage = function(message, messageType, sendTo) {
    if (!_stompClient) {
        return;
    }

    if (!messageType) {
        messageType = "USER_MESSAGE";
    }

    _stompClient.send("/send", {},
        JSON.stringify({
          "message": message,
          "messageType": messageType,
            "sendTo": sendTo
        }));
};

//工具类
Date.prototype.format = function(fmt) {
    const o = {
        "M+": this.getMonth() + 1,               //月份
        "d+": this.getDate(),                    //日
        "h+": this.getHours(),                   //小时
        "m+": this.getMinutes(),                 //分
        "s+": this.getSeconds(),                 //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds()             //毫秒
    };
    if(/(y+)/.test(fmt)) {
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    }
    for(let k in o) {
        if(new RegExp("("+ k +")").test(fmt)){
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length===1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
        }
    }
    return fmt;
}

let _id = 0;
Common.generateId = function() {
    return "id_" + _id++;
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