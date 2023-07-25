<template>
	<view class="content" @touchmove.stop.prevent="" v-if="isShow&&canvasContent" @click.stop="close">
		<view class="">
			<!-- #ifdef APP||MP -->
			<canvas @click.stop="" style="position: fixed;top: -10000px;"
				:style="{ width: canvasW + 'rpx', height: canvasH + 'rpx' }" :canvas-id="canvasId"></canvas>
			<!-- #endif -->
			<!-- #ifdef H5 -->
			<canvas @click.stop="" style="position: fixed;top: -10000px;left: 0; right: 0;margin: auto;"
				:style="{ width: canvasW + 'rpx', height: canvasH + 'rpx' }" :canvas-id="canvasId"></canvas>
			<!-- #endif -->
			<!-- #ifdef APP-PLUS -->
			<image :style="{ width: canvasW + 'rpx', height: canvasH + 'rpx' }" mode="widthFix"
				:src="canvasToTempFile.tempFilePath">
			</image>
			<!-- #endif -->

			<!-- #ifdef MP-WEIXIN -->
			<image :style="{ width: canvasW + 'rpx', height: canvasH + 'rpx' }" mode="widthFix"
				:src="canvasToTempFile.tempFilePath" :show-menu-by-longpress="true">
			</image>
			<!-- #endif -->

			<!-- #ifdef H5 -->
			<image class="h5-canvas-img" :style="{ width: canvasW + 'rpx', height: canvasH + 'rpx', }" mode="widthFix"
				:src="canvasToTempFile">
			</image>
			<!-- #endif -->
		</view>

		<!-- #ifdef APP-PLUS||MP-WEIXIN -->
		<view v-if="isShow&&canvasContent&&canvasToTempFile"
			class="poster-btn border-radius16 t-color-blue t-center bg-white margin-t20" @click="preservation">
			保存图片
		</view>
		<!-- #endif -->
		<!-- #ifdef H5 -->
		<view v-if="isShow&&canvasContent&&canvasToTempFile"
			class="poster-btn border-radius16 t-color-blue t-center bg-white margin-t20" @click="preservation">
			关闭
		</view>
		<!-- #endif -->

	</view>
</template>

