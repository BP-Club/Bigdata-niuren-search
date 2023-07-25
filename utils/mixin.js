// 全局常用方法挂载到this、
export default {
	install(Vue) {
		Vue.mixin({
			created() {

			},
			data() {
				return {}
			},

			methods: {
				//复制内容
				copyContent(content, icon) {
					uni.setClipboardData({
						data: content || '',

						success: () => {
							uni.showToast({
								title: '复制成功',
								icon: icon ? icon : 'none'
							})
						},
						fail: () => {
							uni.showToast({
								title: '复制失败',
								icon: icon ? icon : 'none'
							})
						}
					})
				},

				// //显示消息提示框。
				toast(title, duration, icon) {
					uni.showToast({
						title: title,
						duration: duration ? duration : 1500,
						icon: icon ? icon : 'none',
						mask: true,

					});
				},
				errToast(title, fun, time, ) {
					uni.showToast({
						title,
						mask: true,
						duration: time || 1500,
						icon: "none",
						success: fun
					})
				},
				successToast(title, fun, time) {
					uni.showToast({
						title,
						mask: true,
						duration: time || 1500,
						icon: "success",
						success: fun
					})
				},
				Loading(title, fun, time) {
					uni.showLoading({
						title: title,
						mask: true,
						duration: time || 1500,
						success: fun
					});
				},
				//保留当前页面，跳转到应用内的某个页面
				navigateTo(url) {
					uni.navigateTo({
						url: url,
					});
				},
				//关闭当前页面，跳转到应用内的某个页面
				redirectTo(url) {
					uni.redirectTo({
						url: url,
					});
				},
				//关闭所有页面，打开到应用内的某个页面
				reLaunch(url) {
					uni.reLaunch({
						url: url,
					});
				},
				//跳转到 tabBar 页面，并关闭其他所有非 tabBar 页面
				switchTab(url) {
					uni.switchTab({
						url: url,
					});
				},

				//过滤空值
				filter(obj) {
					var newObj = {}
					if (typeof obj === 'string') {
						obj = JSON.parse(obj)
					}
					if (obj instanceof Array) {
						newObj = []
					}
					if (obj instanceof Object) {
						for (var attr in obj) {

							if (obj.hasOwnProperty(attr) && obj[attr] !== '' && obj[attr] !== null && obj[
									attr] !== undefined) {
								if (obj[attr] instanceof Object) {

									if (JSON.stringify(obj[attr]) === '{}' || JSON.stringify(obj[attr]) ===
										'[]') {
										continue
									}

									newObj[attr] = filter(obj[attr])
								} else if (
									typeof obj[attr] === 'string' &&
									((obj[attr].indexOf('{') > -1 && obj[attr].indexOf('}') > -1) ||
										(obj[attr].indexOf('[') > -1 && obj[attr].indexOf(']') > -1))
								) {

									try {
										var attrObj = JSON.parse(obj[attr])
										if (attrObj instanceof Object) {
											newObj[attr] = filter(attrObj)
										}
									} catch (e) {
										newObj[attr] = obj[attr]
									}
								} else {
									newObj[attr] = obj[attr]
								}
							}
						}
					}
					return newObj
				},
			}
		})
	}
}