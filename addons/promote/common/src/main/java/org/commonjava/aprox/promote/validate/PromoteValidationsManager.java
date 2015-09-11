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
package org.commonjava.aprox.promote.validate;

import org.commonjava.aprox.audit.ChangeSummary;
import org.commonjava.aprox.model.core.StoreKey;
import org.commonjava.aprox.promote.conf.PromoteConfig;
import org.commonjava.aprox.promote.model.ValidationCatalogDTO;
import org.commonjava.aprox.promote.model.ValidationRuleDTO;
import org.commonjava.aprox.promote.model.ValidationRuleSet;
import org.commonjava.aprox.promote.validate.model.ValidationRule;
import org.commonjava.aprox.promote.validate.model.ValidationRuleMapping;
import org.commonjava.aprox.subsys.datafile.DataFile;
import org.commonjava.aprox.subsys.datafile.DataFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class PromoteValidationsManager
{

    private static final String RULES_DIR = "rules";

    private static final String RULES_SETS_DIR = "rule-sets";

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Inject
    private DataFileManager ffManager;

    @Inject
    private PromoteConfig config;

    @Inject
    private ValidationRuleParser ruleParser;

    private Map<String, ValidationRuleMapping> ruleMappings;

    private boolean enabled;

    private Map<String, ValidationRuleSet> ruleSets;

    protected PromoteValidationsManager()
    {
    }

    public PromoteValidationsManager( final DataFileManager ffManager, final PromoteConfig config,
                                      final ValidationRuleParser ruleParser )
        throws PromotionValidationException
    {
        this.ffManager = ffManager;
        this.config = config;
        this.ruleParser = ruleParser;
        parseRules();
    }

    @PostConstruct
    public void cdiInit()
    {
        try
        {
            parseRules();
        }
        catch ( final PromotionValidationException e )
        {
            logger.error( "Failed to parse validation rule: " + e.getMessage(), e );
        }
    }

    public synchronized void parseRules()
        throws PromotionValidationException
    {
        if ( !config.isEnabled() )
        {
            this.enabled = false;
            this.ruleMappings = Collections.emptyMap();

            logger.info( "Autoprox is disabled." );
            return;
        }


        final Map<String, ValidationRuleMapping> ruleMappings = new HashMap<>();

        DataFile dataDir = ffManager.getDataFile( config.getBasedir(), RULES_DIR );
        logger.info( "Scanning {} for promotion validation rules...", dataDir );
        if ( dataDir.exists() )
        {
            final DataFile[] scripts = dataDir.listFiles( (pathname) ->
            {
                    logger.info( "Checking for autoprox script in: {}", pathname );
                    return pathname.getName()
                                   .endsWith( ".groovy" );
            } );

            for ( final DataFile script : scripts )
            {
                logger.info( "Reading promotion validation rule from: {}", script );
                final ValidationRuleMapping rule = ruleParser.parseRule( script );
                if ( rule != null )
                {
                    ruleMappings.put( rule.getName(), rule );
                }
            }
        }

        this.ruleMappings = ruleMappings;

        Map<String, ValidationRuleSet> ruleSets = new HashMap<>();

        dataDir = ffManager.getDataFile( config.getBasedir(), RULES_SETS_DIR );
        logger.info( "Scanning {} for promotion validation rule-set mappings...", dataDir );
        if ( dataDir.exists() )
        {
            final DataFile[] scripts = dataDir.listFiles( (pathname) ->
                                                          {
                                                              logger.info( "Checking for promotion rule-set in: {}", pathname );
                                                              return pathname.getName()
                                                                             .endsWith( ".json" );
                                                          } );

            for ( final DataFile script : scripts )
            {
                logger.info( "Reading promotion validation rule-set from: {}", script );
                final ValidationRuleSet set = ruleParser.parseRuleSet( script );
                if ( set != null )
                {
                    ruleSets.put( script.getName(), set );
                }
            }
        }

        this.ruleSets = ruleSets;
        this.enabled = true;
    }

    public ValidationCatalogDTO toDTO()
    {
        final Map<String, ValidationRuleDTO> rules = new HashMap<>();
        for ( final ValidationRuleMapping mapping : ruleMappings.values() )
        {
            rules.put( mapping.getName(), mapping.toDTO() );
        }

        return new ValidationCatalogDTO( enabled, rules, ruleSets );
    }

    public Set<ValidationRuleMapping> getRuleMappings()
    {
        return new HashSet<>( ruleMappings.values() );
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled( final boolean enabled )
    {
        this.enabled = enabled;
    }

    public ValidationRuleSet getRuleSetMatching( StoreKey storeKey )
    {
        String keyStr = storeKey.toString();
        for ( ValidationRuleSet set : ruleSets.values() )
        {
            if ( set.matchesKey( keyStr ) )
            {
                return set;
            }
        }

        return null;
    }

    public ValidationRule getRuleNamed( final String name )
    {
        final ValidationRuleMapping mapping = getRuleMappingNamed( name );
        return mapping == null ? null : mapping.getRule();
    }

    public synchronized ValidationRuleMapping removeRuleNamed( final String name, final ChangeSummary changelog )
        throws PromotionValidationException
    {
        ValidationRuleMapping mapping = ruleMappings.remove( name );
        if ( mapping == null )
        {
            return null;
        }

        final DataFile dataDir = ffManager.getDataFile( config.getBasedir(), RULES_DIR );
        if ( !dataDir.exists() )
        {
            // this would be a very strange error...implying addition of a rule without writing it to disk.
            return null;
        }

        final DataFile scriptFile = dataDir.getChild( name );
        if ( scriptFile.exists() )
        {
            try
            {
                scriptFile.delete( changelog );

                return mapping;
            }
            catch ( final IOException e )
            {
                throw new PromotionValidationException( "Failed to delete rule: %s to: %s. Reason: %s", e, name, scriptFile,
                                                 e.getMessage() );
            }
        }

        return null;
    }

    public synchronized ValidationRuleMapping storeRule( final String name, final String spec, final ChangeSummary changelog )
        throws PromotionValidationException
    {
        final ValidationRuleMapping mapping = ruleParser.parseRule( spec, name );
        ruleMappings.put( mapping.getName(), mapping );

        final DataFile dataDir = ffManager.getDataFile( config.getBasedir(), RULES_DIR );
        if ( !dataDir.exists() )
        {
            dataDir.mkdirs();
        }

        final DataFile scriptFile = dataDir.getChild( name );
        try
        {
            scriptFile.writeString( spec, changelog );
        }
        catch ( final IOException e )
        {
            throw new PromotionValidationException( "Failed to write rule: %s to: %s. Reason: %s", e, name, scriptFile,
                                             e.getMessage() );
        }

        return mapping;
    }

    public synchronized ValidationRuleMapping getRuleMappingNamed( final String name )
    {
        return ruleMappings.get( name );
    }

}
