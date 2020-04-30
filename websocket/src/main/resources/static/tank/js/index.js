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
	})();
    game.init();
})();
