<template>
	<view class="content">
		<view class="top-block">
			<button :plain="true" class="avatar" open-type="chooseAvatar" @chooseavatar="chooseavatar"><u--image
					shape="circle" :showLoading="true"
					:src="info.headimg?info.headimg:'https://test.cloudskys.cn/wash/static/icon/avatar.png' "
					width="120rpx" height="120rpx"></u--image></button>
			<button open-type="getUserInfo" :plain="true" @getuserinfo="getNickName" class="wx-name">
				<view class="top-block-right">
					<view class="block-right-name">{{info.nickname?info.nickname:'微信用户'}}</view>
				</view>
			</button>

		</view>
		<view class="u-p-t-32">
			<u-cell-group :border="false">
				<u-cell :border="false" @click="toShoppingCart(0)">
					<u--image slot="icon" :src="baseUrl+'/wash/static/icon/dingdan.png'" width="46rpx"
						height="44rpx"></u--image>
					<view slot="title" class="title">订单</view>
					<view slot="right-icon" class="disp-flex order">
						<text class="u-m-r-10 f-s-30">全部</text>
						<u-icon name="arrow-right"></u-icon>
					</view>
				</u-cell>
				<view class="u-m-l-82 u-m-r-82 u-m-t-40 u-m-b-40 disp-flex justify-between">
				
					<view class="position-relative" v-for="(item,index) in order_module" :key="index">
						<icons @iconClick="toShoppingCart(item.index)" :label="item.name" space="16"
							labelColor="#333333" labelPos="bottom" :name="item.icon" size="56" color="#141316"></icons>
						<view class="position-absolute unread">
							<u-badge bgColor="red" color="#fff" shape=""  max="99" :value="item.num"
								:max="99" ></u-badge>
						</view>
						
					</view>


				</view>
				<u-cell :border="false" @click="navigateTo('/subPages/meet/meet')">
					<u--image slot="icon" :src="baseUrl+'/wash/static/icon/youhuijuan.png'" width="46rpx"
						height="44rpx"></u--image>
					<view slot="title" class="title">优惠券</view>
					<view slot="right-icon">
						<u-icon name="arrow-right"></u-icon>
					</view>
				</u-cell>
				<u-cell :border="false" @click="toAddressList">
					<u--image slot="icon" :src="baseUrl+'/wash/static/icon/dizhiguanli.png'" width="46rpx"
						height="44rpx"></u--image>
					<view slot="title" class="title">地址管理</view>
					<view slot="right-icon">
						<u-icon name="arrow-right"></u-icon>
					</view>
				</u-cell>
				<u-cell :border="false" @click="toMyInfo">
					<u--image slot="icon" :src="baseUrl+'/wash/static/icon/guanlizhongxin.png'" width="46rpx"
						height="44rpx"></u--image>
					<view slot="title" class="title">管理中心</view>
					<view slot="right-icon">
						<u-icon name="arrow-right"></u-icon>
					</view>
				</u-cell>
			    <view class="">
			    	<button open-type="contact" :plain="true" class="contact flex align-content-center justify-content-between"><icons name="icon-kefu" color="#1A3387" size="46" label="客服" labelColor="#333" space="10" labelSize="28"></icons><u-icon name="arrow-right"></u-icon> </button>
			    </view>
			</u-cell-group>
		</view>
		<view class="flex justify-center log-out">
			<btn-button title="退出登录" size="32" @click="logOut" height="80" width="600" radius="48rpx" bntColor="#1A3387"
				color="white"></btn-button>
		</view>

	</view>
</template>

<script>
	import {
		getUserInfo
	} from "api/public"
	import {
		logOut
	} from '@/utils/common.js'
	import {
		updateWxInfo
	} from "@/api/user.js"
	import {
		orderDots
	} from "@/api/goods.js"
	export default {
		data() {
			return {
				baseUrl: getApp().globalData.baseUrl,
				info: null, //个人信息
				
				order_module: [{
						icon: 'icon-daiquhuo',
						index: '1',
						name: '待支付',
						num: ''
					},
					{
						icon: 'icon-daiquhuo',
						index: '2',
						name: '待取货',
						num: ''
					},
					{
						icon: 'icon-rukuzhong',
						index: '3',
						name: '入库中',
						num: ''
					}, {
						icon: 'icon-qingxizhong',
						index: '4',
						name: '清洗中',
						num: ''
					}, {
						icon: 'icon-songhuozhong',
						index: '5',
						name: '送货中',
						num: ''
					}
				]
			}
		},
		onLoad(option) {

		},
		onShow() {
			this.getUserInfo();
			this.orderDots()
		},
		methods: {
			logOut() {
				logOut()
			},
			chooseavatar(e) { //选择头像回调
				this.updateWxInfo({
					headimg: e.detail.avatarUrl
				}, e.detail.avatarUrl)

			},
			async orderDots() {
				let res = await orderDots();
				if (res.code == 1) {
					for (let i = 0; i < res.data.length; i++) {
						if (res.data[i].status == 0) {
							this.order_module[0].num = res.data[i].dotNums
						} else if (res.data[i].status == 1) {
							this.order_module[1].num = res.data[i].dotNums
						} else if (res.data[i].status == 2) {
							this.order_module[2].num = res.data[i].dotNums
						} else if (res.data[i].status == 3) {
							this.order_module[3].num = res.data[i].dotNums
						} else if (res.data[i].status == 4) {
							this.order_module[4].num = res.data[i].dotNums
						}
					}

				}

			},
			async updateWxInfo(data, ava) { //修改头像
				let res = await updateWxInfo(data)
				if (res.code == 1) {
					this.info.headimg = data.headimg
				}
			},
			getNickName(e) {
				console.log(e, '杀杀杀')
			},
			async getUserInfo() { //获取用户信息
				let res = await getUserInfo();
				uni.setStorageSync('user_info', res.data);
				this.info = res.data;
			},
			toAddressList() { //跳转到地址列表
				uni.navigateTo({
					url: "/subPages/address-list/addres-list"
				})
			},
			toMyInfo() { //跳转到个人资料
				uni.navigateTo({
					url: "/subPages/my-info/my-info"
				})
			},
			toShoppingCart(index) { //跳转到订单列表
				uni.navigateTo({
					url: "/subPages/my-order/my-order?index=" + index
				})
			}
		}
	}
</script>

<style lang="scss">
	.content {
		height: 100vh;
		background-color: white;
	}
   .contact{
	   border: none !important;
   }
	.log-out {
		margin: 90rpx 0 0 0;
	}
    .unread{
		top: 0;
		right: 0;
	}
	.avatar {
		width: 120rpx;
		height: 120rpx;
		border: none !important;
		padding: 0 !important;
		margin: 0;
	}

	.wx-name {
		border: none !important;
		padding: 0 !important;
		margin: 0;
	}

	.top-block {
		@include flex(row);
		width: 750rpx;
		height: 320rpx;
		background-color: $wash-color-darkblue;
		align-items: center;
		padding-left: 60rpx;
	}

	.top-block-right {
		color: $wash-color-white;
		padding-left: 24rpx;
	}

	.block-right-btn {
		border-radius: 32rpx;
		border: 0.5rpx solid #FFFFFF;
		font-size: 24rpx;
		padding: 5rpx 16rpx;
	}

	.block-right-name {
		text-align: center;
		font-size: 34rpx;
		padding-bottom: 10rpx;
	}

	.title {
		color: #333;
	}

	.order {
		color: #999999;
	}
</style>