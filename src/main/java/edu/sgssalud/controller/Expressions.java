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

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.inject.Named;


/**
 * Factory for EL method and value expressions.
 * 
 * This default implementation uses JBoss EL.
 * 
 * @author Gavin King
 * @adapter José Luis Granda
 */

@Named("org.eqaula.glue.expressions")
public class Expressions implements Serializable
{
   
   /**
    * Get the JBoss EL ExpressionFactory
     * @return 
    */
   public ExpressionFactory getExpressionFactory()
   {
      return ExpressionFactory.newInstance();
   }
   
   /**
    * Get an appropriate ELContext. If there is an active JSF request,
    * use JSF's ELContext. Otherwise, use one that we created.
     * @return 
    */
   public ELContext getELContext()
   {
      //return EL_CONTEXT;
       return null;
   }

   /**
    * Create a value expression.
    * 
    * @param expression a JBoss EL value expression
     * @return 
    */
   public ValueExpression<Object> createValueExpression(String expression)
   {
      return createValueExpression(expression, Object.class);
   }
   
   /**
    * Create a method expression.
    * 
    * @param expression a JBoss EL method expression
     * @return 
    */
   public MethodExpression<Object> createMethodExpression(String expression)
   {
      return createMethodExpression(expression, Object.class);
   }
   
   /**
    * Create a value expression.
    * 
     * @param <T>
    * @param expression a JBoss EL value expression
    * @param type the type of the value 
     * @return  
    */
   public <T> ValueExpression<T> createValueExpression(final String expression, final Class<T> type)
   {
      
      return new ValueExpression<T>()
      {
         private javax.el.ValueExpression facesValueExpression;
         private javax.el.ValueExpression seamValueExpression;
         
         @Override
         public javax.el.ValueExpression toUnifiedValueExpression()
         {
            if ( isFacesContextActive() )
            {
               if (seamValueExpression==null)
               {
                  seamValueExpression = createExpression();
               }
               return seamValueExpression;
            }
            else
            {
               if (facesValueExpression==null)
               {
                  facesValueExpression = createExpression();
               }
               return facesValueExpression;
            }
         }
         
         private javax.el.ValueExpression createExpression()
         {
            return getExpressionFactory().createValueExpression( getELContext(), expression, type );
         }
         
         @Override
         public T getValue()
         {
            return (T) toUnifiedValueExpression().getValue( getELContext() );
         }
         
         @Override
         public void setValue(T value)
         {
            toUnifiedValueExpression().setValue( getELContext(), value );
         }
         
         @Override
         public String getExpressionString()
         {
            return expression;
         }
         
         @Override
         public Class<T> getType()
         {
            return (Class<T>) toUnifiedValueExpression().getType( getELContext() );
         }
         
      };
   }
   
   /**
    * Create a method expression.
    * 
     * @param <T>
    * @param expression a JBoss EL method expression
    * @param type the method return type
    * @param argTypes the method parameter types
     * @return 
    */
   public <T> MethodExpression<T> createMethodExpression(final String expression, final Class<T> type, final Class... argTypes)
   {
      return new MethodExpression<T>()
      {
         private javax.el.MethodExpression facesMethodExpression;
         private javax.el.MethodExpression seamMethodExpression;
         
         @Override
         public javax.el.MethodExpression toUnifiedMethodExpression()
         {
            if ( isFacesContextActive() )
            {
               if (seamMethodExpression==null)
               {
                  seamMethodExpression = createExpression();
               }
               return seamMethodExpression;
            }
            else
            {
               if (facesMethodExpression==null)
               {
                  facesMethodExpression = createExpression();
               }
               return facesMethodExpression;
            }
         }
         
         private javax.el.MethodExpression createExpression()
         {
            return getExpressionFactory().createMethodExpression( getELContext(), expression, type, argTypes );
         }
         
         @Override
         public T invoke(Object... args)
         {
            return (T) toUnifiedMethodExpression().invoke( getELContext(), args );
         }
         
         @Override
         public String getExpressionString()
         {
            return expression;
         }
         
      };
   }
   
   /**
    * A value expression - an EL expression that evaluates to
    * an attribute getter or get/set pair. This interface
    * is just a genericized version of the Unified EL ValueExpression
    * interface.
    * 
    * @author Gavin King
    *
    * @param <T> the type of the value
    */
   public static interface ValueExpression<T> extends Serializable
   {
      public T getValue();
      public void setValue(T value);
      public String getExpressionString();
      public Class<T> getType();
      /**
       * @return the underlying Unified EL ValueExpression
       */
      public javax.el.ValueExpression toUnifiedValueExpression();
   }
   
   /**
    * A method expression - an EL expression that evaluates to
    * a method. This interface is just a genericized version of 
    * the Unified EL ValueExpression interface.
    * 
    * @author Gavin King
    *
    * @param <T> the method return type
    */
   public static interface MethodExpression<T> extends Serializable
   {
      public T invoke(Object... args);
      public String getExpressionString();
      /**
       * @return the underlying Unified EL MethodExpression
       */
      public javax.el.MethodExpression toUnifiedMethodExpression();
   }
   
   protected boolean isFacesContextActive()
   {
      return false;
   }
   
   public static Expressions instance()
   {
      //return (Expressions) Component.getInstance(Expressions.class, ScopeType.APPLICATION);
      return null;
   }
   
}
