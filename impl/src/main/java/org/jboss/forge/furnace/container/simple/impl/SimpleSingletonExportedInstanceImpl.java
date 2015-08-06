/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.container.simple.impl;

import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.exception.ContainerException;
import org.jboss.forge.furnace.proxy.ClassLoaderInterceptor;
import org.jboss.forge.furnace.proxy.Proxies;
import org.jboss.forge.furnace.spi.ExportedInstance;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class SimpleSingletonExportedInstanceImpl<T> implements ExportedInstance<T>
{
   private final Addon addon;
   private final Class<T> type;
   private T delegate = null;

   public SimpleSingletonExportedInstanceImpl(Furnace furnace, Addon addon, Class<T> clazz)
   {
      this.addon = addon;
      this.type = clazz;
   }

   @Override
   public T get()
   {
      if (delegate == null)
      {
         try
         {
            delegate = type.newInstance();
            delegate = Proxies.enhance(addon.getClassLoader(), delegate, new ClassLoaderInterceptor(
                     addon.getClassLoader(), delegate));
         }
         catch (Exception e)
         {
            throw new ContainerException("Could not create instance of [" + type.getName() + "] through reflection.",
                     e);
         }
      }
      return delegate;
   }

   @Override
   public void release(T instance)
   {
      if (instance == delegate)
         delegate = null;
   }

   @Override
   public String toString()
   {
      return "Singleton: " + type.getName() + " from " + addon;
   }

   @Override
   public Class<? extends T> getActualType()
   {
      return type;
   }

   @Override
   public Addon getSourceAddon()
   {
      return addon;
   }
}
