package leo.me.la.codeblue

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.util.DisplayMetrics
import android.view.animation.AccelerateDecelerateInterpolator
import kotlinx.android.synthetic.main.activity_info.profilePicture


fun View.fadeIn(durationInMillis: Int = context.resources.getInteger(android.R.integer.config_shortAnimTime)) {
    animate()
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                alpha = 0f
                visibility = View.VISIBLE
            }
        })
        .alpha(1f)
        .apply {
            duration = durationInMillis.toLong()
        }
}

/**
 * Fades Out the given [View]
 *
 * @param durationInMillis [Int] with the duration of the animation in milliseconds. If nothing
 * given, Android config_shortAnimTime used by default
 */
fun View.fadeOut(durationInMillis: Int = context.resources.getInteger(android.R.integer.config_shortAnimTime)) {
    animate()
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                visibility = View.INVISIBLE
            }
        })
        .alpha(0f)
        .duration = durationInMillis.toLong()
}

fun Activity.turnViewsToVisible(visibleViewIds: Set<Int>) {
    val rootView = findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
    (rootView as? ViewGroup)?.turnViewsToVisible(visibleViewIds)
}

fun ViewGroup.turnViewsToVisible(visibleViewIds: Set<Int>) {
    for (index in 0 until childCount) {
        if (visibleViewIds.contains(getChildAt(index).id)) {
            getChildAt(index).fadeIn()
        } else {
            getChildAt(index).fadeOut()
        }
    }
}

fun Activity.getWidth() : Int {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

fun Activity.getHeight() : Int {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.heightPixels
}

fun View.translateX(from: Float, durationInMillis: Long) {
    post {
        ValueAnimator.ofFloat(from, x)
            .also {
                it.duration = durationInMillis
                it.interpolator = AccelerateDecelerateInterpolator()
                it.addUpdateListener { a ->
                    this.x = a.animatedValue as Float
                }
                it.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {}

                    override fun onAnimationEnd(animation: Animator?) {
                        animation?.cancel()
                    }

                    override fun onAnimationCancel(animation: Animator?) {}

                    override fun onAnimationStart(animation: Animator?) {}
                })
            }
            .start()
    }
}

fun View.translateY(from: Float, durationInMillis: Long) {
    post {
        ValueAnimator.ofFloat(from, y)
            .also {
                it.duration = durationInMillis
                it.interpolator = AccelerateDecelerateInterpolator()
                it.addUpdateListener { a ->
                    this.y = a.animatedValue as Float
                }
                it.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {}

                    override fun onAnimationEnd(animation: Animator?) {
                        animation?.cancel()
                    }

                    override fun onAnimationCancel(animation: Animator?) {}

                    override fun onAnimationStart(animation: Animator?) {}
                })
            }
            .start()
    }
}
