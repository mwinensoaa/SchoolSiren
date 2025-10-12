package com.mwinensoaa.schoolsiren.screens

import android.R.attr.name
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp






import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwinensoaa.schoolsiren.R
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowRight
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val scrollState = rememberScrollState()
    val accentColor = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About Developer") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Info, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image
            Image(
                painter = painterResource(R.drawable.icons_twitter),
                contentDescription = "Developer Image",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = "Kuudaari Crispin Mwineveng Mwinensoaa",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(accentColor)
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))
            Text(
                text = "A passionate Android Developer and AI enthusiast who loves creating intelligent, user-friendly applications.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(thickness = 2.dp, color = accentColor)

            SectionHeader("Hobbies & Interests")
            TwoColumnList(
                leftItems = listOf("Typing", "Documentaries", "Football"),
                rightItems = listOf("Programming", "Astrophysics", "Politics")
            )

            HorizontalDivider(thickness = 2.dp, color = accentColor)

            SectionHeader("Languages & Frameworks")
            TwoColumnList(
                leftItems = listOf("Kotlin", "Python", "Java"),
                rightItems = listOf("Android", "Hibernate", "Django")
            )

            HorizontalDivider(thickness = 2.dp, color = accentColor)

            SectionHeader("Social Links")
            SocialIconRow()


            Spacer(Modifier.height(16.dp))
            Text(
                text = "\"Where ignorance is bliss, ’tis folly to be wise.\" — Thomas Gray",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )

            Spacer(Modifier.height(24.dp))
            Text(
                text = "© 2025 Konyele. All Rights Reserved.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}



@Composable
fun SocialIconRow() {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        SocialIcon(
            iconRes = R.drawable.icons_facebook,
            url = "https://www.facebook.com/yourusername",
            contentDescription = "Facebook"
        )
        SocialIcon(
            iconRes = R.drawable.icons_github,
            url = "https://github.com/mwinensoaa",
            contentDescription = "Github"
        )
        SocialIcon(
            iconRes = R.drawable.icons_twitter,
            url = "https://twitter.com/yourusername",
            contentDescription = "Twitter"
        )
        SocialIcon(
            iconRes = R.drawable.icons_instagram,
            url = "https://www.instagram.com/yourusername",
            contentDescription = "Instagram"
        )
        SocialIcon(
            iconRes = R.drawable.icons_linkedin,
            url = "https://www.linkedin.com/in/yourusername",
            contentDescription = "LinkedIn"
        )
    }
}



@Composable
fun SocialIcon(
    iconRes: Int,
    url: String,
    contentDescription: String
) {
    val context = LocalContext.current

    Image(
        painter = painterResource(id = iconRes),
        contentDescription = contentDescription,
        modifier = Modifier
            .size(40.dp)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                // verify that there is an app (browser) to handle it
                context.startActivity(intent)
            },
        contentScale = ContentScale.Fit
    )
}


@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(vertical = 12.dp)
            .fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun TwoColumnList(leftItems: List<String>, rightItems: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            leftItems.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            rightItems.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
        }
    }
}





