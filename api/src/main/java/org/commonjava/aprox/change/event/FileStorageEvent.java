/*******************************************************************************
 * Copyright 2011 John Casey
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.commonjava.aprox.change.event;

import org.commonjava.aprox.io.StorageItem;

public class FileStorageEvent
    extends FileEvent
{

    public enum Type
    {
        DOWNLOAD, GENERATE, UPLOAD;
    }

    final Type type;

    public FileStorageEvent( final Type type, final StorageItem storageLocation )
    {
        super( storageLocation );
        this.type = type;
    }

    public Type getType()
    {
        return type;
    }

    @Override
    public String getExtraInfo()
    {
        return "type=" + type.name();
    }

}
