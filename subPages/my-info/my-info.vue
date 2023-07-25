<template>
	<view class="content">
		<!-- <view class="content__item">
			<view class="content__item__left">
				头像
			</view>	
			<view class="content__item__right">
				<view class="content__item__right__image">
					<u--image :showLoading="true" radius="50rpx" src="./static/logo.png" width="70rpx" height="70rpx"></u--image>
				</view>
				<view class="content__item__right__symbol">
					>
				</view>
			</view>	
		</view>
		
		<view class="content__item">
			<view class="content__item__left">
				用户名
			</view>	
			<view class="content__item__right">
				<view class="content__item__right__text">
					白熊
				</view>
				<view class="content__item__right__symbol">
					>
				</view>
			</view>	
		</view>
		
		<view class="content__item">
			<view class="content__item__left">
				性别
			</view>	
			<view class="content__item__right">
				<view class="content__item__right__text">
					女
				</view>
				<view class="content__item__right__symbol">
					>
				</view>
			</view>	
		</view>
		
		
		<view class="content__item">
			<view class="content__item__left">
				关联手机
			</view>	
			<view class="content__item__right">
				<view class="content__item__right__text">
					1234***7890
				</view>
				<view class="content__item__right__symbol">
					>
				</view>
			</view>	
		</view>
		
		
		
		
		<view class="content__item">
			<view class="content__item__left">
				协议和说明
			</view>	
			<view class="content__item__right">
				
				<view class="content__item__right__symbol">
					>
				</view>
			</view>	
		</view> -->

		<view>
			<u-cell-group :border="false">
				<u-cell :border="false">
					<view slot="title" class="title">头像</view>
					<view slot="right-icon" class="disp-flex">
						<button :plain="true" class="avatar flex justify-content-between align-items-center" open-type="chooseAvatar"
							@chooseavatar="chooseAvatar"><u--image shape="circle" :showLoading="true"
								:src="info.headimg?info.headimg:'https://test.cloudskys.cn/wash/static/icon/avatar.png' "
								width="70rpx" height="70rpx" radius="50rpx"></u--image>
							<u-icon name="arrow-right"></u-icon>
						</button>
					</view>
				</u-cell>
				<u-cell :border="false" @click="openModal('nickname')">
					<view slot="title" class="title">用户名</view>
					<view slot="right-icon" class="disp-flex">
						<view class="u-m-r-20 text">{{info.nickname?info.nickname:'微信用户'}}</view>
						<u-icon name="arrow-right"></u-icon>
					</view>
				</u-cell>
				<u-cell :border="false" @click="openModal('base_sex')">
					<view slot="title" class="title">性别</view>
					<view slot="right-icon" class="disp-flex">
						<view class="u-m-r-20 text">{{info.base_sex==1?'男':'女'}}</view>
						<u-icon name="arrow-right"></u-icon>
					</view>
				</u-cell>
				<u-cell :border="false" @click="openModal('phone')">
					<view slot="title" class="title">关联手机</view>
					<view slot="right-icon" class="disp-flex">
						<view class="u-m-r-20 text">{{info.phone?info.phone:'未绑定手机'}}</view>
						<u-icon name="arrow-right"></u-icon>
					</view>
				</u-cell>
				<!-- <u-cell :border="false">
					<view slot="title" class="title">协议与说明</view>
					<view slot="right-icon">
						<u-icon name="arrow-right"></u-icon>
					</view>
				</u-cell> -->
			</u-cell-group>
		</view>
		<view class="button" @click="logOut">
			退出登录
		</view>
		<u-modal :show="show_update" :showCancelButton="true" :title="title" @confirm="confirmUpdate"
			@cancel="show_update=false" @close="show_update=false">
			<view class="">
				<view class="input-name flex align-items-center box-sizing-border-box" v-if="type=='nickname'">
					<input type="nickname" v-model="from.nickname" placeholder="请输入昵称">
				</view>
				<view class="base_sex flex align-items-center justify-content-between" v-if="type=='base_sex'">
					<icons space="15" :name="sex_index==index?'icon-ziyuan':'icon-gouxuan1'" size="44"
						@iconClick="sexChange(item,index)" :label="item.name"
						:color="sex_index==index?'#1A3387':'#999999'" labelColor="#1A3387"
						v-for="(item, index) in base_sex_list" :key="index"></icons>
				</view>
				<view class="input-name flex align-items-center box-sizing-border-box" v-if="type=='phone'">
					<input type="number" v-model="from.phone" maxlength="11" placeholder="请输入手机号">
				</view>
			</view>
		</u-modal>
	</view>

