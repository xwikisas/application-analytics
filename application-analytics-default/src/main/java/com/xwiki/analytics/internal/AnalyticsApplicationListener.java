/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.xwiki.analytics.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.extension.event.ExtensionEvent;
import org.xwiki.extension.event.ExtensionInstalledEvent;
import org.xwiki.extension.event.ExtensionUpgradedEvent;
import org.xwiki.model.reference.LocalDocumentReference;
import org.xwiki.observation.AbstractEventListener;
import org.xwiki.observation.event.Event;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.plugin.scheduler.JobState;
import com.xpn.xwiki.plugin.scheduler.SchedulerPlugin;

/**
 * Schedules the jobs for the aggregators macros on install and reschedules them on upgrade to make sure that everything
 * works properly.
 *
 * @version $Id$
 * @since 1.2
 */
@Component
@Named(AnalyticsApplicationListener.ROLE_HINT)
@Singleton
public class AnalyticsApplicationListener extends AbstractEventListener implements Initializable
{
    protected static final String ROLE_HINT = "AnalyticsApplicationListener";

    protected static final List<String> CODE_SPACE = Arrays.asList("Analytics", "Code", "Jobs");

    protected static final List<String> JOB_PAGES = List.of("JobLastSeenUser");

    private static final String ANALYTICS_APPLICATION_ID = "com.xwiki.analytics:application-analytics-ui";

    @Inject
    private Logger logger;

    @Inject
    private Provider<XWikiContext> contextProvider;

    /**
     * Default constructor.
     */
    public AnalyticsApplicationListener()
    {
        //TODO: Filter by ExtensionInstallEvent with the help of the extension id when XCOMMONS-2526 is added to
        // the platform and the analytics application depends on that parent.

        super(ROLE_HINT,
            Arrays.<Event>asList(new ExtensionUpgradedEvent(ANALYTICS_APPLICATION_ID), new ExtensionInstalledEvent()));
    }

    /**
     * The migration should be done at ExtensionUpgradedEvent, but for avoiding XCOMMONS-751: Getting wrong component
     * instance during JAR extension upgrade, it is done also at initialization step, since when an extension is
     * upgraded its listeners are initialized too. After the issue is fixed and the application starts depending on a
     * version of XWiki >= the version where is fixed, then only the migration from inside the event should be executed.
     */
    @Override
    public void initialize() throws InitializationException
    {

        // Don't schedule jobs at xwiki start up time.
        if (this.contextProvider.get() != null) {
            prepareJobs(true);
        }
    }

    @Override
    public void onEvent(Event event, Object source, Object data)
    {

        if (event instanceof ExtensionUpgradedEvent) {
            // Unscheduled and reschedule the jobs
            prepareJobs(true);
        } else if (isAnalyticsInstallEvent(event)) {
            prepareJobs(false);
        }
    }

    /**
     * Will gather all the jobs pages that are bundled in the analytics app.
     * @return list with document references of all the jobs that are brought by the analytics app.
     */
    private List<LocalDocumentReference> getJobPages()
    {
        List<LocalDocumentReference> jobPages = new ArrayList<>();
        for (String jobPage : JOB_PAGES) {
            jobPages.add(new LocalDocumentReference(CODE_SPACE, jobPage));
        }
        return jobPages;
    }

    private void prepareJobs(boolean reschedule)
    {
        List<LocalDocumentReference> jobPages = getJobPages();
        try {

            for (LocalDocumentReference jobPage : jobPages) {
                scheduleJob(reschedule, jobPage);
            }
        } catch (SchedulerException | XWikiException e) {
            logger.error("Failed to schedule jobs", e);
        }
    }

    private static boolean isAnalyticsInstallEvent(Event event)
    {
        return event instanceof ExtensionInstalledEvent && ANALYTICS_APPLICATION_ID.equals(
            ((ExtensionEvent) event).getExtensionId().getId());
    }

    private void scheduleJob(boolean doReschedule, LocalDocumentReference jobDocReference)
        throws XWikiException, SchedulerException
    {
        XWikiContext xcontext = contextProvider.get();

        SchedulerPlugin schedulerPlugin =
            (SchedulerPlugin) xcontext.getWiki().getPluginManager().getPlugin("scheduler");
        XWikiDocument jobDoc = xcontext.getWiki().getDocument(jobDocReference, xcontext);
        BaseObject job = jobDoc.getXObject(SchedulerPlugin.XWIKI_JOB_CLASSREFERENCE);
        JobState jobState = schedulerPlugin.getJobStatus(job, xcontext);

        if (doReschedule && jobState.getQuartzState().equals(Trigger.TriggerState.NORMAL)) {
            schedulerPlugin.unscheduleJob(job, xcontext);
            schedulerPlugin.scheduleJob(job, xcontext);
            logger.info("Job, [{}], was rescheduled successfully", jobDocReference);
        } else if (jobState.getQuartzState().equals(Trigger.TriggerState.NONE)) {
            schedulerPlugin.scheduleJob(job, xcontext);
            logger.info("Job, [{}], was scheduled successfully", jobDocReference);
        }
    }
}
