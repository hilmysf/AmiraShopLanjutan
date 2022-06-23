package hilmysf.amirashoplanjutan.ui.log

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import hilmysf.amirashoplanjutan.ui.log.opname.OpnameLogFragment
import hilmysf.amirashoplanjutan.ui.log.sell.SellLogFragment

class SectionsPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    companion object {
        private val TAB_TITLES = arrayListOf("Opname", "Penjualan")
    }

    override fun getItem(position: Int): Fragment =
        when (position) {
            0 -> OpnameLogFragment()
            1 -> SellLogFragment()
            else -> Fragment()
        }

    override fun getPageTitle(position: Int): CharSequence = TAB_TITLES[position]

    override fun getCount(): Int = 2

}