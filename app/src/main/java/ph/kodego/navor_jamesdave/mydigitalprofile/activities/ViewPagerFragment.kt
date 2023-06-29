package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.TabInfo

abstract class ViewPagerFragment<Binding: ViewBinding> (): Fragment() {
    protected var _binding: Binding? = null
    protected val binding get() = _binding!!
    val tabInfo: TabInfo get() = getTabInformation()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateView(inflater, container, savedInstanceState)
        return binding.root
    }

    protected abstract fun inflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Binding

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    abstract fun getTabInformation(): TabInfo
}