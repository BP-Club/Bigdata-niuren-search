import {request}  from '@/utils/request.js'
/* 新增地址 */
export const addAddress = (data) => {
    return request({
        url: '/data/api.auth.address/set',
        method:"POST",
		data
    })
}
/* 修改地址 */
export const editAddress = (data) => {
    return request({
        url: '/data/api.auth.address/set',
        method:"POST",
		data
    })
}
/* 获取地址列表 */
export const addresslist = (data) => {
    return request({
        url: '/data/api.auth.address/get',
        method:"POST",
		data
    })
}
/* 删除地址信息 */
export const deleteAddress = (data) => {
    return request({
        url: '/data/api.auth.address/remove',
        method:"POST",
		data
    })
}
/* 获取单个地址信息 */
export const getAddressByCode = (data) => {
    return request({
        url: '/data/api.auth.address/getCodeAddress',
        method:"POST",
		data
    })
}
/* 设置为默认地址 */
export const setStaticAdddress = (data) => {
    return request({
        url: '/data/api.auth.address/state',
        method:"POST",
		data
    })
}
/*获取默认地址*/
export const getDefaultAddress = (data) => {
    return request({
        url: '/data/api.auth.address/getDefaultAddress',
        method:"POST",
		data
    })
}