<template>

	<view class="icon-box"  @click="iconClick" :class="[ labelPos?'label-pos--' + labelPos:'',alignmentShow==true?'alignment--'+alignment:'']">
          <image
              class="icon-img"
              v-if="isImg"
              :src="name"
              :mode="imgMode?imgMode:''"
              :style="[imgStyle]"
          ></image>
        
		<!-- 阿里自定义矢量图组件  接受字体大小和字体颜色参数以及图标类名-->
		<text v-else class="iconfont gradient"  :class="name" :style="{
			fontSize:size?size+'rpx':'24rpx',
			lineHeight:size?size+'rpx':'24rpx',
			fontWeight: bold ?bold: 'normal',
			color:color?color:''
		}"></text>
		<!-- labe文字 -->
		<text v-if="label"
			:style="{
				fontSize:labelSize?labelSize+'rpx':'24rpx',
				color:labelColor?labelColor:'',
				fontWeight:bold?bold:'normal',
				marginLeft: labelPos == 'right' ? space+'rpx' : 0,
				marginTop: labelPos == 'bottom' ?space+'rpx' : 0,
				marginRight: labelPos == 'left' ?space+'rpx': 0,
				marginBottom: labelPos == 'top' ? space+'rpx' : 0
			}">{{label}}</text>
	</view>


</template>

<script>
	export default {
		name: "ali-icon-calss",
		props: {
			name: { //图标类名
				type: String,
				default: ''
			},
			size: { //图标大小
				type: [String, Number],
				default: ''
			},
			bgColor:{  //背景颜色
				type: String,
				default: ''
			},
			color: { //图标颜色 /当name为图片链接地址时，该属性无效 /该属性只支持阿里字体
				type: String,
				default: '#000000'
			},
			label: { //图标右侧/下方的label文字
				type: String,
				default: ''
			},
			labelSize: { //label字体大小，单位默认rpx
				type: [String, Number],
				default: ''
			},
			labelColor: { //label字体颜色
				type: String,
				default: '#000000'
			},
			space: { //label与图标的距离，单位默认rpx
				type: [String, Number],
				default: ''
			},
			bold: { //label字体加粗 
				type: [String, Number],
				default: '400'
			},
			labelPos: { //label相对于图标的位置  //值为bottom / top / left/right
				type: String,
				default: 'right'
			},
			// 触摸图标时的类名
			hoverClass: {
				type: String,
				default: ''
			},
			// 是否开启 label与图标对齐
			alignmentShow:{  
				type: Boolean, //默认否
				default: false
			},
			// 对齐方式
			alignment: {//两端对齐 between  终点对齐 end  起点对齐start 居中对齐center  //注意：alignmentShow与alignment搭配使用，单独使用否则不生效
				type: String,
				default: ''
			},
			imgMode:{//图片mode模式
				type: String,
				default: ''
			},
			width:{ //使用图片的时候图片宽
				type: [String, Number],
				default: ''
			},
			height:{//使用图片的时候图片高度
				type: [String, Number],
				default: ''
			},
			gradientsShow:{ //开启渐变属性  //暂时未涉及到，暂时不处理
			      	type: Boolean, //默认否
					default: false
			},
		     gradients:{ //渐变属性值 //暂时未涉及到，暂时不处理
				 type: String,
				 default: ''
			 }
			
		},

		data() {
			return {

			};
		},
		computed:{
			// 判断传入的name属性，是否图片路径，只要带有"/"均认为是图片形式
			isImg() {
				return this.name.indexOf('/') !== -1
			},
			imgStyle() {
				let style = {};
				// 如果设置width和height属性，则优先使用，否则使用size属性
				style.width = this.width ? this.width+'rpx' : this.size+'rpx'
				style.height = this.height ? this.height+'rpx' : this.size+'rpx'
				return style
			},
		},
		
		methods: {
			iconClick() {
				this.$emit('iconClick')
			}
		}


	}
</script>

<style lang="scss" scoped>
	@import url("/static/css/iconfont/calss/iconfont.css");

	// 为了该组件文件可以不用复制外部flex文件情况下再其他项目开箱即用,故不用公共样式
   
	.icon-box {
		display: flex;
		align-items: center;
	}
.icon-img{
			/* #ifndef APP-NVUE */
			height: auto;
			will-change: transform;
			/* #endif */
		}
		.alignment{
			display: flex;
			align-items: center;
			&--center{
					justify-content: center
			}
			&--end{
				justify-content: flex-end
			}
			&--between{
				justify-content: space-between;
			}
			&--start{
				justify-content: flex-start;
			}
		}
		.label-pos {
				display: flex;
			/* #ifndef APP-NVUE */
			display: flex;
			/* #endif */
			align-items: center;
		
			&--left {
				flex-direction: row-reverse;
				align-items: center;
			}
		
			&--right {
				flex-direction: row;
				align-items: center;
			}
		
			&--top {
				flex-direction: column-reverse;
				justify-content: center;
			}
		
			&--bottom {
				flex-direction: column;
				justify-content: center;
			}
		}


</style>
