import {request}  from '@/utils/request.js'
//例子

//获取最近分店
export const getNear = (data) => {
    return request({
        url: '/store/api.store/getNear',
        method:"POST",
		data
    })
	
}
//获取最近一家分店(首页顶部)
export const getLately = (data) => {
    return request({
        url: '/store/api.store/getLately',
        method:"POST",
		data
    })
	
}


//加入购物车
export const setCart = (data) => {
    return request({
        url: '/store/api.auth.order/setCart',
        method:"POST",
		data
    })
}

/* 获取订单列表 */
export const getList = (data) => {
    return request({
        url: '/store/api.auth.order/list',
        method:"POST",
		data
    })
}
/* 下订单 */
export const orderAdd = (data) => {
    return request({
        url: '/store/api.auth.order/add',
        method:"POST",
		data
    })
}
/* 获取支付参数 */
export const getPayment = (data) => {
	return request({
		url: '/store/api.auth.order/getPayment',
		method: "POST",
		data
	})
}
/* 计算获取配送费 */
export const getAddDeliveryFee = (data) => {
	return request({
		url: '/store/api.auth.order/getDeliveryFee',
		method: "POST",
		data
	})
}
/*订单详情*/
export const getOrderDetail = (data) => {
	return request({
		url: '/store/api.auth.order/getDetail',
		method: "POST",
		data
	})
}
/*获取归还方式的默认日期*/
export const getDefaultReturnDate = (data) => {
	return request({
		url: '/store/api.auth.order/getDefaultReturnDate',
		method: "POST",
		data
	})
}


/*提交退款申请，新增++++++++++++++++*/
export const applyRefund = (data) => {
	return request({
		url: '/store/api.auth.order/applyRefund',
		method: "POST",
		data
	})
}




/*提交退款记录，新增++++++++++++++++*/
export const getRefundTrack = (data) => {
	return request({
		url: '/store/api.auth.order/getRefundTrack',
		method: "POST",
		data
	})
}


/*确认收货，新增++++++++++++++++*/
export const orderConfirm = (data) => {
	return request({
		url: '/store/api.auth.order/confirm',
		method: "POST",
		data
	})
}
