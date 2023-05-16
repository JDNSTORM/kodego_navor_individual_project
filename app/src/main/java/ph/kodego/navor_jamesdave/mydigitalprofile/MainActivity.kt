package ph.kodego.navor_jamesdave.mydigitalprofile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ProfileActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.FragmentAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityMainBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.AboutAppDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseAccountDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.fragments.AccountFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.fragments.HomeFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.fragments.LoginFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.IntentBundles

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentAdapter:FragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbTop)

        fragmentAdapter = FragmentAdapter(supportFragmentManager, lifecycle)

        fragmentAdapter.addFragment(HomeFragment())
        val currentUserId = FirebaseAccountDAOImpl(this).getCurrentUserID()
        var accountFragment: AccountFragment? = null
        if (currentUserId.isNotEmpty()) {
            accountFragment = AccountFragment()
            fragmentAdapter.addFragment(accountFragment)
        }else{
            fragmentAdapter.addFragment(LoginFragment())
        }

        with(binding.viewPager2){
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = fragmentAdapter
        }

        TabLayoutMediator(binding.tlNavBottom, binding.viewPager2){
                tab, position ->
            var text: String = "Unknown"
            var icon: Int = R.drawable.ic_unknown_24
            fragmentAdapter.fragmentList[position].arguments?.let {
                text = it.getString("TabName").toString()
                icon = it.getInt("TabIcon")
            }
            tab.text = text
            tab.setIcon(icon)
        }.attach()

        if(currentUserId.isNotEmpty()){
            binding.viewPager2.currentItem = fragmentAdapter.fragmentList.indexOf(accountFragment!!)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.btn_about -> {
                showAboutAppDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAboutAppDialog(){
        val dialog = AboutAppDialog(this)
        dialog.show()
        dialog.onViewGitRepository{ openGitRepositoryURL() }
        dialog.onViewProfile{
            viewDeveloperProfile()
            dialog.dismiss()
        }
    }

    private fun openGitRepositoryURL(){
        val url = Uri.parse("https://github.com/JDNSTORM/kodego_navor_individual_project.git")
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = url
        startActivity(openURL)
    }

    private fun viewDeveloperProfile(){
        val profileID = getString(R.string.profile_id)
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra(IntentBundles.ProfileID, profileID)
        startActivity(intent)
    }
}