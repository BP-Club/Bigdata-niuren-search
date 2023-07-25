<template>
	<view class="u-page">
		<view class="u-page__item">
			<u-gap height="150"></u-gap>
			<u-tabbar  
			    :fixed="true"
			    :placeholder="false"
			    :safeAreaInsetBottom="false"
			>
				<u-tabbar-item
				    v-for="item in datas" 
				    :text="item.text"
				    :icon="item.icon"
					@click="click(item)"
				></u-tabbar-item>
			</u-tabbar>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				cuRoute:'' ,
				baseUrl:'',
				datas:[
					{
						un_current:this.domain+'/wash/static/icon/sywxz.png',
						current:this.domain+'/wash/static/icon/syxz.png',
						icon:this.domain+'/wash/static/icon/sywxz.png',
						text:'首页',
						page:'pages/index/index',
			
					},
					{
						un_current:this.domain+'/wash/static/icon/djwxz.png',
						current:this.domain+'/wash/static/icon/ddxz.png',
						icon:this.domain+'/wash/static/icon/djwxz.png',
						text:'订单',
						page:'subPages/my-order/my-order',
	
					},
					{
						un_current:this.domain+'/wash/static/icon/xylwxz.png',
						current:this.domain+'/wash/static/icon/xylxz.png',
						icon:this.domain+'/wash/static/icon/xylwxz.png',
						text:'洗衣篮',
						page:'subPages/category/category',
					},
					{
						un_current:this.domain+'/wash/static/icon/wdwxz.png',
						current:this.domain+'/wash/static/icon/wdxz.png',
						icon:this.domain+'/wash/static/icon/wdwxz.png',
						text:'我的',
						page:'pages/my-center/my-center',
					},
				]
			};
		},
		mounted(){
			let routes = getCurrentPages(); // 获取当前打开过的页面路由数组
			this.curRoute = routes[routes.length - 1].route //获取当前页面路由
			let curParam = routes[routes.length - 1].options //获取路由参数
			let that = this
			this.datas.forEach(function(item,index){ 
				if(item.page == that.curRoute){			
					item.icon = item.current
				}
			})	
		},
		methods: {
			click(item) {
				uni.$u.route({
					url: item.page,
				})
			
			},
		
		},
	}
</script>

<style  lang="scss">
	.u-page {
		padding:0;
       /* z-index: 100;
        bottom: 0;
        position: fixed;
		width: 100%;*/
		&__item {

			&__title {
				color: $u-tips-color;
				background-color: $u-bg-color;
				padding: 15px;
				font-size: 15px;

				&__slot-title {
					color: $u-primary;
					font-size: 14px;
				}
			}

			&__slot-icon {
				width: 17px;
				height: 17px;
			}
		}
	}
</style>
