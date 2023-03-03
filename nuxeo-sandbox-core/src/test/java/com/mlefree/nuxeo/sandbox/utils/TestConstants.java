package com.mlefree.nuxeo.sandbox.utils;

public class TestConstants {

    public static final String SELECT_DOCUMENT = "SELECT * From Document ";

    public static final String SELECT_DOCUMENT_IN_PROXY = "SELECT * FROM Document WHERE ecm:isProxy = 1 AND (ecm:proxyVersionableId = 'xxxx')";

    public static final String ADMINISTRATORS = "administrators";

    public static final String MEMBERS = "members";

    public static final String PSVD = "PSVD";

    public static final String FACTORIES = "factories";

    public static final String FACTORY_X = "factory-X";

    public static final String FACTORY_Y = "factory-Y";

    public static final String COMPANY_A = "company-A";

    public static final String COMPANY_A_LABEL = "Company A";

    public static final String COMPANY_B = "company-B";

    public static final String COMPANY_B_LABEL = "Company B";

    public static final String FACTORY_X_ADMINISTRATORS = "factory-X-administrators";

    public static final String FACTORY_Y_ADMINISTRATORS = "factory-Y-administrators";

    public static final String COMPANY_A_ADMINISTRATORS = COMPANY_A + "-administrators";

    public static final String PSVD_ADMINISTRATORS = PSVD + "-administrators";

    public static final String WITHDRAW_PSVD = "withdraw-PSVD";

    public static final String WITHDRAW_COMPANY = "withdraw-company";

    public static final String WITHDRAW_FACTORY = "withdraw-factory";

}
