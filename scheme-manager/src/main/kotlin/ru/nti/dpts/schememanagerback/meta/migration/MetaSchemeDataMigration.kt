package ru.nti.dpts.schememanagerback.meta.migration

import com.mongodb.BasicDBObject
import org.bson.Document
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.meta.migration.docfields.ProjectFields
import ru.nti.dpts.schememanagerback.meta.migration.ops.mutateScheme
import ru.nti.dpts.schememanagerback.scheme.persister.PROJECT_COLLECTION

//@Component
class MetaSchemeDataMigration(
    private val mongoTemplate: MongoTemplate
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener(ApplicationReadyEvent::class)
    fun migrateSchemes() {
        val schemesMigrated = mutateAndSaveSchemesAndReturnMutatedSchemeAmount(
            PROJECT_COLLECTION,
            ProjectFields.id,
            ProjectFields.scheme
        )

        if (schemesMigrated != 0) {
            log.info(
                "$schemesMigrated schemes have been migrated" +
                    " to ${ChangeSetsData.relatedMetaSchemeVersion} meta scheme version"
            )
        }
    }

    private fun mutateAndSaveSchemesAndReturnMutatedSchemeAmount(
        collectionName: String,
        ownerIdFieldName: String,
        ownerSchemeFieldName: String
    ): Int {
        val collection = mongoTemplate.getCollection(collectionName)
        val cursor = collection.find().cursor()

        var migratedSchemesAmount = 0

        for (schemeOwner in cursor) {
            schemeOwner[ownerSchemeFieldName]?.let { scheme ->
                val hasBeenMutated = mutateScheme(scheme as Document, ChangeSetsData.changeSets)
                if (hasBeenMutated) {
                    migratedSchemesAmount++
                    schemeOwner[ownerSchemeFieldName] = scheme
                    collection.replaceOne(
                        BasicDBObject(ProjectFields.id, schemeOwner[ownerIdFieldName] as String),
                        schemeOwner
                    )
                }
            }
        }
        return migratedSchemesAmount
    }
}
