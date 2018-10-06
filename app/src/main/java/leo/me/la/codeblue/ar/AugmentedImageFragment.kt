package leo.me.la.codeblue.ar

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
        if (!setupAugmentedImageDatabase(config, session)) {
            SnackbarHelper.instance
                .showError(activity!!, "Could not setup augmented image database")
        }
        return config
    }

    private fun setupAugmentedImageDatabase(config: Config, session: Session): Boolean {
        var augmentedImageDatabase: AugmentedImageDatabase? = null

        val assetManager = (if (context != null) context!!.assets else null) ?: return false

        val augmentedImageBitmap = loadAugmentedImageBitmap(assetManager) ?: return false

        augmentedImageDatabase = AugmentedImageDatabase(session)
        augmentedImageDatabase.addImage(DEFAULT_IMAGE_NAME, augmentedImageBitmap)

        config.augmentedImageDatabase = augmentedImageDatabase
        return true
    }

    private fun loadAugmentedImageBitmap(assetManager: AssetManager?): Bitmap? {
        try {
            assetManager!!.open(DEFAULT_IMAGE_NAME).use { `is` -> return BitmapFactory.decodeStream(`is`) }
        } catch (ignore: IOException) {
        }

        return null
    }

    companion object {

        // This is the name of the image in the sample database.  A copy of the image is in the assets
        // directory.  Opening this image on your computer is a good quick way to test the augmented image
        // matching.
        private var DEFAULT_IMAGE_NAME = "default.jpg"

        // Do a runtime check for the OpenGL level available at runtime to avoid Sceneform crashing the
        // application.
        private var MIN_OPENGL_VERSION = 3.0
    }
}
