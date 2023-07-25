<script>
	import baseURL from "utils/request.js" 
	export default {
		globalData: {
			baseUrl: baseURL,
			mapKey:"",//地图key,
		},
		onLaunch: function() {
			console.log('App Launch')
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		}
	}
</script>

<style lang="scss">
	/* 注意要写在第一行，同时给style标签加入lang="scss"属性 */
	page{
		min-height:100%;
		background-color: #F7F7F7;
	}
	@import "uview-ui/index.scss";
	@import url("static/css/layout.css");
	@import "./static/css/common.scss";
</style>
