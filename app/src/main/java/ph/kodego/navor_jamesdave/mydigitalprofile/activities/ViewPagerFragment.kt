package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.TabInfo

abstract class ViewPagerFragment<Binding: ViewBinding> (): Fragment() {
    protected var _binding: Binding? = null
    protected val binding get() = _binding!!
    protected val tabInfo: TabInfo get() = getTabInformation()

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    abstract fun getTabInformation(): TabInfo
}