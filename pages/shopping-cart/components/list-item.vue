<template>
	<view class="content">
		<view class="disp-flex align-center u-m-r-12">
			<u-checkbox-group>
				<u-checkbox v-model="checked" shape="circle" activeColor="#D41A1E"></u-checkbox>
			</u-checkbox-group>
		</view>
		<view class="disp-flex justify-between">
			<view class="u-m-r-20">
				<u--image radius="5" width="192rpx" height="192rpx"></u--image>
			</view>
			<view class="disp-flex flex-wrap">
				<view class="shopping-name f-s-28">{{data.name}}</view>
				<view class="quantity f-s-24 u-p-t-22">件数：共3件</view>
				<view class="disp-flex justify-between u-p-t-26 count">
					<view class="price">
						<text class="f-s-22">￥</text>
						<text>39.00</text>
						<text class="u-m-l-10 del">￥129.00</text>
					</view>
					<view>
						<u-number-box v-model="value">
							<view slot="minus" class="minus">
								<u--image src="/static/images/minus.svg" width="44rpx" height="44rpx" :fade="false">
								</u--image>
							</view>
							<text slot="input" class="input">{{value}}</text>
							<view slot="plus" class="plus">
								<u--image src="/static/images/plus.svg" width="44rpx" height="44rpx" :fade="false">
								</u--image>
							</view>
						</u-number-box>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		props: {
			checked: {
				type: Boolean,
				default: false
			},
			data: {
				type: Object,
				default: () => {
					return {}
				}
			}
		},
		data() {
			return {
				value: 1
			}
		}
	}
</script>

<style lang="scss" scoped>
	.content {
		background-color: white;
		padding: 32rpx;
		margin-bottom: 16rpx;
		display: flex;
		position: relative;
	}

	.input {
		width: 72rpx;
		text-align: center;
	}

	.shopping-name {
		width: 100%;
		color: #333333;
	}

	.quantity {
		color: #999999;
	}

	.count {
		width: 100%;
		.price {
			color: #D41A1E;

			.del {
				color: #999999;
				font-size: 20rpx;
				text-decoration: line-through;
			}
		}
		.input{
			color: #D41A1E;
		}
	}
	
</style>
