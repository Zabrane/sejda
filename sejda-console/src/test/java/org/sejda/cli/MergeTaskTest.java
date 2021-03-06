/*
 * Created on Jul 1, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.either;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.outline.OutlinePolicy;
import org.sejda.model.parameter.MergeParameters;
import org.sejda.model.pdf.page.PageRange;

/**
 * Tests for the MergeTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class MergeTaskTest extends AbstractTaskTest {

    public MergeTaskTest() {
        super(TestableTask.MERGE);
    }

    @Override
    @Before
    public void setUp() {
        super.setUp();

        createTestPdfFile("/tmp/merge/file1.pdf");
        createTestPdfFile("/tmp/merge/file2.pdf");
        createTestPdfFile("/tmp/merge/file3.pdf");
        createTestPdfFile("/tmp/merge/file4.pdf");

        createTestTextFile("./location/filenames.csv",
                "/tmp/merge/file3.pdf, /tmp/merge/file1.pdf, /tmp/merge/file2.pdf");
        createTestTextFile("./location/empty_filenames.csv", "");
        createTestTextFile("./location/filenames_invalidPaths.csv",
                "/tmp/merge/fileDoesntExist.pdf,/tmp/merge/file1.pdf");

        createTestTextFile("/tmp/filenames.xml", getClass().getResourceAsStream("/merge-filelist-config.xml"));
        // files inside
        createTestPdfFile("/tmp/pdf/inputFile.pdf");
        createTestPdfFile("/tmp/pdf/inputFile2.pdf");
        createTestPdfFile("/tmp/inputFile1.pdf");
        createTestPdfFile("/tmp/inputFile2.pdf");
        createTestPdfFile("/tmp/subdir/inputFile1.pdf");
        createTestPdfFile("/tmp/subdir3/inputFile2.pdf");
        createTestPdfFile("/tmp/subdir2/inputFile1.pdf");
        createTestPdfFile("/tmp/subdir2/inputFile2.pdf");
        createTestPdfFile("/tmp/subdir2/inputFile3.pdf");

        createTestTextFile("./location/filenames_invalidXml.xml", "<filelist><file value=\"/tmp/merge/file1.pdf \">");
        createTestFolder("/tmp/emptyFolder");
        createTestPdfFile("./location/filenames.xls");
    }

    @Test
    public void onCopyFields() {
        MergeParameters parameters = defaultCommandLine().withFlag("--copyFields").invokeSejdaConsole();
        assertTrue(parameters.isCopyFormFields());
    }

    @Test
    public void onAddBlanks() {
        MergeParameters parameters = defaultCommandLine().withFlag("--addBlanks").invokeSejdaConsole();
        assertTrue(parameters.isBlankPageIfOdd());
    }

    @Test
    public void onDiscardBookmarks() {
        MergeParameters parameters = defaultCommandLine().with("-b", "discard").invokeSejdaConsole();
        assertEquals(OutlinePolicy.DISCARD, parameters.getOutlinePolicy());
    }

    @Test
    public void onOneEachDoc() {
        MergeParameters parameters = defaultCommandLine().with("-b", "one_entry_each_doc").invokeSejdaConsole();
        assertEquals(OutlinePolicy.ONE_ENTRY_EACH_DOC, parameters.getOutlinePolicy());
    }

    @Test
    public void onDefaultOutlinePolicy() {
        MergeParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertEquals(OutlinePolicy.RETAIN, parameters.getOutlinePolicy());
    }

    @Test
    public void offCopyFields() {
        MergeParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertFalse(parameters.isCopyFormFields());
    }

    @Test
    public void offAddBlankss() {
        MergeParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertFalse(parameters.isBlankPageIfOdd());
    }

    @Test
    public void folderInput() {
        MergeParameters parameters = defaultCommandLine().without("-f").with("-d", "/tmp/merge").invokeSejdaConsole();

        assertPdfMergeInputsFilesList(
                parameters,
                filesList("/tmp/merge/file1.pdf", "/tmp/merge/file2.pdf", "/tmp/merge/file3.pdf",
                        "/tmp/merge/file4.pdf"));
    }

    @Test
    public void folderInput_emptyFolder() {
        defaultCommandLine().with("-d", "/tmp/emptyFolder").assertConsoleOutputContains("No input files specified in");
    }

    private static List<Matcher<Iterable<File>>> filesList(String... filenames) {
        List<Matcher<Iterable<File>>> result = new ArrayList<Matcher<Iterable<File>>>();
        CollectionUtils.collect(Arrays.asList(filenames), new Transformer() {

            public Object transform(Object input) {
                String filename = input.toString();
                if (FilenameUtils.getPrefixLength(filename) > 0) {
                    return either(hasItem(new File(filename))).or(
                            hasItem(new File(FilenameUtils.separatorsToWindows("C:" + filename))));
                }

                return hasItem(new File(filename));

            }
        }, result);

        return result;
    }

    @Test
    public void fileListConfigInput_csv() {
        MergeParameters parameters = defaultCommandLine().without("-f").with("-l", "./location/filenames.csv")
                .invokeSejdaConsole();

        assertPdfMergeInputsFilesList(parameters,
                filesList("/tmp/merge/file3.pdf", "/tmp/merge/file1.pdf", "/tmp/merge/file2.pdf"));
    }

    @Test
    public void fileListConfigInput_csv_invalidPaths() {
        defaultCommandLine().without("-f").with("-l", "./location/filenames_invalidPaths.csv")
                .assertConsoleOutputContains("Invalid filename found");
    }

    @Test
    public void fileListConfigInput_csv_doesntExist() {
        defaultCommandLine().without("-f").with("-l", "./location/doesntExist.csv")
                .assertConsoleOutputContains("does not exist");
    }

    @Test
    public void fileListConfigInput_xml_invalidXml() {
        defaultCommandLine().without("-f").with("-l", "./location/filenames_invalidXml.xml")
                .assertConsoleOutputContains("Can't extract filenames from");
    }

    @Test
    public void fileListConfigInput_xml_invalidContents() {
        defaultCommandLine().without("-f").with("-l", "./location/filenames_invalidXml.xml")
                .assertConsoleOutputContains("Can't extract filenames from");
    }

    @Test
    public void inputFiles_csv_empty() {
        defaultCommandLine().without("-f").with("-l", "./location/empty_filenames.csv")
                .assertConsoleOutputContains("No input files specified in './location/empty_filenames.csv'");
    }

    @Test
    public void inputFiles_unsupportedFormatConfigList() {
        defaultCommandLine().without("-f").with("-l", "./location/filenames.xls")
                .assertConsoleOutputContains("Unsupported file format: xls");
    }

    @Test
    public void filesInput() {
        MergeParameters parameters = defaultCommandLine().with("-f", "/tmp/merge/file4.pdf /tmp/merge/file2.pdf")
                .invokeSejdaConsole();

        assertPdfMergeInputsFilesList(parameters, filesList("/tmp/merge/file4.pdf", "/tmp/merge/file2.pdf"));
    }

    @Test
    public void input_tooManyOptionsGiven() {
        defaultCommandLine().without("-f").with("-d", "/tmp/merge").with("-l", "./location/filenames.xls")
                .assertConsoleOutputContains("Unsupported file format: xls");
    }

    private void assertPdfMergeInputsFilesList(MergeParameters parameters,
            Collection<Matcher<Iterable<File>>> expectedFilesMatchers) {
        assertPdfMergeInputsFilesList(parameters, expectedFilesMatchers, nullsFilledList(parameters.getInputList()
                .size()));
    }

    private List<String> nullsFilledList(int size) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            result.add(null);
        }

        return result;
    }

    private void assertPdfMergeInputsFilesList(MergeParameters parameters,
            Collection<Matcher<Iterable<File>>> expectedFilesMatchers, List<String> expectedFilesPasswords) {
        List<File> actualFileList = new ArrayList<File>();
        List<String> actualPasswords = new ArrayList<String>();

        for (int i = 0; i < parameters.getInputList().size(); i++) {
            PdfMergeInput each = parameters.getInputList().get(i);
            PdfFileSource pdfFileSource = (PdfFileSource) each.getSource();
            actualFileList.add(pdfFileSource.getSource());
            actualPasswords.add(pdfFileSource.getPassword());
        }

        for (Matcher<Iterable<File>> expectedFileMatcher : expectedFilesMatchers) {
            assertThat(actualFileList, expectedFileMatcher);
        }
        assertEquals(expectedFilesPasswords, actualPasswords);
    }

    @Test
    public void fileListConfigInput_xml() {
        MergeParameters parameters = defaultCommandLine().without("-f").with("-l", "/tmp/filenames.xml")
                .invokeSejdaConsole();

        assertPdfMergeInputsFilesList(
                parameters,
                filesList("/tmp/pdf/inputFile.pdf", "/tmp/pdf/inputFile2.pdf", "/tmp/inputFile1.pdf",
                        "/tmp/inputFile2.pdf", "/tmp/subdir/inputFile1.pdf", "/tmp/subdir3/inputFile2.pdf",
                        "/tmp/subdir2/inputFile1.pdf", "/tmp/subdir2/inputFile2.pdf", "/tmp/subdir2/inputFile3.pdf"),
                Arrays.asList(null, "test", null, null, null, null, null, "secret2", null));
    }

    private static final String NO_PASSWORD = null;
    private static final Set<PageRange> NO_PAGE_RANGE_SPECIFIED = Collections.emptySet();
    private static final Set<PageRange> ALL_PAGES = Collections.emptySet();

    @Test
    public void pageRange_empty() {
        MergeParameters parameters = defaultCommandLine().invokeSejdaConsole();

        assertHasPdfMergeInput(parameters, "inputs/input.pdf", NO_PAGE_RANGE_SPECIFIED);
    }

    @Test
    public void pageRange_simpleInterval() {
        MergeParameters parameters = defaultCommandLine().with("-s", "3003-3010").invokeSejdaConsole();

        assertHasPdfMergeInput(parameters, "inputs/input.pdf", Arrays.asList(new PageRange(3003, 3010)));
        assertHasPdfMergeInput(parameters, "inputs/second_input.pdf", NO_PAGE_RANGE_SPECIFIED);
    }

    @Test
    public void pageRange_openedInterval() {
        MergeParameters parameters = defaultCommandLine().with("-s", "2-").invokeSejdaConsole();

        assertHasPdfMergeInput(parameters, "inputs/input.pdf", Arrays.asList(new PageRange(2)));
        assertHasPdfMergeInput(parameters, "inputs/second_input.pdf", NO_PAGE_RANGE_SPECIFIED);
    }

    @Test
    public void pageRange_singlePage() {
        MergeParameters parameters = defaultCommandLine().with("-s", "3003").invokeSejdaConsole();

        assertHasPdfMergeInput(parameters, "inputs/input.pdf", Arrays.asList(new PageRange(3003, 3003)));
        assertHasPdfMergeInput(parameters, "inputs/second_input.pdf", NO_PAGE_RANGE_SPECIFIED);
    }

    @Test
    public void pageRange_allPages() {
        MergeParameters parameters = defaultCommandLine().with("-s", "all").invokeSejdaConsole();

        assertHasPdfMergeInput(parameters, "inputs/input.pdf", ALL_PAGES);
        assertHasPdfMergeInput(parameters, "inputs/second_input.pdf", NO_PAGE_RANGE_SPECIFIED);
    }

    @Test
    public void pageRange_combined() {
        MergeParameters parameters = defaultCommandLine().with("-s", "all:3,5,8-10,2,2,9-9,30-").invokeSejdaConsole();

        assertHasPdfMergeInput(parameters, "inputs/input.pdf", ALL_PAGES);
        assertHasPdfMergeInput(parameters, "inputs/second_input.pdf", Arrays.asList(new PageRange(3, 3), new PageRange(
                5, 5), new PageRange(8, 10), new PageRange(2, 2), new PageRange(9, 9), new PageRange(30)));
    }

    private void assertHasPdfMergeInput(MergeParameters parameters, String filename,
            Collection<PageRange> expectedPageRanges) {
        assertHasPdfMergeInput(parameters, filename, NO_PASSWORD, expectedPageRanges);
    }

    private void assertHasPdfMergeInput(MergeParameters parameters, String filename, String password,
            Collection<PageRange> expectedPageRanges) {
        boolean found = false;
        File file = new File(filename);
        for (PdfMergeInput each : parameters.getInputList()) {
            PdfFileSource pdfFileSource = (PdfFileSource) each.getSource();
            if (matchesPdfFileSource(file, password, pdfFileSource)) {
                assertContainsAll("For file " + pdfFileSource.getName(), expectedPageRanges, each.getPageSelection());
                found = true;
            }
        }

        assertTrue("File '" + file + "'"
                + (StringUtils.isEmpty(password) ? " and no password" : " and password '" + password + "'"), found);

    }
}
