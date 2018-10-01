package leo.me.la.codeblue.ar

import android.content.Context
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Pose
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import java.util.concurrent.CompletableFuture

class AugmentedImageNode : AnchorNode() {

    private val TAG = "AugmentedImageNode"

    private var image: AugmentedImage? = null
    private var modelFuture: CompletableFuture<ModelRenderable>? = null

    @RequiresApi(Build.VERSION_CODES.N)
    fun augmentedImageNode(context: Context, filename: String) {
        // Upon construction, start loading the modelFuture
        if (modelFuture == null) {
            modelFuture = ModelRenderable.builder().setRegistryId("modelFuture")
                    .setSource(context, Uri.parse(filename))
                    .build()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun setImage(image: AugmentedImage) {
        this.image = image

        if (!modelFuture!!.isDone) {
            CompletableFuture.allOf(modelFuture).thenAccept { aVoid: Void -> setImage(image) }.exceptionally { throwable ->
                Log.e(TAG, "Exception loading", throwable)
                null
            }
        }

        anchor = image.createAnchor(image.centerPose)

        val node = Node()

        val pose = Pose.makeTranslation(0.0f, 0.0f, 0.25f)

        node.setParent(this)
        node.localPosition = Vector3(pose.tx(), pose.ty(), pose.tz())
        node.localRotation = Quaternion(pose.qx(), pose.qy(), pose.qz(), pose.qw())
        node.renderable = modelFuture!!.getNow(null)

    }
}