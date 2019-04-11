package org.commonjava.indy.subsys.infinispan.config;

import org.apache.commons.lang.StringUtils;
import org.commonjava.indy.conf.IndyConfigInfo;
import org.commonjava.indy.conf.SystemPropertyProvider;
import org.commonjava.web.config.annotation.ConfigName;
import org.commonjava.web.config.annotation.SectionName;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.InputStream;
import java.util.Properties;

@SectionName( "infinispan-cluster" )
@ApplicationScoped
public class ISPNClusterConfiguration
                implements IndyConfigInfo, SystemPropertyProvider
{

    private static final String INDY_JGROUPS_GOSSIP_ROUTER_HOSTS = "jgroups.gossip_router_hosts";

    private static final String INDY_JGROUP_TCP_BIND_PORT = "jgroups.tcp.bind_port";

    private static final String DEFAULT_INDY_JGROUP_TCP_BIND_PORT = "7800";

    private static final Boolean DEFAULT_ENABLED = Boolean.FALSE;

    public ISPNClusterConfiguration()
    {

    }

    private Boolean enabled;

    private String gossipRouterHosts;

    private String tcpBindPort;

    public String getGossipRouterHosts() { return gossipRouterHosts; }

    @ConfigName( INDY_JGROUPS_GOSSIP_ROUTER_HOSTS )
    public void setGossipRouterHosts( String gossipRouterHosts ) { this.gossipRouterHosts = gossipRouterHosts; }

    public String getTcpBindPort() { return tcpBindPort == null ? DEFAULT_INDY_JGROUP_TCP_BIND_PORT : tcpBindPort; }

    @ConfigName( INDY_JGROUP_TCP_BIND_PORT )
    public void setTcpBindPort( String tcpBindPort ) { this.tcpBindPort = tcpBindPort; }

    public Boolean isEnabled() { return enabled == null ? DEFAULT_ENABLED : enabled; }

    @ConfigName( "enabled" )
    public void setEnabled( Boolean enabled ) { this.enabled = enabled; }

    @Override
    public String getDefaultConfigFileName()
    {
        return new File( IndyConfigInfo.CONF_INCLUDES_DIR, "infinispan-cluster.conf" ).getPath();
    }

    @Override
    public InputStream getDefaultConfig()
    {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream( "default-infinispan-cluster.conf" );
    }

    @Override
    public Properties getSystemProperties()
    {
        Properties properties = new Properties();
        preparePropertyInSysEnv( properties, INDY_JGROUPS_GOSSIP_ROUTER_HOSTS, getGossipRouterHosts() );
        preparePropertyInSysEnv( properties, INDY_JGROUP_TCP_BIND_PORT, getTcpBindPort() );

        return properties;
    }

    private void preparePropertyInSysEnv( Properties props, String propName, String ifNotInSysEnv )
    {
        String propVal = System.getenv( propName );
        if ( StringUtils.isBlank( propVal ) )
        {
            propVal = System.getProperty( propName );
        }
        if ( StringUtils.isBlank( propVal ) )
        {
            propVal = ifNotInSysEnv;
        }
        if ( StringUtils.isNotBlank( propVal ) )
        {
            propVal = propVal.replace( "\"", "" );

            props.setProperty( propName, propVal );
        }
    }
}
