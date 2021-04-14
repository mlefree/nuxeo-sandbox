const dbName = 'nuxeo';
const collectionName = 'default';
const bulkLimit = 1;
const relationsToCreate = [
    {
        source: '7f8c80be-e903-4287-85e7-25f410ac1883',
        targetToDuplicate: 'a5860090-428f-454f-b9ed-11112ad0178b',
        type: 'File',
        count: 100000
    },
];

const log = (text) => {
    print(text);
};

function uuidv4() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        const r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

const begin = new Date();
log(`${begin.toISOString()} : Relations creation in progress`);
const myDb = db.getSiblingDB(dbName);
const collection = myDb.getCollection(collectionName);

let done = 0;
relationsToCreate.forEach(relation => {
    log('doc id to clone : ' + relation.targetToDuplicate + ' ' + relation.count + ' times...');

    const uuids = [];
    for (let i = 0; i < relation.count; i++) {
        uuids.push(uuidv4());
    }

    // Duplicate target :
    collection.find({'ecm:id': relation.targetToDuplicate}).forEach(document => {
        delete document._id;
        document['dc:title'] = document['dc:title'] + ' - cloned of ' + relation.targetToDuplicate;
        let bulkCount = 0;
        let bulk = collection.initializeUnorderedBulkOp();
        uuids.forEach(uuid => {
            document['ecm:id'] = uuid;
            document['dc:description'] = 'some text : ' + uuid;
            const clonedDoc = {};
            Object.assign(clonedDoc, document);
            bulk.insert(clonedDoc);
            done++;
            if (++bulkCount >= bulkLimit) {
                bulk.execute();
                bulk = collection.initializeUnorderedBulkOp();
            }
        });
    });

    // Create or update relation with new targets :
    const relationToFind = {
        'technicalrelationtyped:source': relation.source,
        'technicalrelationtyped:type': relation.type,
    };
    const alreadyExistingRelationCursor = collection.find(relationToFind);
    const foundCount = alreadyExistingRelationCursor.count();
    log(`\n${foundCount} foundCount ?`);
    if (foundCount >= 1) {
        log(`\n${foundCount} found...`);
        alreadyExistingRelationCursor.forEach(typedRelation => {
            log(`\nRelation update on ecm:id ` + typedRelation['ecm:id']);
            collection.updateOne({_id: typedRelation._id}, {$set: {'technicalrelationtyped:targets': uuids}});
        });
    } else {
        let typedRelation = {
            'dc:title': 'technicalRelation' + Math.random(),
            'ecm:id': uuidv4(),
            'technicalrelationtyped:source': relation.source,
            'technicalrelationtyped:type': relation.type,
            'technicalrelationtyped:targets': uuids,
        };
        log(`\n${typedRelation} to create... none of ` + JSON.stringify(relationToFind));
        // KO -> collection.insert(typedRelation);
    }
})

const end = new Date();
log(`\n${end.toISOString()} : Relations creation done for ${done} records in ${Math.round((end - begin) / 1000)} sec`);


