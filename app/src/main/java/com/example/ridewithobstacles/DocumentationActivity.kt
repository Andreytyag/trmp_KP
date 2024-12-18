package com.example.ridewithobstacles

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.LinkResolver
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin.GlideStore
import com.bumptech.glide.request.target.Target
import android.content.Context


class DocumentationActivity : AppCompatActivity() {
    private lateinit var markwon:Markwon
    private lateinit var markdownTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_documentation)

        markdownTextView = findViewById<TextView>(R.id.markdownText)




        markwon = Markwon.builder(this)
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
                    builder.linkResolver(object : LinkResolver {
                        override fun resolve(view: View, link: String) {
                            val filename = link.split("/").last().split(".").first()

                            val fileIdentifier = resources.getIdentifier(filename, "raw", packageName)
                            val markdownText = resources.openRawResource(fileIdentifier).bufferedReader().use {it.readText()}
                            markwon.setMarkdown(markdownTextView, markdownText)

                        }
                    })
                }
            })
            .usePlugin(GlideImagesPlugin.create(applicationContext))
            .usePlugin(GlideImagesPlugin.create(object : GlideStore {
                override fun load(drawable: AsyncDrawable): RequestBuilder<Drawable> {
                    val identifier = resources.getIdentifier(drawable.destination.split(".").first(), "raw", packageName)
                    return Glide.with(applicationContext).load(identifier)
                }

                override fun cancel(target: Target<*>) {
                    Glide.with(this@DocumentationActivity).clear(target);
                }
            }))
            .build()


        markdownTextView.movementMethod = LinkMovementMethod.getInstance()

        openReadme()

        //val config = CorePlugin.create().a


        val menuButton = findViewById<Button>(R.id.backMenu)
        menuButton.setOnClickListener({
            this.finish()
        })

        val startButton = findViewById<Button>(R.id.toStart)
        startButton.setOnClickListener({
            openReadme()
        })


    }

    private fun openReadme(){
        val markdownText = resources.openRawResource(R.raw.readme).bufferedReader().use { it.readText() }
        markwon.setMarkdown(markdownTextView, markdownText)
    }

}