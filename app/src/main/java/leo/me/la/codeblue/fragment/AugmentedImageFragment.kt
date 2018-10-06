package leo.me.la.codeblue.fragment

import android.app.ActivityManager
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment
import leo.me.la.codeblue.common.helper.SnackbarHelper
import java.io.IOException

private val imageList = listOf(
    "giang.jpg"
)

class AugmentedImageFragment : ArFragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val openGlVersionString = (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .deviceConfigurationInfo
            .glEsVersion
        if (java.lang.Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            SnackbarHelper.instance
                .showError(activity!!, "Sceneform requires OpenGL ES 3.0 or later")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        // Turn off the plane discovery since we're only looking for images
        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
        arSceneView.planeRenderer.isEnabled = false
        return view!!
    }

    override fun getSessionConfiguration(session: Session): Config {
        val config = super.getSessionConfiguration(session)

        //Check if the configuration is set to fixed
        if (config.focusMode == Config.FocusMode.FIXED)
            config.focusMode = Config.FocusMode.AUTO

        //Sceneform requires that the ARCore session is configured to the UpdateMode LATEST_CAMERA_IMAGE.
        //This is probably not required for just auto focus. I was updating the camera configuration as well
        config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE

        //Reconfigure the session
        session.configure(config)
        if (!setupAugmentedImageDatabase(config, session)) {
            SnackbarHelper.instance
                .showError(activity!!, "Could not setup augmented image database")
        }
        return config
    }

    private fun setupAugmentedImageDatabase(config: Config, session: Session): Boolean {
        val augmentedImageDatabase = AugmentedImageDatabase(session)

        val assetManager = (if (context != null) context!!.assets else null) ?: return false

        imageList.forEach {
            val augmentedImageBitmap = loadAugmentedImageBitmap(assetManager, it) ?: return false
            augmentedImageDatabase.addImage(it, augmentedImageBitmap)
        }

        config.augmentedImageDatabase = augmentedImageDatabase
        return true
    }

    private fun loadAugmentedImageBitmap(assetManager: AssetManager, imageName: String): Bitmap? {
        try {
            assetManager.open(imageName).use {
                stream -> return BitmapFactory.decodeStream(stream)
            }
        } catch (ignore: IOException) {
            return null
        }
    }

    companion object {
        // Do a runtime check for the OpenGL level available at runtime to avoid Sceneform crashing the
        // application.
        private var MIN_OPENGL_VERSION = 3.0
    }
}
