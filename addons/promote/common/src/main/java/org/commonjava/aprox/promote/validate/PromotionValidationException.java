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

import org.commonjava.aprox.promote.data.PromotionException;

import java.text.MessageFormat;
import java.util.IllegalFormatException;

public class PromotionValidationException
    extends PromotionException
{
    private static final long serialVersionUID = 1L;

    public PromotionValidationException( final String message, final Throwable error, final Object... params )
    {
        super( message, error, params );
    }

    public PromotionValidationException( final String message, final Object... params )
    {
        super( message, params );
    }

}
