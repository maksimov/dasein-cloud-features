/**
 * Copyright (C) 2009-2015 Dell, Inc.
 * See annotations for authorship information
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.dasein.cloud.features;

import org.dasein.cloud.CloudProvider;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * The matrix build finds all classes extending CloudProvider, instantiates each of them and prints
 * the return values from all the hasXxxSupport() methods, thus building a service implementation matrix.
 *
 * Created by stas on 12/01/2015.
 */
public class MatrixBuilder {
    Map<String, Map<String, Boolean>> featureMap  = new TreeMap<String, Map<String, Boolean>>();
    Map<String, String>               featureDict = new TreeMap<String, String>(); // all features

    public void build() {
        Reflections reflections = new Reflections("org.dasein.cloud");
        Set<Class<? extends CloudProvider>> providers = reflections.getSubTypesOf(CloudProvider.class);
        for( Class<? extends CloudProvider> provider : providers ) {
            CloudProvider providerInstance = null;
            try {
                providerInstance = provider.newInstance();
            }
            catch( InstantiationException e ) { /* ignore abstract clouds */ }
            catch( IllegalAccessException e ) {
            }

            if( providerInstance == null ) {
                continue;
            }
            Map<String, Boolean> providerFeatures = new TreeMap<String, Boolean>();
            featureMap.put(providerInstance.getProviderName(), providerFeatures);
            populateSupportedServices(providerFeatures, "compute", providerInstance.getComputeServices());
            populateSupportedServices(providerFeatures, "admin", providerInstance.getAdminServices());
            populateSupportedServices(providerFeatures, "ci", providerInstance.getCIServices());
            populateSupportedServices(providerFeatures, "platform", providerInstance.getPlatformServices());
            populateSupportedServices(providerFeatures, "network", providerInstance.getNetworkServices());
            populateSupportedServices(providerFeatures, "storage", providerInstance.getStorageServices());
        }

        // TODO: refactor all this printing non-sense out of here
        int maxFeatureLen = longestValue(featureDict.keySet());
        int maxProviderLen = longestValue(featureMap.keySet());
        String rowFormat = "|%-"+maxFeatureLen+"s|";
        for(String key : featureMap.keySet()) {
            rowFormat += "%-"+maxProviderLen+"s|";
        }
        rowFormat += "\n";
        // print header
        Set<String> headers = new TreeSet<String>();
        headers.add(" ");
        headers.addAll(featureMap.keySet());
        String heading = String.format(rowFormat, headers.toArray(new String[0]));
        int rowLen = heading.length();
        System.out.print(heading);

        // build divider
        String[] blanks = new String[featureMap.keySet().size()+1];
        Arrays.fill(blanks, "");
        String divider = String.format(rowFormat, blanks).replace(' ', '-');

        // print values
        List<String> row = new ArrayList<String>();
        for(String key : featureDict.keySet() ) {
            String keyLabel = key;
            if( keyLabel.indexOf(".") > 0) {
                // indent the nested services
                keyLabel = "   " + keyLabel.substring(keyLabel.indexOf(".")+1);
            } else {
                // divide the core services with a line
                System.out.print(divider);
            }
            row.add(keyLabel);
            for(String providerKey : featureMap.keySet()) {
                Boolean value = featureMap.get(providerKey).get(key);
                row.add((value != null && value) ? "X" : "");
            }
            System.out.format(rowFormat, row.toArray(new String[0]));
            row.clear();
        }
    }

    private int longestValue(Set<String> values) {
        int max = 0;
        for( String value : values ) {
            if( value.length() > max) {
                max = value.length();
            }
        }
        return max;
    }

    private void populateSupportedServices( Map<String, Boolean> providerFeatures, String rootService, Object services ) {
        providerFeatures.put(rootService, services != null );
        featureDict.put(rootService, null);
        if( services == null ) {
            return;
        }
        Method[] methods = services.getClass().getMethods();
        for( Method method : methods ) {
            String name = method.getName();
            if( name.startsWith("has") && name.endsWith("Support") ) {
                try {
                    Boolean support = ( Boolean ) method.invoke(services);
                    providerFeatures.put(rootService + "." + name.substring(3, 4).toLowerCase() + name.substring(4, name.indexOf("Support")), support);
                    featureDict.put(rootService + "." + name.substring(3, 4).toLowerCase() + name.substring(4, name.indexOf("Support")), null);
                }
                catch( IllegalAccessException e ) {
                    e.printStackTrace();
                }
                catch( InvocationTargetException e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String [] arg) {
        new MatrixBuilder().build();
    }

}
