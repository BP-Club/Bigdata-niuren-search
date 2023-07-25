import {request}  from '@/utils/request.js'
//例子
export const getGoods = (data) => {
    return request({
        url: '/data/api.goods/getGoods',
        method:"POST",
		data
    })
	
}

export const getCate = (data) => {
    return request({
        url: '/data/api.goods/getCate',
        method:"POST",
		data
    })
	
}
//获取首页分类
export const getIndexCate = (data) => {
    return request({
        url: '/data/api.goods/getIndexCate',
        method:"POST",
		data
    })
	
}

/* 商品详情 */
//获取首页分类
export const getDetail = (data) => {
    return request({
        url: '/data/api.goods/getDetail',
        method:"POST",
		data
    })

}
/*获取订单未读红点*/
export const orderDots = (data) => {
    return request({
        url: '/data/api.auth.user/orderDots',
        method:"POST",
		data
    })

}
/*分享者绑定(已登录调用)*/
export const bindSpread = (data) => {
    return request({
        url: '/data/api.auth.user/bindSpread',
        method:"POST",
		data
    })

}
/*获取选中的门店(已登录)*/
export const getSelectedStore = (data) => {
    return request({
        url: '/data/api.auth.user/getSelectedStore',
        method:"POST",
		data
    })
}
/*保存选中的门店(已登录)*/
export const selectedStore= (data) => {
    return request({
        url: '/data/api.auth.user/selectedStore',
        method:"POST",
		data
    })

}



