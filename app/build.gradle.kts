plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.nixbug.entebus"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.nixbug.entebus"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "OPERATOR_BASE_URL",
                "\"https://dev-api.entebus.com/operator/\""
            )

            buildConfigField(
                "String",
                "PUBLIC_BASE_URL",
                "\"https://dev-api.entebus.com/public/\""
            )

        }
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
        buildConfig = true
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
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.compose.material.icons.extended)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.scalars)

    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    implementation(libs.gson)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.android.splashScreen)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.play.services.location)

    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)

    implementation(libs.mlkit.barcode)
}

tasks.register<Exec>("generateOperatorApiClient") {
    description = "Generates the Operator API client from OpenAPI spec"
    group = "openapi"

    val outputDir = "$projectDir/src/main/java/com/nixbug/entebus/data/openapigenerator/operator"

    commandLine(
        "openapi-generator-cli",
        "generate",
        "-i", "https://dev-api.entebus.com/operator/openapi.json",
        "-g", "kotlin",
        "-o", outputDir,
        "--library", "jvm-retrofit2",
        "--additional-properties",
        "useCoroutines=true,serializationLibrary=gson,dateLibrary=string,packageName=com.nixbug.entebus.data.remote.operator",
        "--api-package", "com.nixbug.entebus.data.remote.operator.api",
        "--model-package", "com.nixbug.entebus.data.remote.operator.model",
        "--invoker-package", "com.nixbug.entebus.data.remote.operator.core",
        "--global-property", "apiTests=false,modelTests=false"
    )
}

tasks.register<Exec>("generatePublicApiClient") {
    description = "Generates the Public API client from OpenAPI spec"
    group = "openapi"

    val outputDir = "$projectDir/src/main/java/com/nixbug/entebus/data/openapigenerator/publicapi"

    commandLine(
        "openapi-generator-cli", "generate",
        "-i", "https://dev-api.entebus.com/public/openapi.json",
        "-g", "kotlin",
        "-o", outputDir,
        "--library", "jvm-retrofit2",
        "--additional-properties",
        "useCoroutines=true,serializationLibrary=gson,dateLibrary=string,packageName=com.nixbug.entebus.data.remote.publicapi",
        "--api-package", "com.nixbug.entebus.data.remote.publicapi.api",
        "--model-package", "com.nixbug.entebus.data.remote.publicapi.model",
        "--invoker-package", "com.nixbug.entebus.data.remote.publicapi.core",
        "--global-property", "apiTests=false,modelTests=false"
    )
}

tasks.register("generateAllApis") {
    group = "openapi"
    dependsOn("generateOperatorApiClient", "generatePublicApiClient")
}
