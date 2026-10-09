plugins {
    `java-library`
    id("com.gtnewhorizons.retrofuturagradle") version "1.4.9"
}

group = "me.almana"
version = "0.1.0"

base {
    archivesName.set("Craftiffia-Core")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
        vendor.set(org.gradle.jvm.toolchain.JvmVendorSpec.AZUL)
    }
}

minecraft {
    mcVersion.set("1.7.10")
    username.set("Developer")
    extraRunJvmArguments.add("-ea:me.almana")
}

repositories {
    maven {
        name = "Curse Maven"
        url = uri("https://www.cursemaven.com")
    }
    maven {
        name = "GregTech Maven"
        url = uri("https://gregtech.mechaenetia.com/")
    }
    maven {
        name = "GTNH Maven"
        url = uri("https://nexus.gtnewhorizons.com/repository/public/")
    }
}

dependencies {
    api("com.gregoriust.gregtech:gregtech_1.7.10:6.17.06:dev") {
        exclude(group = "codechicken", module = "NotEnoughItems")
        exclude(group = "codechicken", module = "CodeChickenCore")
        exclude(group = "codechicken", module = "CodeChickenLib")
    }
    api("com.github.GTNewHorizons:ModularUI2:2.3.88-1.7.10:dev")
    compileOnly("com.github.GTNewHorizons:Applied-Energistics-2-Unofficial:rv3-beta-1081-GTNH:dev") { isTransitive = false }
    compileOnly("com.github.GTNewHorizons:GTNHLib:0.11.52:dev") { isTransitive = false }
    compileOnly("ic2:IC2Classic:1.2.1.8:dev")
    testImplementation("junit:junit:4.13.2")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

val remappedSourceDir = layout.buildDirectory.dir("generated/sources/srgToMcp")
val remapDecompilerSources = tasks.register("remapDecompilerSources") {
    dependsOn("decompressDecompiledSources")
    inputs.dir("src/main/java")
    outputs.dir(remappedSourceDir)

    doLast {
        val mappingDir = gradle.gradleUserHomeDir.resolve(
            "caches/minecraft/net/minecraftforge/forge/1.7.10-10.13.4.1614-1.7.10/unpacked/conf",
        )
        val names = sequenceOf("fields.csv", "methods.csv")
            .flatMap { mappingDir.resolve(it).readLines().drop(1) }
            .map { it.split(',', limit = 3) }
            .associate { it[0] to it[1] }
        val identifier = Regex("\\b(?:field|func)_\\d+_[A-Za-z_]+\\b")
        val inputDir = file("src/main/java")
        val outputDir = remappedSourceDir.get().asFile

        delete(outputDir)
        fileTree(inputDir).matching { include("**/*.java") }.forEach { source ->
            val target = outputDir.resolve(source.relativeTo(inputDir))
            target.parentFile.mkdirs()
            target.writeText(identifier.replace(source.readText()) { names[it.value] ?: it.value })
        }
    }
}

sourceSets.named("main") {
    java.setSrcDirs(listOf(remappedSourceDir))
}

tasks.named<JavaCompile>("compileJava") {
    dependsOn(remapDecompilerSources)
}
