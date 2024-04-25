package com.shangzuo.veindemo;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.blankj.utilcode.util.GsonUtils;
import com.google.gson.reflect.TypeToken;
import com.xgzx.ThreadPool;
import com.xgzx.veinmanager.VeinApi;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class VeinUtil {
    private UsbManager mUsbManager;
    private UsbDevice mUsbDev;
    private long score = 60;
    private List<UserVeinInfo> userVeinInfos = new ArrayList<>();
    static boolean mUsbPerMission = false; //USB设备权限
    public long DevHandle = 0; //设备操作句柄
    private boolean isCollect = true;


    private static VeinUtil instance;

    // 私有构造方法，防止外部直接实例化
    private VeinUtil() {
        // 进行初始化操作
        ChmodUsbRW();
    }

    // 获取 VeinUtil 实例的方法
    public static synchronized VeinUtil getInstance() {
        if (instance == null) {
            instance = new VeinUtil();
        }
        return instance;
    }

    public final String ACTION_DEVICE_PERMISSION = "com.linc.USB_PERMISSION";


    public boolean connetVien(Context context) {


        soundPool = new SoundPool(12, AudioManager.STREAM_SYSTEM, 5);
        soundMap.put(1, soundPool.load(context, R.raw.enroll_success_00, 1)); //登记成功
        soundMap.put(2, soundPool.load(context, R.raw.enroll_fail_02, 1)); //登记失败
        soundMap.put(3, soundPool.load(context, R.raw.verify_success_33, 1)); //验证成功
        soundMap.put(4, soundPool.load(context, R.raw.verify_fail_32, 1)); //验证失败
        soundMap.put(5, soundPool.load(context, R.raw.put_finger_27, 1)); //请放手指
        soundMap.put(6, soundPool.load(context, R.raw.put_again_23, 1)); //请再放一次
        soundMap.put(7, soundPool.load(context, R.raw.put_right_26, 1)); //请正确放置手指
        soundMap.put(8, soundPool.load(context, R.raw.b_35, 1)); //滴1
        soundMap.put(9, soundPool.load(context, R.raw.b_36, 1)); //滴2
        soundMap.put(10, soundPool.load(context, R.raw.bb_37, 1)); //滴滴1
        soundMap.put(11, soundPool.load(context, R.raw.bb_38, 1)); //滴滴2

        SerachUSB(context);
        long ret = 0;
        ret = VeinApi.initUsbCommunication(mUsbDev, mUsbManager);
        DevHandle = ret;
        Log.e("TAG", "onClick:=====PrintfDebug=== " + ret);
//        String sDev = "USB:1";//通过SO连接USB设备，安卓系统要有ROOT权限或USB设备文件有读写权限
//        ret = VeinApi.FVConnectDev(sDev, "00000000"); //默认连接密码是8个字符0
//        Log.e("connetVien", "connetVien: " + ret);
        return ret > 0;
    }

    //如果安卓系统没有ROOT权限，则可以用此方法在APP里获取权限，但只能使用APP的通讯函数，不能使用JNI的通讯API
    public int SerachUSB(Context context) {
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_DEVICE_PERMISSION), PendingIntent.FLAG_MUTABLE);
        IntentFilter permissionFilter = new IntentFilter(ACTION_DEVICE_PERMISSION);
        context.registerReceiver(mUsbReceiver, permissionFilter);

        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceHashMap = mUsbManager.getDeviceList();
        Iterator<UsbDevice> iterator = deviceHashMap.values().iterator();
        VeinApi.PrintfDebug("hasNext:" + iterator.hasNext());

        int FoundUserDev = 0;
        while (iterator.hasNext()) {
            mUsbDev = iterator.next();
            VeinApi.PrintfDebug("device name: " + mUsbDev.getDeviceName() + ",device product id:"
                    + mUsbDev.getProductId() + "vendor id:" + mUsbDev.getVendorId());
            if ((mUsbDev.getProductId() & 0xff00) == 0x7600 && (mUsbDev.getVendorId() == 0x2109 || mUsbDev.getVendorId() == 0x200d)) {
                //if (device.getProductId() == 0x7638 && device.getVendorId() == 0x7B09) {
                if (mUsbManager.hasPermission(mUsbDev)) {
                    VeinApi.PrintfDebug("hasPermission..........");
                    mUsbPerMission = true; //已经有权限了
                } else {
                    VeinApi.PrintfDebug("requestPermission..........");
                    mUsbManager.requestPermission(mUsbDev, mPermissionIntent); //还没有权限，通过用户对话框获取授权
                }
                FoundUserDev++;
                break;
            } else {
                VeinApi.PrintfDebug("no our device, pass...");
                continue;
            }
        }
        if (FoundUserDev > 0) {
            Log.e("TAG", "发现USB设备 ");
        } else {
            Log.e("TAG", "未发现USB设备 ");
        }
        return FoundUserDev;
    }

    public void getAllUsers(String url) {//"http://175.27.131.17:9031/business/SzBaseuser/list"
        Map<String, String> params = new HashMap<>();
        // 添加参数
        params.put("OnlyFinger", "true");
        params.put("PageSize", "1000");
        params.put("PageNum", "1");
        ThreadPool.run(() -> {
            try {
                String response = NetUtils.Companion.sendGetRequest(url, params);
                Type type = new TypeToken<BaseModel<BasePage<List<UserVeinInfo>>>>() {
                }.getType();

                BaseModel<BasePage<List<UserVeinInfo>>> info = GsonUtils.fromJson(response, type);
                userVeinInfos.clear();
                userVeinInfos.addAll(info.getData().getResult());
                Log.e("TAG", "response: " + userVeinInfos.size());
            } catch (Exception e) {
                Log.e("TAG", "===json解析出错===:" + e);
            }
        });
    }


    public void toVerify() {
        GetCharaThread mGetCharaThread = new GetCharaThread();
        mGetCharaThread.start();
    }

    public void toCollect() {
        DebugMsg("开始采集");
        if (DevHandle <= 0) {
            DebugMsg("请先连接设备");
            return;
        }
        //GetTemp(); //采集指静脉模板，通过获取3次特征融合，采集后的模板在sTemp
        GetTempThread mGetTempThread = new GetTempThread();
        mGetTempThread.start();
    }

    class GetCharaThread extends Thread {
        @Override
        public void run() {
            while (true) {
                GetChara();
            }
        }
    }

    class GetTempThread extends Thread {
        @Override
        public void run() {
            GetTemp();
        }
    }

    public void GetChara() {
        String sThreadMsg = "";
        String sChara = VeinApi.GetVeinChara(DevHandle, 10000); //通过指静脉设备采集特征，等待手指放入超时为6秒
        if (sChara.length() < 10) {
            int ret = 0;
            try {
                ret = Integer.parseInt(sChara);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            PlayVoice(VeinApi.XG_VOICE_BEEP11, true);
            if (ret == -16) {
                sThreadMsg = "此设备不支持特征采集";
                DebugMsg(sThreadMsg);
            } else if (ret == -11) {
                sThreadMsg = "手指检测超时";
                DebugMsg(sThreadMsg);
            } else if (ret == -17) {
                sThreadMsg = "请正确放置手指";
                VeinApi.PrintfDebug(sThreadMsg);
                try {
                    Thread.sleep(500); //等语音播完
                } catch (InterruptedException e) {
                }
                PlayVoice(VeinApi.XG_VOICE_PUTFINGER_RIGHT, true);
            } else {
                sThreadMsg = "特征采集失败:" + sChara;
                DebugMsg(sThreadMsg);
            }
        } else {
            PlayVoice(VeinApi.XG_VOICE_BEEP1, true);
            sThreadMsg = "特征采集成功";
            DebugMsg(sThreadMsg);
            Log.e("TAG", "userVeinInfos: " + userVeinInfos.size());
            for (UserVeinInfo userVeinInfo : userVeinInfos) {
                String code = "";
                if (userVeinInfo.getFingerDataoneStr() != null && !userVeinInfo.getFingerDataoneStr().isEmpty()) {
                    code = VeinApi.FVVerifyUser(userVeinInfo.getFingerDataoneStr(), sChara, score);
                    Log.e("TAG", "GetChara 1===: "  + "  name:" + userVeinInfo.getUserName());
                    if (code.length() > 10) {
                        Log.e("TAG", "GetChara: 1==== 对比成功="  + "  name:" + userVeinInfo.getUserName());
                        LiveDataEvent.INSTANCE.getVeinUser().postValue(userVeinInfo);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                        break;
                    }
                }
                if (userVeinInfo.getFingerDatatwoStr() != null && !userVeinInfo.getFingerDatatwoStr().isEmpty()) {
                    code = VeinApi.FVVerifyUser(userVeinInfo.getFingerDatatwoStr(), sChara, score);
                    Log.e("TAG", "GetChara: 2="  + "  name:" + userVeinInfo.getUserName());
                    if (code.length() > 10) {
                        Log.e("TAG", "GetChara: 2==== 对比成功="  + "  name:" + userVeinInfo.getUserName());
                        LiveDataEvent.INSTANCE.getVeinUser().postValue(userVeinInfo);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                        break;
                    }
                }

            }

        }

    }

    public void GetTemp() {
        int MaxCharaNum = 3;
        int error = -1;
        if (DevHandle <= 0) {
            DebugMsg("请先连接设备");
            return;
        }
        String sTemp = ""; //当前采集的模板

        String sThreadMsg = "";
        String[] ssChara = new String[MaxCharaNum];
        String ssTemp = "";
        int CharaNum = 0;
        PlayVoice(VeinApi.XG_VOICE_PUTFINGER, true);
        while (isCollect) {
            ssChara[CharaNum] = VeinApi.GetVeinChara(DevHandle, 6000);//通过指静脉设备采集特征，等待手指放入超时为6秒
            if (ssChara[CharaNum].length() < 10) {
                PlayVoice(VeinApi.XG_VOICE_BEEP11, true);
                try {
                    error = Integer.parseInt(ssChara[CharaNum]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (error == -16) {
                    sThreadMsg = "此设备不支持特征采集";
                    DebugMsg(sThreadMsg);
                    LiveDataEvent.INSTANCE.getVeinString().postValue(sThreadMsg);
                    break;
                } else if (error == -11) {
                    sThreadMsg = "手指检测超时";
                    DebugMsg(sThreadMsg);
                    LiveDataEvent.INSTANCE.getVeinString().postValue(sThreadMsg);
                    break;
                } else if (error == -17) { //特征采集失败可以继续采集下一次
                    sThreadMsg = "请正确放置手指";
                    VeinApi.PrintfDebug(sThreadMsg);
                    try {
                        Thread.sleep(500); //等语音播完
                    } catch (InterruptedException e) {
                    }
                    PlayVoice(VeinApi.XG_VOICE_PUTFINGER_RIGHT, true);
                    try {
                        Thread.sleep(2000); //等语音播完
                    } catch (InterruptedException e) {
                    }
                } else {
                    sThreadMsg = "采集失败:" + ssChara[CharaNum];
                    DebugMsg(sThreadMsg);
                    LiveDataEvent.INSTANCE.getVeinString().postValue("采集失败");
                    break;
                }
            } else {
                error = 0;
                PlayVoice(VeinApi.XG_VOICE_BEEP1, true);

                //通过增加特征的这个函数来创建模板，一个模板最多可以增加6个特征，等同于FVCreateVeinTemp
                ssTemp = VeinApi.FVAddCharaToTemp(ssTemp, ssChara[CharaNum], null, 0);
                if (ssTemp.length() < 10) {
                    sThreadMsg = "增加特征失败:" + ssTemp;
                    DebugMsg(sThreadMsg);
                } else {
                    sThreadMsg = "增加特征成功";
                    DebugMsg(sThreadMsg);
                }

                CharaNum++;
                if (CharaNum == 2) {
                    //检测2次采集的特征是不是同一个手指，可以不检测
                    long ret = VeinApi.FVCharaMatch(ssChara[0], ssChara[1], 60);
                    if (ret != 0) {
                        DebugMsg("不是同一根手指");
                    }
                }
            }
            if (CharaNum >= MaxCharaNum) {
                break;
            }
            PlayVoice(VeinApi.XG_VOICE_PUTFINGER_AGAIN, true);
            //等待手指拿开采集下一次
            VeinApi.CheckFinger(DevHandle, 10000, 0);
        }
        if (error == 0 && isCollect) {
            //通过采集的3个特征融合为一个模板，也可以同时导入用户信息，没有用户信息bUserInfo为null就行
            sTemp = VeinApi.FVCreateVeinTemp(ssChara[0], ssChara[1], ssChara[2], null, 0);
            if (sTemp.length() > 10) {
                PlayVoice(VeinApi.XG_VOICE_ENRORLL_SUCCESS, true);
                sThreadMsg = "模板采集成功,长度:" + sTemp.length();
                DebugMsg(sThreadMsg);
                LiveDataEvent.INSTANCE.getVeinString().postValue(sTemp);
            } else {
                PlayVoice(VeinApi.XG_VOICE_ENRORLL_FAIL, true);
                sThreadMsg = "模板融合失败:" + sTemp;
                DebugMsg(sThreadMsg);
                LiveDataEvent.INSTANCE.getVeinString().postValue("模板融合失败");
            }
        }
    }

    private void DebugMsg(String sThreadMsg) {
        Log.e("TAG", "DebugMsg: " + sThreadMsg);
    }

    private SoundPool soundPool; //本机语音播放
    Map<Integer, Integer> soundMap = new HashMap<Integer, Integer>();

    protected void PlayVoice(int id, boolean DevPlay) {
        if (DevPlay)
            VeinApi.PlayDevSound(DevHandle, id);
        switch (id) {
            case VeinApi.XG_VOICE_ENRORLL_SUCCESS:
                soundPool.play(soundMap.get(1), 1, 1, 1, 0, 1);
                break;
            case VeinApi.XG_VOICE_ENRORLL_FAIL:
                soundPool.play(soundMap.get(2), 1, 1, 1, 0, 1);
                break;
            case VeinApi.XG_VOICE_VERIFY_SUCCESS:
                soundPool.play(soundMap.get(3), 1, 1, 1, 0, 1);
                break;
            case VeinApi.XG_VOICE_VERIFY_FAIL:
                soundPool.play(soundMap.get(4), 1, 1, 1, 0, 1);
                break;
            case VeinApi.XG_VOICE_PUTFINGER:
                soundPool.play(soundMap.get(5), 1, 1, 1, 0, 1);
                break;
            case VeinApi.XG_VOICE_PUTFINGER_AGAIN:
                soundPool.play(soundMap.get(6), 1, 1, 1, 0, 1);
                break;
            case VeinApi.XG_VOICE_PUTFINGER_RIGHT:
                soundPool.play(soundMap.get(7), 1, 1, 1, 0, 1);
                break;
            case VeinApi.XG_VOICE_BEEP1:
                soundPool.play(soundMap.get(8), 1, 1, 1, 0, 1);
                break;
            case VeinApi.XG_VOICE_BEEP2:
                soundPool.play(soundMap.get(9), 1, 1, 1, 0, 1);
                break;
            case VeinApi.XG_VOICE_BEEP11:
                soundPool.play(soundMap.get(10), 1, 1, 1, 0, 1);
                break;
            case VeinApi.XG_VOICE_BEEP22:
                soundPool.play(soundMap.get(11), 1, 1, 1, 0, 1);
                break;
        }
    }


    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    //这个要有ROOT权限才能执行
    public void ChmodUsbRW() {
        String command = "chmod -R 777 /dev/bus/usb";
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            VeinApi.PrintfDebug("BroadcastReceiver in");
            if (ACTION_DEVICE_PERMISSION.equals(action)) {
                synchronized (this) {
                    Log.e("TAG", "onReceive: in");
                    mUsbDev = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (mUsbDev != null) {
                            VeinApi.PrintfDebug("device name: " + mUsbDev.getDeviceName() + ",device product id:"
                                    + mUsbDev.getProductId() + "vendor id:" + mUsbDev.getVendorId());
                            VeinApi.PrintfDebug("getInterfaceCount:" + mUsbDev.getInterfaceCount());
                            Log.e("onReceive", "USB获取权限成功");
                            mUsbPerMission = true;
                        }
                    } else {
                        Log.e("onReceive", "USB获取权限失败");
                    }
                }
            }
        }
    };
}
