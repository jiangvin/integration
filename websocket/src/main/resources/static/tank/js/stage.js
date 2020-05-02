

function Stage(params) {
    this.params = params||{};
    this.settings = {
        index:0,                        //布景索引
        status:0,						//布景状态,0表示未激活/结束,1表示正常,2表示暂停,3表示临时,4表示异常
        items:[],						//对象队列
        controlEvent:function(){}
    };
    Common.extend(this,this.settings,this.params);

    this.receiveStompMessage = function (messageDto) {
        const thisStage = this;
        switch (messageDto.messageType) {
            case "TANKS":
                const tanks = messageDto.message;
                tanks.forEach(function (tank) {
                    if (thisStage.items[tank.id]) {
                        //已存在
                        thisStage.items[tank.id].x = tank.x;
                        thisStage.items[tank.id].y = tank.y;
                        thisStage.items[tank.id].orientation = tank.orientation;
                        thisStage.items[tank.id].action = tank.action;
                    } else {
                        thisStage.createTank({
                            id:tank.id,
                            x:tank.x,
                            y:tank.y,
                            orientation: tank.orientation,
                            action: tank.action,
                            speed: tank.speed
                        });
                    }
                });
                break;
            case "REMOVE_TANK":
                const tankId = messageDto.message;
                if (thisStage.items[tankId]) {
                    delete thisStage.items[tankId];
                }
                break;
        }
    };

    this.draw = function(context) {
        for (let k in this.items) {
            this.items[k].draw(context);
        }
    };

    //更新相关
    this.canUpdate = function() {
        return this.status === 1;
    };
    this.update = function() {
        if (!this.canUpdate()) {
            return;
        }

        for (let k in this.items) {
            if (this.items[k].canUpdate()) {
                this.items[k].update();
            }
        }
    };

    this.createItem = function(options) {
        const item = new Item(options);
        //关系绑定
        item.stage = this;
        if (item.id === "") {
            item.id = Common.generateId();
        }
        this.items[item.id] = item;
        return item;
    };
    this.updateItemId = function (item, newId) {
        //删除旧id
        if (item.id && this.items[item.id]) {
            delete this.items[item.id];
        }

        //增加新id
        item.id = newId;
        this.items[newId] = item;
    };

    this.createTank = function (options) {
        let tankOptions = {};
        Common.extend(tankOptions,{
            x:0,
            y:0,
            speed:0,
            image: Common.images(),
            status: 1,
            id:"",
            action:0,
            orientation:0,

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
        },options);
        return this.createItem(tankOptions);
    };
}