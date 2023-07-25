<template>
	<view class="container">

		<view class="header">
			<view class="shop-box box-sizing-border-box flex align-items-center justify-content-between"
				v-if="store_info">
				<view class="flex shop box-sizing-border-box flex-wrap align-content-between">
					<view class="flex shop-info align-items-center justify-content-between">
						<view class="">
							<text class="shop-text fw500">店铺名称</text> <text
								class="shop-name fw500">{{store_info.name}}</text>
						</view>
						<view class="distance" v-if="store_info.distance">
							{{store_info.distance}}
						</view>
					</view>
					<view class="shop-info">
						<icons name="icon-shijian" size="38" :label="store_info.work_time" space="10"
							labelColor="#999999" labelSize="24" color="#999999"></icons>
					</view>
					<view class="flex shop-info align-items-center justify-content-between">
						<icons name="icon-dizhi2" size="38" :label="store_info.address" labelColor="#999999"
							labelSize="24" space="10" color="#999999"></icons>
						<text class="icon-navigation">地图导航</text>
					</view>
				</view>
				<view class="" @click="navigateTo('/subPages/stores/stores')">
					<icons name="icon-youbian" size="44" color="#999999"></icons>
				</view>

			</view>
			<view class="shop-box box-sizing-border-box flex  align-items-center justify-content-between"
				v-if="!store_info" @click="navigateTo('/subPages/stores/stores')">
				<text>选择门店</text>
				<icons name="icon-youbian" size="44" color="#999999"></icons>
			</view>

			<view class=" receiving box-sizing-border-box flex align-items-center">
				<view class="receiving-left">收件方式</view>
				<view class="receiving-right flex align-items-center justify-between">
					<icons space="15" :name="receiving_index==index?'icon-ziyuan':'icon-gouxuan1'" size="44"
						@iconClick="receivingChange(item,index)" :label="item.name"
						:color="receiving_index==index?'#1A3387':'#999999' " labelColor="#1A3387"
						v-for="(item, index) in receiving_list" :key="index"></icons>
				</view>
			</view>
			<view class="to-store-date box-sizing-border-box flex align-items-center" @click="carryToStore('take_date')"
				v-show="receiving_index==0">
				<view class="to-store-date-left">
					<icons name="icon-shijian2" size="44" color="#1A3387"></icons>
				</view>
				<view class="to-store-date-right flex align-items-center justify-between">
					<view class="address">
						<view class="text">自带到店时间</view>
						<view class="tip">{{form.take_date?form.take_date:'点击选择自带到店时间 '}}</view>
					</view>
					<icons name="icon-youbian" size="44" color="#999999"></icons>
				</view>

			</view>
			<view class="to-store-date box-sizing-border-box flex align-items-center"
				@click="navigateTo('/subPages/address-list/addres-list?add_type=receiving_add')" :border="false"
				v-show="receiving_index==1">
				<view class="to-store-date-left">
					<icons name="icon-shijian2" size="44" color="#1A3387"></icons>
				</view>
				<view class="to-store-date-right flex align-items-center justify-between">
					<view class="address">
						<view class="text"> {{take_address?'上门收件地址':'添加上门收件地址'}}</view>
						<view class="tip">{{take_address?take_address.address:'您还没有添加地址' }}</view>
						<view class="tip" v-if="take_address">{{take_address.name}} {{take_address.phone }}</view>
					</view>
					<icons name="icon-youbian" size="44" color="#999999"></icons>
				</view>
			</view>
			<view class="receiving box-sizing-border-box flex align-items-center">
				<view class="receiving-left">归还方式</view>
				<view class="receiving-right flex align-items-center justify-between">
					<icons space="15" :name="return_index==index?'icon-ziyuan':'icon-gouxuan1'" size="44"
						@iconClick="returnChange(item,index)" :label="item.name"
						:color="return_index==index?'#1A3387':'#999999'" labelColor="#1A3387"
						v-for="(item, index) in return_list" :key="index"></icons>
				</view>
			</view>

			<!-- <view class="to-store-date box-sizing-border-box flex align-items-center" :border="false"
				@click="carryToStore('return_date')" v-show="return_index==0">
				<view class="to-store-date-left">
					<icons name="icon-shijian2" size="44" color="#1A3387"></icons>
				</view>
				<view class="to-store-date-right flex align-items-center justify-between">
					<view class="address">
						<view class="text">到店取件时间</view>
						<view class="tip">{{form.return_date?form.return_date:'点击选择自带到店时间' }}</view>
					</view>
					<icons name="icon-youbian" size="44" color="#999999"></icons>
				</view>
			</view> -->
			<view class="to-store-date box-sizing-border-box flex align-items-center"
				@click="navigateTo('/subPages/address-list/addres-list?add_type=return_add')" :border="false"
				v-show="return_index==1">
				<view class="to-store-date-left">
					<icons name="icon-shijian2" size="44" color="#1A3387"></icons>
				</view>
				<view class="to-store-date-right flex align-items-center justify-between">
					<view class="address">
						<view class="text">{{send_address?'归还地址':'添加归还地址'}}</view>
						<view class="tip">{{send_address?send_address.address:'您还没有添加地址' }}</view>
						<view class="tip" v-if="send_address">{{send_address.name}} {{send_address.phone }}</view>
					</view>
					<icons name="icon-youbian" size="44" color="#999999"></icons>
				</view>
			</view>

		</view>


		<view class="commodity u-p-t-32">
			<view class="activity f-s-28">
				<view class="activity-text">活动：衣鞋类目任洗三件</view>
				<view class="activity-price">￥{{form.total_amount}}</view>
			</view>
			<u-cell-group class="u-p-l-2 u-p-r-2" :border="false">
				<u-cell class="" :border="false" v-for="(item,i) in commodity_list" :key="i">
					<view slot="icon">
						<u--image :src="item.cover" radius="5" width="120rpx" height="120rpx"></u--image>
					</view>
					<view slot="title" class="list">
						<view class="list-name">{{item.name}}</view>
						<view class="list-price">
							<text class="price-symbol">￥</text>
							<text class="price">{{item.price_selling}}</text>
							<text class="original-price">￥{{item.price_market}}</text>
						</view>
					</view>
					<view slot="right-icon" class="sum">x{{item.good_num}}</view>
				</u-cell>
			</u-cell-group>
			<u-cell-group class="u-p-l-2 u-p-r-2" :border="false">
				<u-cell :titleStyle="titleStyle" title="商品总额" :border="false">
					<view slot="right-icon" class="amount">￥{{form.total_amount}}</view>
				</u-cell>
				<!--修改代码++++++++++++++-->
				<u-cell :titleStyle="titleStyle" title="优惠券" :border="false" v-if="coupon.use_count > 0">
					<view @click="toMyCouponPage()" slot="right-icon" class="coupon-use f-s-28" >可使用{{coupon.use_count}}张优惠卷</view>
				</u-cell>
				<!--修改代码++++++++++++++-->
				<u-cell :titleStyle="titleStyle" title="优惠券" :border="false" v-if="coupon.use_count == 0">
					
					<view slot="right-icon" class="coupon f-s-28" >暂无优惠</view>
				
				</u-cell>
				<!--新增代码++++++++++++++-->
				<view  class="list item-place flex" v-for="(couponitem,i) in selected_coupons" :key="i">
					<view slot="right-icon" class="coupon-list f-s-24">{{couponitem.name}}</view>
					<view class="list-price u-p-r-30">
						<text class="price-symbol">￥</text>
						<text class="price">-{{couponitem.reduce_price}}</text>
					</view>
				</view>
				
				<u-cell :titleStyle="titleStyle" title="上门收件费" v-if="receiving_index==1" :border="false">
					<view slot="right-icon" class="amount">￥{{form.take_vistit_fee}}</view>
				</u-cell>

				<u-cell :titleStyle="titleStyle" title="归还配送费" v-if="return_index==1" :border="false">
					<view slot="right-icon" class="amount">￥{{form.send_vistit_fee}}</view>
				</u-cell>

				<u-cell class="" title="备注">
					<view slot="right-icon">
						<view class="remark-input">
							<u--input placeholder="填写备注信息" border="none" class="remark-input" v-model="form.remark">
							</u--input>
						</view>

					</view>
				</u-cell>
				<view class="total f-s-28">
					<text class="u-m-r-20">共{{form.total_num}}件商品</text>
					<text v-if="reduce_dis_price > 0">已优惠：<text class="coupon">￥-{{reduce_dis_price}}</text> </text>
					<text class="u-m-l-20">合计：￥{{form.total_amount}}</text>
				</view>
			</u-cell-group>
		</view>

		<view class="u-p-b-220"></view>
		<!--用于撑开位置-->
		<view class="pay u-p-b-64">
			<view class="pay-money">需支付金额：￥{{total?total:form.total_amount}}</view>
			<view class="submit-btn" @click="submitBtn">提交订单</view>
		</view>
		<u-calendar :show="show_calendar" :round="16" @close="show_calendar=false" mode="single" @confirm="dateConfirm">
		</u-calendar>
	</view>
