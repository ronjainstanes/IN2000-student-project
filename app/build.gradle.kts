plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "no.uio.ifi.in2000.team11.havvarselapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "no.uio.ifi.in2000.team11.havvarselapp"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

secrets {
    // File containing data that should not be included in git
    propertiesFileName = "secrets.properties"

    // File containing default values, can be included in git
    defaultPropertiesFileName = "local.defaults.properties"

    // Specifies which keys to ignore, can be included in git
    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
}

dependencies {
    // Android core functionality
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    // ViewModel, lifecycle, activity
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Jetpack Compose + BOM ensuring all versions of the packages work well together
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Compose debugger
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Testing (Junit, Espresso, Compose UI testing)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Compose extended icons
    implementation("androidx.compose.material:material-icons-extended:1.6.5")

    // NavController
    val navVersion = "2.7.7"
    implementation("androidx.navigation:navigation-compose:$navVersion")

    // Ktor, including Gson
    val ktorVersion = "2.3.8"
    implementation("io.ktor:ktor-serialization-gson:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")

    // JSON
    implementation("org.json:json:20240303")

    // Maps & Android Maps Compose
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.maps.android:maps-compose:4.3.3")

    // Location services, used for the map
    implementation("com.google.android.gms:play-services-location:21.2.0")

    // API for addresses, used in autocomplete textfield
    implementation("com.google.android.libraries.places:places:3.4.0")

    // Android Desugaring, to be able to use ZonedDateTime
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Implementations for Unit Tests
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation ("androidx.test:core:1.5.0")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.12.0")
}
