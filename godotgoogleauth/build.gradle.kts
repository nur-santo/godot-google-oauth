
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

val pluginName = "GodotGoogleAuth"
val pluginPackageName = "org.godotengine.plugin.googleauth"


android {
    namespace = pluginPackageName
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        minSdk = 21

        manifestPlaceholders["godotPluginName"] = pluginName
        manifestPlaceholders["godotPluginPackageName"] = pluginPackageName
        buildConfigField("String", "GODOT_PLUGIN_NAME", "\"${pluginName}\"")
        setProperty("archivesBaseName", pluginName)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("org.godotengine:godot:4.4.1.stable")
    implementation("com.google.android.gms:play-services-auth:21.3.0")
}


