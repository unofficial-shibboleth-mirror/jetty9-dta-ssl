/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.shibboleth.utilities.java.support.resource;

import java.io.InputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.component.AbstractDestructableInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.component.ComponentValidationException;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import com.google.common.base.Objects;

/** Base class for resources. */
public abstract class AbstractResource extends AbstractDestructableInitializableComponent implements Resource {

    /** Location of the resource. */
    private String location;

    /**
     * Checks whether this component is valid and ready to be used.
     * <p>
     * This method delegates to {@link #doValidate()} if the component is not destroyed and is initialized.
     * </p>
     * 
     * @throws ComponentValidationException thrown if the component is not currently in a valid and usable state
     */
    public final void validate() throws ComponentValidationException {
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);

        doValidate();
    }

    /**
     * Checks whether the resource exists.
     * <p>
     * This method delegates to {@link #doExists()} if this component is not destroyed and is initialized.
     * </p>
     * 
     * @return true if the resource exists, false if not
     * 
     * @throws ResourceException thrown if there is a problem determining if the resource exists
     */
    @Nonnull public final boolean exists() throws ResourceException {
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);

        return doExists();
    }

    /**
     * Gets the input stream to the resource's data.
     * <p>
     * This method delegates to {@link #doGetInputStream()} if this component is not destroyed and is initialized.
     * </p>
     * 
     * @return input stream to the resource's data
     * 
     * @throws ResourceException thrown if an input stream can not be created for the resource
     */
    @Nonnull public final InputStream getInputStream() throws ResourceException {
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);

        return doGetInputStream();
    }

    /**
     * Gets the time, in milliseconds since the epoch, when the resource was last modified.
     * <p>
     * This method delegates to {@link #doGetLastModifiedTime()} if this component is not destroyed and is initialized.
     * </p>
     * 
     * @return time, in milliseconds since the epoch, when the resource was last modified
     * 
     * @throws ResourceException thrown if the last modified time can not be determined
     */
    @Nonnull public final long getLastModifiedTime() throws ResourceException {
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);

        return doGetLastModifiedTime();
    }

    /**
     * Gets resource location information. Examples might be filesystem path, URL, etc.
     * 
     * @return resource location information
     */
    @Nonnull public final String getLocation() {
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);

        return location;
    }

    /**
     * Two resources are considered equal if they are of the same type and and have the same location.
     * 
     * {@inheritDoc}
     */
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (obj instanceof Resource) {
            return getClass().equals(obj.getClass()) && Objects.equal(location, ((Resource) obj).getLocation());
        }

        return false;
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return Objects.hashCode(getClass(), location);
    }

    /** {@inheritDoc} */
    public String toString() {
        return Objects.toStringHelper(this).add("location", location).toString();
    }

    /**
     * Sets the resource location.
     * 
     * @param resourceLocation the resource location
     */
    protected void setLocation(@Nullable String resourceLocation) {
        location = StringSupport.trimOrNull(resourceLocation);
    }

    /**
     * Performs the logic to validate this component.
     * <p>
     * This implementation checks that the resource exists via {@link #exists()}.
     * </p>
     * 
     * @throws ComponentValidationException thrown if the component is not valid
     */
    protected void doValidate() throws ComponentValidationException {
        try {
            if (!exists()) {
                throw new ComponentValidationException(toString() + " does not exist");
            }
        } catch (ResourceException e) {
            throw new ComponentValidationException(e);
        }
    }

    /**
     * Checks whether the resource exists.
     * 
     * @return true if the resource exists, false if not
     * 
     * @throws ResourceException thrown if there is a problem determining if the resource exists
     */
    @Nonnull protected abstract boolean doExists() throws ResourceException;

    /**
     * Gets the input stream to the resource's data.
     * 
     * @return input stream to the resource's data
     * 
     * @throws ResourceException thrown if an input stream can not be created for the resource
     */
    @Nonnull protected abstract InputStream doGetInputStream() throws ResourceException;

    /**
     * Gets the time, in milliseconds since the epoch, when the resource was last modified.
     * 
     * @return time, in milliseconds since the epoch, when the resource was last modified
     * 
     * @throws ResourceException thrown if the last modified time can not be determined
     */
    @Nonnull protected abstract long doGetLastModifiedTime() throws ResourceException;
}