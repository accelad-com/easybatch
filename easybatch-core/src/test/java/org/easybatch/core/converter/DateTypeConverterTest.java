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

package org.easybatch.core.converter;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class DateTypeConverterTest extends BaseConverterTest<Date> {

    @Before
    public void setUp() throws Exception {
        converter = new DateTypeConverter();
    }

    @Test
    public void whenInputValueIsLegalValue_ThenShouldReturnValidDate() {
        String date = "2015-01-01";
        Date convertedDate = converter.convert(date);
        assertThat(convertedDate).isNotNull();
        Date expectedDate = java.sql.Date.valueOf(date);
        assertThat(convertedDate.getTime()).isEqualTo(expectedDate.getTime());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenValueIsIllegal_ThenShouldThrowAnIllegalArgumentException() {
        converter.convert("foo");
    }

}
