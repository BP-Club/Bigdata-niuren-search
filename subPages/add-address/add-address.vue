<template>
	<view class="container">
		<u--form :labelStyle="labelStyle" labelWidth="66">
			<u-form-item label="用户名">
				<u--input v-model="formData.name" border="none" placeholder="用于取货时对您的称呼"></u--input>
				<view slot="right" class="font-color f-s-28 disp-flex" @click="wxReport">
					<icons name="icon-weixindaoru" size="36" color="#58AA46"></icons>
					<text class="u-m-l-4">微信导入</text>
				</view>
			</u-form-item>
			<u-form-item label="性别">
				<view class="sex">
					<u-radio-group v-model="formData.sex" placement="row">
						<u-radio labelColor="#333333" iconSize="32rpx" labelSize="28rpx" label="先生" :name="1"></u-radio>
						<u-radio labelColor="#333333" iconSize="32rpx" labelSize="28rpx" label="女士" :name="2"></u-radio>
					</u-radio-group>
				</view>
			</u-form-item>
			<u-form-item label="手机号">
				<u--input v-model="formData.phone" type="number" border="none" placeholder="请输入手机号"></u--input>
			</u-form-item>
			<!--u-form-item label="城市">
				<picker class="picker" mode="region" @change="changeAddress">
					<view v-if="formData.area" class="address">{{formData.province}}-{{formData.city}}-{{formData.area}}
					</view>
					<view v-else class="placeholder">请选择地址</view>
				</picker>
			</u-form-item-->
			<u-form-item label="详细地址">

				<view class="flex align-items-center justify-content-between" @click="chooseLocation">
					<u--input v-model="formData.address" border="none" placeholder="请输入详细地址"></u--input>
					<icons name="icon-dizhi2" size="32" ></icons>
				</view>
			</u-form-item>
			<u-form-item label="门牌号">
				<u--input v-model="formData.number" border="none" placeholder="如：5号楼208室"></u--input>
			</u-form-item>
			<u-form-item label="默认地址">
				<u-switch size="20" activeColor="#0B27A4" :activeValue="1" :inactiveValue="0" v-model="formData.type">
				</u-switch>
			</u-form-item>
		</u--form>
		<view class="save-btn" @click="save">保存</view>
	</view>
</template>

