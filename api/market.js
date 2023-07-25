import {request}  from '@/utils/request.js'

/* 生成用户二维码 */
export const getQrcode = (data) => {
    return request({
        url: '/market/api.auth.sales/getQrcode',
        method:"POST",
		data
    })
}
/* 生成用户二维码 */
export const getPosterBg = (data) => {
    return request({
        url: '/market/api.auth.sales/getPosterBg',
        method:"POST",
		data
    })
}
/* 生成用户邀请海报 */
export const getSharePoster = (data) => {
    return request({
        url: '/market/api.auth.activity/getSharePoster',
        method:"POST",
		data
    })
}
