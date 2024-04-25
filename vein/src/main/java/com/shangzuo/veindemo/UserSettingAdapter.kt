package com.shangzuo.veindemo

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder


class UserSettingAdapter : BaseQuickAdapter<UserVeinInfo, QuickViewHolder>() {
    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: UserVeinInfo?) {
        holder.setText(R.id.tv_num, (position + 1).toString())
        holder.setText(R.id.tv_no, item?.employeeNumber)
        holder.setText(R.id.tv_name, item?.userName)


        val tv_vein1: TextView = holder.getView(R.id.tv_vein1)
        val tv_vein1_recode: TextView = holder.getView(R.id.tv_vein1_recode)

        val tv_vein2: TextView = holder.getView(R.id.tv_vein2)
        val tv_vein2_recode: TextView = holder.getView(R.id.tv_vein2_recode)

        if (!item?.fingerDataoneStr.isNullOrEmpty()) {
            tv_vein1.visibility= View.GONE
            tv_vein1_recode.text="重新录入"
            tv_vein1_recode.visibility= View.VISIBLE
        }else{
            tv_vein1.visibility= View.GONE
            tv_vein1_recode.text="指静脉2录入"
            tv_vein1_recode.visibility= View.VISIBLE
        }

        if (!item?.fingerDatatwoStr.isNullOrEmpty()) {
            tv_vein2.visibility= View.GONE
            tv_vein2_recode.text="重新录入"
          //  tv_vein2_recode.setTextColor(R.color.black)
            tv_vein2_recode.visibility= View.VISIBLE
        }else{
            tv_vein2.visibility= View.GONE
            tv_vein2_recode.text="指静脉2录入"
            tv_vein2_recode.visibility= View.VISIBLE
        }

    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_user_setting, parent)
    }
}