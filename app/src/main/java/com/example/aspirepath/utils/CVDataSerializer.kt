package com.example.aspirepath.utils

import android.os.Build
import android.os.Parcel
import android.util.Base64
import com.example.aspirepath.models.CVData

object CVDataSerializer {

    fun encode(cvData: CVData): String {
        val parcel = Parcel.obtain()
        return try {
            parcel.writeParcelable(cvData, 0)
            val bytes = parcel.marshall()
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } finally {
            parcel.recycle()
        }
    }

    fun decode(encoded: String): CVData? {
        return try {
            val bytes = Base64.decode(encoded, Base64.NO_WRAP)
            val parcel = Parcel.obtain()
            parcel.unmarshall(bytes, 0, bytes.size)
            parcel.setDataPosition(0)

            val cvData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                parcel.readParcelable(CVData::class.java.classLoader, CVData::class.java)
            } else {
                @Suppress("DEPRECATION")
                parcel.readParcelable<CVData>(CVData::class.java.classLoader)
            }

            parcel.recycle()
            cvData
        } catch (_: Exception) {
            null
        }
    }
}