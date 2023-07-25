 <template>
	<view class="container u-p-b-160">
		<!-- <view class="count-down">
			<view class="text-explain">
				<icons name="icon-daojishi" size="40" color="#1A3387"></icons>
				<text class="f-s-28 u-m-l-20 u-m-t-2 f-c-333">活动倒计时</text>
			</view>
			<view class="time f-c-333 f-s-22">
				<text class="time-num">1</text>
				<text class="u-m-t-8">天</text>
				<text class="time-num">23</text>
				<text class="u-m-t-8">时</text>
				<text class="time-num">13</text>
				<text class="u-m-t-8">分</text>
				<text class="time-num">35</text>
				<text class="u-m-t-8">秒</text>
			</view>
		</view> -->
		<view class="rotation">
			<u-swiper height="512rpx" indicator indicatorMode="dot" :list="rotation_list"></u-swiper>
		</view>
		<view class="price-detail box">
			<view class="detail">
				<view class="price fw600 f-s-40"><text class="f-s-24">￥</text>{{info.price_selling}}</view>
				<view class="share u-m-t-30" @click="generatePoster">
					<icons name="icon-fenxiang" size="38" color="#1A3387"></icons>
					<text class="f-s-24 u-m-l-12">分享</text>
				</view>
			</view>
			<view class="disp-flex align-center">
				<view class="title f-s-32 fw600">{{info.name}}</view>
				<view class="store-service f-s-20 u-p-l-10 u-p-r-10 u-m-l-12">到店服务</view>
			</view>
			<view class="content f-s-24">
				{{info.remark}}
			</view>
		</view>
		<!-- <view class="box">
			<view class="title f-s-32 fw600">活动规则</view>
			<view class="content f-s-24">
				每人限购1单，仅限于本门店新用户首次下单可使用，新用户专属福利，活动规则一行的情况，活动规则两行的情况
			</view>
		</view> -->
		<view class="box">
			<view class="title f-s-32 fw600">洗护流程</view>
			<view >
				
				<u-parse :content="info.content"></u-parse>
			</view>
			
		</view>
		<view class="buy disp-flex justify-between align-center">
			<view @click="toHome">
				<icons name="icon-shouye-weixuanzhong" labelSize="24" labelColor="#333333" labelPos="bottom" size="39"
					label="首页" color="#1A3387"></icons>
			</view>
			<view class="flex align-items-center justify-center">
				<btn-button title="加入购物车" @click="openSku('addCar')" bntColor="#F7F7F7" width='250'
					radius="36rpx  0  0  36rpx" height="80" size="32" color="#1A3387" />
				<btn-button title="立即购买" @click="openSku('buyNow')" bntColor="#1A3387" width='250'
					radius=" 0 36rpx 36rpx 0" height="80" size="32" color="#FFFFFF" />
			</view>
			<!-- <view class="immediately-buy" @click="clickBuy">立即购买</view> -->
		</view>
		<u-popup :show="spec_show" mode="bottom" :round="16" @close="closeSku" closeIconPos="top-right"
			:closeOnClickOverlay="true" :closeable="true">
			<goods-sku :goodsInfo="info" :show="spec_show" :type="btn_type" @addCar="addCar"></goods-sku>
		</u-popup>
		<view class="" v-model="posterShow" @touchmove.stop.prevent="">
			<u-overlay :show="posterShow" @click="closePoster">
				<view class="canvas-poster">
					<canvasPoster :radius="18" canvasId="my-canvas" @preservation="preservation" v-if="canvasContent"
						:canvasW="580" :isShow="isShow" @closeCanvas="closeCanvas" bgColor="rgba(255, 255, 255, 1)"
						:canvasH="750" :canvasContent="canvasContent">
					</canvasPoster>
				</view>
			</u-overlay>
		</view>


	</view>
</template>

