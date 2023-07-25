<template>
	<view>
		<view class="cover">
			<u--image :showLoading="true" radius="20rpx" src="" width="453rpx" height="439rpx" @click="click">
			</u--image>
		</view>
		<view class="center">
			<view class="center-btn1">手机号登录</view>
			<view class="center-btn2" @click="wxLogin">微信登录</view>
		</view>
		<view class="text flex align-center">
			<view class=" " @click="readChange">
				<icons :name="is_read?'icon-ziyuan':'icon-gouxuan1'" space="12" size="32"
					:color="is_read?'#0B27A4':'#D1D1D1'" labelSize="22" label="我已阅读并同意"></icons>
			</view>
			<text @click="navigateTo('/subPages/agreement-details/agreement-details?code=conceal')" class="font-size-22 agree-on">《用户隐私协议》</text>
			<text @click="navigateTo('/subPages/agreement-details/agreement-details?code=about')" class="font-size-22 agree-on">《关于我们》</text>
		</view>

	</view>
</template>

<script>
	import {
		wxLoginPost
	} from 'api/user';
	import {
		getUserInfo
	} from "api/public"
	export default {
		data() {
			return {
				is_read: false,
				agree_show: false,
				code: '',
				share_uid:''
			}
		},
		onLoad(o) {
			let that = this;
			that.getCode();
			 let share_uid = uni.getStorageSync('share_uid');
			 if(share_uid){
				 this.share_uid=share_uid
			 }
			 
		},
		methods: {
			readChange() {
				this.is_read = !this.is_read
			},
			async wxLogin() {
				 let share_uid = uni.getStorageSync('share_uid');
				if (!this.is_read) {
					return this.toast('请阅读并同意协议', 1500)
				}
				let res = await wxLoginPost({
					code: this.code,
					share_uid:this.share_uid
				});
				let that = this
				
				if (res.code == 1) {
					uni.setStorageSync("token", res.data.token)
					this.successToast('登录成功', () => {
						setTimeout(function() {
							that.getUserInfo()
							that.switchTab('/pages/index/index')
						}, 2500)
					},2000)

				} else {
					return this.errToast(res.info, () => {})
				}
			},
			async getUserInfo() { //获取用户信息
				let res = await getUserInfo();
				uni.setStorageSync('user_info', res.data);
				
			},
			getCode() { //获取code
				let that = this
				uni.login({
					provider: 'weixin',
					success: function(loginRes) {
						that.code = loginRes.code
					}
				});
			},
			agreementClick(type) {
				this.agree_show = true
				if (type == 1) {

				}
			},
			agreementClose() {
				this.agree_show = false
			},

		}
	}
</script>

<style lang="scss">
	.cover {
		padding: 96rpx 140rpx 92rpx 156rpx;

	}

	.agree-on {
		color: #0B27A4;
	}

	.center {
		padding: 0 90rpx;
		font-size: 27rpx;
	}

	.center-btn1 {
		background: #0B27A4;
		border-radius: 46rpx;
		color: $wash-color-white;
		padding: 24rpx 220rpx;
	}

	.center-btn2 {
		background: #58AA46;
		border-radius: 46rpx;
		color: $wash-color-white;
		padding: 24rpx 220rpx;
		margin-top: 28rpx;
	}

	.text {
		padding: 200rpx 80rpx;
		@include flex(row);

	}

	.contert {
		height: 800rpx;
	}
</style>
