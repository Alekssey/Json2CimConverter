object LibVersion {
    const val spring_boot = "3.1.0"
    const val java_dev_kit = "2.2.3"
    const val rd4j = "4.2.3"
    const val log4j = "2.20.0"
}

object Libs {
    object Kotlin {
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:${Global.kotlin_version}"
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


    object Spring {
        const val starter_web = "org.springframework.boot:spring-boot-starter-web:${LibVersion.spring_boot}"
    }

}
