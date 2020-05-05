//菜单场景，用于封装菜单场景的所有操作

function Menu() {
    this.stage = null;
    this.tankLogo = null;
}

Menu.getOrCreateMenu = function (game) {
  if (this.stage) {
      return this.stage;
  }

  //开始初始化
  this.stage = game.createStage({
      status : 1
  });

  this.tankLogo = this.stage.createTank({
      x: Common.width() / 2,
      y: Common.height() * .45,
      speed: 1
  });

    //游戏名
    this.stage.createItem({
        draw:function(context) {
            context.font = 'bold 55px Helvetica';
            context.textAlign = 'center';
            context.textBaseline = 'middle';
            context.fillStyle = '#FFF';
            context.fillText('Tank World',Common.width() / 2,40);
        }
    });

    //提示信息
    this.stage.createItem({
        id:"info1",
        draw:function(context) {
            context.font = '24px Helvetica';
            context.textAlign = 'center';
            context.textBaseline = 'middle';
            context.fillStyle = '#949494';
            context.fillText('键盘: 上下左右/空格/回车控制游戏',Common.width() / 2,Common.height() * .6);
        }
    });

    this.stage.createItem({
        id:"info2",
        draw:function(context) {
            context.font = '24px Helvetica';
            context.textAlign = 'center';
            context.textBaseline = 'middle';
            context.fillStyle = '#949494';
            context.fillText('触控: 触控屏幕控制游戏',Common.width() / 2,Common.height() * .6 + 30);
        }
    });

  return this.stage;
};

Menu.getTankLogo = function() {
    return this.tankLogo;
}

//删除提示信息
Stage.prototype.deleteInfo = function () {
  delete this.items["info1"];
  delete this.items["info2"];
};