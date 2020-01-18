package kr.lago.inappupdate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import kr.lago.app.update.AppUpdate

class MainActivity : AppCompatActivity() {

    private val appUpdate = AppUpdate(this, AppUpdate.TYPE.FLEXIBLE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appUpdate.initAppUpdate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        appUpdate.activityResult(requestCode, resultCode)
    }


}