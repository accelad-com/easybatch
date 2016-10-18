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

package org.easybatch.jdbc;

import org.easybatch.core.listener.PipelineListener;
import org.easybatch.core.record.Record;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;


import static org.easybatch.core.util.Utils.checkNotNull;

/**
 * Listener that commits a transaction after writing a record.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
public class JdbcTransactionListener implements PipelineListener {



    private Connection connection;

    private long recordNumber;

    /**
     * Create a JDBC transaction listener.
     *
     * @param connection the JDBC connection (should be in auto-commit = false)
     */
    public JdbcTransactionListener(final Connection connection) {
        checkNotNull(connection, "connection");
        this.connection = connection;
    }

    @Override
    public Record beforeRecordProcessing(final Record record) {
        recordNumber++;
        return record;
    }

    @Override
    public void afterRecordProcessing(final Record inputRecord, final Record outputRecord) {
        try {
            connection.commit();

        } catch (SQLException e) {

        }
    }

    @Override
    public void onRecordProcessingException(final Record record, final Throwable throwable) {
        try {
            connection.rollback();
        } catch (SQLException e) {

        }
    }
}
