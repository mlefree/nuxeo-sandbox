package com.mlefree.nuxeo.sandbox.studio;

public class StudioConstant {
    private StudioConstant() {}

    public static final String BUNDLE_NAME = "studio.extensions.mleprevost-SANDBOX";

    public static final String DOMAIN_DOC_TYPE = "Domain";

    public static final String FILE_DOC_TYPE = "File";

    public static final String SYSTEM_SCHEMA = "system";

    public static final String SYSTEM_SCHEMA_FULLTEXT_PROPERTY = "ecm:fulltext";

    public static final String SYSTEM_SCHEMA_PATH_PROPERTY = "ecm:path";

    public static final String SYSTEM_SCHEMA_NAME_PROPERTY = "ecm:name";

    public static final String SYSTEM_SCHEMA_PRIMARY_TYPE_PROPERTY = "ecm:primaryType";

    public static final String SYSTEM_SCHEMA_MIXIN_TYPE_PROPERTY = "ecm:mixinType";

    public static final String SYSTEM_SCHEMA_CURRENT_LIFE_CYCLE_STATE_PROPERTY = "ecm:currentLifeCycleState";

    public static final String SYSTEM_SCHEMA_VERSION_LABEL_PROPERTY = "ecm:versionLabel";

    public static final String SYSTEM_SCHEMA_UUID_PROPERTY = "ecm:uuid";

    public static final String SYSTEM_SCHEMA_PARENT_ID_PROPERTY = "ecm:parentId";

    public static final String SYSTEM_SCHEMA_ANCESTOR_ID_PROPERTY = "ecm:ancestorId";

    public static final String SYSTEM_SCHEMA_REPOSITORY_NAME_PROPERTY = "ecm:repositoryName";

    public static final String SYSTEM_SCHEMA_IS_VERSION_PROPERTY = "ecm:isVersion";

    public static final String SYSTEM_SCHEMA_IS_TRASHED_PROPERTY = "ecm:isTrashed";

    public static final String SYSTEM_SCHEMA_IS_PROXY_PROPERTY = "ecm:isProxy";

    public static final String SYSTEM_SCHEMA_LOCK_OWNER_PROPERTY = "ecm:lockOwner";

    public static final String SYSTEM_SCHEMA_LOCK_CREATED_PROPERTY = "ecm:lockCreated";

    public static final String SYSTEM_SCHEMA_TAG_PROPERTY = "ecm:tag";

    public static final String TEST_SCHEMA = "test";

    public static final String TEST_SCHEMA_TEST_A_PROPERTY = "test:testA";

    public static final String TEST_SCHEMA_TEST_B_PROPERTY = "test:testB";

    /**
     * Fixed Part: "ecm:mixinType != 'HiddenInNavigation' AND ecm:isVersion = 0 AND ecm:isTrashed = 0"
     */
    public static final String TEST_SEARCH_PAGE_PROVIDER = "testSearch";

}
