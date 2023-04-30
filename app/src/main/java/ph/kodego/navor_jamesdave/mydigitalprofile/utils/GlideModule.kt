package ph.kodego.navor_jamesdave.mydigitalprofile.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.module.AppGlideModule
import ph.kodego.navor_jamesdave.mydigitalprofile.R

@GlideModule
class GlideModule() : AppGlideModule() {
    fun loadProfilePhoto(view: ImageView, url: String){
        Glide
            .with(view.context)
            .load(url)
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(view)
    }
}