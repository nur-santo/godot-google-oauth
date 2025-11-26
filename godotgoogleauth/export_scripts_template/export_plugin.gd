@tool
extends EditorExportPlugin

const PLUGIN_NAME := "GodotGoogleAuth"

const ENV_FILE := "res://addons/GodotGoogleAuth/.env"

func _get_name() -> String:
	return PLUGIN_NAME

func _supports_platform(platform: EditorExportPlatform) -> bool:
	return platform is EditorExportPlatformAndroid

func _write_google_auth_xml(client_id: String) -> void:
	var res_dir := "res://android/build/res/values/"
	DirAccess.make_dir_recursive_absolute(res_dir)

	var file_path := res_dir + "godot_google_auth.xml"
	var file := FileAccess.open(file_path, FileAccess.WRITE)

	file.store_line("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
	file.store_line("<resources>")
	file.store_line("    <string name=\"default_web_client_id\">" + client_id + "</string>")
	file.store_line("</resources>")

	file.close()

	print("[GodotGoogleAuth] XML generated → " + file_path)


func _export_begin(features: PackedStringArray, is_debug: bool, path: String, flags: int) -> void:
	print("[GodotGoogleAuth] Export Android dimulai → Inject ENV")

	var env := _load_env()
	if env.has("GOOGLE_WEB_CLIENT_ID"):
		_write_google_auth_xml(env["GOOGLE_WEB_CLIENT_ID"])
	else:
		push_warning("[GodotGoogleAuth] GOOGLE_WEB_CLIENT_ID tidak ditemukan di .env")

	print("[GodotGoogleAuth] Selesai. Resource XML siap untuk plugin.")


# ============================================================
#  LOAD ENV FILE
# ============================================================
func _load_env() -> Dictionary:
	var env := {}
	if not FileAccess.file_exists(ENV_FILE):
		push_warning("[GodotGoogleAuth] Tidak ada .env => skip")
		return env

	var f := FileAccess.open(ENV_FILE, FileAccess.READ)
	while not f.eof_reached():
		var line := f.get_line().strip_edges()
		if line == "" or line.begins_with("#") or not line.contains("="):
			continue
		var parts := line.split("=", false, 1)
		env[parts[0]] = parts[1]
	return env

func _get_android_libraries(platform: EditorExportPlatform, debug: bool) -> PackedStringArray:
	var base_path := "res://addons/%s/bin/" % PLUGIN_NAME
	var aar_path := base_path + ( "debug/%s-debug.aar" % PLUGIN_NAME if debug else "release/%s-release.aar" % PLUGIN_NAME )
	return PackedStringArray([aar_path])

func _get_android_dependencies(platform: EditorExportPlatform, debug: bool) -> PackedStringArray:
	return PackedStringArray([
		"com.google.android.gms:play-services-auth:21.3.0"
	])
