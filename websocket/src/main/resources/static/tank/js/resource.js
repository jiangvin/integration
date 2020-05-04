
// 图片资源类
function Resource() {
    this.images = null;
}

Resource.getImages = function() {
    if (this.images) {
        return this.images;
    }

    this.images = [];

    //加载初始资源
    const img = document.createElement('img');
    img.src = 'tank/image/tank01.png';
    img.widthPics = 4;
    img.heightPics = 1;
    img.displayWidth = img.width / img.widthPics;
    img.displayHeight = img.height / img.heightPics;
    this.images['tank01'] = img;

    return this.images;
};

Resource.getImage = function (id, widthPics, heightPics) {
    const images = Resource.getImages();
    if (!images[id]) {
        widthPics = widthPics ? widthPics : 1;
        heightPics = heightPics ? heightPics : 1;
        const img = document.createElement('img');
        img.src = 'tank/image/' + id + ".png";
        img.widthPics = widthPics;
        img.heightPics = heightPics;
        img.displayWidth = img.width / img.widthPics;
        img.displayHeight = img.height / img.heightPics;
        images[id] = img;
    }
    return images[id];
};