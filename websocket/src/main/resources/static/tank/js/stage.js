

function Stage(params) {
    this.params = params||{};
    this.settings = {
        index:0,                        //布景索引
        status:0,						//布景状态,0表示未激活/结束,1表示正常,2表示暂停,3表示临时,4表示异常
        items:[],						//对象队列
        timeout:0,						//倒计时(用于过程动画状态判断)
        receiveFromServer:function (messageDto) {}
    };
    Common.extend(this,this.settings,this.params);

    this.draw = function(context) {
        this.items.forEach(function (item) {
            item.draw(context)
        });
    };

    //更新相关
    this.canUpdate = function() {
        return this.status === 1;
    };
    this.update = function() {
        if (!this.canUpdate()) {
            return;
        }

        this.items.forEach(function (item) {
            if (item.canUpdate()) {
                item.update();
            }
        });
    };

    this.createItem = function(options) {
        const item = new Item(options);
        //关系绑定
        item.stage = this;
        item.index = this.items.length;
        this.items.push(item);
        if (item.id !== "") {
            this.items[item.id] = item;
        }
        return item;
    };
    this.updateItemId = function (item, newId) {
        //删除旧id
        if (item.id !== null && this.items[item.id] !== null) {
            delete this.items[item.id];
        }

        //增加新id
        item.id = newId;
        this.items[newId] = item;
    };

    //事件绑定
    this.bind = function(eventType, callback) {
        window.addEventListener(eventType,callback);
    }
}