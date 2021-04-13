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

### Relations based on Nuxeo Collections

![alt](https://www.websequencediagrams.com/files/render?link=50cb6bH4DkSEDqIk0uwYzo3YwBEwzSvn2yv4FSBtfzBgUnJWxi5ZWkO9y91adBsp)

In progress :

- [ ] test, limits ?
- [ ] audit test : MongoShell
- [ ] TODO Integration of 2 operations 1) addCollectionRelation(docA, type, docB) , 2)  getCollectionRelation( docA,
  type)

Limitations :

Mongo size => ? NXQL ? ES ? No ->  operation to search

## Links

@mlefree

