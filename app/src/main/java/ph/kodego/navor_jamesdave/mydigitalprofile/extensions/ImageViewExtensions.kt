package ph.kodego.navor_jamesdave.mydigitalprofile.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ph.kodego.navor_jamesdave.mydigitalprofile.R

fun ImageView.loadProfile(url: String){
    Glide
        .with(this)
        .load(url)
        .circleCrop()
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .placeholder(R.drawable.placeholder)
        .error(R.drawable.placeholder)
        .into(this)
}