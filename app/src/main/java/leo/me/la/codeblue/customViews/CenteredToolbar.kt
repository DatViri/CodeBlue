package leo.me.la.codeblue.customViews

import android.content.Context
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView


class CenteredToolbar : Toolbar {


    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttribute: Int)
        : super(context, attrs, defStyleAttribute)

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        setup()
        super.onLayout(changed, l, t, r, b)
    }

    private fun setup() {
        for (i in 0 until childCount) {
            val view = getChildAt(i)

            if (view is TextView) {
                val params = Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.MATCH_PARENT)
                params.gravity = Gravity.CENTER_HORIZONTAL
                view.layoutParams = params
            }
        }
    }
}
