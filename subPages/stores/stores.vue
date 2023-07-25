<template>
	<view class="list box-sizing-border-box" :style="{paddingTop:status_line_height+ 'rpx'}">

		<u-navbar :safeAreaInsetTop="true">
			<view slot="left" @click="navigateBack">
				<icons name="icon-zuobian" size="46"></icons>
			</view>
			<view slot="center">
				<u-search bgColor="#F7F7F7" placeholder="搜索门店" v-model="keyword" :showAction="false"
					@search="search"></u-search>
			</view>
			<view class="" slot="right">

			</view>
		</u-navbar>


		<view class="u-p-t-70" v-if="list.length>0">
			<view class="list__item u-m-t-16" v-for="(item,index)  in list" :key="index" @click="selectStore(item)">
				<view class="list__item__tag" v-if="item.selected">当前选择</view>
				<view class="list__item__head">
					<view class="list__item__head__left">
						<view class="list__item__head__left__title">
							柚衣洗
						</view>
						<u--text :text="item.name" size="28rpx" customStyle="padding-left:12rpx;"></u--text>
					</view>
					<u--text :text="item.distance" size="20rpx" color="#999999" ></u--text>
				</view>

				<view class="list__item__head__center">
					<u-icon size="22rpx" color="#CCCCCC" name="clock"></u-icon>
					<u--text size="24rpx" :text="item.work_time" color="#999999"
						customStyle="padding-left:8rpx;"></u--text>
				</view>

				<view class="list__item__head__bottom">
					<view class="list__item__head__bottom__left">
						<u-icon size="24rpx" color="#CCCCCC" name="map"></u-icon>
						<u--text size="24rpx" :text="item.address" color="#999999" customStyle="padding-left:8rpx;">
						</u--text>
					</view>
					<u--text text="地图导航" size="22rpx" color="#1A3387" 
						@click.stop="openMap(item)"></u--text>
				</view>
			</view>
		</view>
		<u-empty mode="list" v-else  icon="http://cdn.uviewui.com/uview/empty/list.png">
		</u-empty>
		<u-loadmore :status="status" />
	</view>
</template>

<script>
	import {
		getNear
	} from "api/stores"
	import {
		selectedStore
	} from "@/api/goods.js"
	import {
		getLocation,
		localStorage,
		openLocation
	} from "@/utils/common.js"
	export default {
		data() {
			return {
				status_line_height: 0, //状态栏高度
				keyword: '', //搜索关键字
				list: [], //门店列表
				location: null, //定位信息
				page: 1, //当前页
				page_count: '', //总数
				status: '',
				store_id:null,//门店id
			}
		},

		onLoad({
			page_type,store_id
		}) {
			if(store_id&&store_id!=='null'&&store_id!=='undefined'){
				this.store_id=store_id
			}
			this.getLocation(true);
			let that = this
		    
			// #ifdef MP-WEIXIN
			uni.getSystemInfo({
				success: function(data) {
					that.status_line_height = data.statusBarHeright * 2 //获取状态栏高度 乘2是px与rpx转换 获取到的高度单位是px
				}
			})
			// #endif

		},
		onReady() {
			// #ifdef MP-WEIXIN
			let menu_button_info = uni.getMenuButtonBoundingClientRect() //获取胶囊坐标信息
			this.status_line_height = menu_button_info.top * 2
			// #endif

		},

		onPullDownRefresh() {
			this.list = [];
			this.page = 1;
			let that = this
			this.status = 'loading';
			this.Loading('加载中...', () => {
				setTimeout(() => {

					that.getLocation(true)
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
						that.getLocation(true)
						uni.hideLoading()
					}, 1500);
				}, 1500)
			}
		},

		methods: {
			//保存选中的门店(已登录)
		 async	selectedStore(id){
				let res=await selectedStore({store_id:id})
			},
			navigateBack(){
				uni.navigateBack({
					delta: 1
				});
			},
			selectStore(item) { //当前点击门店
		        let token = uni.getStorageSync('token')
                if(token&&this.store_id){
					this.selectedStore(item.id)
				}
				uni.$emit('selectShop', item)
				uni.navigateBack({
					delta: 1
				});
			},
			openMap(item) { //打开地图

				let lat = parseInt(item.lat)
				let lng = parseInt(item.lng)
				let info = {
					name: item.name,
					address: item.address
				}
				openLocation(lng, lat, info)
			},
			search(e) { //搜索
				this.page = 1
				this.list = [];
				let that = this
				this.Loading('加载中...', () => {
					setTimeout(() => {
						that.getStoreList()

					}, 1500);
				}, 1500)
			},
			async getLocation(is) { //获取定位信息
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
				this.getStoreList()
			},
			async getStoreList() { //获取最近分店
			
			    let data ={
					lat: this.location.latitude,
					lng: this.location.longitude,
					page: this.page,
					keyword: this.keyword,
					selected_store_id:this.store_id
				}
				let res = await getNear(this.filter(data))
				if (res.code == 1) {
					if (this.page == 1) {
						this.list = res.data.list
					} else {
						this.list = [...this.list, ...res.data.list]
					}
					this.page_count = res.data.pages;
				}
			}
		}
	}
</script>

<style lang="scss">
	::v-deep .u-search {
		width: 456rpx !important;
		height: 64rpx !important;
		background-color: #F7F7F7 !important;
		border-radius: 36rpx;

	}


	.list {
		padding: 32rpx;
		background-color: #F7F7F7;

		&__item {
			position: relative;
			padding: 30rpx 24rpx 24rpx;
			width: 638rpx;
			height: 176rpx;
			background: #FFFFFF;
			border-radius: 20rpx;

			&__tag {
				position: absolute;
				background-color: #f1d0bf;
				border-radius: 0rpx 20rpx 0rpx 8rpx;
				color: #D41A1E;
				padding: 6rpx 12rpx;
				font-size: 19rpx;
				left: 587rpx;
				top: -1rpx;
			}

			&__head {
				@include flex(row);
				justify-content: space-between;

				&__left {
					@include flex(row);
					width: 92%;

					&__title {
						background-color: #1A3387;
						font-size: 20rpx;
						color: #FFFFFF;
						border-radius: 4rpx;
						padding: 6rpx 12rpx;
					}

				}

				&__center {
					padding: 30rpx 0;
					@include flex(row);
				}

				&__bottom {
					@include flex(row);
					justify-content: space-between;

					&__left {
						@include flex(row);
						width: 86%;
					}
				}
			}
		}
	}
</style>