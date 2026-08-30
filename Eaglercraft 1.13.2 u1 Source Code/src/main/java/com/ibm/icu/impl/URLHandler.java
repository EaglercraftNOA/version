/*
 ******************************************************************************
 * Copyright (C) 2005-2011, International Business Machines Corporation and   *
 * others. All Rights Reserved.                                               *
 ******************************************************************************
 */

package com.ibm.icu.impl;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class URLHandler {
    public static final String PROPNAME = "urlhandler.props";
    
    private static final boolean DEBUG = ICUDebug.enabled("URLHandler");
    
    public static URLHandler get(URL url) {
        if (url == null) {
            return null;
        }
        
        return getDefault(url);
    }
    
    protected static URLHandler getDefault(URL url) {
        URLHandler handler = null;

        String protocol = url.getProtocol();
        try {
            if (protocol.equals("file")) {
                handler = new FileURLHandler(url);
            } else if (protocol.equals("jar") || protocol.equals("wsjar")) {
                handler = new JarURLHandler(url);
            }
        } catch (Exception e) {
            // ignore - just return null
        }
        return handler;
    }
    
    private static class FileURLHandler extends URLHandler {
        File file;

        FileURLHandler(URL url) {
            try {
                file = new File(url.toURI());
            } catch (URISyntaxException use) {
                // fall through
            }
            if (file == null || !file.exists()) {
                if (DEBUG) System.err.println("file does not exist - " + url.toString());
                throw new IllegalArgumentException();
            }
        }
        
        public void guide(URLVisitor v, boolean recurse, boolean strip) {
            if (file.isDirectory()) {
                process(v, recurse, strip, "/", file.listFiles());
            } else {
                v.visit(file.getName());
            }
        }
        
        private void process(URLVisitor v, boolean recurse, boolean strip, String path, File[] files) {
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                
                if (f.isDirectory()) {
                    if (recurse) {
                        process(v, recurse, strip, path + f.getName()+ '/', f.listFiles());
                    }
                } else {
                    v.visit(strip? f.getName() : path + f.getName());
                }
            }
        }
    }
    
    private static class JarURLHandler extends URLHandler {
        JarFile jarFile;
        String prefix;

        JarURLHandler(URL url) {
            try {
                prefix = url.getPath();
                
                int ix = prefix.lastIndexOf("!/");
                
                if (ix >= 0) {
                    prefix = prefix.substring(ix + 2); // truncate after "!/"
                }

                String protocol = url.getProtocol();
                if (!protocol.equals("jar")) {
                    // change the protocol to "jar"
                    // Note: is this really OK?
                    String urlStr = url.toString();
                    int idx = urlStr.indexOf(":");
                    if (idx != -1) {
                        url = new URL("jar" + urlStr.substring(idx));
                    }
                }

                JarURLConnection conn = (JarURLConnection)url.openConnection();
                jarFile = conn.getJarFile();
            }
            catch (Exception e) {
                if (DEBUG) System.err.println("icurb jar error: " + e);
                throw new IllegalArgumentException("jar error: " + e.getMessage());
            }
        }
        
        public void guide(URLVisitor v, boolean recurse, boolean strip) {
            try {
                Enumeration<JarEntry> entries = jarFile.entries();
                
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    
                    if (!entry.isDirectory()) { // skip just directory paths
                        String name = entry.getName();
                        
                        if (name.startsWith(prefix)) {
                            name = name.substring(prefix.length());
                            
                            int ix = name.lastIndexOf('/');
                            
                            if (ix != -1) {
                                if (!recurse) {
                                    continue;
                                }
                                
                                if (strip) {
                                    name = name.substring(ix+1);
                                }
                            }
                            
                            v.visit(name);
                        }
                    }
                }
            }
            catch (Exception e) {
                if (DEBUG) System.err.println("icurb jar error: " + e);
            }
        }
    }

    public void guide(URLVisitor visitor, boolean recurse)
    {
        guide(visitor, recurse, true);
    }
    
    public abstract void guide(URLVisitor visitor, boolean recurse, boolean strip);
    
    public interface URLVisitor {
        void visit(String str);
    }
}
