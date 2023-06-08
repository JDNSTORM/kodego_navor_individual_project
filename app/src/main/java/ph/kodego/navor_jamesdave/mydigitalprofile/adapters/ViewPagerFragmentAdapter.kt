package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ViewPagerFragment

class ViewPagerFragmentAdapter(manager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(manager, lifecycle) {
    val fragments: ArrayList<ViewPagerFragment<*>> = ArrayList()
    fun addFragment(fragment: ViewPagerFragment<*>){
        fragments.add(fragment)
    }
    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment = fragments[position]
}