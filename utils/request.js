const baseURL = process.env.VUE_APP_BASE_URL;
 export default baseURL
 
 let timeout = 100000000; //请求时间
 export const request = (config) => {
 	return new Promise((resolve, reject) => {
 		uni.showNavigationBarLoading();
 		let token = uni.getStorageSync('token');
 		config.header = {
 			...config.header, // 稳妥起见，先展开原本的 header，再额外添加 Authorization
 			"Api-Name": "wxapp",
			 // "content-type":'application/form-data',
 			 "Content-Type": 'application/x-www-form-urlencoded; charset=UTF-8',
 		}
 		// 发起请求前，自动添加请求头，自动带上 token（相当于请求拦截器）
 		if (token.token != '' && token.token != undefined) {
 			token = uni.getStorageSync('token')
 			config.header = {
 				...config.header, // 稳妥起见，先展开原本的 header，再额外添加 Authorization
 				"Api-Token": token.token,
 			}
 			config.data = {
 				...config.data,

 			}
 		}
 		uni.request({
 			// 将剩余配置展开
 			...config,
 			// 拼接接口路径
 			url: baseURL + config.url,
 			timeout,
 			// 成功返回数据
 			success: (res) => {
 				resolve(res.data)
 			},
 			// 失败返回
 			fail: (err) => {
 				reject(err)

 			},
 			// 请求完成：不管成功失败
 			complete: (res) => {
 				uni.hideNavigationBarLoading()
 				let data = res.data
 				if (data.code == 401) { // 服务端返回的状态码不等于200，则reject()
 					// 如果没有显式定义custom的toast参数为false的话，默认对报错进行toast弹出提示
 					uni.$u.toast(data.info)
 					uni.redirectTo({
 							url: '/subPages/login/login'
 						}

 					)
 				} else if(data.code == 0) {

					uni.$u.toast('请登录');
					// uni.redirectTo({
					// 		url: '/subPages/login/login'
					// 	}
					
					// )	
 				}
 			}
 		});

 	})
 }
