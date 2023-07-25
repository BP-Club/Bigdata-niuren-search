<template>
	<view class="invite-wrapper">
		<image :src="topImage" class="top-image" mode="aspectFit"></image>
		<image :src="inviteImage" class="invite-image" mode="aspectFit"></image>
		<view class="invitef-image" :style="{backgroundImage:'url('+centerImage+')'}"></view>
		<view class="common-area my-invite">
			<view class="area-title" :style="{backgroundImage:'url('+areaImage+')'}">
				我的邀请
			</view>
			<view class="invite-friend">
				已邀请{{inviteNum}}位好友</view>
			<view class="progress-wrapper">
				<view class="progress">
					{{inviteNum}}/{{totalNum}}
					<view class="tag-area" :class="item.class" v-for="(item,index) in progeressGroup" :key="index">
						<image :src="shoeImage" class="shoe-img" mode="aspectFit"></image>
						<view class="tip">{{item.tip}}</view>
						<view class="tag">{{item.tag}}</view>
					</view>
				</view>
			</view>
			<view class="invite-tip">还差{{needFriend}}名好友助力即可免费洗鞋</view>
		</view>
		<view class="common-area go-invite">
			<view class="area-title" :style="{backgroundImage:'url('+areaImage+')'}">
				参与步骤
			</view>
			<view class="step-wrapper">
				<view class="step" v-for="(item,index) in stepGroup" :class="item.class" :key="index">
					<view class="step-num">{{item.step}}</view>
					<view class="tip">{{item.tip}}</view>
				</view>
			</view>
		</view>
		<view class="common-area invite-history">
			<view class="area-title" :style="{backgroundImage:'url('+areaImage+')'}">
				邀请记录
			</view>
			<view class="history-content" v-if="inviteHistory.length">
				<view class="histry-item" v-for="(item,index) in inviteHistory" :key="index">
					<view class="nickname">{{item.nickName}}</view>
					<view>{{item.inviteDate}}</view>
					<view>{{item.statusText}}</view>
				</view>
			</view>
			<view class="empty-content" v-else>暂无数据~</view>
		</view>
		<button class="invite-btn" open-type="share">立即邀请</button>
	</view>
</template>