</template>

<script>
	import {
		getUserInfo
	} from "api/public"
	import {
		updateWxInfo
	} from "@/api/user.js"
	import {
		logOut,upload
	} from "@/utils/common.js"
	export default {
		data() {
			return {
				info: {},
				type: '', //类型
				show_update: false, //显示修改用户名
				title: '', //标题名
				sex_index: null, //性别下标
				base_sex_list: [{
					name: '男',
					base_sex: 1
				}, {
					base_sex: 2,
					name: '女'
				}], //性别选择列表
				from: {
					nickname: '',
					base_sex: '',
					phone: '',
					headimg: ''
				}
			}
		},

		onLoad(option) {
			this.load()
		},

		methods: {
			load() {
				getUserInfo().then(res => {
					this.from.base_sex=res.data.base_sex
					this.info = Object.assign({}, res.data, {
						phone: res.data.phone ? res.data.phone.slice(0, 3) + '****' + res.data.phone.slice(
							7) : ""
					})
				})
			},
			/*打开模态框*/
			openModal(type) {
				if (type == 'nickname') {
					this.title = "修改用户昵称"
				} else if (type == 'base_sex') {
					this.title = "修改用户性别"
				} else if (type == 'phone') {
					this.title = "修改用户手机"
				}
				this.type = type
				this.show_update = true
			},
			/*修改用户信息确认*/
			confirmUpdate() {
				let p = /^[1][3-9]\d{9}$|^([6|9])\d{7}$|^[0][9]\d{8}$|^[6]([8|6])\d{5}$/
				if (this.type == 'nickname' && !this.from.nickname) {
					this.errToast('请填写昵称', () => {})
					return false
				} else if (this.type =='base_sex' && !this.from.base_sex) {
					this.errToast('请选择性别', () => {})
					return false
				} else if (this.type == 'phone' && !this.from.phone) {
					this.errToast('请选输入手机号', () => {})
					return false
				} else if (this.type == 'phone' && !(p.test(this.from.phone))) {
					this.errToast("请输入正确的手机号!",() => {})
					return false
				} else {
					this.updateWxInfo()
				}
			},
			/*性别选择*/
			sexChange(item, index) {
				this.sex_index = index
				this.from.base_sex = item.base_sex
			},
			/*选择头像回调*/
			chooseAvatar(e) { 
				this.uploadAvatar(e.detail.avatarUrl);
			},
			/*上传头像*/
		 async	uploadAvatar(img){
				let res= await upload(img)
				this.from.headimg=res
				this.updateWxInfo()
			},
			/*修改用户信息接口*/
			async updateWxInfo() {
				let res = await updateWxInfo(this.filter(this.from))
				if (res.code == 1) {
					this.show_update = false
					this.successToast('修改成功', () => {
						this.info.nickname = res.data.nickname
						this.info.base_sex = res.data.base_sex
						this.info.phone = res.data.phone
						this.from.base_sex=res.data.base_sex
						this.info.base_birthday = res.data.base_birthday
						this.info.headimg = res.data.headimg
					}, 2000)
				}
			},
			logOut() { //退出登录
				logOut()
			}
		}
	}
</script>

<style lang="scss" scoped>
	.content {
		height: 100%;
		background-color: white;
	}

	.base_sex {
		width: 200rpx;
	}

	.input-name {
		width: 600rpx;
		border: #676767 solid 1rpx;
		height: 90rpx;
		border-radius: 48rpx;
		padding: 0 20rpx;
	}

	.title {
		height: 72rpx;
		line-height: 72rpx;
		font-size: 28rpx;
		color: #676767;
	}

	.text {
		color: #333333;
		font-size: 28rpx;
	}
   .avatar {
  
   	border: none !important;
   	padding: 0 !important;
   	margin: 0;
   }
	.button {
		background: #0B27A4;
		border-radius: 46rpx;
		color: $wash-color-white;
		padding: 24rpx 220rpx;
		position: fixed;
		left: 104rpx;
		top: 1200rpx;
		font-size: 28rpx;
	}
</style>