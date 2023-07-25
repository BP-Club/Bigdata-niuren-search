<template>
	<view class="title">
		<u-divider :text="typeName"  :dashed="true"  textSize="32rpx" :hairline="false" :textColor="color" :lineColor="color"></u-divider>
	</view>
</template>

<script>
	export default{
		name:"Title",
		props:{
			typeName:{
				type:String,
				default:undefined
			},
			color:{
				type:String,
				default:'#909399'
			}
		}
	}
</script>

<style lang="scss" scoped>
	.title{
		padding: 2rpx 32rpx;
		background-color: white;
	}
	/deep/.u-divider{
		margin: 30rpx 0 0;
	}
	
</style>