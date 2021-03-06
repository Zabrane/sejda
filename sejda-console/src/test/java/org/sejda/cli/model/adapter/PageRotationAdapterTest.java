/*
 * Created on Jul 10, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.cli.model.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

import org.junit.Test;
import org.sejda.conversion.PageRotationAdapter;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.rotation.PageRotation;
import org.sejda.model.rotation.Rotation;
import org.sejda.model.rotation.RotationType;

/**
 * @author Eduard Weissmann
 * 
 */
public class PageRotationAdapterTest {

    @Test
    public void singlePage() {
        try {
            new PageRotationAdapter("abc:90").getPageRotation();
            fail();
        } catch (SejdaRuntimeException e) {
            assertThat(e.getMessage(), containsString("Unknown page definition: 'abc'"));
        }
    }

    @Test
    public void singlePage_nonNumericPage() {
        PageRotation expected = PageRotation.createSinglePageRotation(77, Rotation.DEGREES_270);
        assertEquals(expected, new PageRotationAdapter("77:270").getPageRotation());
    }

    @Test
    public void multiplePages() {
        assertEquals(PageRotation.createMultiplePagesRotation(Rotation.DEGREES_0, RotationType.ALL_PAGES),
                new PageRotationAdapter("all:0").getPageRotation());

        assertEquals(PageRotation.createMultiplePagesRotation(Rotation.DEGREES_180, RotationType.EVEN_PAGES),
                new PageRotationAdapter("even:180").getPageRotation());

        assertEquals(PageRotation.createMultiplePagesRotation(Rotation.DEGREES_90, RotationType.ODD_PAGES),
                new PageRotationAdapter("odd:90").getPageRotation());
    }

    @Test
    public void negative_noSeparator() {
        try {
            new PageRotationAdapter("all0").getPageRotation();
            fail();
        } catch (SejdaRuntimeException e) {
            assertThat(e.getMessage(),
                    containsString("Invalid input: 'all0'. Expected format: 'pageDefinition:rotation'"));
        }
    }

    @Test
    public void negative_unrecognizedPages() {
        try {
            new PageRotationAdapter("some:0").getPageRotation();
            fail();
        } catch (SejdaRuntimeException e) {
            assertThat(e.getMessage(), containsString("Unknown page definition: 'some'"));
        }
    }

    @Test
    public void negative_unrecognizedRotation() {
        try {
            new PageRotationAdapter("odd:99").getPageRotation();
            fail();
        } catch (SejdaRuntimeException e) {
            assertThat(e.getMessage(), containsString("Unknown rotation: '99'"));
        }
    }

    @Test
    public void negative_nonNumbericRotation() {
        try {
            new PageRotationAdapter("odd:99abc").getPageRotation();
            fail();
        } catch (SejdaRuntimeException e) {
            assertThat(e.getMessage(), containsString("Unknown rotation: '99abc'"));
        }
    }
}
