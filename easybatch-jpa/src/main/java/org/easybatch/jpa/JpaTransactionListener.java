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

package org.easybatch.jpa;

import org.easybatch.core.listener.PipelineListener;
import org.easybatch.core.record.Record;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.logging.Level;


import static org.easybatch.core.util.Utils.checkNotNull;

/**
 * Listener that commits a JPA transaction after each record.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
public class JpaTransactionListener implements PipelineListener {



    private EntityManager entityManager;

    private EntityTransaction transaction;

    private long recordNumber;

    /**
     * Create a JPA transaction listener.
     * <p/>
     * A JPA transaction will be committed after each record.
     *
     * @param entityManager the JPA entity manager
     */
    public JpaTransactionListener(final EntityManager entityManager) {
        checkNotNull(entityManager, "entity manager");
        this.entityManager = entityManager;
    }

    @Override
    public Record beforeRecordProcessing(final Record record) {
        this.transaction = entityManager.getTransaction();
        this.transaction.begin();
        recordNumber++;
        return record;
    }

    @Override
    public void afterRecordProcessing(final Record inputRecord, final Record outputRecord) {
        try {
            entityManager.flush();
            entityManager.clear();
            transaction.commit();

        } catch (Exception e) {

        }
    }

    @Override
    public void onRecordProcessingException(final Record record, final Throwable throwable) {
        try {
            transaction.rollback();

        } catch (Exception e) {

        }
    }

}
