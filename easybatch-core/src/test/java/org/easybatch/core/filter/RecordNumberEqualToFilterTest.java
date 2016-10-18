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

package org.easybatch.core.filter;

import org.easybatch.core.record.Record;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RecordNumberEqualToFilterTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Record record;

    private RecordNumberEqualToFilter recordNumberEqualToFilter;

    @Test
    public void whenTheRecordNumberIsEqualToExpectedNumber_ThenItShouldBeFiltered() {
        recordNumberEqualToFilter = new RecordNumberEqualToFilter(1);

        when(record.getHeader().getNumber()).thenReturn(1l);
        assertThat(recordNumberEqualToFilter.processRecord(record)).isNull();
    }

    @Test
    public void whenTheRecordNumberIsEqualToOneOfTheExpectedNumbers_ThenItShouldBeFiltered() {
        recordNumberEqualToFilter = new RecordNumberEqualToFilter(1, 2);

        when(record.getHeader().getNumber()).thenReturn(2l);
        assertThat(recordNumberEqualToFilter.processRecord(record)).isNull();
    }

    @Test
    public void whenTheRecordNumberIsNotEqualToOneOfTheExpectedNumbers_ThenItShouldNotBeFiltered() {
        recordNumberEqualToFilter = new RecordNumberEqualToFilter(1, 2);

        when(record.getHeader().getNumber()).thenReturn(3l);
        assertThat(recordNumberEqualToFilter.processRecord(record)).isEqualTo(record);
    }

}
