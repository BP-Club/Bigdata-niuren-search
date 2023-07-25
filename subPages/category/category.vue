<template>
	<view class="content box-sizing-border-box">
		<view class="topbar">
			<view class="topbar-box" v-for="(item, index) in topbars" :key="index">
				<view :class="is_top_active==index?'topbar-current':'topbar-box-item'" v-if="item.status == 1" @click="topbarClick(item,index)">
					{{item.name}}
				</view>
				<view class="topbar-box-line"></view>
			</view>
		</view>
		<view class="classify-box flex">
			<view class="classify-scroll-left-box">
				<scroll-view scroll-y="true" class="scroll-left-box">
					<view v-for="(item, index) in leftbars" :key="index"
						:class="is_left_active==index?'leftbar-current':'center-leftbar-item'"
						@click="leftbarClick(item.id,index)" class="f-s-28 item">{{item.name}}
					</view>
				</scroll-view>
			</view>
			<view class="classify-scroll-right-box">
				<scroll-view scroll-y="true" class="scroll-right-box" @scrolltolower="scrolltolower">
					<view class="center-right-item goods" v-for="(item, index) in goods" :key="index"
						@click="toDetail(item)">
						<view class="center-right-item-image">
							<u--image :showLoading="true" radius="20rpx" :src="item.cover" width="168rpx"
								height="168rpx" customStyle="margin:0 auto;" @click="click"></u--image>
						</view>
						<view class="center-right-item-right">
							<view class="center-right-item-right-text">{{item.name}}</view>
							<!--view class="center-right-item-right-middle">
								<view class="center-right-item-right-middle-title">
									新人价
								</view>
							</view-->
							<view class="center-right-item-right-price">
								<view class="center-right-item-right-price-left">
									<view class="sellprice-box">
										<u--text text="¥" size="28rpx" color="#D41A1E"></u--text>
										<u--text :text="item.price_selling" size="28rpx" color="#D41A1E"></u--text>
									</view>
									<view class="discountprice-box">
										<u--text text="¥" size="20rpx" color="#999999" decoration="line-through">
										</u--text>
										<u--text :text="item.price_market" size="20rpx" color="#999999"
											decoration="line-through"></u--text>
									</view>
								</view>
								<view class="center-right-item-right-price-btnbox" @click.stop="addBasket(item,index)">
									<view class="center-right-item-right-price-btnbox-btn">
										+
									</view>
								</view>

							</view>
						</view>
					</view>
					<u-loadmore :status="status" />
				</scroll-view>
			</view>
		</view>
		<!--底部-->
		<view class="bottom">
			<view class="bottom-head">
				<view class="bottom-head-left">
					<view class="bottom-head-left-box">
						<view class="bottom-head-left-box-cricle" @click="basketOpen">
							<u-image src="/static/images/basket2.png" width="92rpx" height="92rpx"></u-image>
							<u-badge type="error" max="100" :value="total_num" absolute="true" :offset="cartOffset">
							</u-badge>
						</view>
					</view>
					<view class="bottom-head-left-price sellprice-box">
						<u--text text="¥" size="28rpx" color="#D41A1E"></u--text>
						<u--text :text="total_amount" size="28rpx" color="#D41A1E"></u--text>
					</view>
				</view>
				<view class="bottom-head-right">
					<view class="bottom-head-right-btn" @click="addCart()"> 去结算</view>
				</view>
			</view>
		</view>

		<!--购物车-->
		<u-popup :show="basket_show" @close="basketClose">
			<view class="center">
				<scroll-view scroll-y="true" class="basket-scroll">
					<view class="center-right" style="width:100%;" v-for="(car,c) in basket_goods" :key="c">
						<view class="center-right-item" v-for="(item,index) in car.list" style="padding:10rpx 30rpx;">
							<view class="center-right-item-image">
								<u--image :showLoading="true" radius="20rpx" :src="item.cover" width="80rpx"
									height="80rpx" customStyle="margin:0 auto;" @click="click"></u--image>
							</view>
							<view class="center-right-item-right" style="padding: 10rpx 23rpx;">
								<view class="center-right-item-right-text2" v-if="!item.is_spec">{{item.name}}</view>
								<view class="center-right-item-right-text2" v-else>{{item.name}}({{item.specStrs}})
								</view>
								<view class="center-right-item-right-price">

									<view class="center-right-item-right-price-left">
										<view class="sellprice-box">
											<u--text text="小计:" size="25rpx" customStyle="width:70rpx;"></u--text>
											<u--text text="¥" size="28rpx" color="#D41A1E"></u--text>
											<u--text :text="item.amount" size="28rpx" color="#D41A1E"></u--text>
										</view>
									</view>
									<view class="center-right-item-right-price-btnbox">
										<view class="center-right-item-right-price-btnbox-btn"
											@click="quantityOperate(item,index,'add',c)">
											+
										</view>
										<view class="good-num">
											{{item.good_num}}
										</view>
										<view class="center-right-item-right-price-btnbox-btn"
											@click="quantityOperate(item,index,'reduce',c)">
											-
										</view>
									</view>
								</view>
							</view>
							<u-icon size="60rpx" name="trash" customStyle="margin-left:108rpx;"
								@click="deleteBasketGood(index,item,c)"></u-icon>
						</view>
					</view>

				</scroll-view>

			</view>
		</u-popup>

		<u-popup :show="spec_show" mode="bottom" :round="16" @close="closeSku" closeIconPos="top-right"
			:closeOnClickOverlay="true" :closeable="true">
			<goods-sku :goodsInfo="goods_info" :show="spec_show" :type="btn_type" @addCar="addCar"></goods-sku>
		</u-popup>

	</view>
