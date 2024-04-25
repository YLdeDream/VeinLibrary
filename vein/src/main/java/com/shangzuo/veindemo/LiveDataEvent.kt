package com.shangzuo.veindemo

import com.kunminx.architecture.domain.message.MutableResult


/**
 * 全局发送消息 通过LiveData实现
 */
object LiveDataEvent {

    val veinUser = MutableResult<UserVeinInfo>(UserVeinInfo())
    val veinString = MutableResult<String>("")

}