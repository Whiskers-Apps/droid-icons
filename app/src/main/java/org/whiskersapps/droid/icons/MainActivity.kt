package org.whiskersapps.droid.icons

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import org.whiskersapps.droid.droid_icons.IconFetcher
import org.whiskersapps.droid.droid_icons.models.Icon
import org.whiskersapps.droid.droid_icons.models.IconPack
import org.whiskersapps.droid.icons.ui.theme.DroidIconsTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val iconFetcher = IconFetcher(this.application)
        val packageNames = listOf(
            "com.google.android.documentsui",
            "com.google.android.apps.docs",
            "com.android.settings",
            "com.google.android.youtube"
        )

        fun getIcons(iconPack: IconPack? = null): List<Icon> {
            return packageNames.map {
                if (iconPack == null) {
                    iconFetcher.getStockIcon(it)
                } else {

                    iconFetcher.getThemedIcon(iconPack, it) ?: iconFetcher.getStockIcon(it)
                }
            }
        }

        setContent {
            DroidIconsTheme {

                val icons = remember { mutableStateOf(getIcons()) }
                val mode = remember { mutableStateOf("Stock") }

                Column(
                    modifier = Modifier
                        .systemBarsPadding()
                        .padding(24.dp)
                ) {

                    Text("Icon Packs", fontWeight = FontWeight.Medium, fontSize = 16.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        icons.value = getIcons()
                                    }
                                    .padding(2.dp)
                            ) {

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(48.dp)
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.ic_launcher_background),
                                        contentDescription = null
                                    )

                                    Image(
                                        painter = painterResource(R.drawable.ic_launcher_foreground),
                                        contentDescription = null
                                    )
                                }

                                Text("Stock Icons")
                            }
                        }

                        items(
                            items = iconFetcher.getIconPacks(),
                            key = { it.packageName },
                        ) { iconPack ->

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        icons.value = getIcons(iconPack)
                                    }
                                    .padding(2.dp)
                            ) {

                                if (iconPack.icon.isAdaptive) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(48.dp)
                                    ) {
                                        iconPack.icon.adaptive?.background?.let {
                                            Image(
                                                bitmap = iconPack.icon.adaptive!!.background!!.toBitmap()
                                                    .asImageBitmap(),
                                                contentDescription = null
                                            )
                                        }


                                        Image(
                                            bitmap = iconPack.icon.adaptive!!.foreground.toBitmap()
                                                .asImageBitmap(),
                                            contentDescription = null
                                        )
                                    }
                                } else {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                    ) {
                                        Image(
                                            bitmap = iconPack.icon.drawable.toBitmap()
                                                .asImageBitmap(),
                                            contentDescription = null,
                                        )
                                    }
                                }

                                Text(iconPack.name)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Example Apps", fontWeight = FontWeight.Medium, fontSize = 16.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(64.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(items = icons.value) { icon ->
                            Box(contentAlignment = Alignment.Center) {
                                if (icon.isAdaptive && mode.value == "Adaptive") {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                    ) {
                                        icon.adaptive?.background?.let {
                                            Image(
                                                bitmap = icon.adaptive!!.background!!.toBitmap()
                                                    .asImageBitmap(),
                                                contentDescription = null
                                            )
                                        }

                                        Image(
                                            bitmap = icon.adaptive!!.foreground.toBitmap()
                                                .asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop,
                                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                                        )
                                    }
                                } else {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                    ) {
                                        Image(
                                            bitmap = icon.drawable.toBitmap().asImageBitmap(),
                                            contentDescription = null,
                                        )
                                    }
                                }
                            }

                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Mode", fontWeight = FontWeight.Medium, fontSize = 16.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    MultiChoiceSegmentedButtonRow {
                        listOf("Stock", "Adaptive").forEachIndexed { index, choice ->
                            SegmentedButton(
                                checked = choice == mode.value,
                                onCheckedChange = { mode.value = choice },
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = 2),
                                label = {
                                    Text(choice)
                                },
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = MaterialTheme.colorScheme.primary,
                                    activeContentColor = MaterialTheme.colorScheme.onPrimary,
                                    activeBorderColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}