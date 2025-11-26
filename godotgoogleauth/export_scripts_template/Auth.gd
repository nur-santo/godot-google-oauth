extends Node
class_name Auth

signal auth_success(id_token: String)
signal auth_failure(error: String)
signal sign_out_success(success: bool)
signal login_status(is_logged_in: bool)
signal user_info(data: Dictionary)

var _plugin: Object

func _ready():
	if Engine.has_singleton("GodotGoogleAuth"):
		_plugin = Engine.get_singleton("GodotGoogleAuth")
		print("GodotGoogleAuth plugin found and initialized!")

		_plugin.connect("auth_success", Callable(self, "_on_auth_success"))
		_plugin.connect("auth_failure", Callable(self, "_on_auth_failure"))
		_plugin.connect("sign_out_success", Callable(self, "_on_sign_out_success"))

		if _plugin.has_signal("login_status"):
			_plugin.connect("login_status", Callable(self, "_on_login_status"))
		if _plugin.has_signal("user_info"):
			_plugin.connect("user_info", Callable(self, "_on_user_info"))
	else:
		push_error("GoogleAuth plugin not found. Cannot sign in.")


func sign_in_with_google():
	if _plugin:
		_plugin.signInWithGoogle()
	else:
		push_error("GodotGoogleAuth singleton not found!")


func sign_out():
	if _plugin:
		_plugin.signOut()
	else:
		push_error("GodotGoogleAuth singleton not found!")


func check_login_status():
	if _plugin:
		_plugin.checkLoginStatus()
	else:
		push_error("GodotGoogleAuth singleton not found!")


func get_user_info():
	if _plugin:
		_plugin.getUserInfo()
	else:
		push_error("GodotGoogleAuth singleton not found!")


func _on_auth_success(id_token: String):
	print("Login berhasil: %s" % id_token)
	emit_signal("auth_success", id_token)


func _on_auth_failure(error: String):
	print("Login gagal: %s" % error)
	emit_signal("auth_failure", error)


func _on_sign_out_success(success: bool):
	print("Sign out status: %s" % success)
	emit_signal("sign_out_success", success)


func _on_login_status(is_logged_in: bool):
	print("Login status: %s" % is_logged_in)
	emit_signal("login_status", is_logged_in)


func _on_user_info(json_string: String):
	var data := JSON.parse_string(json_string)
	if typeof(data) == TYPE_DICTIONARY:
		print("User info:", data)
		emit_signal("user_info", data)
	else:
		push_error("Failed to parse user_info JSON")
