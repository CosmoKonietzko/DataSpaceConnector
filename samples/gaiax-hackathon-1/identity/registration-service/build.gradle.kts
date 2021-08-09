plugins {
    `java-library`
}

val jwtVersion: String by project


dependencies {
    api(project(":spi"))
    implementation(project(":common:util"))
    implementation(project(":samples:gaiax-hackathon-1:identity:ion"))
    implementation("org.quartz-scheduler:quartz:2.3.0")
}