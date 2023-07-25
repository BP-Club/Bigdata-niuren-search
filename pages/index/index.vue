<template>
	<view class="content">
		<view class="area">
			<view class="area__left" @click="goPage()">
				<u-icon size="40rpx" color="#1A3387" name="map-fill" customStyle="padding:8rpx 0 8rpx;top: 2rpx;">
				</u-icon>
				<u--text :text="store_info.name" size="30rpx"></u--text>
			</view>
			<view class="area__right" @click="goPage()">
				<u--text text="切换" color="#999999" size="30rpx"></u--text>
				<u--text text=">" color="#999999" size="30rpx"></u--text>
			</view>
		</view>
		<view class="slide">
			<u-swiper :list="slides" @click="slideClick" keyName="img" height="256rpx"></u-swiper>
		</view>
		<!--中部图标-->
		<view class="center">
			<view class="centerbox">
				<view class="centerbox__head">
					<view class="centerbox__head__item" v-for="(item,index) in classify" :key="index"
						@click="goCatePage(item,index)">
						<u--text :text="item.name" :bold="true" size="30rpx" align="center"></u--text>
						<u--text :text="item.remark" size="22rpx" color="#999999" align="center"
							customStyle="padding-top:12rpx"></u--text>
						<view class="centerbox__head__item__imagebox">
							<u--image :showLoading="true" :src="item.cover" width="80rpx" height="80rpx"
								customStyle="margin:0 auto;"></u--image>
						</view>
					</view>
				</view>
				<view class="line"></view>
				<view class="centerbox__bottom">
					<view class="centerbox__bottom__imagebox">
						<u--image :showLoading="true" :src="baseUrl+'/wash/static/share_avatar.jpg'" width="80rpx"
							height="80rpx" customStyle="margin:0 auto;" @click="click"></u--image>
					</view>
					<view class="centerbox__bottom__center">
						<view class="centerbox__bottom__center__left">
							<u--text text="邀请好友得免费优惠券" :bold="true" size="30rpx" align="left"></u--text>
							<u--text text="邀请一位得免费清洗券" size="22rpx" color="#999999" align="left"
								customStyle="padding-top:12rpx"></u--text>
						</view>
						<button :plain="true" class="invite" open-type="share">去邀请
							<!-- <u--text text="去邀请" size="26rpx" align="center" color="#FFFFFF"
								customStyle="padding:16rpx 24rpx;background-color:#1A3387;border-radius:10rpx;"
								@click="inviteUser"></u--text> -->
						</button>

					</view>
				</view>
			</view>
		</view>
		<!--优惠活动-->
		<!-- <view class="activity">
			<view class="activity__head">
				<view class="activity__head__box">
					<u--text text="新人半享专价" :bold="true" size="30rpx" color="#1A3387"></u--text>
					<u--text text="免费送取,干洗水分类 换季衣物首选" size="22rpx" color="#1A3387"></u--text>
				</view>
			</view>
			<view class="activity__center">
				<view class="activity__center__left">
					<u-icon size="44rpx" color="#1A3387" name="hourglass"></u-icon>
					<u--text text="活动倒计时" size="28rpx"></u--text>
				</view>
				<view class="activity__center__right">
					<u-tag text="01" size="mini" borderColor="#1A3387" bgColor="#1A3387" color="#FFFFFF"></u-tag>
					<u--text text="天" size="22rpx" customStyle="padding:0 1rpx;"></u--text>
					<u-tag text="25" size="mini" borderColor="#1A3387" bgColor="#1A3387" color="#FFFFFF"></u-tag>
					<u--text text="时" size="22rpx" customStyle="padding:0 1rpx;"></u--text>
					<u-tag text="13" size="mini" borderColor="#1A3387" bgColor="#1A3387" color="#FFFFFF"></u-tag>
					<u--text text="分" size="22rpx" customStyle="padding:0 1rpx;"></u--text>
					<u-tag text="35" size="mini" borderColor="#1A3387" bgColor="#1A3387" color="#FFFFFF"></u-tag>
					<u--text text="秒" size="22rpx" customStyle="padding:0 1rpx;"></u--text>
				</view>
			</view>
			<view class="line"></view>
			<view class="activity__bottom">
				<view class="activity__bottom__left">
					<view class="activity__bottom__left__tag">到店服务</view>
					<u--image :showLoading="true" radius="20rpx" src="" width="200rpx" height="200rpx" @click="click">
					</u--image>
				</view>
				<view class="activity__bottom__right">
					<u--text text="常规类鞋任洗3件" size="30rpx" align="left"></u--text>
					<u--text text="没邀请1干洗/水洗专业分类洗护 换季衣物首选" size="22rpx" color="#999999" align="left"></u--text>
					<view class="activity__bottom__right__price">
						<view>
							<u--text text="¥39.00" size="28rpx" color="#D41A1E"></u--text>
						</view>
						<view style="padding:6rpx 10rpx;">
							<u--text text="¥39.00" size="20rpx" color="#999999" decoration="line-through"></u--text>
						</view>
					</view>
				</view>
			</view>
		</view> -->
		<view class="" v-model="poster_show" @touchmove.stop.prevent="">
			<u-overlay :show="poster_show" @click="closePoster">
				<view class="share-image">
                  	<image :src="share_img" mode="widthFix"></image>
					<view class="flex justify-content-center u-m-t-30">
						<btnButton title="保存海报图片" radius="48rpx" @click="saveImage(share_img)" bntColor="#1A3387FF" size="32" color="#fff" height="96" width="600"></btnButton>
					</view>
					
				</view>
			</u-overlay>
		</view>
		<!-- 	<view class="" v-model="poster_show" @touchmove.stop.prevent="">
			<u-overlay :show="poster_show" @click="closePoster">
				<view class="canvas-poster">
					<canvasPoster :radius="18" canvasId="my-canvas" @preservation="preservation"
						v-if="canvas_content&&canvas_bg" :canvasW="580" :isShow="is_show" @closeCanvas="closeCanvas"
						bgColor="rgba(255, 255, 255, 1)" :canvasH="750" :canvasContent="canvas_content">
					</canvasPoster>
				</view>
			</u-overlay>
		</view> -->


	</view>