</template>

<script>
	import {
		submitSettle,
		getBasketList
	} from "api/basket.js"
	import {
		orderAdd,
		getPayment,
		getAddDeliveryFee,
		getDefaultReturnDate,
	} from "api/stores.js"
	import {
		wxPay,
		getLocation,
		localStorage
	} from "@/utils/common.js"
	import {
		getDefaultAddress
	} from "@/api/address.js"
	import {
		getSelectedStore
	} from "@/api/goods.js"
	import {
		getLately
	} from 'api/stores';
	export default {
		data() {
			return {
				show_calendar: false, //打开日历
				receiving_index: null, //收件方式下标
				date_type: '', //日期打开类型
				receiving_list: [{ //收件方式
						name: '自带到店',
						take_type: 1
					},
					{
						name: '上门收取',
						take_type: 2
					},
				],
				total: '', //总价
				return_index: null, //归还方式下标
				return_list: [{ //归还方式
						name: '到店自取',
						return_type: 1
					},
					{
						name: '配送到家',
						return_type: 2
					},
				],
				commodity_list: [], //购物车信息
				store_info: null, //门店信息
				basket_ids: [],
				take_address: null, //上门收件地址
				send_address: null, //归还到家地址
				location: null, //定位信息
				default_address: null, //默认地址
				form: {
					take_type: '', //收件方式
					return_type: '', //归还的方式
					take_address_id: '', //上门收件地址
					send_address_id: '', //归还地址id
					remark: '', //备注
					take_date: '', //上门收件时间
					// return_date: '', //到店自提时间
					basket_ids: '', //购物车id
					total_num: '', //商品总数量
					total_amount: '', //商品总额
					vistit_fee: '',
					take_vistit_fee: '', //上门收件费用
					send_vistit_fee: '', //归还邮费
					store_id: '', //门店id
					total: '', //总价
					discount_amount: '' ,//优惠金额,
				},
				//新增代码+++++++++++++++++++，加入选择的优惠卷id
				coupon:{},
				used_coupon_ids:[],
				selected_coupons:[],//选中的优惠卷
				reduce_dis_price: 0,//减少的优惠价钱
			}
		},
		onLoad() {
			this.basket_ids = uni.getStorageSync("basket_id")
			this.getCarData(this.basket_ids)
			this.getTacitlyApproveAddress();
			this.getSelectedStore()

		},
		onShow() {
			let that = this
			uni.$on('confirmAdd', function(add) {
				if (add) {
					if (add.add_type == 'receiving_add') {
						that.take_address = add.add_info
						that.form.take_address_id = add.add_info.id


					} else {
						that.send_address = add.add_info
						that.form.send_address_id = add.add_info.id

					}
					that.getDeliveryFee(add.add_info.id, that.store_info.id, add.add_type)
				}
			})
			uni.$on('selectShop', function(shop) {
				that.store_info = shop
				that.form.store_id = shop.id
				that.getFeeData()
			})
			//新增代码+++++++++++++++++++，加入选择的优惠卷id
			uni.$on('selectCoupon', function(item) {
				if( that.used_coupon_ids.indexOf(item.id) == -1 ){
				    that.used_coupon_ids.push(item.id)
					that.selected_coupons.push(item)
					that.form.total_amount = that.form.total_amount - item.reduce_price
					if(that.total){
						that.total = that.total - item.reduce_price
					}
				}
			})
			
			//新增代码+++++++++++++++++++，移除取消的优惠卷id
			uni.$on('removeSelectCoupon', function(item) {
				that.used_coupon_ids.forEach((uitem,index,arr) => {
				    if(uitem === item.id){
				        arr.splice(index,1)
				    }
				});
				that.selected_coupons.forEach((sitem,index,arr) => {
				    if(sitem.id === item.id){
				        arr.splice(index,1)
						that.form.total_amount = that.form.total_amount + item.reduce_price
						if(that.total){
							that.total = that.total + item.reduce_price
						}
				    }
				});
			})
			
		},
		methods: {
			/*获取定位信息*/
			async getLocation(is) {
				let getLocations = localStorage('location');
				if (getLocations) {
					let l = JSON.parse(getLocations)
					if (l.result) {
						this.location = l
					} else {
						uni.removeStorageSync(location);
						let updatePositioning = await getLocation(is);
						this.location = updatePositioning
						localStorage('location', JSON.stringify(updatePositioning), 1000)
					}
				} else {
					let location = await getLocation(is);
					this.location = location
					localStorage('location', JSON.stringify(location), 1000)
				}
				let token = uni.getStorageSync('token');
				if (token) {
					this.getSelectedStore()
				} else {
					this.getLately()
				}
			},
			/*获取选中的门店(已登录)*/
			async getSelectedStore() {
				let res = await getSelectedStore();
				if (res.code == 1) {
					if (Object.prototype.toString.call(res.data) === '[object Object]') {

						this.store_info = res.data
						this.form.store_id = res.data.id
					} else {

						this.getLately()
					}
				}
			},
			/*获取门店*/
			async getLately() {
				let res = await getLately({
					lat: this.location.latitude,
					lng: this.location.longitude,
				})
				if (res.code == 1) {
					this.store_info = res.data
					this.form.store_id = res.data.id
				}
			},
			/*获取默认地址*/
			async getTacitlyApproveAddress() {
				let res = await getDefaultAddress()
				if (res.code == 1) {
					this.default_address = res.data
				}
			},
			/*获取计算数据*/
			getFeeData() {
				let take_fee = this.form.take_vistit_fee ? parseFloat(this.form.take_vistit_fee) : 0;
				let send_fee = this.form.send_vistit_fee ? parseFloat(this.form.send_vistit_fee) : 0;
				this.amountCalculation(take_fee, send_fee)
			},
			/*计算总价*/
			amountCalculation(take_vistit_fee, send_vistit_fee) {
				let total
				let total_amount = parseFloat(this.form.total_amount)
				console.log(take_vistit_fee, send_vistit_fee, total_amount)
				total = take_vistit_fee + total_amount + send_vistit_fee

				this.total = total
			},
			/*获取购物车数据*/
			async getCarData(good_codes) {
				let codes = {
					basket_ids: JSON.stringify(good_codes)
				}
				let res = await submitSettle(this.filter(codes))
				if (res.data) {
					this.form.total_num = res.data.total_num
					this.form.total_amount = res.data.total_amount
					this.form.vistit_fee = res.data.vistit_fee
					//新加代码++++++++++++++++++++
					this.coupon = res.data.coupon
					this.commodityList = []
					for (let i in res.data.goods) {
						this.commodity_list.push(res.data.goods[i])
					}
				}
			},
			/*计算运费*/
			async getDeliveryFee(address_id, store_id, add_type) {
				let res = await getAddDeliveryFee({
					address_id: address_id,
					store_id: store_id
				})
				if (res.code == 1) {
					if (add_type == 'receiving_add') {
						this.form.take_vistit_fee = res.data.delivery_fee
					} else {
						this.form.send_vistit_fee = res.data.delivery_fee
					}
					this.getFeeData()
				} else {
					console.log(res.info)
				}
			},
			/*上门收件选择*/
			receivingChange(item, index) {
				if (!this.store_info) {
					return this.errToast('请选择门店', () => {})
				} else if (this.default_address && index == 1) {
					this.take_address = this.default_address
					this.form.take_address_id = this.default_address.id;
					this.getDeliveryFee(this.form.take_address_id, this.store_info.id, 'receiving_add')
				}
				if (index == 0) {
					this.form.take_vistit_fee = '';
					this.form.take_address_id = ''

					this.getFeeData()
				}
				this.receiving_index = index
				this.form.take_type = item.take_type
			},
			/*返回方式选择*/
			returnChange(item, index) {

				if (!this.store_info) {
					return this.errToast('请选择门店', () => {})
				} else{
					this.return_index = index
					this.form.return_type = item.return_type
					if (this.default_address && index == 1) {
						this.send_address = this.default_address
						this.form.send_address_id = this.default_address.id;
						
						this.getDeliveryFee(this.form.send_address_id, this.store_info.id, 'return_add')
					}else if (index == 0) {
						this.form.send_vistit_fee = ''
						this.form.send_address_id = ''
						this.getFeeData()
					}
				}
				 
				


			},
			/*弹窗回调*/
			dateConfirm(e) {
				if (this.date_type == 'take_date') {
					this.form.take_date = e[0]
				} else {
					this.form.return_date = e[0]
				}
				this.show_calendar = false
			},
			/*打开时间弹窗*/
			carryToStore(e) {
				this.show_calendar = true
				this.date_type = e
			},
			/*获取支付参数*/
			async getPayData(code) {
				let that = this
				let res = await getPayment({
					code: code
				})
				if (res.code == 1) {
					let pay_res = await wxPay(res.data) //支付
					if (pay_res.errMsg == "requestPayment:ok" && pay_res) {
						uni.showModal({
							title: "提示",
							content: "支付成功!",
							showCancel: false,
							success: (res2) => {
								if (res2.confirm) {
									setTimeout(() => {
										that.redirectTo('/subPages/my-order/my-order')
									}, 1500);

								} else {
									setTimeout(() => {
										that.redirectTo('/subPages/my-order/my-order')
									}, 1500);
								}
							},
							fail: (err) => {
								that.errToast(err, () => {
									setTimeout(() => {
										that.redirectTo('/subPages/my-order/my-order')
									}, 2000);
								})

							}
						})
					} else {
						that.errToast('支付失败', () => {
							setTimeout(() => {
								that.redirectTo('/subPages/my-order/my-order')
							}, 2000);
						})
					}
				}
			},
			/*获取购物车*/
			async getCar(basket_id) {
				let res = await getBasketList({
					basket_id: basket_id
				})

				if (res.code == 1) {
					this.basket_goods = res.data.goods
				}
			},
			/*校验表单内容*/
			verification() { 
				if(!this.store_info){
					 this.errToast('请选择门店', () => {})
					return false
				}else if(!this.form.take_type){
					this.errToast('请选择收件方式', () => {})
					return false
				}else if(this.form.take_type==1&&!this.form.take_date){
					this.errToast('请选择到店时间', () => {})
					return false
				}else if(this.form.take_type==2&&!this.form.take_address_id){
					this.errToast('请选择收件地址', () => {})
					return false
				}else if(!this.form.return_type){
					 this.errToast('请选择归还方式', () => {})
					return false
				}else  if(this.form.return_type==2&&!this.form.send_address_id){
					 this.errToast('请选择归还地址', () => {})
					return false
				}else{
					return true
				}
				
			},
			/*提交订单*/
			async submitBtn() {
				if(this.verification()){
					this.form = this.filter(this.form)
					this.form.coupon_ids =  JSON.stringify(this.used_coupon_ids)
					
					let res = await orderAdd(this.form)
					let that = this
					if (res.code == 1) {
						this.successToast('订单提交成功', () => {
							setTimeout(function() {
								that.getPayData(res.data.code)
					
							}, 2000)
						})
					
					} else {
						this.errToast(res.info, () => {})
					}
				}
				
			},
			
			toMyCouponPage(){
				let coupon_ids = JSON.stringify(this.coupon.coupon_ids); 
				let used_coupon_ids = JSON.stringify(this.used_coupon_ids); 
				uni.$u.route({
					url: "/subPages/meet/meet",
					params: {
						coupon_ids: coupon_ids,
						used_coupon_ids:used_coupon_ids,
					}
				})
			}

		}
	}
