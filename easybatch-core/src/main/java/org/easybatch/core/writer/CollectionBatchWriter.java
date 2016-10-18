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

package org.easybatch.core.writer;

import org.easybatch.core.record.Batch;
import org.easybatch.core.record.Record;

import java.util.Collection;
import java.util.List;

import static org.easybatch.core.util.Utils.checkNotNull;

/**
 * Convenient processor that writes a batch of records to a {@link Collection}.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
public class CollectionBatchWriter implements RecordWriter<Batch> {

    private CollectionRecordWriter collectionRecordWriter;

    /**
     * Convenient processor that writes a batch of records to a {@link Collection}.
     *
     * @param collection the collection to write records to
     */
    public CollectionBatchWriter(final Collection collection) {
        checkNotNull(collection, "collection");
        collectionRecordWriter = new CollectionRecordWriter(collection);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Batch processRecord(final Batch batch) throws RecordWritingException {
        List<Record> records = batch.getPayload();
        for (Record record : records) {
            collectionRecordWriter.processRecord(record);
        }
        return batch;
    }
}