</template>

<script>
	import {
		getLately
	} from 'api/stores';
	import {
		getSlider
	} from "@/api/public.js"
	import {
		getIndexCate,
		bindSpread,
		getSelectedStore
	} from "@/api/goods.js"
	import {
		getLocation,
		localStorage,
		downloadFile,
		saveImageToPhotosAlbum
	} from "@/utils/common.js"
	import {
		getSharePoster
	} from "@/api/market.js"
	import canvasPoster from "@/components/canvas-poster/canvas-poster.vue"
	import {
		getQrcode,
		getPosterBg
	} from "@/api/market.js"
	export default {
		components: {
			// goodsSku,
			canvasPoster
		},
		data() {
			return {
				baseUrl: getApp().globalData.baseUrl,
				title: 'Hello',
				slides: [], //轮播图
				classify: [], //分类
				current: 0,
				store_info: null, //门店信息
				location: null, //定位信息
				canvas_content: null, //海报内容
				is_show: false, //画布显示
				poster_show: false, //生成海报弹窗显示
				course_qrcode: '', //二维码
				canvas_bg: '', //海报背景
				share_uid: '' ,//分享者id
			    share_img:''
			}
		},
		onLoad(o) {
			let token = uni.getStorageSync('token');
			if (o.uid) {
				this.share_uid = o.uid;
				if (token) {
					this.bindSpread(o.uid)
				} else {
					uni.setStorageSync("share_uid", o.uid)
				}
			}
			let that = this;
			that.getIndexCate();
			that.getSlider();
			that.getLocation(true)
		},
		onPullDownRefresh() {
			this.getLocation(true)
		},
		
		onShareAppMessage(res) {
			let user = uni.getStorageSync('user_info');
			
			return {
				title: '邀请好友洗衣有优惠',
				imageUrl: this.baseUrl +
				'/wash/share_cover.jpg',
				path: `/pages/index/index?uid=${user.id ? user.id : ''}`,
			}
		},
		
		// onShareAppMessage(res) {

		// 	let token = uni.getStorageSync('token');
		// 	let user = uni.getStorageSync('user_info');

		// 	return {
		// 		title: '邀请好友洗衣有优惠',
		// 		imageUrl: this.baseUrl +
		// 		'/wash/share_cover.jpg', //自定义图片路径，可以是本地文件路径、代码包文件路径或者网络图片路径。支持PNG及JPG。显示图片长宽比是 5:4。
		// 		path: '/pages/index/index?share_uid=' + user ? user.id : '',
		// 		success: function(res) {
		// 			//成功回调
		// 		}
		// 	}

		// },
		onShow() {
			let that = this
			uni.$on('selectShop', function(shop) {
				that.store_info = shop
			})

		},
		methods: {
			/*关闭海弹窗*/
			closePoster(){
				this.poster_show = false
			},
			/*保存海报图片*/
			saveImage(imge){
				let imgList = [imge];
				this.downloadFile(imgList)
				
			},
			/*下载海报*/
		async downloadFile(lsit){
				let res=await downloadFile(lsit)
				let imgList=[res]
				saveImageToPhotosAlbum(res,()=>{
					this.poster_show = false
				})
			},
			/*打开邀请海报*/
			// openSharImage() {
				
			// 	let token = uni.getStorageSync('token');
			// 	if (token) {
			// 		 this.getSharePoster();
			// 		 this.poster_show = true
			// 	} else {
			// 		this.errToast('请登录后再分享',()=>{})
			// 	}
               
			// },
			/*获取邀请海报图片*/
		  async	getSharePoster() {
                  let res=await getSharePoster()
				  this.share_img=res.data.url
			},
			/*分享者绑定(已登录调用)*/
			async bindSpread(id) { //分享者绑定(已登录调用)
				let res = await bindSpread({
					share_uid: id
				})
				if (res.code == 1) {
					this.successToast('已经获取优惠券', () => {})
				}
			},
			/*获取选中的门店(已登录)*/

			async getSelectedStore() {
				let res = await getSelectedStore();
				if (res.code == 1) {
					if (Object.prototype.toString.call(res.data) === '[object Object]') {

						this.store_info = res.data
					} else {

						this.getLately()
					}
				}
			},
			/*保存海报图片回调*/
			preservation(e) { //
				this.poster_show = e.posterShow
				this.is_show = e.isShow
				this.canvasContent = null
			},
			/*邀请用户*/
			inviteUser() {
				this.getCanvasBg()
				this.getCourseQrcode()
				let that = this
				that.poster_show = !that.poster_show
				if (that.poster_show == true) {
					that.Loading('海报生成中', () => {

						setTimeout(() => {
							that.is_show = true;
							if (that.is_show == true) {

								that.canvasData()
							} else {
								that.canvas_content = null
							}
						}, 1500)
					})
				}

			},
			/*生成画布数据*/
			canvasData() {

				let canvas_content = {

					mainImg: {
						//海报主图
						url: this.canvas_bg, //图片地址
						r: 0, //圆角半径
						imgW: 580, //宽度
						imgH: 580, //高度
						l: 0,
						top: 0
					},
					codeImg: {
						//小程序码
						url: this.course_qrcode, //二维码图片地址
						codeW: 136, //宽度
						codeH: 136, //高度
						top: 550, //距离顶部距离
						l: 420,
						r: 50 //圆角半径
					},

					codeTips: {
						text: '长按识别二维码', //文本
						fontSize: 20, //字体大小
						color: "rgba(102, 102, 102, 1)", //颜色
						top: 700, //距离顶部距离
						l: 416,
						lineHeight: 28, //行高
						w: 300, //最大宽度
						maxLine: 1 //文字最多显示的行数
					},
					// other: {
					// 	id: this.id,
					// 	user: uni.getStorageSync('userInfo')
					// }

				}

				this.canvas_content = canvas_content

			},
			async getCanvasBg() { //获取画布背景
				let res = await getPosterBg()

				if (res.code == 1) {
					this.canvas_bg = res.data.background
				}

			},
			async getCourseQrcode() { //获取二维码
				let res = await getQrcode()
				if (res.code == 1) {
					this.course_qrcode = res.data.url
				}
			},
			async getLately() { //获取门店
				let res = await getLately({
					lat: this.location.latitude,
					lng: this.location.longitude,
				})
				if (res.code == 1) {
					this.store_info = res.data
				}
			},
			async getLocation(is) { //获取定位信息
				let getLocations = localStorage('location');
				if (getLocations) {
					let l = JSON.parse(getLocations)
					if (l.result) {
						this.location = l
					} else {
						uni.removeStorageSync(location);
						let updatePositioning = await getLocation(is);
						this.location = updatePositioning
						localStorage('location', JSON.stringify(updatePositioning), 1000)
					}
				} else {
					let location = await getLocation(is);
					this.location = location
					localStorage('location', JSON.stringify(location), 1000)
				}
				let token = uni.getStorageSync('token');
				if (token) {
					this.getSelectedStore()
				} else {
					this.getLately()
				}

			},
			async getSlider() { //获取轮播图
				let res = await getSlider()
				if (res.code == 1) {
					console.log(res.data)
					this.slides = res.data
				}
			},

			async getIndexCate() { //获取一级分类
				let res = await getIndexCate();

				if (res.code == 1) {

					this.classify = res.data
				} else {
					return this.toast(res.info, 1500)
				}
			},
			slideClick() {
				uni.$u.route({
					url: "subPages/coupon/coupon",
				});
			},
			goPage() {
				uni.$u.route({
					url: "subPages/stores/stores?store_id=" + this.store_info.id,
				});
			},
			goCatePage(item, index) {
				if (item.status == 1) {
					uni.$u.route({
						url: '/subPages/category/category?id=' + item.id + '&current=' + index,
					});
				} else {
					this.successToast('请期待开放', () => {})
				}

			}

		}
	}
