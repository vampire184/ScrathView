package com.example.scratchview

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mResetBtn:Button
    private lateinit var mScratchView: ScratchView
    private val TAG = "yang"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mResetBtn = findViewById(R.id.reset_btn)
        mScratchView = findViewById(R.id.scratch_view)

        mResetBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.reset_btn -> {
                mScratchView.resetView()
            }
        }
    }
}
