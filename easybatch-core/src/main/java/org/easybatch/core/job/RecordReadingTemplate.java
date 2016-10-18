package org.easybatch.core.job;

import org.easybatch.core.record.Record;
import org.easybatch.core.retry.RetryPolicy;
import org.easybatch.core.retry.RetryTemplate;

import java.util.logging.Level;


class RecordReadingTemplate extends RetryTemplate {



    private EventManager eventManager;

    private JobReport jobReport;

    public RecordReadingTemplate(RetryPolicy retryPolicy, EventManager eventManager, JobReport jobReport) {
        super(retryPolicy);
        this.eventManager = eventManager;
        this.jobReport = jobReport;
    }

    @Override
    protected void beforeCall() {
        eventManager.fireBeforeRecordReading();
    }

    @Override
    protected void afterCall(Object result) {
        eventManager.fireAfterRecordReading((Record) result);
    }

    @Override
    protected void onException(Exception e) {
        eventManager.fireOnRecordReadingException(e);

        jobReport.getMetrics().setLastError(e);
    }

    @Override
    protected void onMaxAttempts(Exception e) {

        jobReport.setStatus(JobStatus.ABORTED);
        jobReport.getMetrics().setEndTime(System.currentTimeMillis());
    }

    @Override
    protected void beforeWait() {

    }

    @Override
    protected void afterWait() {
        // no op
    }

}