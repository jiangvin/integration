'use strict';
/*
* 小型游戏引擎
*/

function Game() {
    const thisGame = this;

    //控制
    const _control = {lastOrientation:-1, lastAction:-1};

    //Game的画布初始化，要放在前面
    const canvas = Common.getCanvas();
    const context = Common.getContext();

    //帧率相关
    let _framesPerSecond = 60;

    //定时触发器类
    let _events = [];

    //用户类
    let _users = [];

    //左下角消息类
    let _messages = [];

    //布景相关
    const _stages = [];
    const _index = 0;

    //渲染控制
    let _drawHandler;
    //运算控制
    let _updateHandler;

    this.receiveStompMessage = function(messageDto) {
        switch (messageDto.messageType) {
            case "USER_MESSAGE":
                thisGame.addMessage(messageDto.message, "#FFF");
                break;
            case "SYSTEM_MESSAGE":
                thisGame.addMessage(messageDto.message, "#FF0");
                break;
            case "USERS":
                _users = messageDto.message;
                break;
            default:
                //给当前场景处理服务消息
                this.currentStage().receiveStompMessage(messageDto);
                break;
        }
    };

    //控制相关
    this.updateControl = function (orientation, action) {
        let update;
        if (orientation !== null && _control.lastOrientation !== orientation) {
            update = true;
        }

        if (action != null && _control.lastAction !== action) {
            update = true;
        }

        if (!update) {
            return;
        }

        _control.lastOrientation = orientation != null ? orientation :_control.lastOrientation;
        _control.lastAction = action != null ? action : _control.lastAction;
        Common.sendStompMessage({
            orientation: _control.lastOrientation,
            action: _control.lastAction
        },"UPDATE_TANK_CONTROL");
    };

    //动画相关
    this.start = function() {
        let totalFrames = 0;
        let lastFrames = 0;
        let lastDate = Date.now();

        //开启运算
        _updateHandler = setInterval(function () {
            thisGame.updateEvents();
            const stage = thisGame.currentStage();
            stage.update();
        }, 18);

        //开启渲染
        const step = function () {

            //计算帧数
            ++totalFrames;
            const offset = Date.now() - lastDate;
            if (offset >= 1000) {
                _framesPerSecond = totalFrames - lastFrames;
                lastFrames = totalFrames;
                lastDate += offset;
            }

            //开始绘制画面
            context.clearRect(0, 0, canvas.width, canvas.height);
            context.fillStyle = '#2b2b2b';
            context.fillRect(0, 0, canvas.width, canvas.height);

            const stage = thisGame.currentStage();
            stage.draw(context);

            thisGame.drawMessage(context);
            thisGame.drawInfo(context);

            _drawHandler = requestAnimationFrame(step);
        };
        _drawHandler = requestAnimationFrame(step);
    };
    this.stop = function(){
        _drawHandler && cancelAnimationFrame(_drawHandler);
        _updateHandler && clearInterval(_updateHandler);
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
    this.addEvent = function (eventType,callBack,timeout) {
        let event = {};
        event.eventType = eventType;
        event.callback = callBack;
        event.timeout = timeout ? timeout : 100; //默认100帧倒计时，不到1.5秒
        console.log("add event:" + event.eventType + " timeout:" + event.timeout);
        _events.push(event);
    };
    this.addUserCheckEvent = function () {
        this.addEvent("USER_CHECK", function () {
            if (_users.length === 0) {
                $.ajaxSettings.async = true; //异步执行
                $.getJSON('/user/getAll', function(result) {
                    if (result.success) {
                        _users = result.data;
                    } else {
                        thisGame.addMessage(result.message,"#F00");
                    }
                });
            }
        });
    };
    this.updateEvents = function () {
        for (let i = 0; i < _events.length; ++i) {
            const event = _events[i];
            if (event.timeout > 0) {
                --event.timeout;
            } else {
                console.log("process event:" + event.eventType);
                event.callback();
                //删除事件
                _events.splice(i,1);
                --i;
            }
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
