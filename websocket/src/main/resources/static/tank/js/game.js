'use strict';
/*
* 小型游戏引擎
*/

function Game() {
    //Game的画布初始化，要放在前面
    const canvas = Common.getCanvas();
    const context = Common.getContext();

    const thisGame = this;

    //帧率相关
    let _framesPerSecond = 60;

    //定时触发器类
    let _events = [];

    //用户类
    let _users = [];

    //左下角消息类
    let _messages = [];

    //webSocket网络通信
    let _stompClient;

    //布景相关
    const _stages = [];
    const _index = 0;

    //帧动画控制
    let _handler;

    //网络连接
    this.getStompClient = function () {
        return _stompClient;
    };
    this.clientConnect = function (name) {
        const socket = new SockJS('/websocket-simple?name=' + name);
        _stompClient = Stomp.over(socket);
        _stompClient.connect({}, function(frame) {
            thisGame.addMessage("网络连接中: " + frame,"#ffffff");

            // 客户端订阅消息, 公共消息和私有消息
            _stompClient.subscribe('/topic/send', function (response) {
                thisGame.receiveFromServer(JSON.parse(response.body));
            });
            _stompClient.subscribe('/user/queue/send', function (response) {
                thisGame.receiveFromServer(JSON.parse(response.body));
            });
        });

        thisGame.addEvent("USER_CHECK");
    };
    this.receiveFromServer = function(messageDto) {
        switch (messageDto.messageType) {
            case "USER_MESSAGE":
                thisGame.addMessage(messageDto.message, "#FFF");
                break;
            case "SYSTEM_MESSAGE":
                thisGame.addMessage(messageDto.message, "#FF0");
                break;
            case "USER_COUNT":
                _users = JSON.parse(messageDto.message);
                break;
            default:
                //todo 下发到具体stage里面
                break;
        }
    };

    //动画相关
    this.start = function() {
        let totalFrames = 0;
        let lastFrames = 0;
        let lastDate = Date.now();
        const step = function () {

            //计算帧数
            ++totalFrames;
            const offset = Date.now() - lastDate;
            if (offset >= 1000) {
                thisGame.setFps(totalFrames - lastFrames);
                lastFrames = totalFrames;
                lastDate += offset;
            }

            //开始绘制画面
            context.clearRect(0, 0, canvas.width, canvas.height);
            context.fillStyle = '#2b2b2b';
            context.fillRect(0, 0, canvas.width, canvas.height);

            thisGame.updateEvents();

            const stage = thisGame.currentStage();
            stage.update();
            stage.draw(context);

            thisGame.drawMessage(context);
            thisGame.drawInfo(context);

            _handler = requestAnimationFrame(step);
        };
        _handler = requestAnimationFrame(step);
    };
    this.stop = function(){
        _handler && cancelAnimationFrame(_handler);
    };

    //FPS相关
    this.setFps = function(framesPerSecond) {
        _framesPerSecond = framesPerSecond;
        if (_framesPerSecond < 50) {
            console.log("fps too low,need reload all data!", _framesPerSecond)
        }
    };

    //布景相关
    this.createStage = function(options){
        const stage = new Stage(options);
        stage.index = _stages.length;
        _stages.push(stage);
        return stage;
    };
    this.currentStage = function () {
      return _stages[_index];
    };

    //消息类
    this.addMessage = function (context,color) {
        let message = {};
        message.date = new Date();
        message.lifetime = 300; //显示时间300帧，5秒
        message.context = context;
        message.color = color;
        _messages.unshift(message); //塞在头部
    };
    this.drawMessage = function (context) {
        let height = Common.height() - 40;
        context.font = '16px Helvetica';
        context.textAlign = 'left';
        context.textBaseline = 'bottom';
        _messages.forEach(function (message) {
            if (message.lifetime > 0) {
                message.lifetime -= 1;
            }
            context.globalAlpha = (message.lifetime / 300);
            context.fillStyle = message.color;
            context.fillText("[" + message.date.format("hh:mm:ss") + "] " + message.context,25,height);
            height -= 18;
        });

        context.globalAlpha = 1;

        //消息全部过期，清除
        if (_messages.length !== 0 && _messages[0].lifetime <= 0) {
            _messages = [];
        }
    };

    //事件类
    this.addEvent = function (eventType,timeout) {
        let event = {};
        event.eventType = eventType;
        event.timeout = timeout ? timeout : 100; //默认100帧倒计时，不到1.5秒
        console.log("add event:" + event.eventType + " timeout:" + event.timeout);
        _events.push(event);
    };
    this.updateEvents = function () {
        for (let i = 0; i < _events.length; ++i) {
            const event = _events[i];
            if (event.timeout > 0) {
                --event.timeout;
            } else {
                thisGame.processEvent(event);
                //删除事件
                _events.splice(i,1);
                --i;
            }
        }
    };
    this.processEvent = function (event) {
        console.log("process event:" + event.eventType);
        switch (event.eventType) {
            case "USER_CHECK":
                if (_users.length === 0) {
                    $.ajaxSettings.async = true; //异步执行
                    $.getJSON('/user/getAll', function(result) {
                        _users = result;
                    });
                }
                break;
            default:
                break;
        }
    };

    //显示版权信息和帧率信息
    this.drawInfo = function (context) {
        //版权信息
        context.font = '14px Helvetica';
        context.textAlign = 'right';
        context.textBaseline = 'bottom';
        context.fillStyle = '#AAA';
        context.fillText('© Created by Vin (WX: Jiang_Vin)',Common.width() - 12,Common.height() - 5);

        //帧率信息
        context.font = '14px Helvetica';
        context.textAlign = 'left';
        context.textBaseline = 'bottom';
        context.fillStyle = '#AAA';
        let text = 'FPS: ' + _framesPerSecond;
        if (_users.length > 0) {
            text += ' / USER: ' + _users.length;
        }
        context.fillText(text, 10, Common.height() - 5);
    };

    //初始化游戏引擎
    this.init = function() {
        this.start();
    };
}
