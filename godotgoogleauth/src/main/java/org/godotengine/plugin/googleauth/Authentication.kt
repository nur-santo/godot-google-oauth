package org.godotengine.plugin.googleauth

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import org.godotengine.godot.plugin.SignalInfo
import org.json.JSONObject

class Authentication(private val plugin: GoogleAuthPlugin) {
	companion object {
		private const val GOOGLE_SIGN_IN = 9001
		private const val TAG = "GodotGoogleAuth"
	}

	private lateinit var activity: Activity
	private lateinit var googleSignInClient: GoogleSignInClient

	fun authSignals(): MutableSet<SignalInfo> {
		val signals: MutableSet<SignalInfo> = mutableSetOf()

		signals.add(SignalInfo("auth_success", String::class.java))
		signals.add(SignalInfo("auth_failure", String::class.java))
		signals.add(SignalInfo("sign_out_success", Boolean::class.javaObjectType))
		signals.add(SignalInfo("login_status", Boolean::class.javaObjectType))
		signals.add(SignalInfo("user_info", String::class.java))

		return signals
	}

	fun init(activity: Activity) {
		this.activity = activity
		val resId = activity.resources.getIdentifier("default_web_client_id", "string", activity.packageName)

		if (resId == 0) {
			Log.e(TAG, "default_web_client_id not found in app resources.")
			return
		}

		val webClientId = activity.getString(resId)

		val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
			.requestIdToken(webClientId)
			.requestEmail()
			.build()

		googleSignInClient = GoogleSignIn.getClient(activity, gso)
	}

	fun signInWithGoogle() {
		if (!::googleSignInClient.isInitialized) {
			Log.e(TAG, "GoogleSignInClient not initialized.")
			plugin.emitGodotSignal("auth_failure", "Google Sign-In not initialized.")
			return
		}
		try {
			val signInIntent = googleSignInClient.signInIntent
			activity.startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
		} catch (e: Exception) {
			Log.e(TAG, "Error starting Google Sign-In", e)
			plugin.emitGodotSignal("auth_failure", e.message ?: "Unknown error")
		}
	}

	fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (requestCode == GOOGLE_SIGN_IN) {
			val task = GoogleSignIn.getSignedInAccountFromIntent(data)
			try {
				val account = task.getResult(ApiException::class.java)!!
				val idToken = account.idToken
				if (idToken != null) {
					Log.d(TAG, "Google Sign-In success: ${account.email}")
					plugin.emitGodotSignal("auth_success", idToken)
				} else {
					Log.w(TAG, "No ID token returned.")
					plugin.emitGodotSignal("auth_failure", "No ID token returned.")
				}
			} catch (e: ApiException) {
				Log.w(TAG, "Google sign in failed", e)
				plugin.emitGodotSignal("auth_failure", e.message ?: "Unknown error")
			}
		}
	}

	fun signOut() {
		if (!::googleSignInClient.isInitialized) {
			plugin.emitGodotSignal("sign_out_success", false)
			return
		}
		googleSignInClient.signOut()
			.addOnCompleteListener(activity) {
				Log.d(TAG, "Signed out from Google.")
				plugin.emitGodotSignal("sign_out_success", true)
			}
			.addOnFailureListener { e ->
				Log.e(TAG, "Sign out failed", e)
				plugin.emitGodotSignal("sign_out_success", false)
				plugin.emitGodotSignal("auth_failure", "Failed to sign out: ${e.message}")
			}
	}


	fun isLoggedIn(): Boolean {
		return GoogleSignIn.getLastSignedInAccount(activity) != null
	}

	fun checkLoginStatus() {
		plugin.emitGodotSignal("login_status", isLoggedIn())
	}

	fun getUserInfo() {
		val account = GoogleSignIn.getLastSignedInAccount(activity)

		if (account == null) {
			plugin.emitGodotSignal("auth_failure", "No user logged in")
			return
		}

		val userData = mapOf(
			"id" to account.id,
			"email" to account.email,
			"name" to account.displayName,
			"given_name" to account.givenName,
			"family_name" to account.familyName,
			"photo_url" to (account.photoUrl?.toString() ?: ""),
			"id_token" to (account.idToken ?: "")
		)

		val json = JSONObject(userData).toString()
		plugin.emitGodotSignal("user_info", json)
	}
}
