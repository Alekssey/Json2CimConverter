dependencies {
    implementation(Libs.Kotlin.reflect)
    implementation(Libs.Kotlin.stdlib)

    implementation(Libs.Dtps.java_dev_kit)

    implementation(Libs.Jackson.kotlin)
    implementation(Libs.Jackson.databind)

    implementation(Libs.Rdf4j.rio_rdfxml)
    implementation(Libs.Rdf4j.repository_api)
    implementation(Libs.Rdf4j.sail_memory)
    implementation(Libs.Rdf4j.repository_sail)

    implementation(Libs.Log4j.api)
    implementation(Libs.Log4j.slf4j)
    implementation(Libs.Log4j.core)

//    testImplementation(Libs.Test.junit_jupiter_engine)
//    testImplementation(Libs.Test.assertj_core)

}
