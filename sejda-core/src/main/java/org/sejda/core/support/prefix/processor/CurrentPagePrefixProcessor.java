/*
 * Created on 01/lug/2010
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
 * Process the input prefix replacing all the [CURRENTPAGE] or [CURRENTPAGE##] occurrences with the input current page number (formatted with the given pattern identified by the
 * number of #). Ex:
 * <p>
 * <b>[CURRENTPAGE]_BLA_[CURRENTPAGE####]_LAB</b> and given page number <b>2</b> will produce <b>2_BLA_0002_LAB</b>
 * </p>
 * 
 * @author Andrea Vacondio
 * 
 */
class CurrentPagePrefixProcessor extends NumberFormatEnabledPrefixProcessor {

    private static final String FIND_REGEXP = "\\[CURRENTPAGE(#*)\\]";

    public String process(String inputPrefix, NameGenerationRequest request) {
        String retVal = "";
        if (request != null && request.getPage() != null) {
            retVal = findAndReplace(inputPrefix, request.getPage());
        }
        return (StringUtils.isBlank(retVal)) ? inputPrefix : retVal;
    }

    /**
     * Try to find matches and replace them with the formatted page number. An empty string is returned if no match is found.
     * 
     * @param inputString
     * @param pageNumber
     * @return
     */
    private String findAndReplace(String inputString, Integer pageNumber) {
        StringBuffer sb = new StringBuffer();
        Matcher m = Pattern.compile(FIND_REGEXP).matcher(inputString);
        while (m.find()) {
            String replacement = getReplacement(m.group(1), pageNumber);
            m.appendReplacement(sb, replacement);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * @param numberPatter
     * @param pageNumber
     *            the page number
     * @return the string used by the processor to replace
     */
    private String getReplacement(String numberPatter, Integer pageNumber) {
        String replacement = "";
        if (StringUtils.isNotBlank(numberPatter)) {
            replacement = formatter(numberPatter).format(pageNumber);
        } else {
            replacement = pageNumber.toString();
        }
        return replacement;
    }

}
