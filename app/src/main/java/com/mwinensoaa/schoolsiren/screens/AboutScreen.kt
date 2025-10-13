package com.mwinensoaa.schoolsiren.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.mwinensoaa.schoolsiren.R
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen() {
    val scrollState = rememberScrollState()
    val accentColor = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About App and Developer") },

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

            Spacer(Modifier.height(8.dp))
            Text(
                text = "About application",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Start
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "A simple and effective alarm app suitable for scheduling various events such as break, close, change lesson, etc" +
                        ", which are common events in schools. it has default sound but allows for custom sound to be uploaded by the user.",
                textAlign = TextAlign.Justify,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(12.dp))
            Text(
                text = "About Developer",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Start
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = "A passionate software Developer and AI enthusiast who loves creating intelligent, user-friendly applications " +
                        "with many years of experience. Always learning and ready for challenges",
                textAlign = TextAlign.Justify,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(2.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically, // centers items vertically
                horizontalArrangement = Arrangement.SpaceBetween // space between image and text
            ) {
                Image(
                    painter = painterResource(R.drawable.icons_comp_logo),
                    contentDescription = "Developer Image",
                    modifier = Modifier
                        .size(width = 200.dp, height = 60.dp),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "Contact: kuudaari@gmail.com",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15.sp,

                )
            }

            Spacer(Modifier.height(2.dp))
            HorizontalDivider(thickness = 2.dp, color = accentColor)
            SectionHeader("Languages & Frameworks")
            TwoColumnList(
                leftItems = listOf("Kotlin", "Python", "Java"),
                rightItems = listOf("Android", "Hibernate", "Django")
            )

            HorizontalDivider(thickness = 2.dp, color = accentColor)
            SectionHeader("Hobbies & Interests")
            TwoColumnList(
                leftItems = listOf("Typing", "Documentaries", "Football"),
                rightItems = listOf("Programming", "Astrophysics", "Politics")
            )
            HorizontalDivider(thickness = 2.dp, color = accentColor)
            SectionHeader("Social Links")
            SocialIconRow()


            HorizontalDivider(thickness = 2.dp, color = accentColor)
            SectionHeader("Favourite Quote")
            Text(
                text = "\"Where ignorance is bliss, ’tis folly to be wise.\" — Thomas Gray",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
            Spacer(Modifier.height(5.dp))
            HorizontalDivider(thickness = 0.5.dp, color = accentColor)
            Text(
                text = "© 2025 Konyele. All Rights Reserved.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(15.dp))
            Text(
                text = "V1.0",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
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
            url = "https://www.facebook.com/kuudaari.crispin",
            contentDescription = "Facebook"
        )
        SocialIcon(
            iconRes = R.drawable.icons_github,
            url = "https://github.com/mwinensoaa",
            contentDescription = "Github"
        )
        SocialIcon(
            iconRes = R.drawable.icons_twitter,
            url = "https://x.com/GadDad_",
            contentDescription = "Twitter"
        )
        SocialIcon(
            iconRes = R.drawable.icons_linkedin,
            url = "https://www.linkedin.com/in/kcm-mwinensoaa-621134143/",
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





