const baseURL = process.env.VUE_APP_BASE_URL;
export default baseURL

//检查登录是否过期
export function checkSession() {
	uni.checkSession({
		success: function(res) {
			console.log(res)
		},
		fail: function(err) {
			uni.removeStorageSync('token');
			uni.removeStorageSync('user_info');
			uni.showToast({
				title: '登录过期，重新登录',
				duration: 2000
			})
			uni.navigateTo({
				url: "/subPages/login/login"
			})
		}
	})
}

//退出登录
export function logOut() {
	uni.removeStorageSync('token');
	uni.removeStorageSync('basket_id');
	uni.removeStorageSync('user_info');
	uni.showToast({
		title: '已退出登录',
		duration: 2000
	})
	setTimeout(() => {
		uni.switchTab({
			url: '/pages/index/index'
		})
	}, 1000)
}

//微信小程序支付
export function wxPay(payData) {
	return new Promise((resolve, rejct) => {
		uni.getProvider({
			service: 'payment',
			success: (res) => {
				let rg = res.provider[0]
				if (rg == 'wxpay') {
					uni.requestPayment({
						provider: 'wxpay',
						timeStamp: payData.timeStamp,
						nonceStr: payData.nonceStr,
						package: payData.package,
						signType: payData.signType,
						paySign: payData.paySign,
						success: function(res) {
							resolve(res)
						},
						fail: function(err) {
							uni.showModal({
								title: "提示",
								content: "支付失败!",
								showCancel: false,
							})
							rejct(err)
						}
					});
				}
			},
		})
	})
}
//选择图片
export function chooseImage(count, type) {
	let sourceType = [];
	if (type == undefined) {
		sourceType = ['album', 'camera']
	} else if (type == 'album') {
		sourceType = ['album']
	} else if (type == 'camera') {
		sourceType = ['camera']
	}
	return new Promise((resolve, rejct) => {
		uni.chooseImage({
			count: count,
			sizeType: ['original', 'compressed'],
			sourceType: sourceType,
			success: res => {
				resolve(res.tempFilePaths)
			},
			fail: (err) => {
				rejct(err)
			}
		});
	})
}

//上传
export function upload(img) {
	
	
	let timeout = 60000
	return new Promise((resolve, rejct) => {
		
			uni.uploadFile({
				url: baseURL + '/data/api.myFile/upFile',
				filePath: img,
				name: 'file',
				timeout: timeout,
				success: (res) => {
					let group = JSON.parse(res.data)
					
					uni.hideLoading();
					resolve(group.data.url)
				},
				fail: (err) => {
					
					uni.hideLoading()
					rejct(err)
				}
			});
		
	})
}
//上传图片  
export function uploadFile(count, type, fun) {
	return new Promise((resolve, rejct) => {
		let timeout = 60000
		let sourceType = [];
		if (type == undefined) {
			sourceType = ['album', 'camera']
		} else if (type == 'album') {
			sourceType = ['album']
		} else if (type == 'camera') {
			sourceType = ['camera']
		}
		uni.chooseImage({
			count: count,
			sizeType: ['original', 'compressed'],
			sourceType: sourceType,
			success: res => {
				let imgList = res.tempFilePaths;
				imgList.forEach((item, index) => {
					uni.uploadFile({
						url: baseURL + '/publics/api.index/uploaderimages',
						filePath: item,
						name: 'file',
						timeout: timeout,
						success: (res) => {
							let group = JSON.parse(res.data)
							uni.showToast({
								title: "上传成功",
								icon: "success"
							})
							fun()
							resolve(group.data._src)
						},
						fail: (err) => {
							uni.showToast({
								title: "上传失败",
								icon: "error"
							})
							rejct(err)
						}
					});
				})
			},
			fail: (err) => {
				rejct(err)
			}
		});
	});
}

