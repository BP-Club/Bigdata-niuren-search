<template>
	<view class="container">
		<view class="empty u-p-t-100" v-if="list.length<=0">
			<u--image :showLoading="true" src="/static/images/empty.png"></u--image>
			<view class="tip">空空如也~~</view>
		</view>
		<u-cell-group :border="false">
			<u-swipe-action>
				<u-swipe-action-item v-for="(item,i) in list" :key="item.code" :show="item.show" :options="options"
					@click="swipeClick($event,item)">
					<u-cell @click="selectAdd($event,item)">
						<view slot="title" class="disp-flex">
							<view class="list-name f-s-32">{{item.name}} {{item.phone}}</view>
							<view v-if="item.type===1" class="static f-s-24 u-m-l-12">默认</view>
						</view>
						<view slot="label" class="address f-s-28 u-m-t-24">
							{{item.province}}{{item.city}}{{item.area}}{{item.address}}{{item.number}}
						</view>
						<view slot="right-icon" @click.stop="toAddressEdit(item)">
							<icons name="icon-bianji" size="34" color="#9EA5B8"></icons>
						</view>

					</u-cell>
				</u-swipe-action-item>
			</u-swipe-action>
		</u-cell-group>
		<view class="to-add">
			<btn-button title="去添加地址" @click="navigateTo('/subPages/add-address/add-address')"
				color="rgba(255, 255, 255, 1)" size="28" width="640" height="88" radius="36rpx" bntColor="#0B27A4">
			</btn-button>
		</view>

	</view>
</template>

<script>
	import {
		addresslist,
		deleteAddress,
		setStaticAdddress
	} from "api/address"
	export default {
		data() {
			return {
				show: false,
				options: [{
						text: '设为默认',
						style: {
							backgroundColor: '#F7F9FC',
							color: "#70798C",
							fontSize: "24rpx"
						}
					},
					{
						text: "删除",
						style: {
							backgroundColor: '#D41A1E',
							fontSize: "24rpx"
						}
					}
				],
				params: {},
				list: [],
				add_type:null,//订单选择地址类型 上门收件receiving_add 返回用户地址return_add
			}
		},
		onLoad({add_type}) {
			if(add_type){
				this.add_type=add_type
			}
		},
		onShow() {
			let that = this
			that.reload()
		},

		methods: {
			load() {
				this.reload()
			},
			async reload() {//获取地址列表
				this.list = [] //解决滑动后无法关闭的问题
				let res = await addresslist(this.params)
				if (res.data.list) {
					this.list = res.data.list
				}

			},

			async setStatic(item) { //设置默认地址
				let res = await setStaticAdddress({
					code: item.code,
					type: 1
				})
				if (res.code === 1) {
					this.successToast("设置成功", () => {
						this.reload()
					})

				} else {
					this.errToast(res.info, () => {})
				}
			},
			async delAddress(item) {//删除接口
				let res = await deleteAddress({
					code: item.code
				})
				if (res.code == 1) {
					this.successToast("删除成功", () => {
						this.reload()
					})
				} else {
					this.errToast(res.info, () => {})
				}

			},
			swipeClick(event, item) {//滑动事件
				item.show = false
				
				if (event.index === 0) { //设置为默认地址
					this.setStatic(item)
				} else if (event.index === 1) { //删除
					this.delAddress(item)
				}
			},
			selectAdd(event, item){
				let add={
					add_info:item,
					add_type:this.add_type
				}
				uni.$emit('confirmAdd',add)
				uni.navigateBack({
					delta: 1
				});
			},
			toAddressEdit(item) {//编辑跳转
				uni.navigateTo({
					url: "/subPages/add-address/add-address?code=" + item.code
				})
			}
		}
	}
</script>

<style lang="scss" scoped>
	.container {
		background-color: white;
		height: 100vh;
	}

	.to-add {
		position: fixed;
		bottom: 30rpx;
		left: 0;
		right: 0;
		margin: auto;
		width: 640rpx;
	}

	.empty {
		display: flex;
		justify-content: center;
		flex-wrap: wrap;

		.tip {
			width: 100%;
			color: #999999;
			text-align: center;
			font-size: 24rpx;
		}
	}

	.list-name {
		color: #2F3B4F;
	}

	.static {
		border-radius: 4rpx;
		color: #D41A1E;
		background-color: rgba(212, 26, 30, .12);
		line-height: 40rpx;
		padding: 0 16rpx;
	}

	.address {
		color: #9EA5B8;
	}
</style>
