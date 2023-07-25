<template>
	<view class="container">
		<view  :class="item.is_old==0?'item position-relative use-bgcolor':'item position-relative expire-bgcolor'"     v-for="(item,i) in list" :key="i">
			<view v-if="item.status == 1" class="new f-s-20">可使用</view>
			<view v-if="item.status == 3" @click="removeUse(item)" class="selected f-s-20">已选择</view>
			<view class="disp-flex">
				<view   :class="item.is_old==0?'amount use-color f-s-80':'amount expire-color f-s-80'"   >{{item.amount}}<text class="f-s-24">{{item.amount_unit}}</text></view>
				<view class="line"></view>
				<view class="disp-flex justify-between align-center rule flex-1">
					<view>
						<view class="disp-flex u-m-b-8">
							<view class="f-s-28 title">{{item.name}}</view>
							<view class="f-s-20 u-m-l-8 tip">{{item.min_consume_amount}}</view>
						</view>
						<view class="f-s-22 time">有效期至:{{item.begin_date}}</view>
						<view class="f-s-22 u-m-t-60 use-rule disp-flex" @click="openContent(item)">
							使用规则
							<u-icon v-if="!item.open" class="u-m-l-4 u-m-t-4" name="arrow-down-fill" size="16rpx"
								color="#CCCCCC"></u-icon>
							<u-icon v-else class="u-m-l-4 u-m-t-4" name="arrow-up-fill" size="16rpx" color="#CCCCCC">
							</u-icon>
						</view>
					</view>
					<view class="btn" v-if="item.status == 0">待使用</view>
					<view class="btn" @click="toUse(item)" v-if="item.status == 1">去使用</view>
					<view class="btn-exprie" v-if="item.status == 2">已过期</view>
					<view class="btn-remove" @click="removeUse(item)"  v-if="item.status == 3">取消</view>
				</view>
			</view>
			<view v-if="item.open" class="content f-s-22 u-m-t-20">
				{{item.intro}}
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getCoupons
	} from "api/coupon"
	export default {
		data() {
			return {
				list: [],
				coupon_ids:'',
				used_coupon_ids:'',
			}
		},
		onLoad(param) {
			let that = this
			if(param.coupon_ids){
				this.coupon_ids = param.coupon_ids
				this.used_coupon_ids = param.used_coupon_ids
			}
			that.getCouponsList()
		},
		methods: {
			getCouponsList() {
				this.getCoupons()
			},
			async getCoupons() {
				let res = await getCoupons({coupon_ids:this.coupon_ids,used_coupon_ids:this.used_coupon_ids})
				if(res.code == 1){
					this.list = res.data
				
				}
			},
			openContent(item) {
				item.open = item.open ? false : true
			},
			toUse(item){
				uni.$emit('selectCoupon', item)
				uni.navigateBack({
					delta: 1
				});
			},
			removeUse(item){
				uni.$emit('removeSelectCoupon', item)
				uni.navigateBack({
					delta: 1
				});
			}
		}
	}
</script>

<style lang="scss" scoped>
	.container {
		background-color: #F7F7F7;
		padding: 32rpx 32rpx 32rpx;
		min-height: 100%;
		box-sizing: border-box;
	}

	.item {
		margin: 0 0 32rpx;
		padding: 32rpx;
		border-radius: 8rpx;
	}
	
	.use-bgcolor
    {
		background-color: white;
		
		
	}
	
	.expire-bgcolor
	{
		background-color:#bfbdbd;
	}

	.new {
		border-radius: 0rpx 8rpx 0rpx 8rpx;
		position: absolute;
		right: 0;
		top: 0;
		background-color: rgba(217, 101, 43, .12);
		color: #D9652B;
		padding: 4rpx 12rpx;
	}

	.amount {
		width: 166rpx;
		height: 166rpx;
		text-align: center;
		line-height: 166rpx;
		font-weight: bold;
	}
	
	.use-color{
		color: #D9652B;
	}
	.expire-color {
		
		color: #877e79;

	}

	.line {
		border-left: dashed 2rpx #CCCCCC;
	}

	.btn {
		padding: 8rpx 32rpx;
		color: #674412;
		font-size: 24rpx;
		background: linear-gradient(135deg, #EBD0A1 0%, #CFAB73 100%);
		border-radius: 24rpx;
	}
    .btn-exprie{
		padding: 8rpx 32rpx;
		color: #43403c;
        font-size: 24rpx;
        background: linear-gradient(135deg, #a8a296 0%, #898681 100%);
		border-radius: 24rpx;
	}
	
	.rule {
		padding: 0 0 0 32rpx;

		.title {
			line-height: 34rpx;
			color: #333333;
		}

		.tip {
			border-radius: 8rpx;
			border: 2rpx solid #D9D6CF;
			color: #B3933E;
			line-height: 30rpx;
		}

		.time {
			color: #999999;
		}

		.use-rule {
			color: #999999;
		}
	}

	.content {
		padding-left: 200rpx;
		color: #999999;
	}
	
	.selected{
		border-radius: 0rpx 8rpx 0rpx 8rpx;
		position: absolute;
		right: 0;
		top: 0;
		background-color: #e0f4eb;
		color: #75bda3;
		padding: 4rpx 12rpx;
	}
	
	
	.btn-remove{
		padding: 8rpx 32rpx;
		color: #fcfcfc;
		font-size: 24rpx;
		background:linear-gradient(135deg, #c98386 0%, #ba655c 100%);
		border-radius: 24rpx;
	}
</style>
