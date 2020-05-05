//主程序,业务逻辑
(function() {
	const game = Common.getGameWithInit();
	//启动页
	(function() {
		const stageMenu = Menu.getOrCreateMenu(game);

		//事件绑定 - 按按钮才触发
        Common.buttonBind(function (e) {
        	const name = Common.inputText();

        	//检测是否输入名字
        	if (name === "") {
        		game.addMessage("名字不能为空!","#ff0000");
        		return;
			}

        	//检测名字是否重复
			$.getJSON('/user/checkName?name=' + name, function(result) {
				if (!result.success) {
					game.addMessage(result.message, "#ff0000");
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

		});

        //其他函数定义
        this.updateAfterConnect = function (name) {
        	const tankLogo = Menu.getTankLogo();
			stageMenu.updateItemId(tankLogo,name);
            game.addConnectCheckEvent();

			//隐藏输入框和按钮
			Common.inputEnable(false);
			Common.buttonEnable(false);

			//删除提示文字
			stageMenu.deleteInfo();

        	//重设输入框的属性和事件
			Common.inputResize();
			Common.inputBindMessageControl();

			//新增文字描述来取代按钮和输入框
            stageMenu.createItem( {
				draw:function (context) {
					context.font = '30px Arial';
					context.textAlign = 'center';
					context.textBaseline = 'middle';
					context.fillStyle = '#5E6C77';
					context.fillText('你的名字: ' + name,Common.width() / 2,85);
				}
			});

            //显示房间列表

			$.getJSON('/user/getRooms', function(result) {
				if (!result.success) {
					game.addMessage(result.message, "#ff0000");
					return;
				}

				const selectWindow = document.getElementById("room-list");
				let selectFlag = false;
				result.data.roomList.forEach(function (room) {
					let div = document.createElement('div');
					div.className = "select-item";
					selectWindow.appendChild(div);

					const input = document.createElement('input');
					input.type = 'radio';
					input.id = room.roomId;
					input.name = "drone";
					//第一个元素被选中
					if (selectFlag === false) {
						input.checked = true;
						selectFlag = true;
					}
					div.appendChild(input);

					const label = document.createElement('label');
					label.setAttribute("for",input.id);
					label.className = "radio-label";
					label.textContent = "房间名:" + room.roomId
						+ " 地图名:" + room.mapId
						+ "," + room.roomType
						+ " 创建者:" + room.creator
						+ " 人数:" + room.userCount;
					div.appendChild(label);


					const select = document.createElement('select');
					select.id = room.roomId + "_";
					const optView = document.createElement('option');
					optView.text = "观看";
					optView.value = "0";
					select.add(optView);
					switch (room.roomType) {
						case "PVP":
							const optRed = document.createElement('option');
							optRed.text = "红队";
							optRed.value = "1";
							select.add(optRed);
							const optBlue = document.createElement('option');
							optBlue.text = "蓝队";
							optBlue.value = "2";
							select.add(optBlue);
							break;
						case "PVE":
							const optPlayer = document.createElement('option');
							optPlayer.text = "玩家";
							optPlayer.value = "1";
							select.add(optPlayer);
							break;
						default:
							break;
					}
					div.appendChild(select);
				});

				//添加末端的按钮
				const div = document.createElement('div');
				div.className = "select-item";
				selectWindow.appendChild(div);

				const btnJoin = document.createElement('button');
				btnJoin.textContent = "加入房间";
				div.appendChild(btnJoin);

				const btnCreate = document.createElement('button');
				btnCreate.textContent = "创建房间";
				div.appendChild(btnCreate);

				const btnNext = document.createElement('button');
				btnNext.textContent = "下一页";
				btnNext.className = "right";
				div.appendChild(btnNext);

				const btnFront = document.createElement('button');
				btnFront.textContent = "上一页";
				btnFront.className = "right";
				div.appendChild(btnFront);

				const pageInfo = document.createElement('label');
				pageInfo.textContent = "1/1";
				pageInfo.className = "right";
				div.appendChild(pageInfo);

				document.getElementById('room-list').style.visibility = 'visible';
			});

			//注册事件
			game.addMessageEvent("USERS", function () {
				if (game.getStatus() !== 2) {
					return;
				}
				game.updateStatus(3,"等待同步数据...")
				Common.sendStompMessage(
					{
						"x": tankLogo.x,
						"y": tankLogo.y,
						"speed": tankLogo.speed,
						"orientation": tankLogo.orientation,
						"action": tankLogo.action
					}, "ADD_TANK");
			});
			game.addMessageEvent("TANKS", function () {
				if (game.getStatus() !== 3) {
					return;
				}
				game.updateStatus(1);
			});
			game.addTimeEvent("READY",function () {
				Common.sendStompMessage(
					"READY","READY");
			},50);
		}
	})();
    game.init();
})();
