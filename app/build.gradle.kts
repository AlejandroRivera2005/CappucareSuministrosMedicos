
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.hilt)
    jacoco
}

android {
    namespace = "com.example.cappucaresuministrosmedicos"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.cappucaresuministrosmedicos"
        minSdk = 30
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
        }

        release {
            optimization {
                enable = true
                packageScope = setOf(
                    "androidx.**",
                    "kotlin.**",
                    "kotlinx.**"
                )
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

jacoco {
    toolVersion = "0.8.14"
}

dependencies {
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    implementation(libs.hilt.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.androidx.compose.material.icons.core)

    ksp(libs.hilt.compiler)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)

    testImplementation(
        "org.junit.jupiter:junit-jupiter-api:5.10.2"
    )
    testRuntimeOnly(
        "org.junit.jupiter:junit-jupiter-engine:5.10.2"
    )
    testRuntimeOnly(
        "org.junit.vintage:junit-vintage-engine:5.10.2"
    )
    testRuntimeOnly(
        "org.junit.platform:junit-platform-launcher:1.10.2"
    )

    testImplementation(
        "io.kotest:kotest-runner-junit5:5.9.1"
    )
    testImplementation(
        "io.kotest:kotest-assertions-core:5.9.1"
    )

    testImplementation(
        "io.mockk:mockk:1.13.5"
    )

    testImplementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2"
    )

    testImplementation(
        "org.robolectric:robolectric:4.14.1"
    )

    androidTestImplementation(
        platform(libs.androidx.compose.bom)
    )
    androidTestImplementation(
        libs.androidx.compose.ui.test.junit4
    )
    androidTestImplementation(
        libs.androidx.espresso.core
    )
    androidTestImplementation(
        libs.androidx.junit
    )

    debugImplementation(
        libs.androidx.compose.ui.test.manifest
    )
    debugImplementation(
        libs.androidx.compose.ui.tooling
    )
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(21))
    })
}

val coverageExcludes = listOf(
    "**/R.class",
    "**/R$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*_Factory.*",
    "**/*_MembersInjector.*",
    "**/*_HiltModules*.*",
    "**/di/**",
    "**/MainActivity*.*",
    "**/CappucareApp*.*",
    "**/model/local/**",
    "**/model/NotificadorStock*",
    "**/model/pokemon/remote/**",
    "**/view/**"
)

val coverageClasses = files(
    fileTree(layout.buildDirectory.dir("intermediates")) {
        include("**/compileDebugKotlin/classes/**/*.class")
        include("**/compileDebugJavaWithJavac/classes/**/*.class")
        exclude(coverageExcludes)
    },
    fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug")) {
        include("**/*.class")
        exclude(coverageExcludes)
    }
)

val coverageExecutionData = fileTree(layout.buildDirectory) {
    include("**/testDebugUnitTest.exec")
    include("**/testDebugUnitTest.ec")
}

tasks.register<JacocoReport>("jacocoDebugUnitTestReport") {
    dependsOn("testDebugUnitTest")
    classDirectories.setFrom(coverageClasses)
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
    executionData.setFrom(coverageExecutionData)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}
