/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.logging.slf4j;

import com.alibaba.mtc.MtContextThreadLocal;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.spi.MDCAdapter;

/**
 *
 */
public class Log4jMDCAdapter implements MDCAdapter {
    MtContextThreadLocal<Map<String, String>> log4j2Context = new MtContextThreadLocal<Map<String, String>>() {
        @Override
        protected void beforeExecute() {
            final Map<String, String> log4j2Context = get();
            for (Map.Entry<String, String> entry : log4j2Context.entrySet()) {
                ThreadContext.put(entry.getKey(), entry.getValue());
            }
        }

        @Override
        protected void afterExecute() {
            ThreadContext.clearAll();
        }

        @Override
        protected Map<String, String> initialValue() {
            return new HashMap<String, String>();
        }
    };

    @Override
    public void put(final String key, final String val) {
        ThreadContext.put(key, val);

        log4j2Context.get().put(key, val);
    }

    @Override
    public String get(final String key) {
        return ThreadContext.get(key);
    }

    @Override
    public void remove(final String key) {
        ThreadContext.remove(key);

        log4j2Context.get().remove(key);
    }

    @Override
    public void clear() {
        ThreadContext.clearMap();

        log4j2Context.get().clear();
    }

    @Override
    public Map<String, String> getCopyOfContextMap() {
        return ThreadContext.getContext();
    }

    @Override
    @SuppressWarnings("unchecked") // nothing we can do about this, restricted by SLF4J API
    public void setContextMap(@SuppressWarnings("rawtypes") final Map map) {
        ThreadContext.clearMap();
        for (final Map.Entry<String, String> entry : ((Map<String, String>) map).entrySet()) {
            ThreadContext.put(entry.getKey(), entry.getValue());

            log4j2Context.get().put(entry.getKey(), entry.getValue());
        }
    }
}
