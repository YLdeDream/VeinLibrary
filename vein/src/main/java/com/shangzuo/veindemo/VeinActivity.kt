package com.shangzuo.veindemo

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.reflect.TypeToken
import com.shangzuo.veindemo.CustomDialog.OnBack
import com.shangzuo.veindemo.NetUtils.Companion.sendGetRequest
import com.shangzuo.veindemo.NetUtils.Companion.sendPostRequest
import com.xgzx.ThreadPool


class VeinActivity : AppCompatActivity() {
    private val data = MutableLiveData<String>()
    private val userVeinInfos: ArrayList<UserVeinInfo> = ArrayList()
    private val userAdapter: UserSettingAdapter by lazy { UserSettingAdapter() }
    lateinit var customDialog: CustomDialog // 在类的顶部声明变量
    private var userId = ""
    private lateinit var tv_count :TextView

    private var isVisible = true
    private var isFirst = true
    val countdownTimer = object : CountDownTimer(VeinUrl.seconds*1000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            tv_count.setText((millisUntilFinished / 1000).toString() + "s")
        }

        override fun onFinish() {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countdownTimer.cancel()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_UP -> countdownTimer.start()
            else -> countdownTimer.cancel()
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vein)
        val veinUtil = VeinUtil.getInstance()
        tv_count=findViewById(R.id.tv_count)

        findViewById<RelativeLayout>(R.id.rl_back).setOnClickListener {
            finish()
        }


        getAllUsers()
        val recyclerView = findViewById<RecyclerView>(R.id.rcy_data)
        recyclerView.adapter = userAdapter
        LiveDataEvent.veinString.observe(this@VeinActivity) {
            if (it.length > 10) {
                Log.e("TAG", "onShow: 上传特征值")
                if (customDialog != null) customDialog.dismiss()
                saveUserInfo(GsonUtils.toJson(VeinSave(userId, it, isFirst)))
            } else {
                Log.e("TAG", "onShow: 采集失败 ==" + it)
                runOnUiThread {
                    ToastUtils.showShort(it)
                }
                if (customDialog != null) customDialog.dismiss()
            }
        }

        userAdapter.addOnItemChildClickListener(R.id.tv_vein1_recode) { adapter, view, position ->
            userId = adapter.getItem(position)!!.userId
            isFirst = true
            customDialog = CustomDialog(this@VeinActivity, object : OnBack {
                override fun onDismiss() {
                    veinUtil.isCollect = false
                }

                override fun onShow() {
                    veinUtil.isCollect = true
                    veinUtil.toCollect()
                }
            })
            customDialog.show()
            true
        }

        userAdapter.addOnItemChildClickListener(R.id.tv_vein2_recode) { adapter, view, position ->
            userId = adapter.getItem(position)!!.userId
            isFirst = false
            customDialog = CustomDialog(this@VeinActivity, object : OnBack {
                override fun onDismiss() {
                    veinUtil.isCollect = false
                }

                override fun onShow() {
                    veinUtil.isCollect = true
                    veinUtil.toCollect()
                }
            })
            customDialog.show()
            true
        }

        countdownTimer.start()

    }

    fun getAllUsers() {
        val params: MutableMap<String, String> = HashMap()
        // 添加参数
        params["PageSize"] = "1000"
        params["PageNum"] = "1"
        ThreadPool.run {
            try {
                val response =
                    sendGetRequest(VeinUrl.getUserUrl, params)
                val type = object :
                    TypeToken<BaseModel<BasePage<List<UserVeinInfo?>?>?>?>() {}.type
                val info =
                    GsonUtils.fromJson<BaseModel<BasePage<List<UserVeinInfo>>>>(
                        response,
                        type
                    )
                userVeinInfos.clear()
                userVeinInfos.addAll(info.data.result)
                runOnUiThread {
                    userAdapter.submitList(userVeinInfos)
                    userAdapter.notifyDataSetChanged()
                    Log.e("TAG", "response: " + userVeinInfos.size)
                }
            } catch (e: Exception) {
                Log.e("TAG", "===json解析出错===:$e")
            }
        }
    }

    fun saveUserInfo(body: String) {
        ThreadPool.run {
            try {
                val response = sendPostRequest(VeinUrl.updateUserUrl, body)
                Log.e("response", "saveUserInfo: " + response)
                val type = object : TypeToken<BaseModel<String?>?>() {}.type
                val info = GsonUtils.fromJson<BaseModel<String>>(response, type)
                if (info.code==200){
                    runOnUiThread {
                        ToastUtils.showShort("保存成功")
                        getAllUsers()
                    }
                }
            }catch (e:Exception){
                Log.e("TAG", "===json解析出错===:$e")

            }
        }
    }


}