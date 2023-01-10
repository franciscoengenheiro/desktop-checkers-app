import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.1.1"
}

group = "pt.isel.leic.tds"
version = "1.0"

repositories {
    google()
    mavenCentral()
    flatDir { dirs("libs") }
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    // Kotlin test framework
    testImplementation(kotlin("test"))
    // Desktop Compose
    implementation(compose.desktop.currentOs)
    // Async File access
    implementation("com.github.javasync:RxIo:1.2.6")
    // MongoDb integration (Sync and Async versions)
    implementation("org.litote.kmongo:kmongo:4.8.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.19.0")
    implementation("org.litote.kmongo:kmongo-coroutine:4.8.0")
    // Custom canvas (for sound access functions)
    implementation("pt.isel:CanvasLib-jvm:1.0.1")
    // Redux Kotlin (middlewares)
    implementation("org.reduxkotlin:redux-kotlin-threadsafe-jvm:0.5.5")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.jar {
    manifest.attributes["Main-Class"] = "checkers.MainKt"
    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree) // OR .map { zipTree(it) }
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

compose.desktop {
    application {
        // Define the main class of the Application
        mainClass = "checkers.MainKt"
        nativeDistributions {
            // macOS — .dmg (TargetFormat.Dmg), .pkg (TargetFormat.Pkg)
            // Windows — .exe (TargetFormat.Exe), .msi (TargetFormat.Msi)
            // Linux — .deb (TargetFormat.Deb), .rpm (TargetFormat.Rpm)
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb)
            packageName = "CheckersDesktopApp"
            packageVersion = "1.23"
            windows {
                // a version for all Windows distributables
                packageVersion = "1.23"
                // a version only for the msi package
                // msiPackageVersion = "..."
                // a version only for the exe package
                exePackageVersion = "1.0.1"
            }
        }
    }
}