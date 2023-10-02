package ani.saikou.connections.anilist.api

import kotlinx.serialization.SerialName
import java.io.Serializable
import java.text.DateFormatSymbols
import java.util.*

@kotlinx.serialization.Serializable
data class FuzzyDate(
    @SerialName("year") val year: Int? = null,
    @SerialName("month") val month: Int? = null,
    @SerialName("day") val day: Int? = null,
) : Serializable, Comparable<FuzzyDate> {
    override fun toString(): String {
        if (day == null && year == null && month == null)
            return "??"
        val a = if (month != null) DateFormatSymbols().months[month - 1] else ""
        return (if (day != null) "$day " else "") + a + (if (year != null) ", $year" else "")
    }

    fun getToday(): FuzzyDate {
        val cal = Calendar.getInstance()
        return FuzzyDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
    }

    fun toVariableString(): String {
        return ("{"
                + (if (year != null) "year:$year" else "")
                + (if (month != null) ",month:$month" else "")
                + (if (day != null) ",day:$day" else "")
                + "}")
    }

    fun toISOString(): String {
        return "${
            year.toString().padStart(4, '0')
        }-${
            month.toString().padStart(2, '0')
        }-${
            day.toString().padStart(2, '0')
        }"
    }

//    fun toInt(): Int {
//        return 10000 * (this.year ?: 0) + 100 * (this.month ?: 0) + (this.day ?: 0)
//    }

    override fun compareTo(other: FuzzyDate): Int = when {
        year != other.year   -> (year ?: 0) - (other.year ?: 0)
        month != other.month -> (month ?: 0) - (other.month ?: 0)
        else                 -> (day ?: 0) - (other.day ?: 0)
    }
}