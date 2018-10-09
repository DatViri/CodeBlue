package leo.me.la.codeblue.fragment

import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_slider.*

import leo.me.la.codeblue.R
import leo.me.la.codeblue.R.id.fragment_title

private const val IMAGE_KEY = "image"
private const val TITLE_KEY = "title"
class SliderFragment : Fragment() {

    private val pageTitle: String by lazy {
        arguments!!.getString(TITLE_KEY)
    }

    private val pageImage: Int by lazy {
        arguments!!.getInt(IMAGE_KEY)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_slider,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragment_title.text = pageTitle
        imageView.setImageResource(pageImage)
    }

    companion object {
        fun instance(@DrawableRes stepImage: Int, pageTitle: String) : SliderFragment {
            return SliderFragment()
                .also {
                    it.arguments = Bundle()
                        .apply {
                            putInt(IMAGE_KEY, stepImage)
                            putString(TITLE_KEY, pageTitle)
                        }
                }
        }
    }
}