<script>
	import {
		getImageInfo,
		base64ToPath,
		saveImageToPhotosAlbum
	} from "@/utils/common.js"
	// import {
	// 	uploadBase64Image
	// } from "@/api/publics.js"
	export default {
		props: {
			canvasW: { //画布宽度
				type: Number,
				default: 0

			},
			radius: { //圆角值
				type: Number,
				default: 0
			},
			canvasId: { //画布id
				type: String,
				default: ''
			},
			isShow: { //是否显示画布
				type: Boolean,
				default: false
			},
			canvasH: { //画布高度
				type: Number,
				default: 0
			},
			bgImg: { //背景图 背景颜色和背景图只能二选一
				type: String,
				default: ''
			},
			bgColor: { //背景颜色 背景颜色和背景图只能二选一
				type: String,
				default: ''
			},
			canvasContent: { //海报内容
				type: Object
			}
		},
		data() {
			return {
				ctx: null, //画布实例
				canvasToTempFile: null, // 保存最终生成的导出的图片对象
				showSaveImgWin: false //保存图片到相册
			}
		},
		created() {
			let that = this

			that.initCanvas();
		},
		methods: {
			close() {
				this.$emit('closeCanvas', false)
			},
			preservation() {
				let that = this;
				// #ifdef APP-PLUS||MP
				let imgList = [];
				imgList.push(this.canvasToTempFile)
				saveImageToPhotosAlbum(imgList, () => {
					setTimeout(() => {
						that.$emit('preservation', {
							posterShow: false,
							isShow: false,
							maskBe: false
						})
						that.canvasToTempFile = null
						that.showSaveImgWin = false
					}, 1000)
				})
				// #endif
				// #ifdef H5
				that.$emit('preservation', {
					posterShow: false,
					isShow: false,
					maskBe: false
				})
				that.canvasToTempFile = null
				that.showSaveImgWin = false
				// #endif


			},
			async initCanvas() { //初始化画布
				uni.showLoading({
					title: '生成中...'
				});
				this.ctx = uni.createCanvasContext(this.canvasId, this)
				this.ctx.fillRect(0, 0, uni.upx2px(this.canvasW + 10), uni.upx2px(this.canvasH + 10))

				if (this.bgImg) { //海报背景
					let bgUrl = '';

					bgUrl = new Promise(resolve => {
						uni.downloadFile({
							//生成临时地址
							url: bgImg,
							success: res => {
								resolve(res.tempFilePath);
							},
							fail: erros => {
								uni.showToast({
									title: '网络错误请重试',
									icon: 'loading'
								});
							}
						});
					});
					this.ctx.drawImage(this.bgImg, 0, 0, this.canvasW, this.canvasH);
				} else {
					//设置画布背景透明
					this.ctx.clearRect(0, 0, this.canvasW, this.canvasH)
					this.ctx.setFillStyle('rgba(255, 255, 255, 0)')
					//设置画布大小
					this.drawRoundRect(this.ctx, 0, 0, uni.upx2px(this.canvasW), uni.upx2px(this.canvasH), uni.upx2px(
						this.radius), this.bgColor);
				}

				//获取 图片
               if(this.canvasContent.mainImg){
				   let headerImg = await getImageInfo(this.canvasContent.mainImg.url)
				    
				   this.drawRoundImg(this.ctx, headerImg.path, uni.upx2px(this.canvasContent.mainImg.l), uni.upx2px(this
				   		.canvasContent.mainImg.top), uni.upx2px(this.canvasContent.mainImg.imgW),
				   	uni.upx2px(this.canvasContent.mainImg.imgH), uni.upx2px(16))
			   }
				
				if (this.canvasContent.title) { //标题
					this.textPrewrap(this.ctx, this.canvasContent.title);
				}
				if (this.canvasContent.content) { //
					this.textPrewrap(this.ctx, this.canvasContent.content);
				}

				if (this.canvasContent.price) { //价格
					this.textPrewrap(this.ctx, this.canvasContent.price);
				}
				if (this.canvasContent.codeTips) { //提示
					this.textPrewrap(this.ctx, this.canvasContent.codeTips);
				}
				//小程序码
				if (this.canvasContent.codeImg) { //提示
					console.log(this.canvasContent.codeImg)

					let qrcodeImg = await getImageInfo(this.canvasContent.codeImg.url);
					this.ctx.drawImage(qrcodeImg.path, uni.upx2px(this.canvasContent.codeImg.l), uni.upx2px(this
							.canvasContent.codeImg.top),
						uni.upx2px(this.canvasContent.codeImg.codeW), uni.upx2px(this.canvasContent.codeImg.codeH))
				}


				//延迟渲染
				setTimeout(() => {
					this.ctx.draw(false, () => {
						uni.canvasToTempFilePath({
								canvasId: this.canvasId,
								quality: 1,
								width: uni.upx2px(this.canvasW + 10),
								height: uni.upx2px(this.canvasH + 10),
								destWidth: uni.upx2px(this.canvasW * 3), //放大解决生成海报模糊
								destHeight: uni.upx2px(this.canvasH * 3), //放大解决生成海报模糊
								success: res => {

									// #ifdef MP-WEIXIN ||APP-PLUS
									this.canvasToTempFile = res;
									// #endif
									// #ifdef H5
									let port = uni.getSystemInfoSync().platform
									if (port == 'ios') {
										base64ToPath(res.tempFilePath).then(res1 => {
											return this.canvasToTempFile = res1;
										})
									} else if (port == 'android') {
										// this.uploadBase64Image(res.tempFilePath).then(res2 => {
										// 	return this.canvasToTempFile = res2;
										// })
									}
									// #endif
									this.showShareImg = true;
									this.toast('海报生成成功', 1000);
								},
								fail: err => {
									this.toast('海报生成失败', 1000);

								},

							},
							this
						);
					})
				}, 1000)
			},
			// async uploadBase64Image(url) {
			// 	let res = await uploadBase64Image({
			// 		base64: url,
			// 		scene: 'poster',
			// 		user_id: this.canvasContent.other.user.user_id,
			// 		id: parseInt(this.canvasContent.other.id),

			// 	})

			// 	if (res.data.code == 200) {
			// 		return res.data.data._src
			// 	}
			// },
			//绘制实心圆
			drawEmptyRound(ctx, x, y, radius) {
				ctx.save()
				ctx.beginPath();
				ctx.arc(x, y, radius, 0, 2 * Math.PI, true);
				ctx.setFillStyle('rgba(0, 0, 0, .4)')
				ctx.fill();
				ctx.restore()
				ctx.closePath()
			},
			//处理文字多出省略号显示
			textPrewrap(ctx, content) {
				ctx.setFontSize(uni.upx2px(content.fontSize));
				ctx.setFillStyle(content.color);
				let endPos = 0; //当前字符串的截断点
				let allRow = Math.ceil(ctx.measureText(content.text).width / content.w); //实际总共能分多少行
				let count = allRow >= content.maxLine ? content.maxLine : allRow; //实际能分多少行与设置的最大显示行数比，谁小就用谁做循环次数
				// 判断内容是否可以一行绘制完毕

				for (let i = 0; i < count; i++) {

					let nowStr = content.text.slice(endPos); //当前剩余的字符串
					let rowWid = content.lineHeight; //每一行当前宽度
					if (ctx.measureText(nowStr).width > content.w) {
						for (let m = 0; m < nowStr.length; m++) {
							rowWid += ctx.measureText(nowStr[m]).width; //当前字符串总宽度
							if (rowWid > content.w) {
								if (i === content.maxLine - 1) {
									//如果是最后一行
									ctx.fillText(nowStr.slice(0, m - 1) + '...', uni.upx2px(content.l), uni.upx2px(
										content.top + (i + 1) * content.lineHeight), content.w);
								} else {
									ctx.fillText(nowStr.slice(0, m), uni.upx2px(content.l), uni.upx2px(content.top + (
										i + 1) * content.lineHeight), content.w);
								}
								endPos += m; //下次截断点
								break;
							}
						}
					} else {
						//如果当前的字符串宽度小于最大宽度就直接输出
						ctx.fillText(nowStr.slice(0), uni.upx2px(content.l), uni.upx2px(content.top + (i + 1) * content
							.lineHeight), content.w);
					}
				}

			},
			//绘制虚线
			drawDashLine(ctx, x1, y1, x2, y2, dashLength) {
				ctx.setStrokeStyle("#cccccc") //设置线条的颜色
				ctx.setLineWidth(1) //设置线条宽度
				var dashLen = dashLength,
					xpos = x2 - x1, //得到横向的宽度;
					ypos = y2 - y1, //得到纵向的高度;
					numDashes = Math.floor(Math.sqrt(xpos * xpos + ypos * ypos) / dashLen);
				//利用正切获取斜边的长度除以虚线长度，得到要分为多少段;
				for (var i = 0; i < numDashes; i++) {
					if (i % 2 === 0) {
						ctx.moveTo(x1 + (xpos / numDashes) * i, y1 + (ypos / numDashes) * i);
						//有了横向宽度和多少段，得出每一段是多长，起点 + 每段长度 * i = 要绘制的起点；
					} else {
						ctx.lineTo(x1 + (xpos / numDashes) * i, y1 + (ypos / numDashes) * i);
					}
				}
				ctx.stroke();
			},
			//带圆角图片
			drawRoundImg(ctx, img, x, y, width, height, radius) {
				ctx.beginPath()
				ctx.save()
				// 左上角
				ctx.arc(x + radius, y + radius, radius, Math.PI, Math.PI * 1.5)
				// 右上角
				ctx.arc(x + width - radius, y + radius, radius, Math.PI * 1.5, Math.PI * 2)
				// 右下角
				ctx.arc(x + width - radius, y + height - radius, radius, 0, Math.PI * 0.5)
				// 左下角
				ctx.arc(x + radius, y + height - radius, radius, Math.PI * 0.5, Math.PI)
				ctx.stroke()
				ctx.clip();
				ctx.drawImage(img, x, y, width, height);
				ctx.restore()
				ctx.closePath()
			},
			//画圆角矩形
			drawRoundRect(ctx, x, y, width, height, radius, color) {
				ctx.save();
				ctx.beginPath(0);
				ctx.setFillStyle(color);
				ctx.setStrokeStyle(color)
				ctx.setLineJoin('round'); //交点设置成圆角
				ctx.setLineWidth(radius);
				ctx.strokeRect(x + radius / 2, y + radius / 2, width - radius, height - radius);
				ctx.fillRect(x + radius, y + radius, width - radius * 2, height - radius * 2);
				ctx.stroke();
				ctx.closePath();
			},
		}

	}
</script>

<style scoped lang="scss">
	.poster-btn {
		width: 100%;
		height: 88rpx;
		 
		 

		line-height: 88rpx;
	}
    .bg-white{
		background-color: white;
	}
	.border-radius16{
		border-radius: 16rpx;
	}
	.margin-t20{
		margin-top: 20rpx;
	}
	.h5-canvas-img {

		z-index: 10080;
	}
     .t-color-blue{
		color: #1A3387;
	 }
	.content {
		display: flex;
		justify-content: center;
		flex-wrap: wrap;
		z-index: 10080;
	}
</style>