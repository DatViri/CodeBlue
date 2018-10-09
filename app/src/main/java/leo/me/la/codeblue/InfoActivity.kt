package leo.me.la.codeblue

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.Utils
import kotlinx.android.synthetic.main.activity_info.bmi
import kotlinx.android.synthetic.main.activity_info.bmi_detail
import kotlinx.android.synthetic.main.activity_info.bmi_message
import kotlinx.android.synthetic.main.activity_info.dobValue
import kotlinx.android.synthetic.main.activity_info.heartRate
import kotlinx.android.synthetic.main.activity_info.heart_detail
import kotlinx.android.synthetic.main.activity_info.heightValue
import kotlinx.android.synthetic.main.activity_info.iconHeartRate
import kotlinx.android.synthetic.main.activity_info.lineChart
import kotlinx.android.synthetic.main.activity_info.nameValue
import kotlinx.android.synthetic.main.activity_info.profilePicture
import kotlinx.android.synthetic.main.activity_info.status
import kotlinx.android.synthetic.main.activity_info.targetHr
import kotlinx.android.synthetic.main.activity_info.weightValue
import leo.me.la.codeblue.bluetooth.BleWrapper
import leo.me.la.codeblue.bluetooth.HEART_RATE_MEASUREMENT_CHAR_UUID
import leo.me.la.codeblue.bluetooth.HEART_RATE_SERVICE_UUID
import leo.me.la.codeblue.presentation.UserViewModel
import leo.me.la.codeblue.presentation.UserViewState
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * This activity show the info of a required patient
 * It displays the patient's information and feedback on their BMI
 * It also shows a real time graph of their heart rate and give feedback on their heart rate
 */
class InfoActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModel()

    private val heartRates by lazy { mutableListOf(Entry(0F, 60F)) }
    private var bleWrapper: BleWrapper? = null
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
    private val popupAnimator = ValueAnimator.ofFloat(1f, 0.9f)
        .also {
            it.duration = 300
            it.repeatMode = REVERSE
            it.repeatCount = INFINITE
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        Utils.init(this)
        userViewModel.viewState.observe(this, Observer {
            it?.run(this@InfoActivity::render)
        })
        val userId = intent.getIntExtra("username", UserIdentities.user1.first)
        val userToken = intent.getStringExtra("userToken") ?: UserIdentities.user1.third
        userViewModel.fetchUser(userId, userToken)
        lineChart.data = lineData
        lineChart.setVisibleXRangeMaximum(40f)
        lineChart.axisRight.isEnabled = false
        lineChart.xAxis.setDrawLabels(false)
    }

    @SuppressLint("SetTextI18n")
    private fun render(viewState: UserViewState) {
        popupAnimator.apply {
            removeAllUpdateListeners()
            cancel()
        }
        when (viewState) {
            UserViewState.Loading -> {
                bleWrapper?.removeAllListeners()
                turnViewsToVisible(
                    setOf(R.id.progressBar)
                )
            }
            is UserViewState.Failure -> {
                bleWrapper?.removeAllListeners()
                //TODO: Show error message
            }
            is UserViewState.Success -> {
                turnViewsToVisible(setOf(
                    R.id.profilePicture,
                    R.id.nameValue,
                    R.id.divider,
                    R.id.heart_detail,
                    R.id.bmi_detail,
                    R.id.lineChart,
                    R.id.heartRate,
                    R.id.iconHeartRate
                ))
                // Display user data
                with(viewState.user) {
                    nameValue.text = "$firstName $lastName"
                    dobValue.text = age.toString() + getString(R.string.years)
                    weightValue.text = "${weight}kg"
                    heightValue.text = "${height}cm"
                    targetHr.text = getString(R.string.target_bpm) + getHRTarget(age).let { "${it.first} - ${it.last} bpm" }
                }
                // Display bmi feedback
                with(viewState.bmi) {
                    bmi.text = "BMI: $first"
                    bmi_message.apply {
                        text = this@InfoActivity.getString(second)
                        setTextColor(ContextCompat.getColor(this@InfoActivity, third))
                    }
                }
                // Connect to associated sensor
                val address = intent.getStringExtra("address") ?: UserIdentities.user1.second
                bleWrapper = BleWrapper(this, address)
                    .apply {
                        connect(false)
                        addListener(object : BleWrapper.BleCallback {
                            override fun onDeviceReady(gatt: BluetoothGatt) {
                                bleWrapper?.getNotifications(gatt, HEART_RATE_SERVICE_UUID, HEART_RATE_MEASUREMENT_CHAR_UUID)
                            }

                            override fun onDeviceDisconnected() {
                                bleWrapper?.removeAllListeners()
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
                                    status.apply {
                                        getHRStatus(viewState.user.age, heartrate).run {
                                            text = getString(first)
                                            setTextColor(ContextCompat.getColor(this@InfoActivity, second))
                                        }
                                    }
                                }
                            }
                        })
                    }
                // Apply animation
                popupAnimator.apply {
                    addUpdateListener {
                        iconHeartRate.scaleX = it.animatedValue as Float
                        iconHeartRate.scaleY = it.animatedValue as Float
                    }
                    start()
                }
                profilePicture.translateX(getWidth().toFloat(), 500)
                bmi_detail.translateX(getWidth().toFloat(), 800)
                nameValue.translateX(getWidth().toFloat(), 800)
                lineChart.translateY(getHeight().toFloat(), 500)
                heart_detail.translateY(getHeight().toFloat(), 800)
                iconHeartRate.translateY(getHeight().toFloat(), 800)
                heartRate.translateY(getHeight().toFloat(), 800)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bleWrapper?.removeAllListeners()
    }

    private val heartRateStatusDecision by lazy {
        listOf(
            0..25 to HeartRateLimitLine(40, 55, 60, 65, 69, 73, 81),
            26..35 to HeartRateLimitLine(40, 54, 60, 65, 70, 74, 81),
            36..45 to HeartRateLimitLine(40, 56, 61, 66, 70, 75, 82),
            46..55 to HeartRateLimitLine(40, 57, 62, 67, 71, 76, 83),
            56..65 to HeartRateLimitLine(40, 56, 62, 67, 71, 75, 80),
            65..Int.MAX_VALUE to HeartRateLimitLine(40, 56, 62, 67, 71, 75, 80)
        )
    }

    private fun getHRTarget(age: Int): IntRange {
        require(age >= 0) { "age must be positive" }
        return heartRateStatusDecision.first {
            it.first.contains(age)
        }.second.let { it.athlete..it.average }
    }

    private fun getHRStatus(age: Int, bpm: Int): Pair<Int, Int> {
        require(age >= 0) { "age must be positive" }
        require(bpm >= 0) { "bpm must be positive" }

        return heartRateStatusDecision.first {
            it.first.contains(age)
        }
            .second
            .let {
                when {
                    bpm < it.low -> Pair(R.string.low, R.color.colorLow)
                    bpm < it.athlete -> Pair(R.string.athlete, R.color.colorAthlete)
                    bpm < it.excellent -> Pair(R.string.excellent, R.color.colorExcellent)
                    bpm < it.good -> Pair(R.string.good, R.color.colorGood)
                    bpm < it.aboveAverage -> Pair(R.string.above, R.color.colorAboveAverage)
                    bpm < it.average -> Pair(R.string.average, R.color.colorAverage)
                    bpm < it.belowAverage -> Pair(R.string.below, R.color.colorBelowAverage)
                    else -> Pair(R.string.poor, R.color.colorPoor)
                }
            }
    }
}

data class HeartRateLimitLine(
    val low: Int,
    val athlete: Int,
    val excellent: Int,
    val good: Int,
    val aboveAverage: Int,
    val average: Int,
    val belowAverage: Int
)
