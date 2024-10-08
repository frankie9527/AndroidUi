package com.amap.location.demo;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationClientOption.AMapLocationProtocol;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;

/**
 * Created by hongming.wang on 2018/1/29.
 * 后台定位示例
 * <p>
 * 从Android 8.0开始，Android系统为了实现低功耗，Android 8.0系统对后台应用获取位置的频率进行了限制，只允许每小时几次位置更新。
 * 根据Android 8.0的开发指引，为了适配这一系统特性，
 * 高德定位SDK从8.0开始增加了两个新接口enableBackgroundLocation和disableBackgroundLocation用来控制是否开启后台定位。
 * 开启后sdk会生成一个前台服务通知，告知用户应用正在后台运行，使得开发者自己的应用退到后台的时候，仍有前台通知在，提高应用切入后台后位置更新的频率。
 * 如果您的应用在退到后台时本身就有前台服务通知，则无需按照本示例的介绍做适配。<br>
 * 示例中提供了两种方法启动和关闭后台定位功能,请根据业务场景进行相应的修改<br>
 * 1、通过按钮触发，点击按钮调用相应的接口开开启或者关闭后台定位，此种方法主要是更直观的展示后台定位的功能
 * 2、通过生命周期判断APP是否处于后台，当处于后台时才开启后台定位功能，恢复到前台后关闭后台定位功能
 * </p>
 */
