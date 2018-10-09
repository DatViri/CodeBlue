package leo.me.la.codeblue

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.ar.core.AugmentedImage
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.ux.ArFragment
import leo.me.la.codeblue.common.helper.SnackbarHelper

class AugmentedImageActivity : AppCompatActivity() {

    private val arFragment: ArFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_augmented_image)
    }

    override fun onResume() {
        super.onResume()
        arFragment.arSceneView.apply {
            scene.addOnUpdateListener(onUpdateFrame)
        }
    }

    private val onUpdateFrame = Scene.OnUpdateListener {
        val frame = arFragment.arSceneView.arFrame

        // If there is no frame or ARCore is not tracking yet, just return.
        if (!(frame == null || frame.camera.trackingState !== TrackingState.TRACKING)) {
            val updatedAugmentedImages = frame.getUpdatedTrackables(AugmentedImage::class.java)
            updatedAugmentedImages.lastOrNull()?.also {
                when (it.trackingState) {
                    TrackingState.PAUSED -> {
                        // When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
                        // but not yet tracked.
                        val text = "Detected Image " + it.index
                        SnackbarHelper.instance.showMessage(this@AugmentedImageActivity, text)
                        removeListener()
                        goToAugmentedImageActivity(
                                if (it.name =="dat.jpg"){
                                    UserIdentities.user2
                                } else{
                                    UserIdentities.user1
                                }
                        )
                    }
                    else -> { }
                }
            }
        }
    }

    private fun goToAugmentedImageActivity(user: Triple<Int,String,String>){
        startActivity(Intent(this, InfoActivity::class.java)
                .apply {
                    putExtra("username",user.first)
                    putExtra("address",user.second)
                    putExtra("userToken",user.third)
                })
    }

    private fun removeListener() {
        arFragment.arSceneView.scene.removeOnUpdateListener(onUpdateFrame)
    }
}
