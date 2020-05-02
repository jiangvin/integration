

function Item(params) {
    this.params = params||{};
    this.settings = {
        id:"",
        stage: null,
        image: null,            //相应图片
        x:0,					//位置坐标:横坐标
        y:0,					//位置坐标:纵坐标
        width:20,				//宽
        height:20,				//高
        type:0,					//对象类型,0表示普通对象(不与地图绑定),1表示玩家控制对象,2表示程序控制对象
        color:'#F00',			//标识颜色
        status:0,				//对象状态,0表示未激活/结束,1表示正常,2表示暂停,3表示临时,4表示异常
        action:0,               //动作,0是停,1是走
        orientation:0,			//当前定位方向,0-3 上下左右
        speed:0,				//移动速度
        frames:1,				//速度等级,内部计算器times多少帧变化一次
        times:0,				//刷新画布计数(用于循环动画状态判断)
        timeout:0,				//倒计时(用于过程动画状态判断)
        control:{},				//控制缓存,到达定位点时处理
        update:function(){}, 	//更新参数信息
        draw:function(){},		//绘制

        //绘图
        drawImage:function(context) {
            if (this.id !== "") {
                context.font = '14px Helvetica';
                context.textAlign = 'center';
                context.textBaseline = 'bottom';
                context.fillStyle = '#FFF';
                context.fillText(
                    this.id,
                    this.x ,
                    this.y - this.image.height / this.image.heightPics / 2 - 5);
            }

            context.drawImage(this.image,
            	this.orientation * this.image.width / this.image.widthPics, 0,
            	this.image.width / this.image.widthPics, this.image.height / this.image.heightPics,
            	this.x - this.image.width / this.image.widthPics / 2, this.y - this.image.height / this.image.heightPics / 2,
            	this.image.width / this.image.widthPics, this.image.height);
        },

        canUpdate:function() {
            return this.status === 1;
        }
    };
    Common.extend(this,this.settings,this.params);
}