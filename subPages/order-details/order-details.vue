<template>
	<view class="index box-sizing-border-box">
   
		<view class="top-block box-sizing-border-box">
			<view class="order-status f-s-40">
				{{order_info.status_text}}
			</view>
			<!--改动的代码++++++++++++++++-->
			<view class="order-status-text f-s-24" v-if="order_info.process_intro">
				{{order_info.process_intro}}
			</view> 
		</view>
		<view class="order">
			<view class="from-box  box-sizing-border-box">
				<view class="shop-box box-sizing-border-box flex align-items-center justify-content-between">
					<view class="flex shop box-sizing-border-box flex-wrap align-content-between">
						<view class="flex shop-info align-items-center justify-content-between">
							<view class="">
								<!--改动的代码++++++++++++++++-->
								<text class="shop-name fw500">{{order_info.store.name}}</text>
								<text v-if="order_info.refund_status == 1" class="shop-text fw500 refund-ing">退款审核中</text> 
								<text v-if="order_info.refund_status == 3" class="shop-text fw500 refund-reject">退款被拒绝</text> 
								<text v-if="order_info.refund_status == 2" class="shop-text fw500 refund-argee">已同意退款</text> 
							</view>
						</view>
						<view class="shop-info">
							<icons name="icon-shijian" size="38" :label="order_info.store.work_time" space="10"
								labelColor="#999999" labelSize="24" color="#999999"></icons>
						</view>
						<view class="flex shop-info align-items-center justify-content-between">
							<icons name="icon-dizhi2" size="38" :label="order_info.store.address" labelColor="#999999"
								labelSize="24" space="10" color="#999999"></icons>
							<text class="icon-navigation" @click="openAddress">地图导航</text>
						</view>
					</view>
				</view>
			</view>
			<view class="from-box  box-sizing-border-box">
				<view class="receiving-box way">
					<view class="way-top u-m-b-40">
						<text class="f-s-30 way-top-left">收件方式：</text> <text class="f-s-30 type">{{order_info.take_type_text}}</text>
					</view>
					<view class="flex address u-p-b-40 align-items-center "  v-if="order_info.take_type==2">
						<view class="">
							<icons name='icon-dizhi' size="36" color='#1A3387'></icons>
						</view>
						<view class="address-ringht flex align-content-between u-m-l-15 flex-wrap">
							<view class="name-phone">
								<text class="f-s-32">{{order_info.take_address.name}}</text> <text
									class="f-s-32 u-m-l-15">{{order_info.take_address.phone}}</text>
							</view>
							<view class="">
								<text class="f-s-28 address">{{order_info.take_address.address}}</text>
							</view>
						</view>
					</view>
					<view class="date  flex align-items-center" v-else>
						<view class="">
							<icons name='icon-shijian2' size="36" color='#1A3387'></icons>
						</view>
						<view class="date-ringht  u-m-l-15 flex align-content-between u-m-l-15 flex-wrap">
							<view class="date-text f-s-30">
								自带到店时间
							</view>
							<view class="time f-s-28">
								{{order_info.take_date}}
							</view>
						</view>
					</view>
				</view>
				<view class=" way">
					<view class="way-top u-m-t-40 u-m-b-40">
						<text class="f-s-30 way-top-left">归还方式：</text> <text class="f-s-30 type">{{order_info.return_type_text}}</text>
					</view>
					<view class="flex address align-items-center" v-if="order_info.return_type==2">
						<view class="">
							<icons name='icon-dizhi' size="36" color='#1A3387'></icons>
						</view>
						<view class="address-ringht flex align-content-between u-m-l-15  flex-wrap">
							<view class="name-phone">
								<text class="f-s-32">{{order_info.send_address.name}}</text> <text
									class="f-s-32 u-m-l-15">{{order_info.send_address.phone}}</text>
							</view>
							<view class="">
								<text class="f-s-28 address">{{order_info.send_address.address}}</text>
							</view>
						</view>

					</view>
					<view class="date  flex align-items-center" v-else>
						<view class="">
							<icons name='icon-shijian2' size="36" color='#1A3387'></icons>
						</view>
						<view class="date-ringht  u-m-l-15 flex align-content-between u-m-l-15 flex-wrap">
							<view class="date-text f-s-30">
								到店取件时间
							</view>
							<view class="time f-s-28">
								{{order_info.return_date}}
							</view>
						</view>
					</view>
				</view>
			</view>
			<view class="commodity from-box  box-sizing-border-box u-p-t-32">
				<u-cell-group class="u-p-l-2 u-p-r-2" :border="false">
					<u-cell class="" :border="false" v-for="(item,i) in order_info.goods_data.goods" :key="i">
						<view slot="icon">
							<u--image :src="item.cover" radius="5" width="120rpx" height="120rpx"></u--image>
						</view>
						<view slot="title" class="list flex flex-wrap align-content-between">
							<view class="list-name">{{item.name}}</view>
							<view class="list-price">
								<text class="price-symbol">￥</text>
								<text class="price">{{item.price_selling}}</text>
								<text class="original-price">￥{{item.price_market}}</text>
							</view>
						</view>
						<view slot="right-icon" class="sum">x{{item.good_num}}</view>
					</u-cell>
				</u-cell-group>
				<u-cell-group class="u-p-l-2 u-p-r-2" :border="false">
					<u-cell :titleStyle="titleStyle" title="商品总额" :border="false">
						<view slot="right-icon" class="amount">￥{{order_info.goods_data.total_amount}}</view>
					</u-cell>
					<!-- 	<u-cell :titleStyle="titleStyle" title="优惠券" :border="false">
						<view slot="right-icon" class="coupon f-s-28">暂无可用优惠</view>
					</u-cell> -->
					<u-cell :titleStyle="titleStyle" title="备注" :border="false">
						<view slot="right-icon" class="amount">{{order_info.remark?order_info.remark:'无'}}</view>
					</u-cell>
					<u-cell :titleStyle="titleStyle" title="上门收件费" :border="false"  v-if="order_info.take_type==2">
						<view slot="right-icon" class="amount">￥{{order_info.visit_fee}}</view>
					</u-cell>

					<u-cell :titleStyle="titleStyle" title="归还配送费" :border="false" v-if="order_info.return_type==2">
						<view slot="right-icon" class="amount">￥{{order_info.delivery_fee}}</view>
					</u-cell>


					<view class="total f-s-28 flex align-items-center justify-content-end ">
						<view class="">
							<text class="u-m-r-20">共{{order_info.goods_data.total_num}}件商品</text>
						</view>
						<view class="">
							<text class="f-s-22">合计：</text><text class="character f-s-22">￥</text> <text
								class="total_amount f-s-28">{{order_info.goods_data.total_amount}}</text>
						</view>
					</view>
				</u-cell-group>
			</view>
			<view class="other from-box flex align-content-between flex-wrap box-sizing-border-box">
				<view class="item flex align-items-center">
					<view class="item-left f-c-333 f-s-28">
						订单编号
					</view>
					<view class="item-ringht" @click="copyContent(order_info.order_no)">
						<icons name="icon-fuzhi" labelSize="28" color='#1A3387' :label="order_info.order_no"
							labelColor="#333333" size="32" space="10" labelPos="left" ></icons>
					</view>
				</view>
				<view class="item flex align-items-center">
					<view class="item-left f-c-333 f-s-28">
						下单时间
					</view>
					<view class="item-ringht f-c-333">
						{{order_info.create_at}}
					</view>
				</view>
				<view class="item flex align-items-center" v-if="order_info.payment_at">
					<view class="item-left f-c-333 f-s-28">
						付款时间
					</view>
					<view class="item-ringht f-c-333">
						{{order_info.payment_at}}
					</view>
				</view>
			</view>
			<view class="flex justify-center btn-list" >
				<btnButton title="立即支付" @click="pay(order_info.order_no)" v-if="order_info.status==0" size="24" color="#FFFFFF"
					bntColor="#1A3387" radius="58rpx" width="686" height="88"></btnButton>
			</view>
		</view>
	</view>

