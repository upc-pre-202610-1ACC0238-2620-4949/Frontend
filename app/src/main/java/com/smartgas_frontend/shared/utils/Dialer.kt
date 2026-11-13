package com.smartgas_frontend.shared.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

fun dialPhone(context: Context, phone: String): Boolean = try {
    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
    true
} catch (e: ActivityNotFoundException) {
    false
}
