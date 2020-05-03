//主程序,业务逻辑
(function() {
	const game = Common.getGame();
	//启动页
	(function() {
		const stage = game.createStage({
			images : Common.images(),
            status : 1
		});
		//Tank Logo
		const tankLogo = stage.createTank({
			x: Common.width() / 2,
			y: Common.height() * .45,
			speed: 1
		});
		//游戏名
		stage.createItem({
			draw:function(context) {
				context.font = 'bold 55px Helvetica';
				context.textAlign = 'center';
				context.textBaseline = 'middle';
				context.fillStyle = '#FFF';
				context.fillText('Tank World',Common.width() / 2,40);
			}
		});
		//提示信息
		const info = stage.createItem({
			draw:function(context) {
				context.font = '24px Helvetica';
				context.textAlign = 'center';
				context.textBaseline = 'middle';
				context.fillStyle = '#949494';
				context.fillText('请选择控制方式',Common.width() / 2,Common.height() - 70);
			}
		});

		//事件绑定 - 按按钮才触发
        Common.buttonBind(function (e) {
        	const name = Common.inputText();

        	//检测是否输入名字
        	if (name === "") {
        		game.addMessage("名字不能为空!","#ff0000");
        		return;
			}

        	//检测名字是否重复
			let success;
			$.ajaxSettings.async = false; //让访问变成同步执行
			$.getJSON('/user/checkName?name=' + name, function(result) {
				success = result.success;
				if (!result.success) {
					game.addMessage(result.message, "#ff0000");
				}
			});
			if (!success) {
				return;
			}

			//设定是否为触控模式
            Common.setTouch(e.currentTarget.id === "button2");

			//开始连接
            game.updateStatus(2,"等待连接中...");
			Common.stompConnect(name,function () {
                updateAfterConnect(name);
            });

		});

        //其他函数定义
        this.updateAfterConnect = function (name) {
            stage.updateItemId(tankLogo,name);
            game.addUserCheckEvent();

			//隐藏输入框和按钮
			Common.inputEnable(false);
			Common.buttonEnable(false);

			//更新提示文字
            delete stage.items[info.id];
            stage.createItem({
                draw: function (context) {
                    let text = Common.getTouch() ? "滑动屏幕控制" : "键盘上下左右控制,回车发言";
                    context.font = '24px Helvetica';
                    context.textAlign = 'center';
                    context.textBaseline = 'middle';
                    context.fillStyle = '#949494';
                    context.fillText(text, Common.width() / 2, Common.height() - 70);
                }
            });

        	//重设输入框的属性和事件
			Common.inputResize();
			Common.inputBindMessageControl();

			//新增文字描述来取代按钮和输入框
            stage.createItem( {
				draw:function (context) {
					context.font = '30px Arial';
					context.textAlign = 'center';
					context.textBaseline = 'middle';
					context.fillStyle = '#5E6C77';
					context.fillText('你的名字: ' + name,Common.width() / 2,85);
				}
			});

			//注册事件，延迟执行同步单位
			game.addEvent("SYNC_MY_TANK",function () {
				Common.sendStompMessage(
					{
						"x": tankLogo.x,
						"y": tankLogo.y,
						"speed": tankLogo.speed,
						"orientation": tankLogo.orientation,
						"action": tankLogo.action
					}, "ADD_TANK");
			},1);
		}
	})();
    game.init();
})();
