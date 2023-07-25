<template>
	<view class="index">
		<view class="goods from box-sizing-border-box flex align-items-center  justify-content-between">
			<u--image :showLoading="true" src="https://cdn.uviewui.com/uview/album/1.jpg" width="160rpx" height="160rpx"
				shape="square" radius="10rpx"></u--image>
			<view class="goods-ringht u-m-l-20 flex align-items-center  justify-content-between">
				<view class="goods-ringht-left flex align-content-between flex-wrap">
					<view class="goods-name f-s-28">
						非常规鞋类
					</view>
					<view class="goods-price flex align-items-center">
						<view class="u-m-r-10 current-price">
							<text class="f-s-22">￥</text><text class="f-s-28">1222</text>
						</view>
						<view class="original-cost">
							<text class="f-s-20">￥</text> <text class="f-s-20">522</text>
						</view>
					</view>
				</view>
				<view class="">
					x1
				</view>
			</view>
		</view>
		<view class="from box-sizing-border-box">

		</view>
		<view class="to-add">
			<btn-button title="去添加地址" @click="navigateTo('/subPages/add-address/add-address')"
				color="rgba(255, 255, 255, 1)" size="28" width="686" height="88" radius="36rpx" bntColor="#0B27A4">
			</btn-button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {

			};
		}
	}
</script>

<style lang="scss" scoped>
	.index {
		width: 100vw;
		height: 100vh;
		background: #F7F7F7;
	}

	.from {
		width: 686rpx;
		border-radius: 20rpx;
		margin: 0 auto 16rpx;
		padding: 40rpx 32rpx;
		background: #FFFFFF;
    
		.goods-ringht {
			width: calc(100% - 140rpx);
			height: 120rpx;
           .goods-ringht-left{
			   height: 100%;
			   .goods-name {
			   	font-weight: 500;
			   	width: 100%;
				color: #333333;
			   }
			   
			   .goods-price {
			   	width: 100%;
				.original-cost{
						text-decoration: line-through;
					color: #999999;
				}
				.current-price{
					text{
						color: #333333;
						
					}
				}
			   }
		   }
			
		}
	}

	.to-add {
		position: fixed;
		bottom: 0;
		left: 0;
		background: #FFFFFF;
		width: 100%;

		right: 0;
		margin: auto;
		padding: 8rpx 32rpx 80rpx 32rpx;

	}
</style>