//保存图片到系统相册
export function saveImageToPhotosAlbum(list, fun) {
	uni.showLoading({
		title: '正在保存图片...'
	});
	//获取用户的当前设置。获取相册权限
	uni.getSetting({
		success: (res) => {
			//如果没有相册权限
			if (!res.authSetting["scope.writePhotosAlbum"]) {
				//向用户发起授权请求
				uni.authorize({
					scope: "scope.writePhotosAlbum",
					success: () => {
						list.forEach((item, index) => {
							//授权成功保存图片到系统相册
							uni.saveImageToPhotosAlbum({
								filePath: item.tempFilePath,
								success: (res) => {
									if (list[list.length - 1]
										.tempFilePath == item.tempFilePath
									) {
										uni.hideLoading();
										return uni.showToast({
											title: "保存成功！",
											success: fun
										});
									}
								},
								fail: (res) => {

									return uni.showToast({
										title: res.errMsg,
									});
								},
								complete: (res) => {
									uni.hideLoading();
								},
							});
						})
					},
					//授权失败
					fail: () => {
						uni.hideLoading();
						uni.showModal({
							title: "您已拒绝获取相册权限",
							content: "是否进入权限管理，调整授权？",
							success: (res) => {
								if (res.confirm) {
									//调起客户端小程序设置界面，返回用户设置的操作结果。（重新让用户授权）
									uni.openSetting({
										success: (res) => {
											console.log(res.authSetting);
										},
									});
								} else if (res.cancel) {
									return uni.showToast({
										title: "已取消！",
									});
								}
							},
						});
					},
				});
			} else {

				//如果已有相册权限，直接保存图片到系统相册
				list.forEach((item, index) => {
					uni.saveImageToPhotosAlbum({
						filePath: item.tempFilePath,
						success: (res) => {

							if (list[list.length - 1].tempFilePath == item
								.tempFilePath) {
								uni.hideLoading();
								return uni.showToast({
									title: "保存成功！",
									success: fun
								});
							}
						},
						fail: (err) => {
							uni.hideLoading();
							return uni.showToast({
								title: err.errMsg,
							});
						}

					});
				})

			}
		}

	})
}
//下载图片
export function downloadFile(list, showLoading) {
	return new Promise((resolve, rejct) => {
		let arr = []
		showLoading
		uni.showLoading({
			title: '下载中...',
			mask: true
		});
		list.forEach((item, index) => {
			uni.downloadFile({
				url: item,
				methods: 'GET',
				success: (res) => {
					arr.push(res)
					setTimeout(() => {
						uni.hideLoading();
					}, 1500)
					resolve(arr)
				},
				fail: (error) => {
					uni.hideLoading();
					rejct(error)
				}
			});
		})
	})

}
//获取图片信息
export function getImageInfo(src) {

	return new Promise((resolve, rejct) => {
		uni.getImageInfo({
			src: src,
			success: function(image) {
		
				resolve(image)
			},
			fail: function(err) {

				rejct(err)
			}
		});
	})
}
//图片预览
export function previewImage(current, list){
	uni.previewImage({
		urls: list,
		loop:true,
		current: current,
	
	})
}
//处理富文本
export function formatRichText(html) {
	return new Promise((resolve, rejct) => {
		let newContent = html.replace(/<img[^>]*>/gi, function(match, capture) {
			match = match.replace(/style="[^"]+"/gi, '').replace(/style='[^']+'/gi, '');
			match = match.replace(/width="[^"]+"/gi, '').replace(/width='[^']+'/gi, '');
			match = match.replace(/height="[^"]+"/gi, '').replace(/height='[^']+'/gi, '');
			return match;
		});
		
		newContent = newContent.replace(/style="[^"]+"/gi, function(match, capture) {
			match = match.replace(/width:[^;]+;/gi, 'max-width:100%;').replace(/width:[^;]+;/gi,
				'max-width:100%;');
			return match;
		});
		newContent = newContent.replace(/<br[^>]*\/>/gi, '');
		newContent = newContent.replace(/\<img/gi, '<img style="width:100%;height:auto;\"');
		
		var stylePattern = /style="[^=>]*"[\s+\w+=|>]/g;
		newContent.replace(stylePattern, function(matches) {
		});
		resolve(newContent)
	})
}
//打开地图
export function openLocation(longitude, latitude, info) {
	console.log(info, '嗡嗡嗡')
	uni.openLocation({
		latitude: latitude,
		longitude: longitude,
		name: info && info.name ? info.name : '',
		address: info && info.address ? info.address : '',
		success: function() {
			console.log('success');
		}
	});

}

//拨打电话
export function makePhoneCall(phone) {
	uni.makePhoneCall({
		phoneNumber: phone
	});
}

//设置本地缓存过期时间  可存可取
export function localStorage(key, value, seconds) {
	var timestamp = Date.parse(new Date()) / 1000
	if (key && value === null) {
		//删除缓存
		uni.removeStorageSync(key);
	} else if (key && value) {
		//设置缓存

		var expire = timestamp + seconds
		value = value + "|" + expire
		uni.setStorageSync(key, value);
	} else if (key) {
		//获取缓存
		var val = uni.getStorageSync(key);
		var tmp = val.split("|")
		if (!tmp[1] || timestamp >= tmp[1]) {
			uni.removeStorageSync(key)
			return ''
		} else {
			return tmp[0]
		}
	}
}
//获取当前位置信息
export function getLocation(isAnalysis) {
	let QQMapWX = require('../utils/qqmap-wx-jssdk');
	return new Promise((resolve, rejct) => {
		uni.getLocation({
			type: 'gcj02',
			isHighAccuracy: true,
			success: function(res) {
				let location=res
				if (isAnalysis) {
					let qqmapsdk = new QQMapWX({
						key: 'USABZ-2I6CQ-4SS5Q-45L36-GDXX7-VHFGP' // 必填
					});
					qqmapsdk.reverseGeocoder({
						location: {
							latitude: res.latitude,
							longitude: res.longitude
						},
						success: (res1) => {
						
							if (res1.status == 0) {
								location.result=res1.result
								resolve(location)
								
							} else {
								rejct(res)
                                return uni.showToast({
                                	title: res1.status+'定位失败',
                                	icon: "error"
                                });
								
							}

						},
						fail: (err) => {
							rejct(err)
							
						}
					})
				} else {
				 resolve(location)
				}
			},
			fail: function(err) {
				if (err.errMsg == "getLocation:fail auth deny") {
					uni.showModal({
						content: '检测到您没打开获取位置功能权限，是否去设置打开？',
						confirmText: "确认",
						cancelText: '取消',
						success: (res => {

							if (res.confirm) {
								uni.openSetting({
									success(res) {
										console.log(res)
									}
								})
							}
						})
					})
				}

			}
		});
	})


}