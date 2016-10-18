/*
 *  The MIT License
 *
 *   Copyright (c) 2016, Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 */

package org.easybatch.core.job;

import org.easybatch.core.listener.JobListener;
import org.easybatch.core.processor.ComputationalRecordProcessor;
import org.easybatch.core.processor.RecordProcessor;
import org.easybatch.core.retry.RetryPolicy;
import org.easybatch.core.util.Utils;

import java.util.logging.Level;


import static org.easybatch.core.job.JobParameters.DEFAULT_ERROR_THRESHOLD;
import static org.easybatch.core.job.JobParameters.DEFAULT_LIMIT;
import static org.easybatch.core.job.JobParameters.DEFAULT_TIMEOUT;
import static org.easybatch.core.util.Utils.toMinutes;

/**
 * Job listener that logs and reports job parameters and metrics.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
class DefaultJobListener implements JobListener {



    private JobImpl job;

    DefaultJobListener(JobImpl job) {
        this.job = job;
    }

    @Override
    public void beforeJobStart(final JobParameters jobParameters) {
        if (jobParameters.isSilentMode()) {

        }
        String dataSource = jobParameters.getDataSource();
        long limit = jobParameters.getLimit();
        long timeout = jobParameters.getTimeout();
        long errorThreshold = jobParameters.getErrorThreshold();
        String jobName = jobParameters.getName();
        RetryPolicy retryPolicy = jobParameters.getRetryPolicy();















        job.getJobReport().getMetrics().setStartTime(System.currentTimeMillis()); //System.nanoTime() does not allow to have start time (see Javadoc)
        job.getJobReport().setStatus(JobStatus.STARTED);

    }

    @Override
    public void afterJobEnd(JobReport jobReport) {
        jobReport.getMetrics().setEndTime(System.currentTimeMillis());
        if (!jobReport.getStatus().equals(JobStatus.ABORTED) && !jobReport.getStatus().equals(JobStatus.FAILED)) {
            jobReport.setStatus(JobStatus.COMPLETED);
        }
        // The job result is held by the last processor in the pipeline (which should be of type ComputationalRecordProcessor)
        RecordProcessor lastRecordProcessor = job.getPipeline().getLastProcessor();
        if (lastRecordProcessor instanceof ComputationalRecordProcessor) {
            ComputationalRecordProcessor computationalRecordProcessor = (ComputationalRecordProcessor) lastRecordProcessor;
            Object jobResult = computationalRecordProcessor.getComputationResult();
            jobReport.setJobResult(new JobResult(jobResult));
        }
        if (job.getJobReport().getParameters().isJmxMonitoring()) {
            job.getJobMonitor().notifyJobReportUpdate();
        }

    }
}
