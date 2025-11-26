@tool
extends EditorPlugin

var export_plugin: EditorExportPlugin

func _enter_tree():
	export_plugin = preload("res://addons/GodotGoogleAuth/export_plugin.gd").new()
	add_export_plugin(export_plugin)
	print("GodotGoogleAuth EditorPlugin loaded")

	if not ProjectSettings.has_setting("autoload/GoogleAuth"):
		add_autoload_singleton("GoogleAuth", "res://addons/GodotGoogleAuth/Auth.gd")
		ProjectSettings.save()
		print("Added autoload: GoogleAuth")
	else:
		print("Autoload already exists")


func _exit_tree():
	if export_plugin:
		remove_export_plugin(export_plugin)
		export_plugin = null
	print("GodotGoogleAuth plugin unloaded")
