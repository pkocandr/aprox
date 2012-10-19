package org.commonjava.aprox.conf;

import org.commonjava.web.config.ConfigUtils;

public abstract class AbstractAproxConfigInfo
    implements AproxConfigInfo
{

    private final Class<?> type;

    private final String sectionName;

    AbstractAproxConfigInfo()
    {
        type = Object.class;
        sectionName = null;
    }

    protected AbstractAproxConfigInfo( final Class<?> type )
    {
        this( type, null );
    }

    protected AbstractAproxConfigInfo( final Class<?> type, final String sectionName )
    {
        this.type = type;
        this.sectionName = sectionName;
    }

    /* (non-Javadoc)
     * @see org.commonjava.aprox.conf.AproxConfigInfo#getConfigurationClass()
     */
    @Override
    public Class<?> getConfigurationClass()
    {
        return type;
    }

    /* (non-Javadoc)
     * @see org.commonjava.aprox.conf.AproxConfigInfo#getSectionName()
     */
    @Override
    public String getSectionName()
    {
        return sectionName;
    }

    @Override
    public String toString()
    {
        final String key = sectionName == null ? ConfigUtils.getSectionName( type ) : sectionName;
        return key + " [" + type.getName() + "]";
    }

}