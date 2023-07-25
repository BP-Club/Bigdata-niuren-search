<template>
	<view class="" v-model="show">
		<view class="goods-center box-sizing-border-box ">
			<view class="flex goods-info align-content-center">
				<image class="goods-img" :src="goods.cover" mode="aspectFit"></image>
				<view class="goods-ringht flex align-content-between flex-wrap">
					<view class="price">
						<text class="character">￥</text><text>{{goods.price_selling}}
							<!-- goodsInfo.price_selling --></text>
					</view>
					<view class="">
						规格选中
					</view>
					<view class="">
						{{num}}件
					</view>
				</view>
			</view>
			<view class="goods-sku" v-if="goods.is_spec">
				<scroll-view scroll-y="true" class="scroll-goods-sku">
					<view class="goods-spc" v-for="(item,index) in specs" :key="index">
						<view class="spc">
							{{item.name}}
						</view>

						<view class="sku flex flex-wrap">
							<text @click="checkboxSku(index,i,sku.isClick)" v-for="(sku,i) in item.list" :key="i"
								:class="sku.isClick?'sku-click':'sku-no-click'">{{sku.name}}</text>
						</view>
					</view>
				</scroll-view>
			</view>
			<view class="flex align-items-center justify-content-between">
				<view class="">
					购买数量
				</view>
				<view class="">
					<u-number-box v-model="num"></u-number-box>
				</view>
			</view>
			<view class="btn-box flex justify-content-center" v-if="type=='addCar'">
				<btn-button title="加入购物车" @click="addCar" bntColor="#F7F7F7" width='320' radius="36rpx  0  0  36rpx"
					height="80" size="32" color="#1A3387" />
				<btn-button title="立即购买" @click="addCar('buyNow')" bntColor="#1A3387" width='320'
					radius=" 0 36rpx 36rpx 0" height="80" size="32" color="#FFFFFF" />
			</view>
			<view class="btn-box flex justify-content-center" v-if="type=='buyNow'">

				<btn-button title="立即购买" @click="addCar('buyNow')" bntColor="#1A3387" width='640' radius="36rpx"
					height="80" size="32" color="#FFFFFF" />
			</view>
		</view>
	</view>
</template>

<script>
	import {
		setCart
	} from "@/api/basket.js"
	export default {
		props: {
			goodsInfo: { //商品信息
				type: Object,
				default: {}
			},
			show: {
				type: Boolean, //是否显示商品组件
				default: false
			},
			type: { //什么方式打开的 加入购物车还是购买 addCar为加入购物车，buyNow为购买
				type: String,
				default: 'addCar'
			}
		},
		name: "sku",
		computed: {
			goodsPrice() {
				if (this.goodsInfo) {
					this.price = this.goodsInfo.price_selling
				}

				return this.price
			}
		},
		data() {
			return {
				num: 1, //数量
				price: '', // 当前选中的sku价格 默认第一个
				goods: null, //接收父组件传值 方便直接再子组件操作父组件数据
				specs: [], //spc
				skuIndex: null, //sku下标
				spcIndex: null, //spc下标

			};
		},
		watch: {
			goodsInfo(newVal) {
				if (newVal) {
					this.goods = newVal
					let specs = newVal.specs
					specs.forEach((item) => {
						item.list.forEach((sku, index) => {
							let isClick = false
							sku.isClick = isClick
							return sku
						})

						return item
					})
					this.specs = specs

				}
			}
		},
		methods: {
			addCar(type) { //加入购物车按钮
				let arrays = [];
				let sku = [];
				this.specs.forEach((item) => {
					item.list.forEach((skus) => {
						if (skus.isClick) {
							sku.push(skus.spec_code)
						}
					})
				})
				if (this.goodsInfo.is_spec && this.specs.length == 0) {
					return this.toast('请选择规格')
				} else {
					let item = {
						goods_code: this.goods.code,
						good_num: this.num,
						spec_codes: !this.goods.is_spec ? '' : sku
					}
					arrays.push(item)
					this.addBasket(JSON.stringify(arrays), type)
				}
			},
			async addBasket(data, type, ) { //加入购物车api
				let that = this
				let res = await setCart({
					goods: data
				});
				if (res.code == 1) {
					if (type) {
						uni.setStorage({
							key: 'basket_id',
							data: [res.data.basket_id],
							success: function() {
								setTimeout(() => {
									that.navigateTo('/subPages/submit-order/submit-order')
								}, 1500)
							}
						});
						this.$emit('addCar', false)
					} else {
						this.successToast('加入购物车成功', () => {
							this.$emit('addCar', false)
						})
					}
				} else {
					this.toast(res.info, 1500)
				}
			},
			checkboxSku(spIndex, skIndex, flag) { //sku切换
				if (flag) {
					return
				}
				this.spcIndex = spIndex;
				this.skuIndex = skIndex;
				this.specs[spIndex].list = this.specs[spIndex].list.map((item, i) => {
					return Object.assign({}, item, {
						isClick: skIndex === i ? true : false
					})
				})

			},
		}
	}
</script>

<style lang="scss" scoped>
	.sku-click {
		color: #FFFFFF;
		background: #1A3387;

	}

	.sku-no-click {

		background: #F5F5F5;
		color: #1A3387;
	}

	.btn-box {
		margin-top: 30rpx;
	}

	.goods-center {
		width: 100%;
		overflow: hidden;
		padding: 30rpx 20rpx 20rpx 20rpx;

		.goods-sku {
			width: 100%;
			height: 230rpx;
			margin-bottom: 30rpx;

			.scroll-goods-sku {
				height: 230rpx;

				.goods-spc {

					font-size: 32rpx;

					border-bottom: 2rpx solid #F7F7F7;

					.spc {
						font-size: 28rpx;

					}

					.sku {
						padding: 20rpx;

						text {
							font-size: 26rpx;
							padding: 5rpx 20rpx;
							margin-right: 10rpx;
							border-radius: 10rpx;
						}
					}

					margin-bottom: 20rpx;
				}
			}


		}

		.goods-info {
			margin-bottom: 30rpx;

			.goods-img {
				width: 192rpx;
				height: 192rpx;
				border-radius: 20rpx;
				margin-right: 15rpx;
			}

			.goods-ringht {
				height: 192rpx;
				width: calc(100% - 227rpx);

				view {
					width: 100%;

				}
			}

			.price {
				.character {
					font-size: 20rpx;
				}

				color: #D41A1E;

			}
		}

	}
</style>