</template>

<script>
	import {
		getOrderDetail,getPayment,
	} from "@/api/stores.js"
	import {
		openLocation,wxPay
	} from "@/utils/common.js"
	export default {
		data() {
			return {
				id: '',
				order_info: null, //订单信息
			};
		},
		onLoad({
			id
		}) {
			this.id = id
			this.getOrderDetail(this.id)
		},
		methods: {
			pay(order_no){//立即发起支付
				this.getPayData(order_no)
			},
			openAddress() {//打开地图
				let longitude = parseInt(this.order_info.store.lng)
				let latitude = parseInt(this.order_info.store.lat)
				let info = {
					name: this.order_info.store.name,
					address: this.order_info.store.address
				}
				openLocation(longitude, latitude, info)
			},
			async getPayData(code) { //获取支付参数
				let that = this
				let res = await getPayment({
					code: code
				})
				if (res.code == 1) {
					let pay_res = await wxPay(res.data) //支付
					if (pay_res.errMsg == "requestPayment:ok" && pay_res) {
						uni.showModal({
							title: "提示",
							content: "支付成功!",
							showCancel: false,
							success: (res2) => {
								if (res2.confirm) {
									// setTimeout(() => {
									// 	that.redirectTo('/subPages/my-order/my-order')
									// }, 1500);
			
								}
							},
							fail: (err) => {
								that.errToast(err, () => {
									// setTimeout(() => {
									// 	that.redirectTo('/subPages/my-order/my-order')
									// }, 2000);
								})
			
							}
						})
					} else {
						that.errToast('支付失败', () => {
							setTimeout(() => {
								that.redirectTo('/subPages/my-order/my-order')
							}, 2000);
						})
					}
				}
			},
			async getOrderDetail(id) { //获取订单详情
				let res = await getOrderDetail({
					order_id: id
				})
				if (res.code == 1) {
					this.order_info = res.data
				}
			}
		}

	}
