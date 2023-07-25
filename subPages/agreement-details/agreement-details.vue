<template>
	<view>
		<u-parse :content="agreement_info.content"></u-parse>
	</view>
</template>

<script>
	import {getAgreeDetail} from "@/api/public.js"
	export default {
		data() {
			return {
				agreement_info:null //协议详情
			};
		},
		onLoad({code}) {
			this.getAgreeDetail(code)
		},
		methods:{
			/*获取协议详情*/
			async getAgreeDetail(code){
				let res=await  getAgreeDetail({code:code})
				if(res.code==1){
					this.agreement_info=res.data
					uni.setNavigationBarTitle({
					    title: res.data.name
					});
				}
				
			}
		}
	}
</script>

<style lang="scss">

</style>
