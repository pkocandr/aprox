/**
 * Copyright (C) 2011 Red Hat, Inc. (jdcasey@commonjava.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.commonjava.aprox.promote.model;

import java.util.Collections;
import java.util.Set;

/**
 * Contains the result of a promotion attempt. If the promotion is a success, the pending paths and error will be <b>null</b>. Otherwise, these are
 * populated to support the resume feature (for transient or correctable errors).
 * 
 * @author jdcasey
 *
 */
public class PathsPromoteResult
{

    private PathsPromoteRequest request;

    private Set<String> pendingPaths;

    private Set<String> completedPaths;

    private ValidationResult validations;

    private String error;

    public PathsPromoteResult()
    {
    }

    public PathsPromoteResult( final PathsPromoteRequest request, final Set<String> pending, final Set<String> complete,
                               final String error )
    {
        this.request = request;
        this.pendingPaths = pending;
        this.completedPaths = complete;
        this.error = error;
    }

    public PathsPromoteResult( final PathsPromoteRequest request, final Set<String> pending, final Set<String> complete,
                               final ValidationResult validations )
    {
        this.request = request;
        this.pendingPaths = pending;
        this.completedPaths = complete;
        this.validations = validations;
        this.error = null;
    }

    public ValidationResult getValidations()
    {
        return validations;
    }

    public void setValidations( ValidationResult validations )
    {
        this.validations = validations;
    }

    public Set<String> getPendingPaths()
    {
        return pendingPaths == null ? Collections.<String> emptySet() : pendingPaths;
    }

    public void setPendingPaths( final Set<String> pendingPaths )
    {
        this.pendingPaths = pendingPaths;
    }

    public Set<String> getCompletedPaths()
    {
        return completedPaths == null ? Collections.<String> emptySet() : completedPaths;
    }

    public void setCompletedPaths( final Set<String> completedPaths )
    {
        this.completedPaths = completedPaths;
    }

    public String getError()
    {
        return error;
    }

    public void setError( final String error )
    {
        this.error = error;
    }

    public PathsPromoteRequest getRequest()
    {
        return request;
    }

    public void setRequest( final PathsPromoteRequest request )
    {
        this.request = request;
    }

    @Override
    public String toString()
    {
        return String.format( "PathsPromoteResult [\n  request=%s\n  pendingPaths=%s\n  completedPaths=%s\n  error=%s\n  validations:\n  %s\n]",
                              request, pendingPaths, completedPaths, error, validations );
    }

}
