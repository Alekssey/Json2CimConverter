plugins {
    plugSpringBoot()
    plugSpringKotlin()
    plugSpringDependencyManagement()
    application
}

group = "ru.nti.dpts"
version = "0.10.0"
description = "Scheme Manager Back"

dependencies {
    implementation(Libs.Kotlin.arrow)
    implementation(Libs.Dtps.java_dev_kit)

    implementation(Libs.Kotlin.reflect)
    implementation(Libs.Kotlin.stdlib)
    implementation(Libs.Jackson.kotlin)
    implementation(Libs.Jackson.databind)

//    implementation(Libs.Spring.kafka)
    implementation(Libs.Spring.starter_web)
    implementation(Libs.Spring.starter_webflux)
    implementation(Libs.Spring.starter_websocket)
    implementation(Libs.Spring.starter_security)
    implementation(Libs.Spring.starter_security_oauth2_client)
    implementation(Libs.Spring.starter_security_oauth2_resource_server)
    implementation(Libs.Spring.starter_data_mongo)
    implementation(Libs.Spring.security_messaging)

    implementation(Libs.Actuator.spring_starter_actuator)
    implementation(Libs.Actuator.prometheus)
    implementation(project(":cim-converter"))

    implementation(Libs.Javax.validation)
    implementation(Libs.Javax.activation)

    implementation(Libs.Jaxb.api)
    implementation(Libs.Jaxb.runtime)

    implementation(Libs.Caffeine.caffeine)

    testImplementation(Libs.Test.spring_starter_test)
    testImplementation(Libs.Test.flapdoodle_mongo)
    testImplementation(Libs.Test.flapdoodle_spring)
    testImplementation(Libs.Test.assertj_arrow_core)
}

springBoot {
    mainClass.set("ru.nti.dpts.schememanagerback.SchemeManagerBackAppKt")
}