<script>
	import {
		addAddress,
		getAddressByCode,
		editAddress
	} from "api/address"
	import {
		getLocation,
		localStorage,
	} from "@/utils/common.js"
	export default {
		data() {
			return {
				formData: {
					name: "",
					tag: "",
					province: "",
					city: "",
					area: "",
					address: "",
					type: "",
					lat: "",
					lng: "",
					code: "",
					phone: '',
					sex: ''
				},

				labelStyle: {
					fontSize: "28rpx",
					color: "#676767"
				}
			}
		},
		onLoad(option) {
			if (option.code) {
				this.code = option.code
				uni.setNavigationBarTitle({
					title: "编辑地址"
				})
				this.load()
			}
			let that = this
			that.getLocation(true)
		},
		methods: {
			chooseLocation() { //打开地图选择位置
				let that = this
				uni.chooseLocation({
					success: function(res) {
                        console.log('address',res)
						that.formData.address = res.address
						that.formData.lat = res.latitude   //经度
						that.formData.lng = res.longitude //维度
					}
				});
			},
			async getLocation(is) { //获取定位信息
				let getLocations = localStorage('location');
				if (getLocations) {
					let l = JSON.parse(getLocations)
					if (l.result) {

						this.formData.lat = l.latitude
						this.formData.lng = l.longitude
					} else {
						uni.removeStorageSync(location);
						let updatePositioning = await getLocation(is);
						this.formData.lat = updatePositioning.latitude
						this.formData.lng = updatePositioning.longitude
						localStorage('location', JSON.stringify(updatePositioning), 1000)
					}
				} else {
					let location = await getLocation(is);

					this.formData.lat = location.latitude
					this.formData.lng = location.longitude
					localStorage('location', JSON.stringify(location), 1000)
				}

			},
			async load() {
				let res = await getAddressByCode({
					code: this.code
				})
				if (res.code === 1) {
					this.formData = res.data
					this.formData.address = this.formData.province + this.formData.city + this.formData.area + this.formData.address
 				}

			},
			wxReport() {
				let that = this
				uni.getUserInfo({
					success(res) {
						that.formData.name = res.userInfo.nickName
					},
					fail() {
						that.toast("获取失败", 1500)
					}
				})
			},
			changeAddress({
				detail
			}) {
				this.formData.province = detail.value[0]
				this.formData.city = detail.value[1]
				this.formData.area = detail.value[2]
			},
			async saveAddress() { //保存接口
				let res = await addAddress(this.formData)
				if (res.code == 1) {
					this.successToast("保存成功", () => {
						setTimeout(function() {
							uni.navigateBack({
								delta: 1
							});
						}, 2000)

					}, 1500)
				} else {
					this.errToast(res.info, () => {})
				}
			},
			verification() { //校验表单内容
				let p = /^[1][3-9]\d{9}$|^([6|9])\d{7}$|^[0][9]\d{8}$|^[6]([8|6])\d{5}$/
				if (!this.formData.name) {
					this.errToast('请填写收获名', () => {})
					return false
				} else if (!this.formData.sex) {
					this.errToast('请选择性别', () => {})
					return false
				} else if (!this.formData.phone) {
					this.errToast('请输入电话', () => {})
					return false
				} else if (!(p.test(this.formData.phone))) {
					this.errToast("请输入正确的手机号!", () => {})
					return false
				// } else if (!this.formData.province && !this.formData.city && !this.formData.area) {
				// 	this.errToast('请选择城市', () => {})
				// 	return false
				 } else if (!this.formData.address) {
					this.errToast('请填写详细地址', () => {})
					return false
				} else {
					return true
				}
			},
			async editAdd() { //编辑接口
				let res = await editAddress(this.formData)
				if (res.code == 1) {
					this.successToast("保存成功", () => {
						setTimeout(function() {
							uni.navigateBack({
								delta: 1
							});
						}, 2000)

					}, 1500)
				} else {
					this.errToast(res.info, () => {})
				}
			},
			save() { //按钮
				if (!this.code) { //新增
					if (this.verification()) {
						this.saveAddress()
					}
				} else {
					this.editAdd()

				}
			}
		}
	}
</script>

<style lang="scss" scoped>
	.container {
		padding: 0 32rpx 0;
	}

	/deep/.uni-input-placeholder {
		font-size: 28rpx !important;
	}

	.font-color {
		color: #676767;
	}

	.font-family {
		font-family: Helvetica;
	}

	.sex {
		/deep/ .u-radio:nth-child(2) {
			margin-left: 64rpx;
		}
	}

	.tag-class {
		height: 48rpx;
		box-sizing: border-box;
		border-radius: 24rpx;
		line-height: 48rpx;
		font-size: 24rpx;
		padding: 0 40rpx 0;
		margin-right: 20rpx;
	}

	.tag {
		background: #ffffff;
		color: #676767;
	}

	.active-tag {
		background: #0B27A4;
		color: white;
	}

	.save-btn {
		width: 550rpx;
		height: 88rpx;
		background: #0B27A4;
		border-radius: 46rpx;
		color: white;
		font-size: 28rpx;
		text-align: center;
		line-height: 88rpx;
		margin: 400rpx auto 160rpx;
	}

	.picker {
		height: 100%;

		.placeholder {
			height: 100%;
			color: #c0c4cc;
			font-size: 28rpx;
		}
	}

	/deep/ .u-form-item__body__right__content__slot {
		height: 100%;
	}

	/deep/.input-placeholder {
		font-size: 28rpx;
	}
</style>