<script>
	import {
		getDetail
	} from "api/goods.js"
	import {
		settle
	} from "api/stores.js"
	import {
		goodsSku
	} from "@/components/sku/sku.vue"
	import canvasPoster from "@/components/canvas-poster/canvas-poster.vue"
	import {
		getQrcode
	} from "@/api/market.js"
	export default {
		components: {
			goodsSku,
			canvasPoster
		},
		data() {
			return {
				rotation_list: [],
				code: "",
				btn_type: '',
				info: {}, //商品详情
				canvasContent: null, //海报内容
				isShow: false, //画布显示
				posterShow: false, //生成海报弹窗显示
				spec_show: false, //显示规格
				courseQrcode: '' //二维码
			}
		},
		onLoad(option) {
			if (option.code) {
				this.code = option.code
				this.load()
			}
		},
		methods: {
			addCar(e) { //sku添加回调
				this.spec_show = e
			},
			load() {
				getDetail({
					code: this.code
				}).then(res => {
					if (res.data) {
						this.info = res.data
						this.rotation_list = res.data.slider
					}
				})
			},
			preservation(e) { //保存海报图片回调
				this.posterShow = e.posterShow
				this.isShow = e.isShow
				this.canvasContent = null
			},
			closeCanvas(e) { //关闭画布
				this.isShow = e
			},

			generatePoster() { //生成海报二维码
				let that = this
				that.posterShow = !that.posterShow
				if (that.posterShow == true) {
					that.Loading('海报生成中', () => {
						// that.getCourseQrcode(that.id)
						that.getCourseQrcode()
						setTimeout(() => {
							that.isShow = true;


							that.maskBe = false;
							if (that.isShow == true) {

								that.canvasData()
							} else {
								that.canvasContent = null
							}
						}, 1500)
					})
				}
			},
			canvasData() { //生成画布数据  

				let canvasContent = {
					mainImg: {
						//海报主图
						url: this.info.cover, //图片地址
						r: 10, //圆角半径
						imgW: 540, //宽度
						imgH: 500, //高度
						l: 20,
						top: 25
					},
					title: {
						//海报标题
						text: this.info.name, //文本
						fontSize: 30, //字体大小
						color: "rgba(153, 153, 153, 1)", //颜色
						top: 540, //距离顶部距离
						l: 26,
						w: 200, //最大宽度
						lineHeight: 40, //行高
						maxLine: 1 //文字最多显示的行数
					},
					content: {
						//海报内容
						text: this.info.remark_short, //文本
						fontSize: 28, //字体大小
						color: "rgba(20, 20, 20, 1)", //颜色
						top: 620, //距离顶部距离
						l: 26,
						w: 200, //最大宽度
						lineHeight: 28, //行高
						maxLine: 2 //文字最多显示的行数
					},

					codeImg: {
						//小程序码
						url: this.courseQrcode, //二维码图片地址
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

				this.canvasContent = canvasContent

			},



			openSku(type) { //打开购物组件

				this.btn_type = type
				this.spec_show = true
			},
			closeSku() { //关闭sku

				this.spec_show = false

			},
			toHome() {
				uni.switchTab({
					url: "/pages/index/index"
				})
			},
			async getCourseQrcode() { //获取二维码
				let res = await getQrcode()
				console.log(res, '是 撒的 ')
				if (res.code == 1) {
					this.courseQrcode = res.data.url
				}
			}
			// clickBuy() {
			// 	uni.setStorageSync("good_codes", [this.code])
			// 	uni.navigateTo({
			// 		url: "/subPages/submit-order/submit-order"
			// 	})
			// }
		}
	}
</script>

<style lang="scss" scoped>
	.container {
		background-color: #F7F8FA;
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

	.count-down {
		padding: 0 34rpx;
		box-sizing: border-box;
		background-color: #EEF2FF;
		height: 92rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;

		.text-explain {
			display: flex;
		}

		.time {
			display: flex;
			height: 38rpx;

			.time-num {
				width: 38rpx;
				height: 38rpx;
				background: #1A3387;
				border-radius: 6rpx;
				line-height: 38rpx;
				text-align: center;
				color: white;
				margin: 0 8rpx;
			}
		}
	}

	.rotation {
		height: 512rpx;
		padding: 16rpx 32rpx;
	}

	.box {
		padding: 24rpx;
		background: #FFFFFF;
		border-radius: 20rpx;
		margin: 0 32rpx 16rpx;
	}

	.price-detail {
		.detail {
			display: flex;
			justify-content: space-between;

			.price {
				color: #D41A1E;
			}
		}

		.share {
			display: flex;
			align-items: center;
			color: #1A3387;
			height: 38rpx;
		}
	}

	.store-service {
		background: #1A3387;
		border-radius: 4rpx;
		height: 40rpx;
		color: #FFFFFF;
		line-height: 40rpx;
	}



	.title {
		color: #333333;
		line-height: 48rpx;
	}

	.content {
		margin: 28rpx 0 0;
		color: #999;
		line-height: 36rpx;
	}

	.buy {
		padding: 0 32rpx 0 52rpx;
		box-sizing: border-box;
		background-color: white;
		width: 100%;
		height: 130rpx;
		position: fixed;
		bottom: 0;

		.immediately-buy {
			line-height: 48rpx;
			color: white;
			padding: 24rpx 50rpx;
			background-color: #1A3387;
			border-radius: 48rpx;
		}
	}
</style>