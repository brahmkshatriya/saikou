package ani.saikou.home

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import ani.saikou.databinding.ActivityNoInternetBinding
import ani.saikou.isOnline
import ani.saikou.navBarHeight
import ani.saikou.startMainActivity
import ani.saikou.statusBarHeight

class NoInternet : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityNoInternetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.refreshContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = statusBarHeight
            bottomMargin = navBarHeight
        }
        binding.refreshButton.setOnClickListener {
            if (isOnline(this)) {
                startMainActivity(this)
            }
        }
    }
}