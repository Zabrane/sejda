/*
 * Created on 03/lug/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.support.prefix.processor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.sejda.core.support.prefix.model.NameGenerationRequest;

/**
 * Process the input prefix replacing all the [FILENUMBER] or [FILENUMBERE##] or [FILENUMBER##11] or [FILENUMBER11] occurrences with the input file number (formatted with the given
 * pattern identified by the number of # and incremeted by the starting nmber if found). Ex:
 * <p>
 * <b>[FILENUMBER]_BLA_[FILENUMBERE###]_LAB_[FILENUMBER####100]</b> and given file number <b>2</b> will produce <b>2_BLA_002_LAB_0102</b>
 * </p>
 * 
 * @author Andrea Vacondio
 * 
 */
class FileNumberPrefixProcessor extends NumberFormatEnabledPrefixProcessor {

    private static final String FIND_REGX = "\\[FILENUMBER(#*)(\\d*)\\]";

    public String process(String inputPrefix, NameGenerationRequest request) {
        String retVal = "";
        if (request != null && request.getFileNumber() != null) {
            retVal = findAndReplace(inputPrefix, request.getFileNumber());
        }
        return (StringUtils.isBlank(retVal)) ? inputPrefix : retVal;
    }

    /**
     * Try to find matches and replace them with the formatted file number. An empty string is returned if no match is found.
     * 
     * @param inputString
     * @param fileNumber
     * @return the processed string if a match is found. An empty string if no match is found.
     */
    private String findAndReplace(String inputString, Integer fileNumber) {
        StringBuffer sb = new StringBuffer();
        Matcher m = Pattern.compile(FIND_REGX).matcher(inputString);
        while (m.find()) {
            String replacement = getReplacement(m.group(1), m.group(2), fileNumber);
            m.appendReplacement(sb, replacement);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * @param numberPatter
     * @param startingNumber
     * @param fileNumber
     * @return the string the processor will use to perform replacement
     */
    private String getReplacement(String numberPatter, String startingNumber, Integer fileNumber) {
        String replacement = "";
        Integer number = getReplacementNumber(startingNumber, fileNumber);
        if (StringUtils.isNotBlank(numberPatter)) {
            replacement = formatter(numberPatter).format(number);
        } else {
            replacement = number.toString();
        }
        return replacement;
    }

    /**
     * @param startingNumber
     * @param fileNumber
     * @return the number calculated as fileNumber + startingNumber
     */
    private Integer getReplacementNumber(String startingNumber, Integer fileNumber) {
        Integer retVal = fileNumber;
        if (StringUtils.isNotBlank(startingNumber)) {
            retVal += Integer.valueOf(startingNumber);
        }
        return retVal;
    }

}
