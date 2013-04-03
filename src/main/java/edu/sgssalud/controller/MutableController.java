/*
 * Copyright 2012 jlgranda.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.sgssalud.controller;

import java.io.Serializable;

/**
 *
 * @author jlgranda
 */
public abstract class MutableController<T> 
      extends PersistenceController<T> 
      implements Serializable, Mutable
{
   //copy/paste from AbstractMutable
   
   private transient boolean dirty;

   public boolean clearDirty()
   {
      boolean result = dirty;
      dirty = false;
      return result;
   }
   
   /**
    * Set the dirty flag if the value has changed.
    * Call whenever a subclass attribute is updated.
    * 
    * @param oldValue the old value of an attribute
    * @param newValue the new value of an attribute
    * @return true if the newValue is not equal to the oldValue
    */
   protected <U> boolean setDirty(U oldValue, U newValue)
   {
      boolean attributeDirty = oldValue!=newValue && (
            oldValue==null || 
            !oldValue.equals(newValue) 
         );
      dirty = dirty || attributeDirty;
      return attributeDirty;
   }
   
   /**
    * Set the dirty flag.
    */
   protected void setDirty()
   {
      dirty = true;
   }

}