<script>
	import {
		getDirectRecords
	} from "@/api/coupon.js"
	
	export default {
		data() {
			return {
				topImage: `${getApp().globalData.baseUrl}/wash/static/login-invite.png`,
				inviteImage: `${getApp().globalData.baseUrl}/wash/static/invite.png`,
				centerImage: `${getApp().globalData.baseUrl}/wash/static/invitef.png`,
				areaImage: `${getApp().globalData.baseUrl}/wash/static/invite-title.png`,
				shoeImage: `${getApp().globalData.baseUrl}/wash/static/sh.png`,
				baseUrl: getApp().globalData.baseUrl,
				progeressGroup: [
					{tip: '免费洗一双鞋',tag:'1位',class:'area1'},
					{tip: '免费洗一双鞋',tag:'3位',class:'area2'},
					{tip: '免费洗一双鞋',tag:'5位',class:'area3'},
				],
				stepGroup: [
					{tip: '点击【立即邀请】 邀请好友',step:'1',class:'step1'},
					{tip: '好友完成注册认证',step:'2',class:'step2'},
					{tip: '好友完成助力',step:'3',class:'step3'},
				],
				inviteNum: 0,
				totalNum: 5,
				inviteHistory: []
			}
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
		
		onLoad() {
			let that = this;
			that.getShareDetail();
		},
		
		computed:{
			// 判断传入的name属性，是否图片路径，只要带有"/"均认为是图片形式
			needFriend() {
				if(this.inviteNum < 1) {
					return 1;
				} else if (this.inviteNum >= 1 & this.inviteNum < 3) {
					return 3 - this.inviteNum;
				} else if (this.inviteNum >= 3 & this.inviteNum < 5) {
					return 5 - this.inviteNum;
				}
				return 1;
			},
		},
		
		methods: {
			async getShareDetail() {
				try {
					const res = await getDirectRecords();
					if (res.code == 1) {
						this.inviteNum = res.data.inviteNum;
						this.totalNum = res.data.totalNum;
						this.inviteHistory = res.data.inviteHistory||[];
					} else {
						return this.toast(res.info, 1500)
					}
				} catch(e){
					console.log(e)
				}
			},
		}
	}
</script>

<style lang="scss" scoped>
.invite-wrapper {
	height: auto;
	width: 100%;
	background: linear-gradient(#00D7FF,#007FFF);
	padding-bottom: 54rpx;
	
	.top-image {
		position: relative;
		width: 700rpx;
		left: 50%;
		transform: translateX(-50%);
		top: -100rpx;
	}
	
	.invite-image {
		position: relative;
		left: 50%;
		width: 447rpx;
		transform: translateX(-50%);
		top: -440rpx;
	}
	
	.invitef-image {
		position: relative;
		top: -640rpx;
		width: 100%;
		height: 800rpx;
		background-size: cover;
		background-repeat: no-repeat;
	}
	
	.common-area {
		margin: 0 33rpx;
		box-sizing: border-box;
		height: 426rpx;
		background-color: #fff;
		border-radius: 12rpx;
		position: relative;
		
		.area-title {
			width: 408rpx;
			height: 87rpx;
			position: absolute;
			left: 50%;
			transform: translateX(-50%);
			z-index: 9;
			background-size: cover;
			background-repeat: no-repeat;
			top: -30rpx;
			display: flex;
			justify-content: center;
			align-items: center;
			font-size: 42rpx;
			font-family: Alibaba PuHuiTi;
			font-weight: bold;
			color: #E11F00;
			padding-top: 10rpx;
		}
	}
	
	.my-invite {
		top: -800rpx;
		display: flex;
		justify-content: center;
		
		.invite-tip {
			position: absolute;
			width: 100%;
			text-align: center;
			bottom: 52rpx;
			left: 50%;
			transform: translateX(-50%);
			font-size: 24rpx;
			font-family: Source Han Sans CN;
			font-weight: 500;
			color: #E11F00;
		}
		
		.progress-wrapper {
			position: relative;
			top: 270rpx;
			
			.progress {
				width: 598rpx;
				height: 29rpx;
				background: #FCEC9B;
				border-radius: 15rpx;
				font-size: 16rpx;
				font-family: Source Han Sans CN;
				font-weight: 400;
				color: #E11F00;
				display: flex;
				align-items: center;
				padding: 0 21rpx;
				position: relative;
			}
			
			.tag-area {
				position: absolute;
				display: flex;
				flex-direction: column;
				align-items: center;
				bottom: -10rpx;
				
				.shoe-img {
					width: 75rpx;
					height: 50rpx;
				}
				
				.tip {
					font-size: 16rpx;
					font-family: Source Han Sans CN;
					font-weight: 400;
					color: #3D3D3D;
					margin: 13rpx 0;
				}
				
				.tag {
					width: 47rpx;
					height: 47rpx;
					background: #FFFFFF;
					box-shadow: 0rpx 0rpx 16rpx 0rpx rgba(158,132,0,0.36);
					border-radius: 50%;
					display: flex;
					justify-content: center;
					align-items: center;
					font-size: 16rpx;
					font-family: Source Han Sans CN;
					font-weight: 400;
					color: #E11F00;
				}
			}
			
			.area1 {
				left: 66rpx;
			}
			
			.area2 {
				left: 273rpx;
			}
			
			.area3 {
				right: 0rpx;
			}
		}
		
		.invite-friend {
			position: absolute;
			left: 43rpx;
			top: 101rpx;
			font-size: 28rpx;
			font-family: Source Han Sans CN;
			font-weight: 500;
			color: #72808F;
		}
	}
	
	.go-invite {
		top: -700rpx;
		height: 326rpx;
		
		.step-wrapper {
			position: absolute;
			left: 50%;
			transform: translateX(-50%);
			top: 148rpx;
			width: 228px;
			height: 0px;
			border: 1px dashed #E11F00;
			display: flex;
			
			.step {
				position: absolute;
				display: flex;
				flex-direction: column;
				align-items: center;
				
				.step-num {
					width: 47rpx;
					height: 47rpx;
					background: #FCEC9B;
					border-radius: 50%;
					display: flex;
					justify-content: center;
					align-items: center;
					font-size: 33rpx;
					font-family: Alimama ShuHeiTi;
					font-weight: bold;
					font-style: italic;
					color: #E11F00;
				}
				
				.tip {
					font-size: 20rpx;
					font-family: Source Han Sans CN;
					font-weight: 400;
					color: #E11F00;
					width: 160rpx;
					text-align: center;
				}
			}
			
			.step1 {
				left: -100rpx;
				bottom: -75rpx;
			}
			
			.step2 {
				left: 150rpx;
				bottom: -50rpx;
			}
			
			.step3 {
				right: -70rpx;
				bottom: -50rpx;
			}
		}
	}
	
	.invite-history {
		top: -600rpx;
		height: 526rpx;
		
		.empty-content {
			position: absolute;
			top: 105rpx;
			width: 100%;
			text-align: center;
		}
		
		.history-content {
			position: absolute;
			top: 105rpx;
			left: 50%;
			transform: translateX(-50%);
			width: 100%;
			height: 400rpx;
			overflow-y: auto;
			
			.histry-item {
				display: flex;
				align-items: center;
				justify-content: space-between;
				font-size: 20rpx;
				font-family: Source Han Sans CN;
				font-weight: 400;
				color: #333333;
				margin: 0 43rpx;
				box-sizing: border-box;
				margin-bottom: 33rpx;
				
				.nickname {
					width: 100rpx;
					overflow: hidden;
					text-overflow: ellipsis;
					white-space: nowrap;
				}
			}
		}
	}
	
	.invite-btn {
		position: relative;
		top: -500rpx;
		width: 544rpx;
		height: 114rpx;
		background: linear-gradient(0deg, #FE7954 0%, #EB3506 100%);
		box-shadow: 1px 15px 16px 0px rgba(245,89,43,0.18);
		border-radius: 57rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		font-size: 48rpx;
		font-family: Alibaba PuHuiTi;
		font-weight: bold;
		color: #FBF9EB;
	}
}
</style>
