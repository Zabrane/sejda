/*
 * Created on Sep 30, 2011
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
package org.sejda.cli.model;

import java.util.List;

import org.sejda.cli.model.adapter.PdfFileSourceAdapter;
import org.sejda.cli.model.adapter.RectangularBoxAdapter;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * CLI interface for the Crop task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " crop")
public interface CropTaskCliArguments extends CliArgumentsWithPdfFileOutput {

    @Option(shortName = "c", description = "list of rectangles crop areas. A crop area is defined by two points: bottomLeft and topRight, int the format [bottom:left][top:right]. Ex: --cropAreas [0:0][5:10] [5:0][10:10] (required)")
    List<RectangularBoxAdapter> getCropAreas();

    // override default -f option that is described as expecting a list of files with a description stating that it is expecting a single file
    @Option(shortName = "f", description = FILES_OPTION_DESCRIPTION_WHEN_EXPECTING_A_SINGLE_FILE)
    List<PdfFileSourceAdapter> getFiles();
}
