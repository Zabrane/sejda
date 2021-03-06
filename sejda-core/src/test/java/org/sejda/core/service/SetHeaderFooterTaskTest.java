/*
 * Copyright 2012 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.core.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.model.HorizontalAlign;
import org.sejda.model.VerticalAlign;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.parameter.SetHeaderFooterParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.model.pdf.headerfooter.Numbering;
import org.sejda.model.pdf.headerfooter.NumberingStyle;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.task.Task;

import com.lowagie.text.pdf.PdfReader;

/**
 * @author Eduard Weissmann
 * 
 */
@Ignore
public abstract class SetHeaderFooterTaskTest extends PdfOutEnabledTest implements
        TestableTask<SetHeaderFooterParameters> {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);
    private SetHeaderFooterParameters parameters;

    @Before
    public void setUp() {
        TestUtils.setProperty(victim, "context", context);
    }

    /**
     * Set up of the set page labels parameters
     * 
     */
    private void setUpParams(VerticalAlign vAlign, PdfSource<?> source) {
        parameters = new SetHeaderFooterParameters();
        parameters.setNumbering(new Numbering(NumberingStyle.ARABIC, 100));
        parameters.setPageRange(new PageRange(3));

        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setFont(StandardType1Font.CURIER_BOLD_OBLIQUE);

        parameters.setSource(source);
        parameters.setOverwrite(true);
        parameters.setHorizontalAlign(HorizontalAlign.LEFT);
        parameters.setVerticalAlign(vAlign);
        parameters.setFontSize(new BigDecimal("7"));
    }

    private void setUpParameters(VerticalAlign vAlign) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "test_file.pdf");
        setUpParams(vAlign, source);
    }

    private void setUpParametersEncrypted(VerticalAlign vAlign) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/enc_with_modify_perm.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceWithPassword(stream, "test_file.pdf", "test");
        setUpParams(vAlign, source);
    }

    @Test
    public void testExecuteFooter() throws TaskException, IOException {
        setUpParameters(VerticalAlign.BOTTOM);
        doTestExecute();
        assertSpecificFooterExpectations(getResultFile());
    }

    @Test
    public void testExecuteHeader() throws TaskException, IOException {
        setUpParameters(VerticalAlign.TOP);
        doTestExecute();
        assertSpecificHeaderExpectations(getResultFile());
    }

    @Test
    public void testExecuteFooterEnc() throws TaskException, IOException {
        setUpParametersEncrypted(VerticalAlign.BOTTOM);
        doTestExecute();
        assertSpecificFooterExpectations(getResultFile());
    }

    @Test
    public void testExecuteHeaderEnc() throws TaskException, IOException {
        setUpParametersEncrypted(VerticalAlign.TOP);
        doTestExecute();
        assertSpecificHeaderExpectations(getResultFile());
    }

    private void doTestExecute() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewFileOutput(parameters);
        victim.execute(parameters);

        PdfReader reader = getReaderFromResultFile();

        assertCreator(reader);
        assertVersion(reader, PdfVersion.VERSION_1_6);
        assertEquals(4, reader.getNumberOfPages());

        reader.close();

    }

    protected abstract void assertSpecificFooterExpectations(File result) throws TaskException;

    protected abstract void assertSpecificHeaderExpectations(File result) throws TaskException;

    protected SetHeaderFooterParameters getParameters() {
        return parameters;
    }

}
