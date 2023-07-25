<template>
	<view class="content">
		<view class="top_rad">
			<view class="top-rad-left">收件方式</view>
			<view class="top-rad-right">
				<u-radio-group v-model="takeModel" placement="row" @change="takeGroupChange">
					<u-radio :customStyle="{marginLeft: '40rpx',marginTop:'4rpx'}" v-for="(item, index) in takeList" :key="index"
						:name="item.id" :label="item.name"  labelSize="13" size="13">
					</u-radio>
				</u-radio-group>
			</view>
		</view>
		<view class="line"></view>
		<view class="top" v-show="takeAddrShow">
			<view class="top-item">
				<view class="top-item-icon">
					<u--image :showLoading="true" radius="20rpx" :src="icon1" width="44rpx" height="44rpx"
						@click="click">
					</u--image>
				</view>
				<view class="top-item-right">
					<view class="top-item-right-left">
						<view class="top-item-right-left-text1">添加上门地址</view>
						<view class="top-item-right-left-text2">您还没有添加地址</view>
					</view>
					<view class="top-item-right-right">
						>
					</view>
				</view>
			</view>
		</view>
		
			<view class="line"></view>
		
		<view class="top_rad" >
			<view class="top-rad-left">归还方式</view>
			<view class="top-rad-right">
				<u-radio-group v-model="returnModel" placement="row" @change="returnGroupChange"  >
					<u-radio :customStyle="{marginLeft: '40rpx',marginTop:'4rpx'}" v-for="(item, index) in returnList" :key="index"
						:name="item.id" :label="item.name"  labelSize="13" size="13">
					</u-radio>
				</u-radio-group>
			</view>
		</view>
		<view class="line"></view>
		<view class="top" v-show="returnAddrShow">
			<view class="top-item">
				<view class="top-item-icon">
					<u--image :showLoading="true" radius="20rpx" :src="icon1" width="44rpx" height="44rpx"
						@click="click">
					</u--image>
				</view>
				<view class="top-item-right">
					<view class="top-item-right-left">
						<view class="top-item-right-left-text1">添加送回地址</view>
						<view class="top-item-right-left-text2">您还没有添加地址</view>
					</view>
					<view class="top-item-right-right">
						>
					</view>
				</view>
			</view>
			<view class="top-item">
				<view class="top-item-icon">
					<u--image :showLoading="true" radius="20rpx" :src="icon2" width="44rpx" height="44rpx"
						@click="click">
					</u--image>
				</view>
				<view class="top-item-right">
					<view class="top-item-right-left">
						<view class="top-item-right-left-text1">到店取件时间</view>
						<view class="top-item-right-left-text2">点击选择上门时间</view>
					</view>
					<view class="top-item-right-right">
						>
					</view>
				</view>
			</view>
		</view>
		
		
		<view class="line"></view>
		<view class="center">
			<view class="center-activity">
				<view class="center-activity-left">活动：衣鞋类目任洗3件</view>
				<view class="center-activity-right">
					<u--text text="¥39.00" size="28rpx" color="#D41A1E"></u--text>
				</view>
			</view>

			<view class="center-goods">
				<view class="goods-item">
					<u--image :showLoading="true" radius="20rpx" src="" width="120rpx" height="120rpx" @click="click">
					</u--image>
					<view class="goods-item-right">
						<view class="goods-item-right-left">
							<view class="goods-item-title">常规鞋类</view>
							<view class="goods-item-prices">
								<view>
									<u--text text="¥39.00" size="28rpx" color="#D41A1E"></u--text>
								</view>
								<view style="padding:6rpx 10rpx;">
									<u--text text="¥39.00" size="20rpx" color="#999999" decoration="line-through">
									</u--text>
								</view>
							</view>
						</view>
						<view class="goods-item-right-right">
							x1
						</view>
					</view>
				</view>


				<view class="goods-item">
					<u--image :showLoading="true" radius="20rpx" src="" width="120rpx" height="120rpx" @click="click">
					</u--image>
					<view class="goods-item-right">
						<view class="goods-item-right-left">
							<view class="goods-item-title">常规鞋类</view>
							<view class="goods-item-prices">
								<view>
									<u--text text="¥39.00" size="28rpx" color="#D41A1E"></u--text>
								</view>
								<view style="padding:6rpx 10rpx;">
									<u--text text="¥39.00" size="20rpx" color="#999999" decoration="line-through">
									</u--text>
								</view>
							</view>
						</view>
						<view class="goods-item-right-right">
							x1
						</view>
					</view>
				</view>


				<view class="goods-item">
					<u--image :showLoading="true" radius="20rpx" src="" width="120rpx" height="120rpx" @click="click">
					</u--image>
					<view class="goods-item-right">
						<view class="goods-item-right-left">
							<view class="goods-item-title">常规鞋类</view>
							<view class="goods-item-prices">
								<view>
									<u--text text="¥39.00" size="28rpx" color="#D41A1E"></u--text>
								</view>
								<view style="padding:6rpx 10rpx;">
									<u--text text="¥39.00" size="20rpx" color="#999999" decoration="line-through">
									</u--text>
								</view>
							</view>
						</view>
						<view class="goods-item-right-right">
							x1
						</view>
					</view>
				</view>
			</view>

			<view class="item-detail frist">
				<view class="item-detail-left">商品总额</view>
				<view class="item-detail-right">¥39.00</view>
			</view>

			<view class="item-detail">
				<view class="item-detail-left">优惠卷</view>
				<view class="item-detail-right coupon">暂无可用优惠卷</view>
			</view>


			<view class="item-detail">
				<view class="item-detail-left">上门费</view>
				<view class="item-detail-right coupon">¥0</view>
			</view>

			<view class="item-detail">
				<view class="item-detail-left">其他服务</view>
				<view class="item-detail-right">¥0</view>
			</view>

			<view class="item-detail bottom">
				<view class="item-detail-left">备注</view>
				<view class="item-detail-right coupon">请填写备注信息</view>
			</view>
			<view class="total-item">
				<view class="total-item-text">共3件商品</view>
				<view class="total-item-text">已优惠：</view>
				<view class="total-item-price">¥39.00</view>
				<view class="total-item-text">合计：</view>
				<view class="total-item-text">¥39.00</view>
			</view>
		</view>
		<view class="bottom-box">
			<view class="bottom-box-left">
				<view class="total-item-text">需要支付金额:</view>
				<view class="total-item-text">¥99</view>
			</view>
			<view class="bottom-box-btn">
				提交订单
			</view>
		</view>


	</view>
