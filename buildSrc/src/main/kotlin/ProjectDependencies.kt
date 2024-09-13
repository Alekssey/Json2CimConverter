object LibVersion {
    const val spring_boot = "3.1.0"
    const val spring_boot_messaging = "6.1.0"
//    const val spring_kafka = "3.0.12"
    const val java_dev_kit = "2.2.3"
    const val arrow = "1.1.5"
    const val rd4j = "4.2.3"
    const val log4j = "2.20.0"
    const val junit = "5.7.0"
    const val assertj = "3.23.1"
    const val assertj_arrow = "0.2.0"
    const val flapdoodle = "4.6.0"
    const val prometheus = "1.10.2"
    const val jaxb = "2.3.1"
}

object Libs {
    object Kotlin {
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:${Global.kotlin_version}"
        const val jdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Global.kotlin_version}"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Global.kotlin_version}"
        const val arrow = "io.arrow-kt:arrow-core:${LibVersion.arrow}"
    }

    object Dtps {
        const val java_dev_kit = "ru.nti.dtps:dtps-java-dev-kit:${LibVersion.java_dev_kit}"
    }

    object Jackson {
        const val kotlin = "com.fasterxml.jackson.module:jackson-module-kotlin"
        const val databind = "com.fasterxml.jackson.core:jackson-databind"
    }

    object Rdf4j {
        const val rio_rdfxml = "org.eclipse.rdf4j:rdf4j-rio-rdfxml:${LibVersion.rd4j}"
        const val repository_api = "org.eclipse.rdf4j:rdf4j-repository-api:${LibVersion.rd4j}"
        const val sail_memory = "org.eclipse.rdf4j:rdf4j-sail-memory:${LibVersion.rd4j}"
        const val repository_sail = "org.eclipse.rdf4j:rdf4j-repository-sail:${LibVersion.rd4j}"
    }

    object Log4j {
        const val api = "org.apache.logging.log4j:log4j-api:${LibVersion.log4j}"
        const val core = "org.apache.logging.log4j:log4j-core:${LibVersion.log4j}"
        const val slf4j = "org.apache.logging.log4j:log4j-slf4j-impl:${LibVersion.log4j}"
    }

//    object Test {
//        const val junit_jupiter_engine = "org.junit.jupiter:junit-jupiter-engine:${LibVersion.junit}"
//        const val assertj_core = "org.assertj:assertj-core:${LibVersion.assertj}"
//        const val flapdoodle_spring = "de.flapdoodle.embed:de.flapdoodle.embed.mongo.spring27x:${LibVersion.flapdoodle}"
//        const val flapdoodle_mongo = "de.flapdoodle.embed:de.flapdoodle.embed.mongo:${LibVersion.flapdoodle}"
//        const val spring_starter_test = "org.springframework.boot:spring-boot-starter-test:${LibVersion.spring_boot}"
//        const val assertj_arrow_core = "in.rcard:assertj-arrow-core:${LibVersion.assertj_arrow}"
//    }


    object Spring {
//        const val kafka = "org.springframework.kafka:spring-kafka:${LibVersion.spring_kafka}"
        const val starter_web = "org.springframework.boot:spring-boot-starter-web:${LibVersion.spring_boot}"
//        const val starter_webflux = "org.springframework.boot:spring-boot-starter-webflux:${LibVersion.spring_boot}"
//        const val starter_websocket =
//            "org.springframework.boot:spring-boot-starter-websocket:${LibVersion.spring_boot}"
//        const val starter_security =
//            "org.springframework.boot:spring-boot-starter-security:${LibVersion.spring_boot}"
//        const val starter_security_oauth2_client =
//            "org.springframework.boot:spring-boot-starter-oauth2-client:${LibVersion.spring_boot}"
//        const val starter_security_oauth2_resource_server =
//            "org.springframework.boot:spring-boot-starter-oauth2-resource-server:${LibVersion.spring_boot}"
//        const val starter_data_mongo =
//            "org.springframework.boot:spring-boot-starter-data-mongodb:${LibVersion.spring_boot}"

//        const val security_messaging =
//            "org.springframework.security:spring-security-messaging:${LibVersion.spring_boot_messaging}"
    }

    object Actuator {
        const val spring_starter_actuator =
            "org.springframework.boot:spring-boot-starter-actuator:${LibVersion.spring_boot}"
//        const val prometheus = "io.micrometer:micrometer-registry-prometheus:${LibVersion.prometheus}"
    }

    object Javax {
        const val validation = "javax.validation:validation-api:2.0.1.Final"
        const val activation = "javax.activation:activation:1.1.1"
    }


    //Jaxb
    object Jaxb {
        const val runtime = "org.glassfish.jaxb:jaxb-runtime:${LibVersion.jaxb}"
        const val api = "javax.xml.bind:jaxb-api:${LibVersion.jaxb}"
    }

    object Caffeine {
        const val caffeine = "com.github.ben-manes.caffeine:caffeine:3.1.8"
    }

}
