<template>
	<view class="container">
		<view class="amount f-s-28 fw400">
			<view>洗衣篮共{{data.total_num}}件商品</view>
			<view @click="isEdit=!isEdit">{{isEdit?"取消":"编辑"}}</view>
		</view>
		<view class="u-p-b-100">
			<u-checkbox-group v-model="value" placement="column" @change="changeChecked">
				<view v-for="(item,i) in list" :key="i">
					<type-title :typeName="item.type" color="#1A3387"></type-title>
						<view class="list" v-for="(temp,index) in item.list" :key="index">
							<view class="disp-flex align-center u-m-r-12">
								<u-checkbox :name="temp.basket_id" shape="circle" activeColor="#D41A1E"></u-checkbox>
							</view>
							<view class="disp-flex justify-between">
								<view class="u-m-r-20">
									<u--image :src="temp.cover" radius="5" width="192rpx" height="192rpx"></u--image>
								</view>
								<view class="disp-flex flex-wrap">
									<view class="shopping-name f-s-28">{{temp.name}}</view>
									<view class="quantity f-s-24 u-p-t-22">件数：共{{temp.good_num}}件</view>
									<view class="disp-flex justify-between u-p-t-26 count">
										<view class="price">
											<text class="f-s-22">￥</text>
											<text>{{temp.price_selling}}</text>
											<text class="u-m-l-10 del">￥{{temp.price_market}}</text>
										</view>
										<view>

											<u-number-box v-model="temp.good_num">
												<view slot="minus" class="minus"
													@click="quantityChange($event,temp,index,'reduce',i)">
													<u--image src="/static/images/minus.svg" width="44rpx" height="44rpx"
														:fade="false">
													</u--image>
												</view>
												<text slot="input" class="input">{{temp.good_num}}</text>
												<view slot="plus" class="plus"
													@click="quantityChange($event,temp,index,'add',i)">
													<u--image src="/static/images/plus.svg" width="44rpx" height="44rpx"
														:fade="false">
													</u--image>
												</view>
											</u-number-box>
										</view>
									</view>
								</view>
							</view>
						</view>
				</view>
			</u-checkbox-group>
		</view>
		<view class="settle-accounts disp-flex justify-between align-center">
			<view>
				<u-checkbox-group v-model="checkboxValue" placement="column">
					<u-checkbox label="全选" name="all" shape="circle" activeColor="#D41A1E" @change="changeAllChecked"></u-checkbox>
				</u-checkbox-group>
			</view>
			<view class="btn-delete f-s-26" v-if="isEdit" @click="deleted">删除</view>
			<view class="btn f-s-26" v-else @click="submit">去结算</view>
		</view>
	</view>
</template>

<script>
	import typeTitle from "./components/title.vue"
	import { getBasketList, setBasketNumber, submitSettle } from "api/basket"
	export default {
		components: {
			typeTitle
		},
		data() {
			return {
				value: [],
				list_sum: 0,
				amount: 3,
				checkboxValue: [],
				isEdit: false,
				params: {},
				data: {
					total_amount: 0,
					total_num: 0
				},
				list: []
			}
		},
		
		onShow() {
			this.reload()
		},
		methods: {
			quantityChange(e, item, index, type,index1) {//商品数量加减
			
				if (type == 'reduce' && item.good_num == 1) {
					return this.toast('商品数量最低为1')
				}
				let that=this
				this.Loading('加载中...', () => {
					setTimeout(()=>{
						that.opSet(type, item.basket_id, index,index1)
					},1500)
				}, 1000)
			},
			/* 洗衣篮商品数量加减 */
			async opSet(type, id, index,indexs) {
					let res = await setBasketNumber({action: type,basket_id:id})
				      
				   if(res.code==1){
					     if(type=='remove'){
							 this.reload()
						 }else{
							  this.list[indexs].list[index]=res.data.goods
						 }
					    this.$forceUpdate();
				   }
				  
			},
			reload() {//获取购物车商品
				getBasketList(this.params).then(res => {
					if (res.data) {
						this.data = {
							total_amount: res.data.total_amount || 0,
							total_num: res.data.total_num || 0
						}
						this.list_sum=0
						if (res.data.goods) {
							this.list = res.data.goods
							this.list.forEach(item=>{
								if(item.list&&item.list.length){
									item.list.forEach(temp=>{
										this.list_sum++
									})
								}
							})
						}
					}else{
						this.list=[]
					}
				})
			},
		
			// 单选按钮
			changeChecked(value) {
				if (value.length === this.list_sum && this.list_sum != 0) {
					this.checkboxValue = ["all"]
				} else {
					this.checkboxValue = []
				}
			},
			// 全选按钮
			changeAllChecked(value) {
				this.value = []
				if (!value) {
					return
				}
				this.list.forEach(item => {
					item.list.forEach(temp => {
						this.value.push(temp.basket_id)
					})
				})
			},
			// 提交
			submit() { //此处应该是直接跳转到订单 
				if(this.value.length>0){
					uni.setStorageSync("basket_id",this.value)
					uni.navigateTo({
						url:"/subPages/submit-order/submit-order"
					})
				}else{
					this.toast("请选择商品")
				}
			},
			// 删除
			deleted() {
				if (this.value.length <= 0) {
					return
				}
				let that=this
				this.Loading('加载中...', () => {
					setTimeout(()=>{
						that.opSet( "remove", JSON.stringify(this.value))
					},1500)
				}, 1000)
				
			}
		}
	}
</script>

<style lang="scss" scoped>
	.container {
		background-color: #F7F7F7;
		padding-bottom: 32rpx;
	}

	.amount {
		padding: 40rpx 32rpx;
		display: flex;
		justify-content: space-between;
		color: #333;
		box-sizing: border-box;
	}

	.settle-accounts {
		width: 100%;
		height: 98rpx;
		position: fixed;
		bottom: var(--window-bottom);
		background: #ffffff !important;
		padding: 0 32rpx;
		box-shadow: 0rpx -2rpx 20rpx #EEEEEE;
		box-sizing: border-box;

		.btn {
			color: white;
			background-color: #1A3387;
			padding: 20rpx 40rpx;
			border-radius: 58rpx;
		}

		.btn-delete {
			color: white;
			background-color: #D41A1E;
			padding: 20rpx 40rpx;
			border-radius: 58rpx;
		}
	}

	.list {
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

		.input {
			color: #D41A1E;
		}
	}
</style>
