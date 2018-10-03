package leo.me.la.codeblue

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.google.ar.core.AugmentedImage
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.ux.ArFragment
import leo.me.la.codeblue.common.helper.SnackbarHelper

class AugmentedImageActivity : AppCompatActivity() {

    private var arFragment: ArFragment? = null
    private var fitToScanView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_augmented_image)

        arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment?
        fitToScanView = findViewById(R.id.image_view_fit_to_scan)

        arFragment!!.arSceneView.scene.addOnUpdateListener(onUpdateFrame)
    }
    /**
     * Registered with the Sceneform Scene object, this method is called at the start of each frame.
     *
     * @param frameTime - time since last frame.
     */

    private val onUpdateFrame: (FrameTime) -> Unit = {
        val frame = arFragment!!.arSceneView.arFrame

        // If there is no frame or ARCore is not tracking yet, just return.
        if (!(frame == null || frame.camera.trackingState !== TrackingState.TRACKING)) {
            val updatedAugmentedImages = frame!!.getUpdatedTrackables(AugmentedImage::class.java)
            for (augmentedImage in updatedAugmentedImages) {
                when (augmentedImage.trackingState) {
                    TrackingState.PAUSED -> {
                        // When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
                        // but not yet tracked.
                        val text = "Detected Image " + augmentedImage.index
                        SnackbarHelper.instance.showMessage(this, text)

                        removeListener()
                    }
                    else -> { }
                }
            }
        }
    }

    private fun removeListener() {
        arFragment!!.arSceneView.scene.removeOnUpdateListener(onUpdateFrame)
    }
}
