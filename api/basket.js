import {
	request
} from '@/utils/request.js'
//加入购车
export const setCart = (data) => {
	return request({
		url: '/store/api.auth.basket/setCart',
		method: "POST",
		data
	})

}
export const incOneNum = (data) => {
	return request({
		url: '/store/api.auth.basket/incOneNum',
		method: "POST",
		data
	})

}

export const redOneOneNum = (data) => {
	return request({
		url: '/store/api.auth.basket/redOneNum',
		method: "POST",
		data
	})

}
export const remove = (data) => {
	return request({
		url: '/store/api.auth.basket/remove',
		method: "POST",
		data
	})
}

/* 获取洗衣篮数据 */
export const getBasketList = (data) => {
	return request({
		url: '/store/api.auth.basket/getData',
		method: "POST",
		data
	})
}
/* 洗衣篮商品数量调整或删除 */
export const setBasketNumber = (data) => {
	return request({
		url: '/store/api.auth.basket/opSet',
		method: "POST",
		data
	})
}
/* 提交结算 */
export const submitSettle = (data) => {
	return request({
		url: '/store/api.auth.basket/settle',
		method: "POST",
		data
	})
}
