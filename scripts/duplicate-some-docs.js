const dbName = 'nuxeo';
const collectionName = 'default';
const bulkLimit = 1000;
const idsToDuplicate = [
    {id: '22aef702-fccd-4f5e-a5f5-659b4affbe30', count: 100000},
    {id: '22aef702-fccd-4f5e-a5f5-659b4affbe30', count: 100000},
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
log(`${begin.toISOString()} : Update in progress`);
const myDb = db.getSiblingDB(dbName);
const collection = myDb.getCollection(collectionName);

let done = 0;
idsToDuplicate.forEach(id => {
    log('doc id to clone : ' + id.id + ' ' + id.count + ' times...');
    collection.find({'ecm:id': id.id}).forEach((document) => {
        delete document._id;
        document['dc:title'] = document['dc:title'] + ' - cloned of ' + id.id;
        let bulkCount = 0;
        let bulk = collection.initializeUnorderedBulkOp();
        for (let i = 0; i < id.count; i++) {
            const uuid = uuidv4();
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
        }
    });
})

const end = new Date();
log(`\n${end.toISOString()} : Update done for ${done} records in ${Math.round((end - begin) / 1000)} sec`);