</script>

<style lang="scss" scoped>
	.container {
		background-color: #F5F5F5;
		min-height: 100%;
	}

	.shop-box {
		width: 100%;
		padding: 32rpx 40rpx;
		background: #FFFFFF;
		border-bottom: 2rpx solid #F5F5F5;

		.shop {
			width: 90%;
			height: 140rpx;

		}

		.shop-info {
			width: 100%;

			.shop-text {
				background: #1A3387;
				border-radius: 4rpx;
				padding: 6rpx 12rpx;
				margin-right: 22rpx;
				font-size: 20rpx;
				text-align: center;
				color: #FFFFFF;
			}

			.shop-name {
				font-size: 28rpx;
				color: #333333;
			}

			.distance {
				font-size: 20rpx;
				color: #999999;
			}

			.icon-navigation {
				font-size: 22rpx;

				color: #1A3387;
			}
		}

	}

	.receiving {
		width: 100%;
		padding: 40rpx 40rpx;
		border-bottom: 2rpx solid #F5F5F5;

		.receiving-left {
			width: 160rpx;

		}

		.receiving-right {
			width: calc(100% - 300rpx);
		}
	}

	.to-store-date {
		width: 100%;
		padding: 40rpx 40rpx;
		border-bottom: 2rpx solid #F5F5F5;

		.to-store-date-left {
			width: 80rpx;
		}

		.to-store-date-right {
			width: calc(100% - 80rpx);
		}
	}

	.header {
		background-color: white;
		margin-bottom: 16rpx;
	}

	.address {
		.text {
			font-size: 30rpx;
			color: #1A3387;
			font-weight: 600;
		}
	}

	.time {
		.text {
			font-size: 30rpx;
			color: #333333;
			font-weight: 600;
		}
	}

	.tip {
		font-size: 28rpx;
		color: #999999;
	}

	.activity {
		background-color: white;
		margin: 0 32rpx 32rpx;
		padding: 0 40rpx 0 24rpx;
		width: calc(100% - 64rpx);
		height: 80rpx;
		line-height: 80rpx;
		background: #EEF2FF;
		border-radius: 10rpx;
		display: flex;
		justify-content: space-between;
		box-sizing: border-box;
		font-weight: 600;

		.activity-text {
			color: #1A3387;
		}

		.activity-price {
			color: #D41A1E;
		}
	}

	.commodity {
		background-color: white !important;
	}

	.list {
		margin-left: 10rpx;

		.list-name {
			height: 80rpx;
			color: #333;
			font-size: 28rpx;
			font-weight: 600;
		}

		.list-price {
			.price-symbol {
				color: #D41A1E;
				font-size: 24rpx;
			}

			.price {
				color: #D41A1E;
				font-size: 28rpx;
			}

			.original-price {
				margin-left: 10rpx;
				color: #999999;
				font-size: 20rpx;
				text-decoration: line-through;
			}
		}
	}

	.sum {
		font-size: 28rpx;
		color: #333;
	}

	.amount {
		font-size: 28rpx;
		color: #333;
	}

	.coupon {
		font-size: 28rpx;
		color: #999999;
	}
    .coupon-use{
		font-size: 28rpx;
		color: #cb8e6b;
	}
	.remark-input {
		/deep/ .u-input__content__field-wrapper__field {
			text-align: right !important;

			/deep/.uni-input-input {
				text-align: right;
			}

			/deep/.uni-input-placeholder {
				text-align: right;
				font-size: 28rpx;
				color: #999999 !important;
			}
		}

		text-align: right !important;

		/deep/.uni-input-input {
			text-align: right;
		}

		/deep/.uni-input-placeholder {
			text-align: right;
			font-size: 28rpx;
			color: #999999 !important;
		}
	}

	.total {
		color: #333333;
		text-align: right;
		padding: 30rpx 32rpx 30rpx 0;

		.coupon {
			color: #D41A1E;
		}
	}

	.payment {
		background-color: white;

		.recharge {
			padding: 6rpx 16rpx;
			line-height: 22rpx;
			background: #1A3387;
			border-radius: 58rpx;
			font-size: 24rpx;
			color: white;
		}
	}

	.pay {
		box-shadow: 0rpx -2rpx 20rpx #EEEEEE;
		box-sizing: border-box;
		padding: 0 30rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;
		position: fixed;
		bottom: 0;
		z-index: 66;
		background-color: white;
		height: 164rpx;
		width: 100%;

		.pay-money {
			font-size: 32rpx;
			color: #333333;
			font-weight: 600;
		}

		.submit-btn {
			width: 240rpx;
			height: 80rpx;
			line-height: 80rpx;
			color: white;
			background: #1A3387;
			border-radius: 48rpx;
			text-align: center;
			font-size: 30rpx;
		}
	}
	
	.coupon-list{
		color:#1A3387;
		text-align:right;
		padding:10rpx 25rpx;
	}
	.item-place{
		align-items: center;
		justify-content: flex-end;
	}
</style>