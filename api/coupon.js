import {request}  from '@/utils/request.js'

//获取优惠卷列表
export const getCoupons = (data) => {
    return request({
        url: '/market/api.auth.coupon/list',
        method:"POST",
		data
    })
}

//获取邀请好友记录
export const getDirectRecords = (data) => {
    return request({
        url: '/market/api.auth.activity/getDirectRecords',
        method:"POST",
		data
    })
}

