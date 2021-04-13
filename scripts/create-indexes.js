const dbName = 'nuxeo';
const collectionName = 'default';

const indexes = [
    {
        "v": 2,
        "key": {
            "_id": 1
        },
        "name": "_id_",
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "ecm:parentId": 1
        },
        "name": "ecm:parentId_1",
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "ecm:ancestorIds": 1
        },
        "name": "ecm:ancestorIds_1",
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "ecm:versionSeriesId": 1
        },
        "name": "ecm:versionSeriesId_1",
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "ecm:proxyTargetId": 1
        },
        "name": "ecm:proxyTargetId_1",
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "ecm:proxyVersionSeriesId": 1
        },
        "name": "ecm:proxyVersionSeriesId_1",
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "ecm:racl": 1
        },
        "name": "ecm:racl_1",
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "ecm:parentId": 1,
            "ecm:name": 1
        },
        "name": "ecm:parentId_1_ecm:name_1",
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "ecm:primaryType": 1
        },
        "name": "ecm:primaryType_1",
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "ecm:lifeCycleState": 1
        },
        "name": "ecm:lifeCycleState_1",
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "ecm:isTrashed": 1
        },
        "name": "ecm:isTrashed_1",
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "ecm:fulltextJobId": 1
        },
        "name": "ecm:fulltextJobId_1",
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "ecm:acp.acl.user": 1
        },
        "name": "ecm:acp.acl.user_1",
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "ecm:acp.acl.status": 1
        },
        "name": "ecm:acp.acl.status_1",
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "dc:modified": -1
        },
        "name": "dc:modified_-1",
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "rend:renditionName": 1
        },
        "name": "rend:renditionName_1",
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "drv:subscriptions.enabled": 1
        },
        "name": "drv:subscriptions.enabled_1",
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "collectionMember:collectionIds": 1
        },
        "name": "collectionMember:collectionIds_1",
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "nxtag:tags": 1
        },
        "name": "nxtag:tags_1",
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "unique": true,
        "key": {
            "ecm:id": 1
        },
        "name": "ecm:id_1",
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "ecm:isProxy": 1
        },
        "name": "ecm:isProxy_1",
        "background": false,
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "rend:sourceVersionableId": 1
        },
        "name": "rend:sourceVersionableId_1",
        "background": false,
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "ecm:proxyVersionableId": 1
        },
        "name": "ecm:proxyVersionableId_1",
        "background": false,
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "rend:sourceId": 1
        },
        "name": "rend:sourceId_1",
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "dc:creator": 1
        },
        "name": "dc:creator_1",
        "background": false,
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "ecm:mixinTypes": 1
        },
        "name": "ecm:mixinTypes",
        "ns": "nuxeo.default",
        "background": false
    },
    {
        "v": 2,
        "key": {
            "collection:documentIds": 1
        },
        "name": "collection:documentIds",
        "background": false,
        "ns": "nuxeo.default"
    },
    {
        "v": 2,
        "key": {
            "relation:predicate": 1,
            "relation:targetUri": 1,
            "ecm:primaryType": 1
        },
        "name": "relation:predicate_1_relation:targetUri_1_ecm:primaryType_1",
        "ns": "nuxeo.default"
    },
];

const myDb = db.getSiblingDB(dbName);
const collection = myDb.getCollection(collectionName);

indexes.forEach(indexToCreate => {

    const indexObject = indexToCreate.key
    const options = {};
    if (indexToCreate.unique) {
        options.unique = true;
    }
    collection.createIndex(indexObject, options);
})