public class Location_BackGround_Activity extends CheckPermissionsActivity
		implements
			OnClickListener{
	private TextView tvResult;
	private Button btLocation;
	private Button btEnableBackgroundLocation;
	private Button btDisableBackgroundLocation;

	private AMapLocationClient locationClient = null;
	private AMapLocationClientOption locationOption = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_background);
		setTitle(R.string.title_locationBackground);
		
		initView();

		initLocation();
	}


	@Override
	protected void onResume() {
		super.onResume();
		//切入前台后关闭后台定位功能
		if(null != locationClient) {
			locationClient.disableBackgroundLocation(true);
		}
	}


	@Override
	protected void onStop() {
		super.onStop();
		boolean isBackground = ((MyApplication)getApplication()).isBackground();
		//如果app已经切入到后台，启动后台定位功能
		if(isBackground){
			if(null != locationClient) {
				locationClient.enableBackgroundLocation(2001, buildNotification());
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	//初始化控件
	private void initView(){

		tvResult = (TextView) findViewById(R.id.tv_result);
		btLocation = (Button) findViewById(R.id.bt_location);
		btEnableBackgroundLocation = (Button) findViewById(R.id.bt_enableBackground);
		btDisableBackgroundLocation = (Button) findViewById(R.id.bt_disableBackground);

		btLocation.setOnClickListener(this);
		btEnableBackgroundLocation.setOnClickListener(this);
		btDisableBackgroundLocation.setOnClickListener(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		destroyLocation();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.bt_location) {
			if (btLocation.getText().equals(
					getResources().getString(R.string.startLocation))) {
				btLocation.setText(getResources().getString(
						R.string.stopLocation));
				tvResult.setText("正在定位...");
				startLocation();
			} else {
				btLocation.setText(getResources().getString(
						R.string.startLocation));
				stopLocation();
				tvResult.setText("定位停止");
			}
		}

		if(v.getId() == R.id.bt_enableBackground){
			if(null == locationClient){
				try {
					locationClient = new AMapLocationClient(this);
					//启动后台定位
					locationClient.enableBackgroundLocation(2001, buildNotification());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		if(v.getId() == R.id.bt_disableBackground){
			if(null == locationClient){
				try {
					locationClient = new AMapLocationClient(this);
					//关闭后台定位
					locationClient.disableBackgroundLocation(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}
	
	/**
	 * 初始化定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void initLocation(){
		//初始化client
		try {
			locationClient = new AMapLocationClient(this.getApplicationContext());
			locationOption = getDefaultOption();
			//设置定位参数
			locationClient.setLocationOption(locationOption);
			// 设置定位监听
			locationClient.setLocationListener(locationListener);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 默认的定位参数
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private AMapLocationClientOption getDefaultOption(){
		AMapLocationClientOption mOption = new AMapLocationClientOption();
		mOption.setLocationMode(AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
		mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
		mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
		mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
		mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
		mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
		mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
		AMapLocationClientOption.setLocationProtocol(AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
		mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
		mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
		mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
		return mOption;
	}
	
	/**
	 * 定位监听
	 */
	AMapLocationListener locationListener = new AMapLocationListener() {
		@Override
		public void onLocationChanged(AMapLocation location) {
			if (null != location) {

				StringBuffer sb = new StringBuffer();
				//errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
				if(location.getErrorCode() == 0){
					sb.append("定位成功" + "\n");
					sb.append("定位类型: " + location.getLocationType() + "\n");
					sb.append("经    度    : " + location.getLongitude() + "\n");
					sb.append("纬    度    : " + location.getLatitude() + "\n");
					sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
					sb.append("提供者    : " + location.getProvider() + "\n");

					sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
					sb.append("角    度    : " + location.getBearing() + "\n");
					// 获取当前提供定位服务的卫星个数
					sb.append("星    数    : " + location.getSatellites() + "\n");
					sb.append("国    家    : " + location.getCountry() + "\n");
					sb.append("省            : " + location.getProvince() + "\n");
					sb.append("市            : " + location.getCity() + "\n");
					sb.append("城市编码 : " + location.getCityCode() + "\n");
					sb.append("区            : " + location.getDistrict() + "\n");
					sb.append("区域 码   : " + location.getAdCode() + "\n");
					sb.append("地    址    : " + location.getAddress() + "\n");
					sb.append("地    址    : " + location.getDescription() + "\n");
					sb.append("兴趣点    : " + location.getPoiName() + "\n");
					//定位完成的时间
					sb.append("定位时间: " + Utils.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
				} else {
					//定位失败
					sb.append("定位失败" + "\n");
					sb.append("错误码:" + location.getErrorCode() + "\n");
					sb.append("错误信息:" + location.getErrorInfo() + "\n");
					sb.append("错误描述:" + location.getLocationDetail() + "\n");
				}
				sb.append("***定位质量报告***").append("\n");
				sb.append("* WIFI开关：").append(location.getLocationQualityReport().isWifiAble() ? "开启":"关闭").append("\n");
				sb.append("* GPS状态：").append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus())).append("\n");
				sb.append("* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites()).append("\n");
				sb.append("****************").append("\n");
				//定位之后的回调时间
				sb.append("回调时间: " + Utils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");

				//解析定位结果，
				String result = sb.toString();
				tvResult.setText(result);
			} else {
				tvResult.setText("定位失败，loc is null");
			}
		}
	};


	/**
	 * 获取GPS状态的字符串
	 * @param statusCode GPS状态码
	 * @return
	 */
	private String getGPSStatusString(int statusCode){
		String str = "";
		switch (statusCode){
			case AMapLocationQualityReport.GPS_STATUS_OK:
				str = "GPS状态正常";
				break;
			case AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER:
				str = "手机中没有GPS Provider，无法进行GPS定位";
				break;
			case AMapLocationQualityReport.GPS_STATUS_OFF:
				str = "GPS关闭，建议开启GPS，提高定位质量";
				break;
			case AMapLocationQualityReport.GPS_STATUS_MODE_SAVING:
				str = "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量";
				break;
			case AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION:
				str = "没有GPS定位权限，建议开启gps定位权限";
				break;
		}
		return str;
	}

	/**
	 * 开始定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void startLocation(){
		try {
			// 设置定位参数
			locationClient.setLocationOption(locationOption);
			// 启动定位
			locationClient.startLocation();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 停止定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void stopLocation(){
		try {
			// 停止定位
			locationClient.stopLocation();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	/**
	 * 销毁定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void destroyLocation(){
		if (null != locationClient) {
			/**
			 * 如果AMapLocationClient是在当前Activity实例化的，
			 * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
			 */
			locationClient.onDestroy();
			locationClient = null;
			locationOption = null;
		}
	}

	private void createNotificationChannel(){

	}


	private static final String NOTIFICATION_CHANNEL_NAME = "BackgroundLocation";
	private NotificationManager notificationManager = null;
	boolean isCreateChannel = false;
	@SuppressLint("NewApi")
	private Notification buildNotification() {

		Notification.Builder builder = null;
		Notification notification = null;
		if(android.os.Build.VERSION.SDK_INT >= 26) {
			//Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
			if (null == notificationManager) {
				notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			}
			String channelId = getPackageName();
			if(!isCreateChannel) {
				NotificationChannel notificationChannel = new NotificationChannel(channelId,
						NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
				notificationChannel.enableLights(true);//是否在桌面icon右上角展示小圆点
				notificationChannel.setLightColor(Color.BLUE); //小圆点颜色
				notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
				notificationManager.createNotificationChannel(notificationChannel);
				isCreateChannel = true;
			}
			builder = new Notification.Builder(getApplicationContext(), channelId);
		} else {
			builder = new Notification.Builder(getApplicationContext());
		}
		builder.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(Utils.getAppName(this))
				.setContentText("正在后台运行")
				.setWhen(System.currentTimeMillis());

		if (android.os.Build.VERSION.SDK_INT >= 16) {
			notification = builder.build();
		} else {
			return builder.getNotification();
		}
		return notification;
	}

}
