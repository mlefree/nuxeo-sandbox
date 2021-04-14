# nuxeo-sandbox
nuxeo mle sandbox built with https://doc.nuxeo.com/nxdoc/nuxeo-cli/#multiple-modules-empty-nuxeo-project :

```bash
# > parent + core :
nuxeo bootstrap multi-module
#id : com.mlefree.nuxeo.sandbox
#nuxeo version : 11.1-SNAPSHOT or 11.5.97 or ... ?

# > package :
nuxeo bootstrap package

# > link nuxeo studio mleprevost-SANDBOX :
nuxeo studio
```

## Install a representative db

helped by [some mongoshell scripts](./scripts)

- [ ]  Mongo dump from representative dump

```bash
oc rsync <pod-name>:/dump/. ./mongo_home/dump
```

- [ ]  And import mongo dump

```bash
cd /home/dump && ./importeverything.sh && cd /home/dump/default-case && ./importeverything.sh
```

- [ ] Simulate a "real" world (import by duplication):

```bash
cd /home && mongo duplicate-some-docs.js
```

- [ ] Update mongo Indexes :

```bash
cd /home && mongo create-indexes.js
```

- [ ] Finally, reindex ES

Note : Mongo shell can also be done remotely (
ex:`mongo -u <> -p <p> mongo.url.com:27017/your-db duplicate-some-docs.js`)

## Patterns

### Pattern : Technical Type Relations

![alt](https://www.websequencediagrams.com/files/render?link=50cb6bH4DkSEDqIk0uwYzo3YwBEwzSvn2yv4FSBtfzBgUnJWxi5ZWkO9y91adBsp)

Done :

- [x] Operation `Document.AddTechnicalTypedRelation(docA, docB)`
- [x] 100K relations perf test => OK [example](screenshots/technicalTypedRealtion100K.json)
- [ ] 1M relations perf test => KO :

```bash
2021-04-14T14:17:19.735+0000 E QUERY    [js] Error: Converting from JavaScript to BSON failed: Object size 48888989 exceeds limit of 16793600 bytes.  
```

- [ ] TODO `Document.GetTechnicalTypedRelation(docA, type)` dev and integration

Limitations :

1. Size of document in Mongo: 16 Mb. As an example, the [100K relation doc](screenshots/technicalTypedRealtion100K.json)
   has a size of 4,6 Mb.
1. Size of typed relations field : approximately **300K** relations. (per Type of relation)
1. Due to [ES limitation](https://doc.nuxeo.com/nxdoc/nxql/#elasticsearch-nxql-limitations), joining relations are not a
   good idea though NXQL query (page provider etc...); It's preferable to use dedicated operations.

### Pattern : Technical Many-to-Many Relations

Same as "Technical Type Relations", this pattern is based on many technical objects that contains each relation
information. It's following the (Many to Many)[https://en.wikipedia.org/wiki/Many-to-many_(data_model)] pattern. More
modular and open, but more difficult/slow to retrieve information (like type) => it needs operations.

Done :

- [ ] TODO Operations
- [ ] TODO Perf tests

Limitations :

1. Size of document in Mongo: 16 Mb
1. Due to [ES limitation](https://doc.nuxeo.com/nxdoc/nxql/#elasticsearch-nxql-limitations), joining relations are not a
   good idea though NXQL query (page provider etc...); It's preferable to use dedicated operations.

## Contact

@mlefree

[Licence](./LICENSE)

