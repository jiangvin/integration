'use strict';
/*
* 小型游戏引擎
*/

function Game() {
    //Game的画布初始化，要放在前面
    const canvas = Common.getCanvas();
    const context = Common.getContext();

    //帧率相关
    let _framesPerSecond = 60;

    //布景相关
    const _stages = [];
    const _index = 0;

    //帧动画控制
    let _handler;

    //动画开始
    this.start = function() {
        let totalFrames = 0;
        let lastFrames = 0;
        let lastDate = Date.now();
        const thisGame = this;
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

            const stage = thisGame.currentStage();

            //这里迟早会删掉
            if (stage.timeout) {
                stage.timeout--;
            }

            stage.update();
            stage.draw(context);

            thisGame.drawMessage(context);
            thisGame.drawInfo(context);

            _handler = requestAnimationFrame(step);
        };
        _handler = requestAnimationFrame(step);
    };
    //动画结束
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

    //显示消息
    this.drawMessage = function (context) {
        let height = Common.height() - 40;
        context.font = '16px Helvetica';
        context.textAlign = 'left';
        context.textBaseline = 'bottom';
        Common.messages().forEach(function (message) {
            if (message.lifetime > 0) {
                message.lifetime -= 1;
            }
            context.globalAlpha = (message.lifetime / 300);
            context.fillStyle = message.color;
            context.fillText("[" + message.date.format("hh:mm:ss") + "] " + message.context,25,height);
            height -= 18;
        });
        context.globalAlpha = 1;
        if (Common.messages().length !== 0 && Common.messages()[0].lifetime <= 0) {
            Common.clearMessages();
        }
    };
    //显示版权信息和帧率信息
    this.drawInfo = function (context) {
        //版权信息
        context.font = '14px Helvetica';
        context.textAlign = 'right';
        context.textBaseline = 'bottom';
        context.fillStyle = '#AAA';
        context.fillText('© Created by Vin 2020',Common.width() - 12,Common.height() - 5);

        //帧率信息
        context.font = '14px Helvetica';
        context.textAlign = 'left';
        context.textBaseline = 'bottom';
        context.fillStyle = '#AAA';
        context.fillText('FPS: ' + _framesPerSecond, 10, Common.height() - 5);
    };

    //初始化游戏引擎
    this.init = function() {
        this.start();
    };
}
