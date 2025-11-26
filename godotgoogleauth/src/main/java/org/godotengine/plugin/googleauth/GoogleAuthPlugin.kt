package org.godotengine.plugin.googleauth

import android.app.Activity
import android.content.Intent
import android.view.View
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot

class GoogleAuthPlugin(godot: Godot) : GodotPlugin(godot) {
	override fun getPluginName(): String = "GodotGoogleAuth"

	private val auth = Authentication(this)

	override fun onMainCreate(activity: Activity?): View? {
		activity?.let { auth.init(it) }
		return super.onMainCreate(activity)
	}

	override fun onMainActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		auth.handleActivityResult(requestCode, resultCode, data)
	}

	override fun getPluginSignals(): MutableSet<SignalInfo> {
		return auth.authSignals()
	}

	fun emitGodotSignal(signalName: String, arg1: Any?, arg2: Any? = null) {
		if (arg2 != null) {
			emitSignal(signalName, arg1, arg2)
		} else {
			emitSignal(signalName, arg1)
		}
	}

	@UsedByGodot
	fun signInWithGoogle() = auth.signInWithGoogle()

	@UsedByGodot
	fun signOut() = auth.signOut()

	@UsedByGodot
	fun getUserInfo() = auth.getUserInfo()

	@UsedByGodot
	fun checkLoginStatus() = auth.checkLoginStatus()

}
