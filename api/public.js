import {request}  from '@/utils/request.js'
//获取首页轮播图
export const getSlider = (data) => {
    return request({
        url: '/data/api.data/getSlider',
        method:"POST",
		data
    })
	
}
//获取协议详情内容
export const getAgreeDetail = (data) => {
    return request({
        url: '/data/api.data/getAgreeDetail',
        method:"POST",
		data
    })
}

/* 获取用户资料 */
export const getUserInfo = (data) => {
    return request({
        url: '/data/api.auth.user/getInfo',
        method:"POST",
		data
    })
}