</template>

<script>
	import {
		getGoods,
		getCate,
		getIndexCate
	} from '@/api/goods.js';
	import {
		getBasketList,
		setCart,
		setBasketNumber
	} from 'api/basket';

	import {
		goodsSku
	} from "@/components/sku/sku.vue"
	export default {
		components: {
			goodsSku
		},
		data() {
			return {
				page: 1,
				page_count: 0,
				is_top_active: 0,
				topbars: [],
				car: [],
				is_left_active: 0,
				leftbars: [],
				goods: [],
				total_amount: 0,
				total_num: 0,
				basket_goods: [],
				offset: [-6, -8],
				cartOffset: [1, 0],
				basket_show: false,
				spec_show: false,
				statusLineHeight: '',
				id: '',
				status: '',
				goods_info: null, //当前商品信息
				btn_type: '',

			}
		},
		onLoad({
			id,
			current
		}) {
			let that = this;
			if (id) {
				that.id = id
			}
			let token = uni.getStorageSync('token');
			if (current) {
				that.is_top_active = parseInt(current)
			}
			this.getIndexCate();
			if (token) {
				that.getCar()
			}

		},

		onPullDownRefresh() {
			this.page = 1;
			let that = this;
			that.is_top_active = 0
			this.is_left_active = 0
			that.Loading('加载中...', () => {
				setTimeout(() => {
					that.getIndexCate()
					uni.stopPullDownRefresh();
					uni.hideLoading()
				}, 1500);
			}, 1500)

		},

		methods: {
			addCar(e) { //sku添加回调
				this.spec_show = e
				this.goods_info = null
				this.getCar()
			},
			async getIndexCate() { //获取一级分类
				let res = await getIndexCate();
				if (res.code == 1) {
					this.topbars = res.data;
					this.getCate(res.data[this.is_top_active].id);
				} else {
					return this.toast(res.info, 1500)
				}
			},
			async getCate(id) { //获取二级分类
				let res = await getCate({
					top_id: id
				});
				if (res.code == 1) {
					this.leftbars = res.data
					this.getGoodData(this.leftbars[this.is_left_active].id)
				} else {
					return this.toast(res.info, 1500)
				}

			},


			async getGoodData(id) { //获取商品列表
				let res = await getGoods({
					cateid: id,
					page: this.page,

				})
				if (res.code == 1) {
					if (this.page == 1) {
						this.goods = res.data.list
					} else {
						this.goods = [...this.goods, ...res.data.list]
					}
					this.page_count = res.data.pages

				}
			},
			topbarClick(item, index) { //顶部栏目切换

				let that = this;
				this.is_top_active = index
				this.page = 1;
				this.goods = []
				this.Loading('加载中...', () => {
					setTimeout(res => {

						that.getCate(item.id)
					})
				}, 1500)
			},
			leftbarClick(id, index) { //左边栏切换
				let that = this
				this.is_left_active = index
				this.page = 1;
				this.goods = []
				this.Loading('加载中...', () => {
					setTimeout(res => {
						this.getGoodData(id)
					}, 1500)
				}, 1500)

			},
			scrolltolower(e) { //右边栏加载
				this.page++;
				let that = this
				this.status = 'loading';
				if (this.page > this.page_count) {
					this.status = 'nomore';
				} else {
					this.Loading('加载中...', () => {
						setTimeout(res => {
							that.getGoodData(that.leftbars[that.is_left_active].id)
						}, 1500)
					}, 1500)
				}
			},
			closeSku() { //关闭sku

				this.spec_show = false
				this.goods_info = null
			},
			addBasket(item, index) { //打开sku组件
				console.log(item, '杀杀杀')


				if (!item.is_spec) {
					let arrays = [];
					let items = {
						goods_code: item.code,
						good_num: 1,
						spec_codes: ''
					}
					arrays.push(items)
					this.addCar(JSON.stringify(arrays))

				} else {
					this.goods_info = item
					this.spec_show = true

					this.btn_type = "addCar"
				}


			},
			/*加入购物车*/
			async addCar(data) {
				let that = this
				let res = await setCart({
					goods: data
				});
				if (res.code == 1) {
					this.successToast('加入购物车成功', () => {
						this.getCar()
					})
				} else {
					this.toast(res.info, 1500)
				}
			},
			async getCar() { //获取购物车
				let res = await getBasketList()

				if (res.code == 1) {
					if (res.data.goods) {
						this.basket_goods = res.data.goods;
						this.total_amount = res.data.total_amount
						this.total_num = res.data.total_num
					} else {
						this.basket_goods = []
						this.total_amount = 0
						this.total_num = 0
					}


				}
			},
			async setCar(basket_id, index, type, indexs) { //购物车操作   add 增加 //remove 删除  //reduce减少
				let res = await setBasketNumber({
					action: type,
					basket_id: basket_id
				})
				if (res.code == 1) {
					if (type == 'remove') {
						this.getCar()
					} else {
						this.total_amount = res.data.total_amount
						this.total_num = res.data.total_num
						this.basket_goods[indexs].list[index] = res.data.goods
						this.$forceUpdate();

					}
				}
			},
			quantityOperate(item, index, type, indexs) { //购物车商品数量操作
				let that = this
				this.Loading('加载中...', () => {
					setTimeout(res => {
						that.setCar(item.basket_id, index, type, indexs)
					}, 1500)
				})

			},
			deleteBasketGood(index, item, indexs) { //删除购物车
				let that = this
				let basket_id = [];
				basket_id.push(item.basket_id);

				this.Loading('加载中...', () => {
					setTimeout(res => {
						that.setCar(JSON.stringify(basket_id), index, 'remove', indexs)
					}, 1500)
				}, 1500)

			},
			basketOpen() {
				this.getCar()
				this.basket_show = true
			},
			basketClose() {
				this.basket_show = false
			},

			addCart() { //结算购物车
				let that = this
				let value = []
				this.basket_goods.map((item, index) => {

					item.list.map((items, indexs) => {

						value.push(items.basket_id)
					})
				})
				if (value.length > 0) {
					uni.setStorage({
						key: 'basket_id',
						data: value,
						success: function() {
							setTimeout(() => {
								that.navigateTo('/subPages/submit-order/submit-order')
							}, 1500)
						}
					});
				} else {
					this.errToast('请加入购物车再结算', () => {})
				}



			},
			toDetail(item) { //打开详情页
				uni.navigateTo({
					url: "/subPages/commodity-detail/commodity-detail?code=" + item.code,
				})
			}

		}
	}
