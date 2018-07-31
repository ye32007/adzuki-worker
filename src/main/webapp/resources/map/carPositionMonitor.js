var TrackPlayer = {
	data: [],			// 原始数据
	lineArr: [],		// 轨迹数据
	polyline: null,		// 轨迹线
	infoWindow: null,	// 轨迹信息窗口
	startMarker: null,	// 轨迹起点覆盖物
	endMarker: null,	// 轨迹终点覆盖物
	pathSimplifierIns: null, //轨迹展示组件
	navigator: null,	//轨迹巡航器
	currnetIndex: 0,	// 轨迹播放的当前位置
	open: function(type, data) {
		map.clearMap();
		// 数据初始化
		this.currnetIndex = 0;
		this.data = data;
		this.createLineArr();
		
		// 展示
		if(type == 'show') {
			this.createShowPanel();
			this.createPathSimplifier();
			this.createStartMarker();
			this.createEndMarker();
	   		map.setFitView();
			
		// 回放
		} else {
			this.createPlayPanel();
			this.createStartMarker();
			this.createEndMarker();
			this.createInfoWindow();
	   		
			this.createPathSimplifier();
			this.createNavigator();
			
	   		map.setFitView();
			this.playPause();
		}
		
		// 颜色选择按钮绑定click事件
		$('.ctrlColors > span').click(function(){
			$('.ctrlColors > span').removeClass('ctrlColorActive');
			$(this).addClass('ctrlColorActive');
			TrackPlayer.pathSimplifierIns.render();
		});
	},
	close: function() {
		map.clearMap();
		this.currnetIndex = 0;
		$('.ctrlPop').remove();
		if(this.navigator) {
			this.navigator.destroy();
		}
		if(this.pathSimplifierIns) {
			this.pathSimplifierIns.hide();
		}
		if(this.onclose) {
			this.onclose();
		}
	},
	onclose: null,
	createLineArr: function() {
		this.lineArr = [];
		for (var i = 0; i < this.data.items.length; i++) {
			this.lineArr.push(this.data.items[i].lnglat.split(','));
		}
	},
	createStartMarker: function() {
		if(this.startMarker) map.remove(this.startMarker);
		this.startMarker = new AMap.Marker({
            map: map,
            position: this.lineArr[0],
            icon: new AMap.Icon({
            	imageSize:new AMap.Size(24,30),
            	image:'/resources/image/track_start.png'
            }),
            offset: new AMap.Pixel(-12, -32)
        });
	},
	createEndMarker: function() {
		if(this.endMarker) map.remove(this.endMarker);
		this.endMarker = new AMap.Marker({
            map: map,
            position: this.lineArr[this.lineArr.length - 1],
            icon: new AMap.Icon({
            	imageSize:new AMap.Size(24,30),
            	image:'/resources/image/track_end.png'
            }),
            offset: new AMap.Pixel(-12, -32)
        });
	},
	createInfoWindow: function(info) {
		if(this.infoWindow == null) {
			this.infoWindow = new AMap.InfoWindow({autoMove:false,offset: new AMap.Pixel(0, -17)});
		}
		
		var content = "";
		content += '<div class="trackInfoWindow">';
		content += '	<div><span>　车牌号：</span><div>' + this.data.carNo + '</div></div>';
		content += '	<div><span>路径长度：</span><div>' + this.data.mileage + '</div></div>';
		content += '	<div><span>　　起点：</span><div>' + this.data.startPosition + '</div></div>';
		content += '	<div><span>　　终点：</span><div>' + this.data.endPosition + '</div></div>';
		content += '	<div><span>　　时间：</span><div id="cd_time">' + this.data.startTime + '</div></div>';
		content += '	<div><span>　　速度：</span><div id="cd_speed">' + (this.data.items[0].speed!=null?(this.data.items[0].speed + 'km/h'):'-') + '</div></div>';
		content += '	<div><span>　　SOC：</span><div id="cd_soc">' + (this.data.items[0].soc!=null?(this.data.items[0].soc + '%'):'-') + '</div></div>';
		content += '</div>';
		
		this.infoWindow.setContent(content);
		this.infoWindow.open(map, this.lineArr[0]);
	},
	createPathSimplifier: function() {
		if(this.pathSimplifierIns == null) {
			this.pathSimplifierIns = new PathSimplifier({
	            zIndex: 100,
	            clickToSelectPath:false,
	            autoSetFitView:true,
	            eventSupport:false,
	            map: map, 
	            getPath: function(pathData, pathIndex) {
	                return pathData.path;
	            },
	            renderOptions: {
	            	eventSupport: false,
	            	eventSupportInvisible: false,
	                getPathStyle: function(pathItem, zoom) {
	                    var color = $('.ctrlColors > span.ctrlColorActive').css('background-color');
	                    return {
	                        pathLineStyle: {
	                            strokeStyle: color,
	                            dirArrowStyle: true,
	                            lineWidth: 4
	                        }
	                    };
	                }
	            }
	        });
		}
		
		this.pathSimplifierIns.setData([{
	        path: this.lineArr
	    }]);
		
		this.pathSimplifierIns.show();
	},
	createNavigator: function() {
		this.navigator = this.pathSimplifierIns.createPathNavigator(0, {
            loop: true,
            speed: 200,
            pathNavigatorStyle: {
            	//initRotateDegree:-90,
                width: 36,
                height: 36,
                autoRotate:false,
                content: PathSimplifier.Render.Canvas.getImageContent('/resources/image/track_car.png'),
                strokeStyle: null,
                fillStyle: null
            }
        });
		
		this.navigator.on('move', function(r){
			map.setCenter(TrackPlayer.navigator.getPosition());
			if(TrackPlayer.currnetIndex != r.target.cursor.idx) {
				$('#cd_time').text(E7.dateFormat(TrackPlayer.data.items[TrackPlayer.currnetIndex].timestamp));
				$('#cd_speed').text(TrackPlayer.data.items[TrackPlayer.currnetIndex].speed!=null?(TrackPlayer.data.items[TrackPlayer.currnetIndex].speed + 'km/h'):'-');
				$('#cd_soc').text(TrackPlayer.data.items[TrackPlayer.currnetIndex].soc!=null?(TrackPlayer.data.items[TrackPlayer.currnetIndex].soc + '%'):'-');
			}
			
			TrackPlayer.infoWindow.setPosition(TrackPlayer.navigator.getPosition());
			TrackPlayer.currnetIndex = r.target.cursor.idx;
        });
	},
	createShowPanel: function() {
		var show = $(_showPanelHtml);
		show.find('#cd_carNo').text(this.data.carNo);
		show.find('#cd_mileage').text(this.data.mileage);
		show.find('#cd_startTime').text(this.data.startTime);
		show.find('#cd_startPosition').text(this.data.startPosition);
		show.find('#cd_endTime').text(this.data.endTime);
		show.find('#cd_endPosition').text(this.data.endPosition);
		
		$('body').append(show);
	},
	createPlayPanel: function() {
		var play = $(_playPanelHtml);
		play.find('#cd_carNo').text(this.data.carNo);
		$('body').append(play);
	},
	playPause: function() {
		if($('.ctrlBtn > .glyphicon-play').length > 0) {
			$('.ctrlBtn > .glyphicon-play').addClass('glyphicon-pause');
			$('.ctrlBtn > .glyphicon-play').removeClass('glyphicon-play');
			if(this.currnetIndex == 0) {
				this.navigator.start();
			} else {
				this.navigator.resume();
			}
		} else {
			$('.ctrlBtn > .glyphicon-pause').addClass('glyphicon-play');
			$('.ctrlBtn > .glyphicon-pause').removeClass('glyphicon-pause');
			this.navigator.pause();
		}
		
	},
	stop: function() {
		this.navigator.start();
		this.navigator.stop();
		this.currnetIndex = 0;
		this.createInfoWindow();
		$('.ctrlBtn > .glyphicon-pause').addClass('glyphicon-play');
		$('.ctrlBtn > .glyphicon-pause').removeClass('glyphicon-pause');
		map.setFitView();
	},
	speed: function(multiple) {
		this.navigator.setSpeed(this.navigator.getSpeed() * multiple);
	}
};

