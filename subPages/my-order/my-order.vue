<template>
	<view class="container">
		<view class="top">
			<view class="search-box box-sizing-border-box">
				<u-search placeholder="请输入关键词搜索" v-model="keyword" :showAction="false" @search="search"></u-search>
			</view>
			<u-tabs :current="current" :list="tabs" lineColor="#1A3387" lineHeight="8rpx"
				:activeStyle="{fontWeight: 500,color: '#333333',fontSize: '30rpx'}" :inactiveStyle="{color: '#999999'}"
				@click="changeTabs"></u-tabs>
		</view>
		<view class="empty u-p-t-100" v-if="list.length == 0">
			<u--image :showLoading="true" src="/static/images/empty.png"></u--image>
			<view class="tip">空空如也~~</view>
		</view>
		<view class="goods-list overflow-hidden box-sizing-border-box">
			<view class="item box-sizing-border-box" v-for="(item,index) in list" :key="index">
				<view class="item-top fx align-items-center justify-content-between"
					@click="navigateTo('/subPages/order-details/order-details?id='+item.id)">
					<view class="distribution">
						<text class="mode f-s-20 fw500">{{item.return_type_text}}</text><text
							class="shop-name f-s-24 ">店铺名称({{item.store_name}})</text>
					</view>
					<view class="state f-s-24 ">
						{{item.status_text}}
					</view>
				</view>
				<view class="item-center " @click="navigateTo('/subPages/order-details/order-details?id='+item.id)">
					<view class="goods fx justify-content-between align-items-center"
						v-for="(gooods,i) in item.goods_data.goods" :key="i">
						<view class="left fx align-items-center">
							<u--image shape="square" :lazy-load="true" radius="10rpx"
								src="https://cdn.uviewui.com/uview/album/1.jpg" width="120rpx" height="120rpx">
							</u--image>
							<view class="goods-left-ringht fx flex-wrap align-content-around">
								<view class="f-s-28 fw500 goods-name">
									{{gooods.name}}
								</view>
								<view class="f-s-24 goods-piece">
									件数：共{{gooods.good_num}}件

								</view>
							</view>
						</view>
						<view class="ringht f-s-28">

						</view>
					</view>

					<view class="process-intro f-s-26" v-if="item.process_intro">
						*{{item.process_intro}}
					</view>

					<view class="order-no f-s-26">
						订单编号:{{item.order_no}}

					</view>
					<view class="goods-other fx  justify-content-between">
						<view class="date f-s-24 ">

							{{item.create_at}}
						</view>

						<view class="fx">
							<text class="f-s-22">共{{item.goods_data.goods.length}}件商品</text>
							<view class="amount-money fx">
								<text class="f-s-22">合计：</text>
								<text class="f-s-22">￥</text>
								<text class="f-s-28 money">{{item.discount_price}}</text>
							</view>
						</view>
					</view>
				</view>



				<view class="fx justify-content-end">
					<view class="btn-box" v-if="item.status==1">
						<btnButton title="确认取货" size="24"  color="#FFFFFF" bntColor="#1A3387"
							radius="58rpx" width="160" height="56" @click=""></btnButton>
                    </view>

					<!--新增代码，退款申请++++++++++++++++-->
					<view class="btn-box" v-if="item.status==4">
					     <btnButton title="确认收货" size="24"  color="#FFFFFF" bntColor="#1A3387"
						radius="58rpx" width="160" height="56" @click="orderConfirmShow(item.id)"></btnButton>
                    </view>


					<!--新增代码，退款申请++++++++++++++++-->
					<view class="btn-box" v-if="item.status==1 && item.refund_status == 0">
						<btnButton title="退款" size="24"  color="#FFFFFF"
							bntColor="#1A3387" radius="58rpx" width="160" height="56" @click="showRefundModal(item.id)">
						</btnButton>
                    </view>
					<view class="btn-box" v-if="item.refund_status == 1">
						<btnButton title="退款审核中" size="24" v-if="item.refund_status == 1 " color="#FFFFFF"
							bntColor="#7d818e" disabled="true" radius="58rpx" width="160" height="56"
							@click="toRefundPage(item.id)"></btnButton>
                    </view>
                    <view class="btn-box" v-if="item.refund_status == 3">
						<btnButton title="退款被拒绝" size="24"  color="#FFFFFF"
							bntColor="#e66f66" disabled="true" radius="58rpx" width="160" height="56"
							@click="toRefundPage(item.id)"></btnButton>
                    </view>
                    <view class="btn-box" v-if="item.refund_status == 2">
					    <btnButton title="已同意退款" size="24"  color="#FFFFFF"
							bntColor="#59aba1" disabled="true" radius="58rpx" width="160" height="56"
							@click="toRefundPage(item.id)"></btnButton>
                    </view>
					<!--新增代码，退款申请++++++++++++++++-->
					<btnButton title="立即支付" @click="navigateTo('/subPages/order-details/order-details?id='+item.id)"
						v-if="item.status==0" size="24" color="#FFFFFF" bntColor="#1A3387" radius="58rpx" width="160"
						height="56"></btnButton>
				</view>
			</view>
		</view>
		<u-loadmore :status="status" />
		<!--退款申请弹窗，新增代码++++++++++++-->
		<u-modal :show="show_refund" :showCancelButton="true" :title="title" @confirm="confirmSend"
			@cancel="show_refund=false" @close="show_refund=false">
			<view style="width: 600rpx;">
				<u--form labelPosition="left" :model="refundForm">
					<u-form-item label="理由">
						<u--textarea v-model="refundForm.remark" width="100" placeholder="请输入退款理由"></u--textarea>
					</u-form-item>
					<u-form-item label="凭证">
						<u-upload :fileList="fileList" name="1" multiple :maxCount="10" width="60rpx" height="60rpx"
							@afterRead="afterRead" @delete="deletePic"></u-upload>
					</u-form-item>
				</u--form>
			</view>
		</u-modal>
		<!--确认收货弹窗，新增代码++++++++++++-->
		<u-modal :show="show_confirm" content="是否确认已收获？" showConfirmButton="true" showCancelButton="true"
			@confirm="orderOk()" @cancel="this.show_confirm = false"></u-modal>

	</view>