</script>

<style lang="scss" scoped>
	.content {
		width: 100vw;
		height: 100vh;
		background: #FFFFFF;
	}

	.basket-scroll {
		height: 550rpx;
		width: 100%;
	}

	.good-num {
		width: 50rpx;
		text-align: center;
	}

	.search-box {
		margin-left: 32rpx;
		width: 456rpx;
	}

	.topbar-current {
		width: 30%;
		font-size: 30rpx;
		padding: 0 92rpx;
		color: #292626;
	}

	.u-nav-slot {
		padding: 0 34rpx;
		box-sizing: border-box;
	}

	.leftbar-current {

		background: #FFFFFF;
		border-top: 1px solid rgba(153, 153, 153, 0.16) !important;
		box-shadow: $wash-border;
	}


	.space {
		height: 16rpx;
		width: 100%;

	}

	.topbar {
		@include flex(row);
		box-shadow: $wash-border;

		&-box {
			padding: 24rpx 0;
			width: 100%;
			@include flex(row);

			&-item {
				width: 30%;
				font-size: 30rpx;
				padding: 0 92rpx;
				color: #999999;
			}

			&-line {
				width: 2rpx;
				height: 32rpx;
				background: #E6E6E6;
				border-radius: 1rpx;
			}
		}
	}

	.classify-box {
		height: calc(100% - 350rpx);
		width: 100%;

		.scroll-left-box {
			width: 176rpx;
			height: 100%;
			background: #E6E6E6;

			.item {
				text-align: center;
				padding: 38rpx 0;
				border-top: 1px solid #FFFFFF;
			}
		}

		.scroll-right-box {
			width: 574rpx;
			height: 100%;
			background: #FFFFFF;

			.goods {}
		}

	}

	.center {
		@include flex(row);




		&-right {


			&-item {
				@include flex(row);
				padding: 10rpx;
				border-bottom: 0.5rpx solid #F7F7F7;

				&-image {
					padding: 20rpx;
				}

				&-right {
					font-size: 28rpx;
					padding: 10rpx 10rpx;
					width: 327rpx;

					&-text {
						padding: 10rpx 0;
					}

					&-text2 {}

					&-middle {
						padding: 10rpx 0;
						@include flex(row);

						&-title {
							font-size: 24rpx;
							padding: 6rpx 12rpx;
							background: #1A3387;
							color: #FFE785;
							border-radius: 4rpx;
						}
					}

					&-price {
						padding: 10rpx 0;
						@include flex(row);
						justify-content: space-between;

						&-left {
							@include flex(row);
							width: 218rpx;

							&-price {
								padding-top: 58rpx;
								@include flex(row);
							}

						}

						&-specbox {
							width: 218rpx;
							font-size: 25rpx;
						}



						&-btnbox {
							@include flex(row);
							justify-content: space-between;

							&-btn {
								position: relative;
								font-size: 32rpx;
								color: #FFFFFF;
								border-radius: 4rpx;
								background: #1A3387;
								width: 40rpx;
								height: 40rpx;
								line-height: 40rpx;
								text-align: center;
								vertical-align: middle;
								margin: 0 10rpx;
							}
						}

						&-btnspec {
							background: #1A3387;
							color: #FFFFFF;
							padding: 5rpx 12rpx;
							font-size: 25rpx;
							border-radius: 10rpx;
						}
					}
				}
			}
		}
	}

	.bottom {
		padding: 0 32rpx;
		position: relative;
		height: 160rpx;
		background: $wash-color-white;

		&-head {
			@include flex(row);
			justify-content: space-between;
			width: 100%;
			height: 92rpx;

			&-left {
				@include flex(row);

				&-box {
					padding-right: 32rpx;
					position: relative;
					width: 91rpx;

					&-cricle {
						width: 92rpx;
						height: 92rpx;
						background: #1A3387;
						border: 2rpx solid #FFFFFF;
						border-radius: 55rpx;
						position: absolute;
						top: -30rpx;

					}

				}

				&-price {
					color: $wash-color-red;
					font-size: 32rpx;
					padding: 30rpx 0;
				}
			}

			&-right {
				@include flex(row);
				align-items: center;

				&-btn {
					background-color: $wash-color-darkblue;
					color: $wash-color-white;
					padding: 16rpx 40rpx;
					font-size: 26rpx;
					border-radius: 10rpx
				}
			}
		}
	}

	.sellprice-box {
		@include flex(row);
	}

	.discountprice-box {
		padding: 6rpx 10rpx;
		@include flex(row);

	}

	.spec-item {
		width: 500rpx;

		&-text {
			color: #999999;
			font-size: 28rpx;
		}

		&-tags {
			@include flex(row);
			padding: 10rpx 0;

			&-item {
				padding: 5rpx 15rpx;
				margin-right: 20px;
				font-size: 30rpx;
				border: 1px solid #1A3387;
				color: #1A3387;
				border-radius: 13rpx;

			}

			&-current {
				background-color: #1A3387;
				padding: 5rpx 15rpx;
				margin-right: 20px;
				font-size: 30rpx;
				border-radius: 13rpx;
				color: $wash-color-white;
				border: 1px solid #1A3387;
			}
		}

	}
</style>