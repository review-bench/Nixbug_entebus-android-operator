plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.nixbug.entebus"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.nixbug.entebus"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.scalars)

    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    implementation(libs.gson)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.android.splashScreen)
}

//Task to generate and re-generate API client library
tasks.register<Exec>("generateApiClient") {
    description = "Generates the API client from OpenAPI spec"
    group = "openapi"

    val outputDir = "$projectDir/src/main/java/com/nixbug/entebus/data/remote"

    commandLine(
        "openapi-generator-cli", "generate",
        "-i", "$projectDir/src/main/openApiSpecification/openApiSpecification.json",
        "-g", "kotlin",
        "-o", outputDir,
        "--library", "jvm-retrofit2",
        "--additional-properties", "useCoroutines=true,serializationLibrary=gson",
        "--global-property", "apiTests=false,modelTests=false"
    )
}

