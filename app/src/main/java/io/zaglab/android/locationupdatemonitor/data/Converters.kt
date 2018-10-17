package io.zaglab.android.locationupdatemonitor.data

import androidx.room.TypeConverter
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime

class Converters {
    @TypeConverter
    fun fromLocalTimeToString(time: LocalTime): String {
        return time.toString()
    }

    @TypeConverter
    fun fromStringToLocalTime(time: String): LocalTime {
        return LocalTime.parse(time)
    }

    @TypeConverter
    fun fromLocalDateToString(date: LocalDate): String {
        return date.toString()
    }

    @TypeConverter
    fun fromStringToLocalDate(date: String): LocalDate {
        return LocalDate.parse(date)
    }

    @TypeConverter
    fun fromLocalDateTimeToString(date: LocalDateTime): String {
        return date.toString()
    }

    @TypeConverter
    fun fromStringToLocalDateTime(date: String): LocalDateTime {
        return LocalDateTime.parse(date)
    }

    @TypeConverter
    fun fromCallbackTypeToString(type: CallbackType): String {
        return type.name
    }

    @TypeConverter
    fun fromCallbackStringToType(type: String): CallbackType {
        return CallbackType.valueOf(type)
    }
}