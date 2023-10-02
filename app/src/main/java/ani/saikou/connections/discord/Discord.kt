package ani.saikou.connections.discord

import android.content.Context
import android.content.Intent
import android.widget.TextView
import androidx.core.content.edit
import ani.saikou.R
import ani.saikou.connections.discord.serializers.User
import ani.saikou.others.CustomBottomDialog
import ani.saikou.toast
import ani.saikou.tryWith
import ani.saikou.tryWithSuspend
import io.noties.markwon.Markwon
import io.noties.markwon.SoftBreakAddsNewLinePlugin
import kotlinx.coroutines.Dispatchers
import java.io.File

object Discord {

    var token: String? = null
    var userid: String? = null
    var avatar: String? = null

    private const val TOKEN = "discord_token"

    fun getSavedToken(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        token = sharedPref.getString(TOKEN, null)
        return token != null
    }

    fun saveToken(context: Context, token: String) {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        sharedPref.edit {
            putString(TOKEN, token)
            commit()
        }
    }

    fun removeSavedToken(context: Context) {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        sharedPref.edit {
            remove(TOKEN)
            commit()
        }

        tryWith(true) {
            val dir = File(context.filesDir?.parentFile, "app_webview")
            if (dir.deleteRecursively())
                toast(context.getString(R.string.discord_logout_success))
        }
    }

    private var rpc : RPC? = null
    suspend fun getUserData() = tryWithSuspend(true) {
        if(rpc==null) {
            val rpc = RPC(token!!, Dispatchers.IO).also { rpc = it }
            val user: User = rpc.getUserData()
            userid = user.username
            avatar = user.userAvatar()
            rpc.close()
            true
        } else true
    } ?: false


    fun warning(context: Context) = CustomBottomDialog().apply {
        title = context.getString(R.string.warning)
        val md = context.getString(R.string.discord_warning)
        addView(TextView(context).apply {
            val markWon =
                Markwon.builder(context).usePlugin(SoftBreakAddsNewLinePlugin.create()).build()
            markWon.setMarkdown(this, md)
        })

        setNegativeButton(context.getString(R.string.cancel)) {
            dismiss()
        }

        setPositiveButton(context.getString(R.string.login)) {
            dismiss()
            loginIntent(context)
        }
    }

    private fun loginIntent(context: Context) {
        val intent = Intent(context, Login::class.java)
        context.startActivity(intent)
    }

    fun defaultRPC(): RPC? {
        return token?.let {
            RPC(it, Dispatchers.IO).apply {
                applicationId = "876336799774056754"
                smallImage = RPC.Link(
                    "Saikou",
                    "mp:attachments/711125416575893534/1136239891500503050/Logo_Main.png"
                )
                buttons.add(RPC.Link("Stream on Saikou", "https://github.com/saikou-app/saikou/"))
            }
        }
    }
}