package com.mlefree.nuxeo.sandbox.services;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("qrcontent")
public class QRDescriptor {

    @XNode("separator@value")
    protected String separator;

    @XNodeList(value = "xpath@value", type = String[].class, componentType = String.class)
    protected String[] xpaths;
}

