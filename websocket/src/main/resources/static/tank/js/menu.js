//菜单场景，用于封装菜单场景的所有操作
{
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
    };

//删除提示信息
    Menu.deleteInfo = function() {
        delete this.stage.items["info1"];
        delete this.stage.items["info2"];
    };

    Menu.showRoomList = function () {
        const selectWindow = document.getElementById("room-list");

        //添加末端的按钮
        const div = document.createElement('div');
        div.className = "select-item";
        div.id = "button-label";
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
        queryRoomList(0,5);
    };

    const queryRoomList = function (start,limit) {
        $.getJSON('/user/getRooms?start=' + start + "&limit=" + limit, function(result) {
            if (!result.success) {
                Resource.getGame().addMessage(result.message, "#ff0000");
                return;
            }

            //删除之前的元素
            const buttonChild = document.getElementById("button-label");
            const selectWindow = document.getElementById("room-list");
            selectWindow.childNodes.forEach(function (child) {
                if (child.id !== "button-label") {
                    selectWindow.removeChild(child);
                }
            });

            let selectFlag = false;
            result.data.roomList.forEach(function (room) {
                let div = document.createElement('div');
                div.className = "select-item";
                selectWindow.insertBefore(div,buttonChild);

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
        });
    };
}