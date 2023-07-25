<template>
	<view class="coupon-wrapper" :style="{backgroundImage:'url('+bgImage+')'}">
		<image :src="icon" class="icon" mode="aspectFit"></image>
		<image :src="receiveBg" class="receive-image" mode="aspectFit"></image>
		<u-popup :show="show" :round="10" mode="center" overlay="true" closeable="true" @close="close" @open="open" class="pop-up">
			<view class="coupon-toast">
				<view>恭喜您成功领取</view>
				<view class="coupon-num">6.1折券</view>
				<view>已放入您的账户</view>
				<view class="order-btn" @click="goBack()">马上下单</view>
				<view class="invite-btn" @click="goInvite()">邀请好友免费洗鞋</view>
			</view>
		</u-popup>
		<view class="receive-btn" @click="showToast">立即领取</view>
	</view>
</template>

<script>
	export default {
		data() {
		  return {
			show: false,
			icon: `${getApp().globalData.baseUrl}/wash/static/logo2.png`,
			bgImage: `${getApp().globalData.baseUrl}/wash/static/bd.png`,
			receiveBg: `${getApp().globalData.baseUrl}/wash/static/go.png`,
		  }
		},
		
		methods: {
			goBack(){
				uni.$u.route({
					type:'navigateBack'
				});
			},
			
			goInvite() {
				uni.$u.route({
					url: 'subPages/coupon/invite',
				})
			},
			
			showToast() {
				this.show = true;
			},
			
			open() {
			},
			
			close() {
				this.show = false;
			}
		}
	}
</script>

<style lang="scss" scoped>
	.coupon-wrapper {
		position: relative;
		height: 100vh;
		width: 100vw;
		background-size: cover;
		background-repeat: no-repeat;
		
		.icon {
			position: absolute;
			left: 56rpx;
			top: 56rpx;
			width: 220rpx;
			height: 70rpx;
		}
		
		.receive-image {
			width: 600rpx;
			position: absolute;
			left: 50%;
			transform: translateX(-50%);
		}
		
		.receive-btn {
			width: 544rpx;
			height: 114rpx;
			background: linear-gradient(0deg, #FE7954 0%, #EB3506 100%);
			box-shadow: 1rpx 15rpx 16rpx 0rpx rgba(245,89,43,0.18);
			border-radius: 57rpx;
			display: flex;
			justify-content: center;
			align-items: center;
			position: fixed;
			bottom: 52rpx;
			left: 50%;
			transform: translateX(-50%);
			font-size: 48rpx;
			font-family: Alibaba PuHuiTi;
			font-weight: bold;
			color: #FBF9EB;
		}
		
		.coupon-toast {
			display: flex;
			align-items: center;
			background-color: #fff;
			border-radius: 16rpx;
			flex-direction: column;
			padding: 50rpx 50rpx 0rpx 50rpx;
			font-size: 30rpx;
			font-family: Source Han Sans CN;
			font-weight: 400;
			color: #000000;
			
			.coupon-num {
				color: #027CFF;
				margin: 10rpx 0;
			}
			
			.order-btn {
				width: 358rpx;
				height: 65rpx;
				display: flex;
				justify-content: center;
				align-items: center;
				border: 1px solid #027CFF;
				background-color: #fff;
				border-radius: 12rpx;
				color: #027CFF;
				margin-top: 46rpx;
				box-sizing: border-box;
			}
			
			.invite-btn {
				width: 358rpx;
				height: 65rpx;
				background: #027CFF;
				border-radius: 12rpx;
				display: flex;
				justify-content: center;
				align-items: center;
				color: #fff;
				margin-top: 20rpx;
			}
		}
	}

</style>
