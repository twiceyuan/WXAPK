import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = 35

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.twiceyuan.wxapk"
        minSdk = 21
        targetSdk = 35
        namespace = "com.twiceyuan.wxapk"
        versionCode = getAppVersionCode()
        versionName = getLastTagName()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            if (System.getenv("CI") != null) {
                storeFile = file(System.getenv("TWICEYUAN_KEYSTORE") ?: "")
                storePassword = System.getenv("TWICEYUAN_KEYSTORE_PASSWD")
                keyAlias = System.getenv("TWICEYUAN_KEY_ALIAS")
                keyPassword = System.getenv("TWICEYUAN_KEY_PASSWD")
            } else {
                val keystore = System.getenv("WXAPK_KEYSTORE")
                storeFile = file(keystore ?: "/dev/null")
                storePassword = System.getenv("WXAPK_KEYSTORE_PASSWD")
                keyAlias = System.getenv("WXAPK_KEY_ALIAS")
                keyPassword = System.getenv("WXAPK_KEY_PASSWD")
            }
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    applicationVariants.configureEach {
        if (buildType.name != "release") return@configureEach
        outputs.forEach { output ->
            val buildTypeName = buildType.name
            val version = "${versionName}-${versionCode}"
            if (output is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                output.outputFileName = "WXAPK-${version}-${buildTypeName}.apk"
            }
        }
    }
}

// 根据 runner action 编号生成版本号
fun getAppVersionCode(): Int {
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(FileInputStream(localPropertiesFile))
    }
    val version = properties.getProperty("GITHUB_RUN_NUMBER") ?: System.getenv("GITHUB_RUN_NUMBER")
    val baseVersionCode: Int = rootProject.properties["app.baseVersionCode"]?.toString()?.toInt() ?: 0
    return (version?.toInt() ?: 1) + baseVersionCode
}

// 获取最新的 tag 名称
fun getLastTagName(): String {
    return try {
        val stdout = ByteArrayOutputStream()
        project.exec {
            commandLine("git", "describe", "--abbrev=0", "--tags")
            standardOutput = stdout
        }
        stdout.toString().trim()
    } catch (e: Exception) {
        "v1.0.0"
    }
}

tasks.register("printVersionInfo") {
    doLast {
        println("VersionName: ${getLastTagName()}, VersionCode: ${getAppVersionCode()}")
    }
}

dependencies {
    val kotlinVersion = rootProject.extra["kotlinVersion"] as String
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("androidx.documentfile:documentfile:1.0.1")
    implementation("androidx.activity:activity-ktx:1.9.1")
}
