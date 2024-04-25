package com.shangzuo.veinlibrary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.shangzuo.veindemo.VeinActivity
import com.shangzuo.veindemo.VeinUrl
import com.shangzuo.veindemo.VeinUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val veinUtil = VeinUtil.getInstance()


        GlobalScope.launch {
            veinUtil.SerachUSB(this@MainActivity)
            delay(1000)
            val result = veinUtil.connetVien(this@MainActivity)
            veinUtil.getAllUsers(VeinUrl.getUserUrl)
            Log.e("TAG", "connetVien: $result")
            veinUtil.toVerify()
        }
        startActivity(Intent(this,VeinActivity::class.java))
    }
}