</template>

<script>
	export default {
		data() {
			return {
				icon1: this.domain + '/wash/static/icon/address.png',
				icon2: this.domain + '/wash/static/icon/time.png',
				takeAddrShow:false,
				returnAddrShow:false,
				takeModel: 1,
				takeList: [{
						name: '自带到店',
						id:1
					},
					{
						name: '上门收取',
						id:2
					},
				],
				returnModel:1,
				returnList: [{
						name: '到店自取',
						id:1
					},
					{
						name: '配送到家',
						id:2
					},
				],
			}
		},
		methods: {
			takeGroupChange() {
			   if(this.takeModel == 2){
				   this.takeAddrShow = true
			   }else{
				   this.takeAddrShow = false
			   }
               
			},
			returnGroupChange() {
               if(this.returnModel == 2){
				   this.returnAddrShow = true
			   }else{
				   this.returnAddrShow = false
			   }
               
			}
		}
	}
</script>

<style lang="scss">
	.top_rad {
		padding: 25rpx 45rpx;
		background-color: #FFFFFF;
		@include flex(row);
	}

	.top-rad-left {
		font-size: 30rpx;
	}
    .top-rad-right{
		padding: 3rpx 50rox 0 0;
	}
	.content {
		width: 100%;
		background-color: #F5F5F5;
		height: 100vh;
	}

	.top {
		background-color: #FFFFFF;
	}

	.top-item {
		padding: 0 32rpx;
		@include flex(row);
		border-bottom: 2rpx #F5F5F5 solid;
	}

	.top-item-icon {
		padding: 66rpx 0 40rpx 8rpx;

	}

	.top-item-right {
		padding-left: 20rpx;
		@include flex(row);
		justify-content: space-between;
		width: 89%;
	}

	.top-item-right-left {}

	.top-item-right-left-text1 {
		padding: 40rpx 0 16rpx 0;
		font-size: 30rpx;
		font-weight: 400;
		color: #1A3387;
	}

	.top-item-right-left-text2 {
		font-size: 28rpx;
		font-weight: 400;
		color: #999999;
	}

	.top-item-right-right {
		line-height: 166rpx;
		font-size: 28rpx;
	}

	.line {
		height: 16rpx;
		background: #F5F5F5;
	}




	.center {
		background-color: #FFFFFF;
		padding: 32rpx 32rpx;
	}

	.center-activity {
		padding: 24rpx;
		background: #EEF2FF;
		border-radius: 10rpx;
		@include flex(row);
		justify-content: space-between;
	}

	.center-activity-left {
		color: #1A3387;
		font-size: 28rpx;
	}

	.center-activity-right {}



	.center-goods {
		padding: 40rpx 0 0 0;
	}

	.goods-item {
		@include flex(row);
	}

	.goods-item-right {
		width: 89%;
		padding-left: 20rpx;
		@include flex(row);
		justify-content: space-between;
	}

	.goods-item-right-left {
		font-size: 28rpx;
		padding: 8rpx 0 48rpx 0;
	}

	.goods-item-prices {
		padding-top: 29rpx;
		@include flex(row);
	}

	.goods-item-title {
		color: #333333;
	}

	.goods-item-right-right {
		padding-top: 28rpx;
	}

	.item-detail {
		font-size: 28rpx;
		@include flex(row);
		justify-content: space-between;
		color: #333333;
		padding-top: 60rpx;
	}

	.item-detail-left {}

	.item-detail-right {}

	.coupon {
		color: #999999;
	}

	.frist {
		padding-top: 0;
	}

	.bottom {
		padding-bottom: 30rpx;
		border-bottom: 5rpx solid #EEEEEE;
	}

	.total-item {
		padding: 40rpx 0;
		@include flex(row);
		justify-content: flex-end;
		font-size: 28rpx;
	}


	.total-item-text {

		color: $wash-color-black;
		padding-left: 10rpx;
	}

	.total-item-price {

		color: $wash-color-red;
	}

	.bottom-box {
		padding: 0 32rpx;
		position: relative;
		height: 160rpx;
		background: $wash-color-white;
		box-shadow: $wash-border;
		@include flex(row);
		justify-content: space-between;
	}

	.bottom-box-left {
		padding-top: 34rpx;
		font-size: 32rpx;
		@include flex(row);
	}

	.bottom-box-btn {
		font-size: 28rpx;
		background-color: #1A3387;
		border-radius: 48rpx;
		padding: 18rpx 60rpx;
		color: #FFFFFF;
		position: absolute;
		margin-left: 441rpx;
		margin-top: 10rpx;
	}
</style>