</script>

<style lang="scss">
	.content {
		padding: 32rpx;
		height: 100%;
		background-color: #F7F7F7;
	}
   .share-image{
	 width: 640rpx;
	  
       height: 80%;
	   position: absolute;
	   left: 0;
	   right: 0;
	   top: 0;
	   bottom: 0;
	   margin: auto;
	   border-radius: 20rpx; 
	   image{
		border-radius: 20rpx; 
		   width: 100%;
	   }
   }
	.canvas-poster {
		width: 580rpx;
		position: absolute;
		height: 688rpx;
		left: 0;
		right: 0;
		top: 0;
		bottom: 0;
		margin: auto;
	}

	.invite {

		border: none !important;
		width: 128rpx;
		height: 60rpx;
		background: #1A3387 !important;
		border-radius: 10rpx;
		padding: 0;
		margin: 0;
		font-size: 26rpx;
		color: #FFFFFF !important;
	}

	.area {
		@include flex(row);
		justify-content: space-between;
		width: 686rpx;
		height: 104rpx;
		font-size: 30rpx;
		background-color: #FFFFFF;
		border-radius: 8rpx;
		box-shadow: $wash-border;

		&__left {
			@include flex(row);
			padding: 0 40rpx;

		}

		&__right {
			width: 20%;
			@include flex(row);

		}
	}

	.slide {
		width: 686rpx;
		height: 256rpx;
		padding: 16rpx 0;
	}

	.center {
		padding-bottom: 16rpx;
	}




	.centerbox {
		width: 686rpx;
		height: 436rpx;
		box-shadow: $wash-border;
		background-color: #FFFFFF;
		border-radius: 8rpx;


		&__head {
			@include flex(row);
			width: 686rpx;
			height: 256rpx;

			&__item {
				width: 172rpx;
				padding: 32rpx 0 32rpx 0;

				&__imagebox {
					width: 172rpx;
					padding: 20rpx 0;
				}
			}
		}

		&__bottom {
			@include flex(row);
			justify-content: space-between;
			width: 686rpx;
			height: 180rpx;
			align-items: center;

			&__imagebox {
				width: 172rpx;
				padding: 40rpx 0;
			}

			&__center {
				@include flex(row);
				width: 514rpx;
				align-items: center;

				&__left {
					width: 315rpx;
					padding: 45rpx 0 58rpx 0;

				}
			}
		}

	}



	.activity {
		width: 686rpx;
		height: 502rpx;
		box-shadow: $wash-border;
		background-color: #FFFFFF;
		border-bottom-left-radius: 8rpx;
		border-bottom-right-radius: 8rpx;

		&__head {
			background-color: #E4EAFF;
			width: 100%;
			height: 132rpx;
			border-top-left-radius: 8rpx;
			border-top-right-radius: 8rpx;

			&__box {
				padding: 29rpx 32rpx;
			}
		}

		&__center {
			@include flex(row);
			justify-content: space-between;
			width: 686rpx;

			&__left {
				@include flex(row);
				width: 50%;
				padding: 27rpx;

			}

			&__right {
				@include flex(row);
				width: 50%;
				padding: 28rpx;

			}

		}

		&__bottom {
			@include flex(row);
			width: 686rpx;

			&__left {
				padding: 28rpx;
				position: relative;

				&__tag {
					font-size: 20rpx;
					color: #FFFFFF;
					background-color: #1A3387;
					border-radius: 16rpx 0rpx 16rpx 0rpx;
					padding: 12rpx;
					position: absolute;
					z-index: 9;
				}
			}

			&__right {
				padding: 28rpx;

				&__price {
					padding-top: 58rpx;
					@include flex(row);
				}
			}

		}
	}


	.line {
		width: 638rpx;
		height: 2rpx;
		border: 2rpx solid #F7F7F7;
		background-color: #F7F7F7;
		margin: 0 auto;
	}
</style>