</script>

<style lang="scss" scoped>
	.index {
		width: 100vw;
		min-height: 100vh;
		background: #F7F7F7;
		padding: 0 0 186rpx 0;
	}
    .btn-list{
	   background-color: #FFFFFF;
	    padding: 40rpx 120rpx;
		z-index: 90;
		position: fixed;
		left: 0;
		right: 0;
		margin: 0 auto;
		bottom: 0;
	}
	.receiving-box {
		border-bottom: 1rpx solid #F5F5F5;
	}

	.way {
		width: 100%;
		
	    .date{
			height: 90rpx;
			width: 100%;
			.date-ringht{
				height: 90rpx;
				width: 100%;
				.date-text{
					color: #333333;
				}
				.time{
					color: #999999;
				}
				view{
									 width: 100%; 
				}
			}
		}
        .address{
			height: 90rpx;
			
			.address-ringht{
				 height: 90rpx;
				 width: 100%;
				 .address{
				color: #9EA5B8;	 
				 }
				 .name-phone{
					 
					 text{
						color: #2F3B4F; 
					 }
				 }
				 view{
					 width: 100%; 
				 }
			}
		}
		.way-top {
			.type{
				color: #1A3387;
			}
			.way-top-left{
				font-weight: 500;
				color: #333333;
			}
		}
	}

	.total_amount {
		color: #D41A1E;
	}

	.character {
		color: #D41A1E;
	}

	.to-store-date {
		width: 100%;
		padding: 40rpx 40rpx;
		border-bottom: 2rpx solid #F5F5F5;

		.to-store-date-left {
			width: 80rpx;
		}

		.to-store-date-right {
			width: calc(100% - 80rpx);
		}
	}

	.top-block {
		width: 100%;
		height: 256rpx;
		background: #1A3387;
		padding: 32rpx;

		.order-status {
			color: #FFFFFF;
			font-weight: 500;
			margin-bottom: 16rpx;
		}

		.order-status-text {
			color: #FFFFFF;
		}
	}

	.order {
		position: relative;
		margin-top: -80rpx;
	}

	.shop-box {
		width: 100%;
		background: #FFFFFF;
		margin-top: -12rpx;

		.shop {
			width: 90%;
			height: 140rpx;

		}

		.shop-info {
			width: 100%;

			.shop-text {
				border-radius: 4rpx;
				padding: 6rpx 12rpx;
				margin-right: 22rpx;
				font-size: 20rpx;
				text-align: center;
				color: #FFFFFF;
			}
			
			
           //修改代码，退款申请++++++++++++++++
			.shop-name {
				font-size: 28rpx;
				color: #333333;
				padding-right: 10rpx;
			}

			.distance {
				font-size: 20rpx;
				color: #999999;
			}

			.icon-navigation {
				font-size: 22rpx;

				color: #1A3387;
			}
		}

	}

	.commodity {
		background-color: white !important;
	}

	.total {
		height: 108rpx;
		border-top: 1rpx solid #F7F7F7;

		.commodity {
			font-size: 22rpx;
			color: #D41A1E;
		}
	}

	/deep/ .u-cell__body {
		padding: 20rpx 0 !important;
	}

	.from-box {
		width: 686rpx;
		margin: 16rpx auto 0;
		background: #FFFFFF;
		border-radius: 20rpx;
		padding: 32rpx 30rpx;
	}

	.sum {
		font-size: 28rpx;
		color: #333;
	}

	.list {
		width: 100%;
		height: 120rpx;
	}

	.list-name {
		width: 100%;
		font-weight: 500;
		color: #333333;
	}

	.list-price {
		width: 100%;

		.price-symbol {
			color: #D41A1E;
			font-size: 24rpx;

		}

		.price {
			color: #D41A1E;
			font-size: 28rpx;
		}

		.original-price {
			margin-left: 10rpx;
			color: #999999;
			font-size: 20rpx;
			text-decoration: line-through;
		}
	}

	.other {
		height: 212rpx;

		.item {
			width: 100%;

			.item-left {
				margin-right: 48rpx;
			}

			.item-ringht {
				color: #333333;
			}
		}
	}
	//改动的代码++++++++++++++++
	.refund-argee{
		background:#59aba1
	}
	.refund-reject{
		background:#e66f66
	}
	.refund-ing{
		background:#7d818e
	}
</style>