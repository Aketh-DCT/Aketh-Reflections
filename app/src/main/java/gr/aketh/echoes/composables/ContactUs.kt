package gr.aketh.echoes.composables

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import gr.aketh.echoes.R
import gr.aketh.echoes.ui.theme.Echoes_AkethTheme
import kotlinx.coroutines.launch

object ContactUs {

    @Composable
    fun test(navController: NavController){
        Text("Your baby")
    }



    @Composable
    fun FormM(contact_us: String){

        var expanded by remember { mutableStateOf(false) }

        val dividerWidth by animateDpAsState(
            targetValue = if (expanded) 0.3f.dp else 0.dp, label = "",
            animationSpec = tween(durationMillis = 2000)
        )

        LaunchedEffect(Unit) {
            expanded = true
        }


        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()) {
            Text(
                text = contact_us,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 60.dp)
            )

            Text(
                text = stringResource(id = R.string.contact_us_desc),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(top = 10.dp)
            )
            Divider(color = Color.Black, thickness = 5.dp, modifier = Modifier
                .fillMaxWidth(dividerWidth.value)
                .align(Alignment.CenterHorizontally)
                .padding(top = 7.0.dp, bottom = 2.0.dp)
                .clip(RoundedCornerShape(5.dp)))


            Image(painter = painterResource(id = R.drawable.dieuthinsi_protovathmias_trikalon), contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 20.0.dp))



            Text(
                text = stringResource(id = R.string.contact_us_coordinators_details),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 15.dp)
            )



            ClickableLinkText(
                prefix = stringResource(id = R.string.contact_us_email),
                link = stringResource(id = R.string.contact_us_email_dipe),
                linkType = LinkType.EMAIL,
                modifier = Modifier.padding(top = 15.dp)
            )

            ClickableLinkText(
                prefix = stringResource(id = R.string.contact_us_phone_prefix),
                link = stringResource(id = R.string.contact_us_phone),
                linkType = LinkType.PHONE,
                modifier = Modifier.padding(top = 15.dp)
            )

            ClickableLinkText(
                prefix = stringResource(id = R.string.contact_us_website_prefix),
                link = stringResource(id = R.string.contact_us_website),
                linkType = LinkType.WEBPAGE,
                modifier = Modifier.padding(top = 15.dp)
            )

            ClickableLinkText(
                prefix = stringResource(id = R.string.contact_us_project_website),
                link = stringResource(id = R.string.contact_us_project_website_link),
                linkType = LinkType.WEBPAGE,
                modifier = Modifier.padding(top = 15.dp)
            )

            val context = LocalContext.current
            var facebook_site = stringResource(id = R.string.contact_us_facebook_website)

            Image(painter = painterResource(id = R.drawable.facebook_official), contentDescription = "",
                modifier = Modifier
                    .padding(top = 20.0.dp)
                    .width(width = 40.dp)
                    .clickable(onClick = {
                        

                        if (facebook_site.startsWith("https://www.facebook.com/")) {
                            facebook_site = facebook_site.replace("https://www.facebook.com/", "")
                        }
                        val fbIntent = Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=$facebook_site"))
                        val resolvedInfos = context.packageManager.queryIntentActivities(fbIntent, PackageManager.MATCH_ALL)
                        if (resolvedInfos.size > 0) {
                            context.startActivity(fbIntent)
                        } else {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/$facebook_site")))
                        }

                        //val intent = Intent(Intent.ACTION_VIEW, Uri.parse(facebook_site))
                        //context.startActivity(intent)
                    })

            )






        }
    }

    enum class LinkType {
        WEBPAGE, EMAIL, PHONE
    }

    @Composable
    fun ClickableLinkText(
        prefix: String,
        link: String,
        linkType: LinkType,
        modifier: Modifier = Modifier,
        textAlign: TextAlign = TextAlign.Center,
        style: TextStyle = MaterialTheme.typography.bodyLarge
    ) {
        val annotatedString = buildAnnotatedString {
            append(prefix)
            withStyle(style = SpanStyle(color = Color.Blue)) {
                pushStringAnnotation(tag = "LINK", annotation = "$linkType:$link")
                append( " $link")
                pop()
            }
        }
        val context = LocalContext.current

        ClickableText(
            text = annotatedString,
            onClick = { offset ->
                annotatedString.getStringAnnotations(tag = "LINK", start = offset, end = offset + 1)
                    .firstOrNull()?.let { annotation ->
                        val linkInfo = annotation.item.split(":")
                        val linkType = LinkType.valueOf(linkInfo[0])
                        val link = linkInfo[1]

                        when (linkType) {
                            LinkType.WEBPAGE -> {
                                var linkA = link
                                if (!link.startsWith("http://") && !link.startsWith("https://")) {
                                    linkA = "http://$link"
                                }
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkA))
                                context.startActivity(intent)
                            }
                            LinkType.EMAIL -> {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:$link")
                                }
                                context.startActivity(intent)
                            }
                            LinkType.PHONE -> {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:$link")
                                }
                                context.startActivity(intent)
                            }
                        }
                    }
            },
            style = style.merge(TextStyle(textAlign = textAlign)),
            modifier = modifier,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}