# nuxeo-sandbox
nuxeo mle sandbox built with https://doc.nuxeo.com/nxdoc/nuxeo-cli/#multiple-modules-empty-nuxeo-project :

```bash
# > parent + core :
nuxeo bootstrap multi-module
#id : com.mlefree.nuxeo.sandbox
#nuxeo version : 2021.0

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

### Pattern : One To Many

It' something supported by Nuxeo OOTB. It's usually more efficient to model things in the same way that Nuxeo model
internally with the parent-child relationships for folder containment:
the child ("many" side of the relation) has a field that references its parent ("one" side).

### Pattern : Technical Type Relations

If you need to add a dynamically typed relation from one document to others, this pattern as
a [One-To-Many](https://en.wikipedia.org/wiki/One-to-many_(data_model)) adaptation that allows you to do it.

Relation is materialized by a "TechnicalTypedRelation" object in Nuxeo :

![alt](https://www.websequencediagrams.com/files/render?link=50cb6bH4DkSEDqIk0uwYzo3YwBEwzSvn2yv4FSBtfzBgUnJWxi5ZWkO9y91adBsp)

Done :

- [x] Operation `Document.AddTechnicalTypedRelation(docA, docB)` (cf src code)
- [x] 100K relations perf test => OK [example](screenshots/technicalTypedRealtion100K.json)
- [ ] 1M relations perf test => KO :

```bash
2021-04-14T14:17:19.735+0000 E QUERY    [js] Error: Converting from JavaScript to BSON failed: Object size 48888989 exceeds limit of 16793600 bytes.  
```

- [ ] TODO `Document.GetTechnicalTypedRelation(docA, type)` dev and integration

Remarks/Limitations/Drawbacks :

1. Size of document in Mongo: 16 Mb. As an example, the [100K relation doc](screenshots/technicalTypedRealtion100K.json)
   . has a size of 4,6 Mb.
2. Size of typed relations field : approximately **300K** relations. (per Type of relation).
3. Due to [ES limitation](https://doc.nuxeo.com/nxdoc/nxql/#elasticsearch-nxql-limitations), joining relations are not a
   good idea though NXQL query (page provider etc...) - it's not supported by Nuxeo; It's preferable to use dedicated
   operations.

### Pattern : Technical Many-to-Many Relations

Same as "Technical Type Relations", this pattern is based on many technical objects that contains each relation
information. It's following the [Many to Many](https://en.wikipedia.org/wiki/Many-to-many_(data_model)) pattern. More
modular and open, but more difficult/slow to retrieve information (like type) => it needs operations.

Done :

- [ ] TODO Operations
- [ ] TODO Perf tests

Remarks/Limitations/Drawbacks  :

1. Size of document in Mongo: 16 Mb.
2. Due to [ES limitation](https://doc.nuxeo.com/nxdoc/nxql/#elasticsearch-nxql-limitations), joining relations are not a
   good idea though NXQL query (page provider etc...) - not supported by Nuxeo; It's preferable to use dedicated
   operations.
3. Create a document for each relation increase database size.

## Contact

@mlefree @nuxeo

[Licence](./LICENSE)

