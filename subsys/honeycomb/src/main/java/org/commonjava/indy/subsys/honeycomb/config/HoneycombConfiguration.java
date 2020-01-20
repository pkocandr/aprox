/**
 * Copyright (C) 2011-2020 Red Hat, Inc. (https://github.com/Commonjava/indy)
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
package org.commonjava.indy.subsys.honeycomb.config;

import org.commonjava.indy.conf.IndyConfigInfo;
import org.commonjava.propulsor.config.annotation.ConfigName;
import org.commonjava.propulsor.config.annotation.SectionName;

import javax.enterprise.context.ApplicationScoped;
import java.io.InputStream;

import static org.commonjava.indy.metrics.RequestContextHelper.CLIENT_ADDR;
import static org.commonjava.indy.metrics.RequestContextHelper.CONTENT_TRACKING_ID;
import static org.commonjava.indy.metrics.RequestContextHelper.HTTP_METHOD;
import static org.commonjava.indy.metrics.RequestContextHelper.HTTP_STATUS;
import static org.commonjava.indy.metrics.RequestContextHelper.PACKAGE_TYPE;
import static org.commonjava.indy.metrics.RequestContextHelper.PATH;
import static org.commonjava.indy.metrics.RequestContextHelper.PREFERRED_ID;
import static org.commonjava.indy.metrics.RequestContextHelper.REST_METHOD_PATH;
import static org.commonjava.indy.metrics.RequestContextHelper.X_FORWARDED_FOR;

@SectionName( "honeycomb" )
@ApplicationScoped
public class HoneycombConfiguration
                implements IndyConfigInfo
{
    private static final String[] FIELDS =
            { CONTENT_TRACKING_ID, HTTP_METHOD, HTTP_STATUS, PREFERRED_ID, CLIENT_ADDR, PATH, PACKAGE_TYPE, REST_METHOD_PATH };

    private boolean enabled;

    private String writeKey;

    private String dataset;

    public HoneycombConfiguration()
    {
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    @ConfigName( "enabled" )
    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }

    public String getWriteKey()
    {
        return writeKey;
    }

    @ConfigName( "write.key" )
    public void setWriteKey( String writeKey )
    {
        this.writeKey = writeKey;
    }

    public String getDataset()
    {
        return dataset;
    }

    @ConfigName( "dataset" )
    public void setDataset( String dataset )
    {
        this.dataset = dataset;
    }

    @Override
    public String getDefaultConfigFileName()
    {
        return "honeycomb.conf";
    }

    @Override
    public InputStream getDefaultConfig()
    {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream( "default-honeycomb.conf" );
    }

    public String[] getFields()
    {
        return FIELDS;
    }
}