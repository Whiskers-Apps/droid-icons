package org.whiskersapps.droid.droid_icons

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AdaptiveIconDrawable
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import org.whiskersapps.droid.droid_icons.models.Icon
import org.whiskersapps.droid.droid_icons.models.IconPack
import org.xmlpull.v1.XmlPullParser

class IconFetcher(
    private val context: Context,
) {
    private val packageManager = context.packageManager
    private val packIntent = Intent("com.novalauncher.THEME")
    private var iconPacks: List<IconPack> = packageManager.queryIntentActivities(packIntent, 0)
        .map {
            val packageName = it.activityInfo.packageName

            IconPack(
                packageName = packageName,
                name = packageManager.getApplicationInfo(packageName, 0).loadLabel(packageManager)
                    .toString(),
                icon = getStockIcon(packageName)
            )
        }

    fun getIconPacks(): List<IconPack> {
        return iconPacks
    }

    /** Get the default app icon */
    fun getStockIcon(packageName: String): Icon {
        val info = packageManager.getApplicationInfo(packageName, 0)
        val drawable = packageManager.getApplicationIcon(info)

        if (drawable is AdaptiveIconDrawable) {
            try {
                return Icon(
                    drawable = drawable,
                    adaptive = Icon.Adaptive(
                        background = if (drawable.background.intrinsicHeight > 0) drawable.background else null,
                        foreground = drawable.foreground
                    )
                )
            } catch (e: Exception) {
                Log.e("IconFetcher", "Error getting stock adaptive icon. Reason: $e")
            }
        }

        return Icon(
            drawable = drawable,
            adaptive = null
        )
    }


    @SuppressLint("DiscouragedApi")
    private fun getResourceId(
        packageContext: Context,
        resourceType: String,
        resourceName: String
    ): Int {
        return packageContext.resources.getIdentifier(
            resourceName,
            resourceType,
            packageContext.packageName
        )
    }

    fun getThemedIcon(iconPack: IconPack, appPackageName: String): Icon? {
        try {
            val iconPackContext = context.createPackageContext(
                iconPack.packageName, Context.CONTEXT_IGNORE_SECURITY
            )

            val appFilterId = getResourceId(iconPackContext, "xml", "appfilter")
            if (appFilterId == 0) return null

            val parser = iconPackContext.resources.getXml(appFilterId)
            var eventType = parser.eventType

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.name == "item") {
                        val componentInfo = parser.getAttributeValue(null, "component")

                        if (componentInfo != null) {
                            val packageNames = componentInfo
                                .removePrefix("ComponentInfo{").removeSuffix("}")
                                .split("/")

                            if (packageNames.contains(appPackageName)) {
                                val drawableName = parser.getAttributeValue(null, "drawable")

                                if (drawableName != null) {

                                    val drawableId =
                                        getResourceId(iconPackContext, "drawable", drawableName)

                                    if (drawableId != 0) {
                                        val drawable = ResourcesCompat.getDrawable(
                                            iconPackContext.resources,
                                            drawableId,
                                            iconPackContext.theme
                                        )

                                        if (drawable == null) {
                                            return null
                                        }

                                        if (drawable is AdaptiveIconDrawable) {
                                            return Icon(
                                                drawable = drawable,
                                                adaptive = Icon.Adaptive(
                                                    background = if (drawable.background.intrinsicHeight > 0) drawable.background else null,
                                                    foreground = drawable.foreground
                                                )
                                            )
                                        }

                                        return Icon(
                                            drawable = drawable,
                                            adaptive = null
                                        )
                                    }
                                }
                            }
                        }

                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.e(
                "IconFetcher",
                "Error getting themed adaptive icon. Reason: $e"
            )
        }

        return null
    }
}