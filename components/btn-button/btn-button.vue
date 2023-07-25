<template>
	<view>
        <view class="btn" @click="clickBtn" :style="{fontSize:size+'rpx',lineHeight:height+'rpx',borderRadius:radius,height:height+'rpx',width:width+'rpx',color:color,fontWeight:bold?bold:'',background:bntColor}">
        	{{title}}
        </view>
	</view>
</template>

<script>
	export default {
		props: {
			title: { //按钮文字
				type: String,
				default: ''
			},
			size:{
				type: [String, Number],
				default: '10'
			},
			color: { //按钮文字颜色
				type: String,
				default: 'rgb(0,0,0,1)' //默认白色
			},
			width:{  //默认宽度
				type: [String, Number],
				default: '' 
			},
			height:{  //默认高度
				type: [String, Number],
				default: '' 
			},
			bntColor:{ //背景颜色
				type: String,
				default: 'rgba(255, 255, 255, 1)'  //默认白色背景
			},
			bold:{ //字体加粗
				type: [String, Number],
				default: ''
			},
            radius:{ //圆角值
				type: [String, Number],
				default: '0'
			}
		},
		name: "btn-button",
		data() {
			return {

			};
		},
		methods:{
			clickBtn(){
				this.$emit('click')
			}
		}
	}
</script>

<style lang="scss" scoped >
     .btn{
		 text-align: center;
		height:96rpx;
		width: 95rpx;
	 }
</style>
