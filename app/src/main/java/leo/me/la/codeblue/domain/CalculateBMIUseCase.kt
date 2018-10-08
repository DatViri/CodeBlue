package leo.me.la.codeblue.domain

import io.reactivex.Scheduler
import io.reactivex.Single
import leo.me.la.codeblue.R

typealias BMIStatus = Triple<String, Int, Int>

class CalculateBMIUseCase(
    private val threadExecutor: Scheduler,
    private val postExecutionThread: Scheduler
) {
    fun calculateBMI(weightInKilo: Int, heightInCentimeter: Int): Single<BMIStatus> {
        require(weightInKilo > 0) { "weight must be positive" }
        require(heightInCentimeter > 0) { "height must be positive" }
        return Single.fromCallable {
            val bmi = weightInKilo / Math.pow(heightInCentimeter / 100.0, 2.0)
            val message = when {
                bmi < 18.5 -> R.string.underweight
                bmi < 24.9 -> R.string.normal_weight
                bmi < 29.9 -> R.string.overweight
                else -> R.string.obesity
            }
            val color = when {
                bmi < 18.5 -> R.color.colorUnderweight
                bmi < 24.9 -> R.color.colorNormalWeight
                bmi < 29.9 -> R.color.colorOverWeight
                else -> R.color.colorObesity
            }
            Triple("%.2f".format(bmi), message, color)
        }
            .subscribeOn(threadExecutor)
            .observeOn(postExecutionThread)
    }
}
