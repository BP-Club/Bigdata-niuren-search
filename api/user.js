import {request}  from '@/utils/request.js'
//微信登录获取session
export const wxLoginPost = (data) => {
    return request({
        url: '/data/api.wxapp/session',
        method:"POST",
		data
    })
	
}
//修改用户资料
export const updateWxInfo = (data) => {
    return request({
        url: '/data/api.auth.user/updateWxInfo',
        method:"POST",
		data
    })
	
}

