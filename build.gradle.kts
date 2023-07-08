plugins {
    java
}

sourceSets {
    main {
        java {
            setSrcDirs(setOf(file("src/backend")))
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.quiltmc:quiltflower:1.9.0")
}

tasks.withType<JavaCompile> {
    options.release.set(17)
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "juuxel.bloom.backend.BloomBackend",
            "Class-Path" to configurations.runtimeClasspath.get()
                .resolve()
                .joinToString(separator = " ") { it.name }
        )
    }
}

tasks.register<Copy>("setupDev") {
    from(configurations.runtimeClasspath)
    from(tasks.jar) {
        rename { "backend.jar" }
    }
    from(fileTree(file("src/frontend")))
    into("build/dev")
}
