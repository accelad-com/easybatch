/*
 * The MIT License
 *
 *  Copyright (c) 2016, Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package org.easybatch.core.job;

import org.easybatch.core.listener.JobListener;
import org.easybatch.core.listener.PipelineListener;
import org.easybatch.core.listener.RecordReaderListener;
import org.easybatch.core.processor.RecordProcessingException;
import org.easybatch.core.processor.RecordProcessor;
import org.easybatch.core.reader.RecordReader;
import org.easybatch.core.record.Record;

import java.util.ArrayList;
import java.util.logging.Level;


/**
 * Core Easy Batch job implementation.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
final class JobImpl implements Job {



    private RecordReader recordReader;
    private RecordReadingCallable recordReadingCallable;
    private RecordReadingTemplate recordReadingTemplate;

    private Pipeline pipeline;

    private EventManager eventManager;

    private JobReport report;

    private JobMonitor jobMonitor;

    private JobParameters parameters;

    private JobMetrics metrics;

    private boolean timedOut;

    JobImpl() {
        this.recordReader = new NoOpRecordReader();
        this.eventManager = new EventManager();
        this.report = new JobReport();
        this.parameters = report.getParameters();
        this.metrics = report.getMetrics();
        this.jobMonitor = new JobMonitor(report);
        this.pipeline = new Pipeline(new ArrayList<RecordProcessor>(), eventManager);
        this.eventManager.addPipelineListener(new DefaultPipelineListener(this));
        this.eventManager.addPipelineListener(new JobTimeoutListener(this));
        this.eventManager.addJobListener(new DefaultJobListener(this));
        this.eventManager.addJobListener(new MonitoringSetupListener(this));
        this.recordReadingCallable = new RecordReadingCallable(recordReader);
        this.recordReadingTemplate = new RecordReadingTemplate(parameters.getRetryPolicy(), eventManager, report);
    }

    @Override
    public String getName() {
        return parameters.getName();
    }

    @Override
    public String getExecutionId() {
        return parameters.getExecutionId();
    }

    @Override
    public JobReport call() {

        try {

            if (!openRecordReader()) {
                return report;
            }
            eventManager.fireBeforeJobStart(parameters);
            long recordCount = 0;
            while (recordReader.hasNextRecord() && recordCount < parameters.getLimit()) {

                //Abort job if timeout is exceeded
                if (timedOut) {

                    break;
                }

                //read next record
                Record currentRecord = readNextRecord();
                if (currentRecord == null) {
                    return report;
                }
                recordCount++;

                //Skip records if any
                if (shouldSkipRecord(recordCount)) {
                    metrics.incrementSkippedCount();
                    continue;
                }

                //Process record
                try {
                    pipeline.process(currentRecord);
                } catch (RecordProcessingException e) {
                    if (metrics.getErrorCount() >= parameters.getErrorThreshold()) {

                        report.setStatus(JobStatus.ABORTED);
                        report.getMetrics().setLastError(e);
                        break;
                    }
                }
            }
            metrics.setTotalCount(recordCount);

        } catch (Exception e) {

            report.setStatus(JobStatus.FAILED);
            report.getMetrics().setLastError(e);
        } finally {
            closeRecordReader();
            eventManager.fireAfterJobEnd(report);
        }
        return report;

    }

    private Record readNextRecord() {
        try {
            return recordReadingTemplate.execute(recordReadingCallable);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean shouldSkipRecord(long recordCount) {
        return recordCount <= parameters.getSkip();
    }

    private boolean openRecordReader() {
        try {
            metrics.setStartTime(System.currentTimeMillis());
            recordReader.open();
            String dataSourceName = recordReader.getDataSourceName();
            parameters.setDataSource(dataSourceName);
        } catch (Exception e) {

            report.setStatus(JobStatus.FAILED);
            metrics.setLastError(e);
            return false;
        }
        return true;
    }

    private void closeRecordReader() {

        try {
            if (!parameters.isKeepAlive()) {
                recordReader.close();
            }
        } catch (Exception e) {

        }
    }

    /*
     * Setters for job parameters
     */

    void setRecordReader(final RecordReader recordReader) {
        this.recordReader = recordReader;
        this.recordReadingCallable = new RecordReadingCallable(recordReader);
        this.recordReadingTemplate = new RecordReadingTemplate(parameters.getRetryPolicy(), eventManager, report);
    }

    void addRecordProcessor(final RecordProcessor recordProcessor) {
        pipeline.addProcessor(recordProcessor);
    }

    void addJobListener(final JobListener jobListener) {
        eventManager.addJobListener(jobListener);
    }

    void addRecordReaderListener(final RecordReaderListener recordReaderListener) {
        eventManager.addRecordReaderListener(recordReaderListener);
    }

    void addPipelineListener(final PipelineListener pipelineListener) {
        eventManager.addPipelineListener(pipelineListener);
    }

    void setTimedOut(boolean timedOut) {
        this.timedOut = timedOut;
    }

    /*
     * Getters for job components (needed by package private artifacts)
     */

    JobReport getJobReport() {
        return report;
    }

    RecordReader getRecordReader() {
        return recordReader;
    }

    Pipeline getPipeline() {
        return pipeline;
    }

    JobMonitor getJobMonitor() {
        return jobMonitor;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{name='").append(parameters.getName()).append('\'');
        stringBuilder.append(", executionId='").append(parameters.getExecutionId()).append('\'');
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
