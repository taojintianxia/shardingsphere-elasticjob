/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.elasticjob.tracing.rdb.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.shardingsphere.elasticjob.tracing.api.TracingStorageConfiguration;
import org.apache.shardingsphere.elasticjob.tracing.exception.TracingStorageUnavailableException;
import org.apache.shardingsphere.elasticjob.tracing.storage.TracingStorageConverter;
import org.apache.shardingsphere.elasticjob.tracing.storage.TracingStorageConverterFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataSourceTracingStorageConverterTest {
    
    @Mock
    private DataSource dataSource;
    
    @Mock
    private Connection connection;
    
    @Mock
    private DatabaseMetaData databaseMetaData;
    
    @Test
    void assertConvert() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(databaseMetaData);
        when(databaseMetaData.getURL()).thenReturn("jdbc:url");
        DataSourceTracingStorageConverter converter = new DataSourceTracingStorageConverter();
        TracingStorageConfiguration<DataSource> configuration = converter.convertObjectToConfiguration(dataSource);
        assertNotNull(configuration);
    }
    
    @Test
    void assertConvertFailed() {
        assertThrows(TracingStorageUnavailableException.class, () -> {
            DataSourceTracingStorageConverter converter = new DataSourceTracingStorageConverter();
            doThrow(SQLException.class).when(dataSource).getConnection();
            converter.convertObjectToConfiguration(dataSource);
        });
    }
    
    @Test
    void assertStorageType() {
        TracingStorageConverter<HikariDataSource> converter = TracingStorageConverterFactory.findConverter(HikariDataSource.class).orElse(null);
        assertNotNull(converter);
        assertThat(converter.storageType(), is(DataSource.class));
    }
}
