/*
 * Created on 06/ago/2011
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
package org.sejda.core.manipulation.model.parameter;

import javax.validation.constraints.Min;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Parameter class for a split by bookmark level task.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SplitByBookmarkLevelParameters extends AbstractSplitParameters {

    @Min(1)
    private int levelToSplitAt;
    private String matchingBookmarkRegEx;

    public SplitByBookmarkLevelParameters(int levelToSplitAt) {
        super();
        this.levelToSplitAt = levelToSplitAt;
    }

    public int getLevelToSplitAt() {
        return levelToSplitAt;
    }

    public String getMatchingBookmarkRegEx() {
        return matchingBookmarkRegEx;
    }

    public void setMatchingBookmarkRegEx(String matchingBookmarkRegEx) {
        this.matchingBookmarkRegEx = matchingBookmarkRegEx;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(levelToSplitAt).append(matchingBookmarkRegEx)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SplitByBookmarkLevelParameters)) {
            return false;
        }
        SplitByBookmarkLevelParameters parameter = (SplitByBookmarkLevelParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other))
                .append(levelToSplitAt, parameter.getLevelToSplitAt())
                .append(matchingBookmarkRegEx, parameter.getMatchingBookmarkRegEx()).isEquals();
    }
}
