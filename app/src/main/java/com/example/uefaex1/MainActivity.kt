package com.example.uefaex1

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.uefaex1.databinding.ActivityMainBinding
import java.util.*
import kotlin.concurrent.fixedRateTimer


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.vm = RandomVideModel()
        setContentView(binding.root)
    }
}

class RandomVideModel : ViewModel() {

    var progress = MutableLiveData<Float>()

    private var timer = Timer()

    val handler = Handler(Looper.getMainLooper())

    init {
        progress.value = 33f
        val t = 3000.toLong()
        timer = fixedRateTimer(initialDelay = t, period = t) {
            handler.post {
                progress.value = (Math.random() * 100).toFloat()
            }
        }
    }

    override fun onCleared() {
        timer.cancel()
        super.onCleared()
    }
}

object Commons {
    fun dpToPx(context: Context, dip: Float): Float {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                context.getResources().displayMetrics
        )
    }
}