</template>

<script>
	import {
		getList,
		getPayment,
		applyRefund,
		orderConfirm
	} from "api/stores"
	import {
		wxPay,
		upload
	} from "@/utils/common.js"
	export default {
		data() {
			return {
				current: 0,
				tabs: [{
						name: '全部',
						status: ''
					},
					{
						name: '待支付',
						status: 0
					},
					{
						name: '待取货',
						status: 1
					},
					{
						name: '入库中',
						status: 2
					},
					{
						name: '清洗中',
						status: 3
					},
					{
						name: '送货中',
						status: 4
					},
					{
						name: '已完成',
						status: 5
					},
					{
						name: '退款/售后',
						status: 6
					},
					{
						name: '已取消',
						status: 7
					}
				],
				keyword: '',
				page: 1, //当前页
				page_count: '', //总数
				status: '',
				list: [],
				//新增代码++++++++++++
				show_refund: false,
				title: "退款申请",
				refundForm: {
					remark: '',
					datas: [],
					order_id: 0,
				},
				fileList: [],
				show_confirm: false,
				current_order_id:'',
			}
		},
		onLoad(option) {
			if (option.index) {
				this.current = Number(option.index)
			}
			this.getOrder(this.current)
		},
		onPullDownRefresh() {
			this.list = [];
			this.page = 1;
			let that = this
			this.status = 'loading';
			this.Loading('加载中...', () => {
				setTimeout(() => {
					that.getOrder()
					that.status = 'nomore';
					uni.stopPullDownRefresh();
					uni.hideLoading()

				}, 1500);
			}, 1500)

		},
		onReachBottom() {
			this.status = 'loading';
			this.page++;
			let that = this
			if (this.page > this.page_count) {
				this.status = 'nomore';
			} else {
				this.Loading('加载中...', () => {
					setTimeout(() => {
						that.getOrder()
						uni.hideLoading()
					}, 1500);
				}, 1500)
			}
		},
		methods: {
			pay(item) { //立即发起支付
				this.getPayData(item.order_no)
			},
			async getPayData(code) { //获取支付参数
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
									// setTimeout(() => {
									// 	that.redirectTo('/subPages/my-order/my-order')
									// }, 1500);

								}
							},
							fail: (err) => {
								that.errToast(err, () => {
									// setTimeout(() => {
									// 	that.redirectTo('/subPages/my-order/my-order')
									// }, 2000);
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
			search() { //搜索
				let that = this
				this.Loading('加载中...', () => {
					setTimeout(() => {
						that.getOrder()
						uni.hideLoading()
					}, 1500);
				}, 1500)
			},
			getOrder() { //获取订单
				let data = {
					page: this.page,
					keyword: this.keyword,
					status: this.tabs[this.current].status
				}
				getList(data).then(res => {
					if (this.page == 1) {
						this.list = res.data.list
					} else {
						this.list = [...this.list, ...res.data.list]
					}
					this.page_count = res.data.pages;
				})
			},

			changeTabs(item) { //改变顶部
				this.current = item.index
				let that = this
				this.Loading('加载中...', () => {
					setTimeout(() => {
						that.getOrder()
						uni.hideLoading()
					}, 1500);
				}, 1500)

			},

			//新增代码，退款申请++++++++++++++++
			showRefundModal(order_id) {
				this.show_refund = true
				this.refundForm.order_id = order_id
			},
			deletePic(event) {
				this.fileList.splice(event.index, 1)
				this.refundForm.datas.splice(event.index, 1)
			},
			afterRead(event) {
				this.uploadFile(event.file[0].url)
			},
			async uploadFile(file) {
				let res = await upload(file)
				this.fileList.push({
					url: res,
				})
				this.refundForm.datas.push(res)
			},
			//提交腿看申请
			confirmSend() {
				if (this.refundForm.remark == '') {
					this.errToast('请填写退款理由', () => {})
					return false
				}
				this.applyRefundInfo()
			},
			async applyRefundInfo() {
				let res = await applyRefund(this.refundForm)
				this.successToast(res.info, () => {
					this.show_refund = false
					this.getOrder()
				}, 2000)
			},
			//跳转到退款流程页
			toRefundPage(order_id) {
				uni.$u.route({
					url: '/subPages/order-refund/order-refund?id=' + order_id,
				});
			},
			orderConfirmShow(order_id){
				this.show_confirm = true
				this.current_order_id = order_id
			},
			//确认收获
			orderOk() {
			  let order_id = this.current_order_id	
              this.orderConfirm(order_id)
			},
			async orderConfirm(order_id) {
				let res = await orderConfirm({
					order_id: order_id
				})
				this.successToast(res.info, () => {
                    this.show_confirm = false
					this.getOrder()
				}, 2000)
			},
		}
	}
</script>

<style lang="scss" scoped>
	.container {
		width: 100vw;
		background: #F7F7F7;
	}

	.top {
		width: 100%;
		background: #FFFFFF;
	}

	::v-deep .u-search {
		width: 686rpx;
		height: 64rpx !important;
		background: #F7F7F7 !important;
		border-radius: 36rpx;

	}

	.search-box {
		width: 100%;
		padding: 8rpx 32rpx;

	}

	.process-intro {
		color: #728fd7;
		padding: 20rpx 0;
	}

	.order-no {
		color: #999999;
		padding: 20rpx 0;
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

	.goods-list {
		width: 100%;
		padding: 32rpx;

		.item {
			background: #FFFFFF;
			border-radius: 20rpx;
			padding: 0 24rpx 32rpx 24rpx;
			margin-bottom: 16rpx;

			.item-center {
				.goods {
					margin-bottom: 40rpx;

					.ringht {
						color: #333333;
					}

					.goods-left-ringht {
						height: 120rpx;
						margin-left: 20rpx;

						view {
							width: 100%;
						}

						.goods-name {
							color: #333333;
						}

						.goods-piece {
							color: #999999;
						}
					}
				}

				.goods-other {
					.date {
						color: #999999;
					}

					margin-bottom: 34rpx;

					.amount-money {

						text {
							color: #333333;
						}

						margin-left: 20rpx;

						.money {
							line-height: 28rpx;
						}
					}
				}

			}

			.item-top {
				padding: 32rpx 0;
				border-bottom: 2rpx solid #F7F7F7;
				margin-bottom: 32rpx;

				.distribution {
					.mode {
						padding: 6rpx 12rpx;
						height: 32rpx;
						background: #1A3387;
						border-radius: 4rpx;

						line-height: 32rpx;
						color: #FFFFFF;
					}

					.shop-name {

						color: #333333;
						margin: 0 0 0 12rpx;
					}
				}

				.state {

					color: #D41A1E;
					line-height: 36rpx;
				}
			}
		}
	}

	.input-title {
		padding-right: 40rpx;
	}

	.input-name {}

	.box-sizing-border-box {}
	.btn-box{
		padding:0 8rpx;
	}
</style>
