<template>
	<view class="index box-sizing-border-box">
	
		<view class="top-block box-sizing-border-box">
			<view class="order-status f-s-40">
                 售后
			</view>
			<!-- <view class="order-status-text f-s-24">
				您的衣物正在送货中…
			</view> -->
		</view>
		<view class="order">
			<view class="from-box  box-sizing-border-box">
				<view class="receiving-box way">
					<view class="way-top u-m-b-40">
						<text class="f-s-30 way-top-left">申请过程</text> 
					</view>
					<u-steps current="1" direction="column" v-for="(item,i) in tracks" :key="i" dot="true">
						<u-steps-item :title="item.title" :desc="item.create_at"  :error="item.status == 2" >
				
						</u-steps-item>
					    <view class="f-s-25 remark">
							{{item.remark}}
						</view>   
					</u-steps>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getRefundTrack
	} from "@/api/stores.js"
	export default {
		data() {
			return {
				id:'',
				tracks:[]
			}
		},
		onLoad({
			id
		}) {
			this.id = id
			this.getRefundTracks(this.id)
		},
		methods: {
			
			async getRefundTracks(id) { //获取订单详情
				let res = await getRefundTrack({
					order_id: id
				})
				if (res.code == 1) {
					this.tracks = res.data
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
		margin-top: -120rpx;
	}


	.from-box {
		width: 686rpx;
		margin: 16rpx auto 0;
		background: #FFFFFF;
		border-radius: 20rpx;
		padding: 32rpx 30rpx;
	}

	.remark{
		padding-left: 40rpx;
		padding-bottom: 40rpx;
		color:#909193;
		margin-top:-10rpx;
	}
</style>
