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

import org.easybatch.core.listener.PipelineListener;
import org.easybatch.core.record.Record;

import java.util.logging.Level;


/**
 * Listener that logs and reports filtered/error records.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
class DefaultPipelineListener implements PipelineListener {



    private JobImpl job;

    DefaultPipelineListener(JobImpl job) {
        this.job = job;
    }

    /**
     * Called before the record gets processed.
     * If you create a new record, you <strong>must</strong> keep the original header of the modified record.
     *
     * @param record The record that will be processed.
     */
    @Override
    public Record beforeRecordProcessing(Record record) {
        if (job.getJobReport().getParameters().isJmxMonitoring()) {
            job.getJobMonitor().notifyJobReportUpdate();
        }
        return record;
    }

    /**
     * Called after the record has been processed.
     *
     * @param inputRecord  The record to process.
     * @param outputRecord The processed record. <strong>May be null if the record has been filtered</strong>
     */
    @Override
    public void afterRecordProcessing(Record inputRecord, Record outputRecord) {
        if (outputRecord == null) {

            job.getJobReport().getMetrics().incrementFilteredCount();
        } else {
            job.getJobReport().getMetrics().incrementSuccessCount();
        }
    }

    /**
     * Called when an exception occurs during record processing
     *
     * @param record    the record attempted to be processed
     * @param throwable the exception occurred during record processing
     */
    @Override
    public void onRecordProcessingException(Record record, Throwable throwable) {

        job.getJobReport().getMetrics().incrementErrorCount();
    }
}
