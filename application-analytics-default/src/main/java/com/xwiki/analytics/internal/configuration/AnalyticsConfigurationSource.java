package com.xwiki.analytics.internal.configuration;
import groovy.lang.Singleton;
import jdk.vm.ci.meta.Local;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.configuration.internal.AbstractDocumentConfigurationSource;
import org.xwiki.model.reference.LocalDocumentReference;
import org.xwiki.model.reference.SpaceReference;

import javax.inject.Named;
import java.util.Arrays;
import java.util.List;

@Component
@Singleton
@Named("analytics")
public class AnalyticsConfigurationSource extends AbstractDocumentConfigurationSource {
    private static final List<String> SPACE_NAMES= Arrays.asList("Analytics", "Code");
    private static final LocalDocumentReference DOCUMENT_REFERENCE=
            new LocalDocumentReference(SPACE_NAMES, "Configuration");
    private static final LocalDocumentReference CLASS_REFERENCE=
            new LocalDocumentReference(SPACE_NAMES, "ConfigurationClass");


    @Override
    protected DocumentReference getDocumentReference()
    {
        return new DocumentReference(DOCUMENT_REFERENCE, this.getCurrentWikiReference());
    }

    @Override
    protected LocalDocumentReference getClassReference()
    {
        return CLASS_REFERENCE;
    }

    @Override
    protected String getCacheId() {
        return "configuration.document.analytics";
    }
}