// 面板头部
var _pannelHeaderHtml = '';
_pannelHeaderHtml += '<div class="ctrlPop">';
_pannelHeaderHtml += '	<div class="ctrlTitle">';
_pannelHeaderHtml += '		<b>轨迹展示</b>';
_pannelHeaderHtml += '		<span class="close" onclick="TrackPlayer.close();">x</span>';
_pannelHeaderHtml += '	</div>';
_pannelHeaderHtml += '	<div class="ctrlCarNo" id="cd_carNo"></div>';

// 面板尾部
var _pannelFooterHtml = '';
_pannelFooterHtml += '	<div class="ctrlColorTitle">&nbsp;</div>';
_pannelFooterHtml += '	<div class="ctrlColorTitle">切换颜色</div>';
_pannelFooterHtml += '	<div class="ctrlColors">';
_pannelFooterHtml += '		<span class="ctrlColorActive" style="background-color: #D9544F;">&nbsp;</span>';
_pannelFooterHtml += '		<span style="background-color: #A8BD7C;">&nbsp;</span>';
_pannelFooterHtml += '		<span style="background-color: #EFAD4D;">&nbsp;</span>';
_pannelFooterHtml += '		<span style="background-color: #5D87AF;">&nbsp;</span>';
_pannelFooterHtml += '	</div>';
_pannelFooterHtml += '</div>';

//展示控制面板
var _showPanelHtml = _pannelHeaderHtml;
_showPanelHtml += '	<div class="ctrlDetail">';
_showPanelHtml += '		<div><span>路径长度：</span><div id="cd_mileage"></div></div>';
_showPanelHtml += '		<div><span>　　起点：</span><div id="cd_startPosition"></div></div>';
_showPanelHtml += '		<div><span>起点时间：</span><div id="cd_startTime"></div></div>';
_showPanelHtml += '		<div><span>　　终点：</span><div id="cd_endPosition"></div></div>';
_showPanelHtml += '		<div><span>终点时间：</span><div id="cd_endTime"></div></div>';
_showPanelHtml += '	</div>';
_showPanelHtml += _pannelFooterHtml;

//回放控制面板
var _playPanelHtml = _pannelHeaderHtml;
_playPanelHtml += '	<div class="ctrlBtn">';
_playPanelHtml += '		<span class="glyphicon glyphicon-stop" aria-hidden="true" onclick="TrackPlayer.stop();"></span>';
_playPanelHtml += '		<span class="glyphicon glyphicon-backward" aria-hidden="true" onclick="TrackPlayer.speed(0.5);"></span>';
_playPanelHtml += '		<span class="glyphicon glyphicon-play" aria-hidden="true" onclick="TrackPlayer.playPause();"></span>';
_playPanelHtml += '		<span class="glyphicon glyphicon-forward" aria-hidden="true" onclick="TrackPlayer.speed(2);"></span>';
_playPanelHtml += '		<span class="glyphicon glyphicon-eject" aria-hidden="true" onclick="TrackPlayer.close();"></span>';
_playPanelHtml += '	</div>';
_playPanelHtml += _pannelFooterHtml;
