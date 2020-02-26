/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.unomi.graphql.fetchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.apache.unomi.api.conditions.Condition;
import org.apache.unomi.api.services.DefinitionsService;
import org.apache.unomi.graphql.types.input.CDPEventFilterInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

public abstract class BaseDataFetcher<T> implements DataFetcher<T> {

    protected static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static int DEFAULT_PAGE_SIZE = 10;

    private static final Logger logger = LoggerFactory.getLogger(BaseDataFetcher.class);

    protected <E> E parseObjectParam(String name, Class<E> clazz, DataFetchingEnvironment environment) {
        final Object param = environment.getArgument(name);
        if (param == null) {
            return null;
        }
        return OBJECT_MAPPER.convertValue(param, clazz);
    }

    protected <K> K parseParam(final String name, K defaultValue, final DataFetchingEnvironment environment) {
        return (K) Optional.of(environment.getArgument(name)).orElse(defaultValue);
    }

    protected Date parseDateParam(final String name, final DataFetchingEnvironment environment) {
        final String paramValue = environment.getArgument(name);
        Date param = null;
        if (!Strings.isNullOrEmpty(paramValue)) {
            try {
                param = DateFormat.getInstance().parse(paramValue);
            } catch (ParseException e) {
                logger.warn(String.format("Invalid date format for field '%s': %s", name, paramValue));
            }
        }
        return param;
    }

    protected Condition createBoolCondition(final String operator, DefinitionsService definitionsService) {
        final Condition andCondition = new Condition(definitionsService.getConditionType("booleanCondition"));
        andCondition.setParameter("operator", operator);
        return andCondition;
    }

}
