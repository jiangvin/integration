//主程序,业务逻辑
(function() {
	const game = new Game('canvas');
	//启动页
	(function() {
		const stage = game.createStage({
			images : Common.images(),
            status : 1
		});
		//Tank Logo
		const tankLogo = stage.createItem({
			x: Common.width() / 2,
			y: Common.height() * .45,
			image: Common.images(),
			speed: 1,
			status: 1,
			draw: function (context) {
				this.drawImage(context);
			},
			update: function () {
				if (this.action === 0) {
					return;
				}

				switch (this.orientation) {
					case 0:
						this.y -= this.speed;
						break;
					case 1:
						this.y += this.speed;
						break;
					case 2:
						this.x -= this.speed;
						break;
					case 3:
						this.x += this.speed;
						break;
				}
			}
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
        	const name = Common.getInputText();

        	//检测是否输入名字
        	if (name === "") {
        		Common.addMessage("名字不能为空!","#ff0000");
        		return;
			}

        	//检测名字是否重复
			let success;
			$.ajaxSettings.async = false; //让访问变成同步执行
			$.getJSON('/user/checkName?name=' + name, function(result) {
				success = result.success;
				if (!result.success) {
					Common.addMessage(result.message, "#ff0000");
				}
			});
			if (!success) {
				return;
			}

			//开始连接
			const socket = new SockJS('/websocket-simple?name=' + name);
			stompClient = Stomp.over(socket);
			stompClient.connect({}, function(frame) {
				Common.addMessage("网络连接中: " + frame,"#ffffff");

				stage.updateAfterConnect(name);
				// 客户端订阅消息, 公共消息和私有消息
				stompClient.subscribe('/topic/send', function (response) {
					// receive(JSON.parse(response.body));
				});
				stompClient.subscribe('/user/queue/send', function (response) {
					// receive(JSON.parse(response.body));
				});
			});
		});

        stage.updateAfterConnect = function (name) {
        	//重设输入框的属性，使其变成消息输入框
			Common.inputResize();

			//隐藏输入框和按钮
			Common.inputEnable(false);
			Common.buttonEnable(false);

			//新增文字描述来取代按钮和输入框
			this.createItem( {
				draw:function (context) {
					context.font = '30px Arial';
					context.textAlign = 'center';
					context.textBaseline = 'middle';
					context.fillStyle = '#5E6C77';
					context.fillText('你的名字: ' + name,Common.width() / 2,85);
				}
			})
		}
	})();
    game.init();
})();
