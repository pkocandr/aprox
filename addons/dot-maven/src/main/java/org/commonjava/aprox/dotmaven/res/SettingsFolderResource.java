package org.commonjava.aprox.dotmaven.res;

import io.milton.http.Auth;
import io.milton.http.Request;
import io.milton.http.Request.Method;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.resource.CollectionResource;
import io.milton.resource.MakeCollectionableResource;
import io.milton.resource.PropFindableResource;
import io.milton.resource.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.commonjava.aprox.data.ProxyDataException;
import org.commonjava.aprox.data.StoreDataManager;
import org.commonjava.aprox.dotmaven.DotMavenException;
import org.commonjava.aprox.dotmaven.webctl.RequestInfo;
import org.commonjava.aprox.model.ArtifactStore;
import org.commonjava.util.logging.Logger;

@Named( "settings-folder" )
@RequestScoped
public class SettingsFolderResource
    implements MakeCollectionableResource, PropFindableResource
{
    private final Logger logger = new Logger( getClass() );

    @Inject
    private StoreDataManager aprox;

    @Inject
    private SettingsResourceHandler settingsResourceHandler;

    @Inject
    private RequestInfo requestInfo;

    private String host;

    public void setHost( final String host )
    {
        this.host = host;
    }

    @Override
    public Resource child( final String storeName )
        throws NotAuthorizedException, BadRequestException
    {
        return settingsResourceHandler.createResource( host, storeName );
    }

    @Override
    public List<? extends Resource> getChildren()
        throws NotAuthorizedException, BadRequestException
    {
        List<ArtifactStore> all;
        try
        {
            all = aprox.getAllArtifactStores();
        }
        catch ( final ProxyDataException e )
        {
            logger.error( "Failed to retrieve list of artifact stores: %s", e, e.getMessage() );
            throw new BadRequestException( "Failed to retrieve list of settings configurations." );
        }

        final List<SettingsResource> resources = new ArrayList<SettingsResource>( all.size() );
        for ( final ArtifactStore store : all )
        {
            try
            {
                final StringBuilder storeName = new StringBuilder();
                storeName.append( "settings-" )
                         .append( store.getKey()
                                       .getType()
                                       .singularEndpointName() )
                         .append( '-' )
                         .append( store.getName() )
                         .append( ".xml" );

                resources.add( settingsResourceHandler.createResource( host, storeName.toString(), store ) );
            }
            catch ( final ProxyDataException e )
            {
                logger.error( "Failed to format settings for: %s. Reason: %s", e, store.getKey(), e.getMessage() );
                throw new BadRequestException( "Cannot retrieve list of settings configurations" );
            }
            catch ( final DotMavenException e )
            {
                logger.error( "Failed to format settings for: %s. Reason: %s", e, store.getKey(), e.getMessage() );
                throw new BadRequestException( "Cannot retrieve list of settings configurations" );
            }
        }

        return resources;
    }

    @Override
    public String getUniqueId()
    {
        return "/";
    }

    @Override
    public String getName()
    {
        return "/";
    }

    @Override
    public Object authenticate( final String user, final String password )
    {
        // FIXME: Badgr integration!
        return "ok";
    }

    @Override
    public boolean authorise( final Request request, final Method method, final Auth auth )
    {
        // FIXME: Badgr integration!
        return true;
    }

    @Override
    public String getRealm()
    {
        return requestInfo.getRealm();
    }

    @Override
    public Date getModifiedDate()
    {
        return new Date();
    }

    @Override
    public String checkRedirect( final Request request )
        throws NotAuthorizedException, BadRequestException
    {
        return null;
    }

    @Override
    public Date getCreateDate()
    {
        return new Date();
    }

    @Override
    public CollectionResource createCollection( final String newName )
        throws NotAuthorizedException, ConflictException, BadRequestException
    {
        throw new NotAuthorizedException(
                                          "Use the AProx UI to create artifact stores, then access the corresponding settings here",
                                          this );
    }

}