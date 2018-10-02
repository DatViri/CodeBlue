package leo.me.la.codeblue

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.Utils
import kotlinx.android.synthetic.main.activity_info.dob
import kotlinx.android.synthetic.main.activity_info.dobValue
import kotlinx.android.synthetic.main.activity_info.heartRate
import kotlinx.android.synthetic.main.activity_info.height
import kotlinx.android.synthetic.main.activity_info.heightValue
import kotlinx.android.synthetic.main.activity_info.lineChart
import kotlinx.android.synthetic.main.activity_info.name
import kotlinx.android.synthetic.main.activity_info.nameValue
import kotlinx.android.synthetic.main.activity_info.profilePicture
import kotlinx.android.synthetic.main.activity_info.weight
import kotlinx.android.synthetic.main.activity_info.weightValue
import leo.me.la.codeblue.bluetooth.BleWrapper
import leo.me.la.codeblue.bluetooth.HEART_RATE_MEASUREMENT_CHAR_UUID
import leo.me.la.codeblue.bluetooth.HEART_RATE_SERVICE_UUID
import leo.me.la.codeblue.presentation.UserViewModel
import leo.me.la.codeblue.presentation.UserViewState
import org.koin.android.viewmodel.ext.android.viewModel

class InfoActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModel()

    private val heartRates by lazy { mutableListOf(Entry(0F, 60F)) }
    private lateinit var bleWrapper: BleWrapper
    private val polarListener = object : BleWrapper.BleCallback {
        override fun onDeviceReady(gatt: BluetoothGatt) {
            bleWrapper.getNotifications(gatt, HEART_RATE_SERVICE_UUID, HEART_RATE_MEASUREMENT_CHAR_UUID)
        }

        override fun onDeviceDisconnected() {
            bleWrapper.removeAllListeners()
        }

        @SuppressLint("SetTextI18n")
        override fun onNotify(characteristic: BluetoothGattCharacteristic) {
            runOnUiThread {
                val format = if (characteristic.properties and 0x01 != 0) {
                    BluetoothGattCharacteristic.FORMAT_UINT16
                } else {
                    BluetoothGattCharacteristic.FORMAT_UINT8
                }
                val heartrate = characteristic.getIntValue(format, 1)
                heartRates.add(Entry(counter.toFloat(), heartrate.toFloat()))
                lineDataSet.notifyDataSetChanged()
                lineData.notifyDataChanged()
                lineChart.notifyDataSetChanged()
                lineChart.invalidate()
                heartRate.text = heartrate.toString()
                counter += 1
            }
        }
    }
    private val lineDataSet by lazy {
        LineDataSet(heartRates, "heart rate").also {
            it.axisDependency = YAxis.AxisDependency.LEFT
            it.setDrawCircles(false)
            it.setDrawCircleHole(false)
            it.color = ContextCompat.getColor(this, R.color.colorAccent)
            it.lineWidth = 2f
            it.mode = LineDataSet.Mode.CUBIC_BEZIER
            it.cubicIntensity = 0.1f
            it.setDrawValues(false)
        }
    }
    private val lineData by lazy { LineData(lineDataSet) }
    private var counter = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        Utils.init(this)
        userViewModel.viewState.observe(this, Observer {
            it?.run(this@InfoActivity::render)
        })
        val userId = intent.getIntExtra("username", UserIdentities.user1.first)
        val address = intent.getStringExtra("address") ?: UserIdentities.user1.second
        userViewModel.fetchUser(userId)
        lineChart.data = lineData
        lineChart.setVisibleXRangeMaximum(40f)
        lineChart.axisRight.isEnabled = false
        lineChart.xAxis.setDrawLabels(false)
        bleWrapper = BleWrapper(this, address)
            .apply {
                connect(false)
                addListener(polarListener)
            }
    }

    @SuppressLint("SetTextI18n")
    private fun render(viewState: UserViewState) {
        when (viewState) {
            UserViewState.Loading -> {
                //TODO: React to this state
            }
            is UserViewState.Failure -> {
                //TODO: Show error message
            }
            is UserViewState.Success -> {
                showViews(setOf(
                    name,
                    nameValue,
                    dobValue,
                    dob,
                    weight,
                    weightValue,
                    heightValue,
                    height,
                    profilePicture
                ))
                with(viewState.user) {
                    nameValue.text = "$firstName $lastName"
                    dobValue.text = birthdate
                    weightValue.text = "${weight}kg"
                    heightValue.text = "${height}cm"
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bleWrapper.removeAllListeners()
    }

    private fun showViews(views: Set<View>) {
        views.forEach {
            it.visibility = View.VISIBLE
        }
    }
}
