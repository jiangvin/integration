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
		stage.createItem({
			draw:function(context) {
				context.font = '24px Helvetica';
				context.textAlign = 'center';
				context.textBaseline = 'middle';
				context.fillStyle = '#949494';
				context.fillText('上下左右移动，回车说话',Common.width() / 2,Common.height() - 70);
			}
		});
		//事件绑定
		stage.bind('keydown',function(e) {
			switch (e.key) {
				case "Up":
				case "ArrowUp":
					tankLogo.orientation = 0;
					tankLogo.action = 1;
					break;
				case "Down":
				case "ArrowDown":
					tankLogo.orientation = 1;
                    tankLogo.action = 1;
					break;
				case "Left":
				case "ArrowLeft":
					tankLogo.orientation = 2;
                    tankLogo.action = 1;
					break;
				case "Right":
				case "ArrowRight":
					tankLogo.orientation = 3;
                    tankLogo.action = 1;
					break;
				default:
					break;

			}
		});
        stage.bind('keyup',function(e) {
            switch (e.key) {
                case "ArrowUp":
                case "ArrowDown":
                case "ArrowLeft":
                case "ArrowRight":
				case "Up":
				case "Down":
				case "Left":
				case "Right":
                    tankLogo.action = 0;
                    break;
                default:
                    break;

            }
        });
        Common.buttonBind(function () {
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

			//开始连接
			Common.stompConnect(name);
			game.addUserCheckEvent();

			stage.updateAfterConnect(name);

			stage.updateItemId(tankLogo,name);
		});

        stage.updateAfterConnect = function (name) {
			//隐藏输入框和按钮
			Common.inputEnable(false);
			Common.buttonEnable(false);

        	//重设输入框的属性和事件
			Common.inputResize();
			Common.inputBindMessageControl();

			//新增文字描述来取代按钮和输入框
			this.createItem( {
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
			});
		}
	})();
    game.init();
})();
